package com.boats.n.fluids;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.boats.n.containers.ArrayTableGrid;
import com.boats.n.containers.Grid;
import com.boats.n.containers.Grids;
import com.boats.n.containers.Pair;
import com.boats.n.functions.Isomorphism;
import com.boats.n.utils.*;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * Created by ben on 8/18/14.
 */
public class JosStamFluidSolver implements FluidSolver
{
    private static final Supplier<Vector2> ZERO_VECTOR_SUPPLIER = new Supplier<Vector2>() {
        @Override
        public Vector2 get() {
            return Vector2.Zero.cpy();
        }
    };
    private static final float VISC = 0.01f;
    private static final float DIFF = 0.000001f;

    int width;
    int height;

    private Grid<Float> density;
    private Grid<Float> densityBuffer;
    private Grid<Vector2> velocity;
    private Grid<Vector2> velocityBuffer;
    private Grid<Float> curl;
    private DensitySolver densitySolver;
    private VelocitySolver velocitySolver;
    private List<Pair<Vector2, Vector2>> velocityUpdateBuffer;
    public static final Isomorphism<Float, Vector1> FLOAT_VECTOR_1_ISOMORPHISM = new Isomorphism<Float, Vector1>()
    {
        @Override
        public Vector1 forward(Float from)
        {
            return new Vector1(from);
        }

        @Override
        public Float backward(Vector1 to)
        {
            return to.x;
        }
    };

    public JosStamFluidSolver(int w, int h)
    {
        width = w;
        height = h;

        densitySolver = new DensitySolver();
        velocitySolver = new VelocitySolver();

        velocityUpdateBuffer = Lists.newArrayList();

        velocityBuffer = ArrayTableGrid.create(width + 2, height + 2, ZERO_VECTOR_SUPPLIER);
        densityBuffer = ArrayTableGrid.create(width + 2, height + 2, 0.0f);

        density = ArrayTableGrid.create(width + 2, height + 2, new Supplier<Float>()
        {
            @Override
            public Float get()
            {
                return 0.6f;
            }
        });

        velocity = ArrayTableGrid.create(width + 2, height + 2, ZERO_VECTOR_SUPPLIER);

                /* new ArrayTableGrid<Vector2>(width + 2, height + 2, new Function<Pair<Integer, Integer>, Vector2>()
        {
            @Override
            public Vector2 apply(Pair<Integer, Integer> input)
            {
                int x = input.getFirst();
                int y = input.getSecond();
                float dx = width / 2.0f - x;
                float dy = height / 2.0f - y;

                float angle = -MathUtils.atan2(dx, dy);
                float dist = (float) Math.sqrt(dx * dx + dy * dy) / 50.0f;

                return new Vector2(MathUtils.cos(angle) * dist, MathUtils.sin(angle) * dist);
            }
        }); */
        curl = ArrayTableGrid.create(width + 2, height + 2, 0.0f);
    }

    @Override
    public void step(float dt)
    {
        for (Pair<Vector2, Vector2> positionAndHeading : velocityUpdateBuffer)
        {
            Vector2 position = positionAndHeading.getFirst();
            Vector2 heading = positionAndHeading.getSecond();

            _addVelocity(position, heading, dt);
        }

        velocityUpdateBuffer.clear();

        velocitySolver.solve(velocityBuffer, velocity, VISC, dt);
        densitySolver.solve(densityBuffer, density, velocityBuffer, DIFF, dt);

        velocity = velocityBuffer;
        velocityBuffer = ArrayTableGrid.create(width + 2, height + 2, ZERO_VECTOR_SUPPLIER);
        densityBuffer = ArrayTableGrid.create(width + 2, height + 2, 0.0f);

    }

    public class DensitySolver
    {
        public void diffuse(Grid<Float> buffer, Grid<Float> curDensity, float diff, float dt)
        {
            float a = dt * diff * width * height;
            for (int k = 0; k < 20; k++) {
                for (int x = 1; x <= width ; x++) {
                    for (int y = 1; y <= height; y++) {
                        buffer.put(x, y, (curDensity.get(x, y) + a*(buffer.get(x - 1, y) +
                                buffer.get(x + 1, y) +
                                buffer.get(x, y - 1) +
                                buffer.get(x, y + 1)))/(1 + 4 * a));
                    }
                }
                setBoundary(0, Grids.transform(buffer, FLOAT_VECTOR_1_ISOMORPHISM));
            }
        }

        public void advect(Grid<Float> buffer, Grid<Float> curDensity, Grid<Vector2> velocity, float dt)
        {
            float dt0 = (float) (dt * Math.sqrt(width * width + height * height));
            for (int x = 1; x <= width; x++)
            {
                for (int y = 1; y <= height; y++)
                {
                    float i = x - dt0 * velocity.get(x, y).x;
                    float j = y - dt0 * velocity.get(x, y).y;
                    i = MathUtils.clamp(i, 0.5f, width + 0.5f);
                    j = MathUtils.clamp(j, 0.5f, height + 0.5f);

                    int x0 = (int) i;
                    int y0 = (int) j;
                    int x1 = x0 + 1;
                    int y1 = y0 + 1;

                    float dx1 = i - x0;
                    float dx0 = 1 - dx1;
                    float dy1 = j - y0;
                    float dy0 = 1 - dy1;

                    buffer.put(x, y, dx0 * (dy0 * curDensity.get(x0, y0) + dy1 * curDensity.get(x0, y1)) + dx1 * (dy0 * curDensity.get(x1, y0) + dy1 * curDensity.get(x1, y1)));
                }
            }
        }

        public void solve(Grid<Float> densityBuffer, Grid<Float> curDensity, Grid<Vector2> velocity, float diff, float dt)
        {

            diffuse(densityBuffer, curDensity, diff, dt);
            advect(curDensity, densityBuffer, velocity, dt);
            setBoundary(0, Grids.transform(curDensity, FLOAT_VECTOR_1_ISOMORPHISM));
        }
    }

    public class VelocitySolver
    {
        public void solve(Grid<Vector2> buffer, Grid<Vector2> curVelocity, float visc, float dt)
        {
            diffuse(buffer, curVelocity, visc, dt);
            project(curVelocity, buffer);

            advect(buffer, curVelocity, curVelocity, dt);
            setBoundary(1, curVelocity);
            project(buffer, curVelocity);
        }

        private void project(Grid<Vector2> curVelocity, Grid<Vector2> buffer)
        {
            float h = (float) (1.0 / Math.sqrt(width * width + height * height));

            for (int x = 1; x <= width; x++)
            {
                for (int y = 1; y <= height; y++)
                {
                    buffer.get(x, y).x = -0.5f * h * (curVelocity.get(x + 1, y).x -
                                                      curVelocity.get(x - 1, y).x +
                                                      curVelocity.get(x, y + 1).y -
                                                      curVelocity.get(x, y - 1).y);
                    buffer.get(x, y).y = 0;
                }
            }

            setBoundary(0, buffer);

            for (int k = 0; k < 20; k++) {
                for (int x = 1; x <= width ; x++) {
                    for (int y = 1; y <= height; y++) {
                        buffer.get(x, y).y = (buffer.get(x, y).y +
                                              buffer.get(x - 1, y).x +
                                              buffer.get(x + 1, y).x +
                                              buffer.get(x, y - 1).x +
                                              buffer.get(x, y + 1).x)/4.0f;
                    }
                }
                setBoundary(0, buffer);
            }

            for (int x = 1; x <= width; x++)
            {
                for (int y = 1; y <= height; y++)
                {
                    curVelocity.get(x, y).x -= 0.5f * (buffer.get(x + 1, y).y - buffer.get(x - 1, y).y) / h;
                    curVelocity.get(x, y).y -= 0.5f * (buffer.get(x, y + 1).y - buffer.get(x, y - 1).y) / h;
                }
            }
            setBoundary(1, curVelocity);
        }

        public void diffuse(Grid<Vector2> buffer, Grid<Vector2> curVelocity, float visc, float dt)
        {
            float a = dt * visc * width * height;
            for (int k = 0; k < 20; k++) {
                for (int x = 1; x <= width ; x++) {
                    for (int y = 1; y <= height; y++) {
                        buffer.put(x, y, buffer.get(x - 1, y).cpy()
                                               .add(buffer.get(x + 1, y))
                                               .add(buffer.get(x, y - 1))
                                               .add(buffer.get(x, y + 1))
                                               .scl(a)
                                               .add(curVelocity.get(x, y))
                                               .scl(1.0f/(1 + 4 * a)));
                    }
                }
                setBoundary(1, buffer);
            }
        }
    }

    /**
     * Calculate the input array after advection. We start with an
     * input array from the previous timestep and an and output array.
     * For all grid cells we need to calculate for the next timestep,
     * we trace the cell's center position backwards through the
     * velocity field. Then we interpolate from the grid of the previous
     * timestep and assign this value to the current grid cell.
     *
     * @param to Array to store the advected field.
     * @param from The array to advect.
     * @param velocity the velocity grid.
     **/
    private <T extends Vector<T>> void advect(Grid<T> to, Grid<T> from, Grid<Vector2> velocity, float dt)
    {
        float n = (float) Math.sqrt(width*width + height*height);
        float dt0 = dt * n;

        for (int col = 1; col <= width; col++)
        {
            for (int row = 1; row <= height; row++)
            {
                float x = MathUtils.clamp(col - dt0 * velocity.get(col, row).x, 0.5f, width + 0.5f);
                float y = MathUtils.clamp(row - dt0 * velocity.get(col, row).y, 0.5f, height + 0.5f);


                int left = (int) x;
                int right = left + 1;

                int top = (int) y;
                int bottom = top + 1;

                float dLeft = x - left;
                float dRight = 1 - dLeft;
                float dTop = y - top;
                float dBottom = 1 - dTop;

                T topLeft = from.get(left, top).cpy().scl(dLeft).scl(dTop);
                T bottomLeft = from.get(left, bottom).cpy().scl(dLeft).scl(dBottom);
                T bottomRight = from.get(right, bottom).cpy().scl(dRight).scl(dBottom);
                T topRight = from.get(right, top).cpy().scl(dRight).scl(dTop);

                to.put(col, row, topLeft
                        .add(bottomLeft)
                        .add(topRight)
                        .add(bottomRight));

            }
        }
    }

    private <T extends Vector<T>> void setBoundary(int b, Grid<T> grid)
    {

        for (int x = 1; x <= width; x++)
        {
            grid.put(x, 0, b == 1 ? grid.get(x, 1).cpy().scl(0) : grid.get(x, 1).cpy().scl(0));
            grid.put(x, height + 1, b == 1 ? grid.get(x, height).cpy().scl(0) : grid.get(x, height).cpy().scl(0));
        }

        for (int y = 1; y <= height; y++)
        {
            grid.put(0, y, b != 1 ? grid.get(1, y).cpy().scl(0) : grid.get(1, y).cpy());
            grid.put(width + 1, 0, b != 1 ? grid.get(width, y).cpy().scl(0) : grid.get(width, y).cpy().scl(0));
        }

        grid.put(0, 0, grid.get(1, 0).cpy().add(grid.get(0, 1)).scl(0.5f));
        grid.put(0, height +1, grid.get(1, height + 1).cpy().add(grid.get(0, height)).scl(0.5f));
        grid.put(width + 1, 0, grid.get(width, 0).cpy().add(grid.get(height +1, 1)).scl(0.5f));
        grid.put(width +1, height +1, grid.get(width, height +1).cpy().add(grid.get(width +1, height)).scl(0.5f));
    }

    @Override
    public void addVelocity(int x, int y, float dx, float dy)
    {
        velocityUpdateBuffer.add(Pair.of(new Vector2(x, y), new Vector2(dx, dy)));
    }

    @Override
    public void addVelocity(int x, int y, Vector2 delta)
    {
           velocityUpdateBuffer.add(Pair.of(new Vector2(x, y), delta.cpy()));
    }

    private void _addVelocity(Vector2 position, Vector2 heading, float dt)
    {
        velocity.get(MathUtils.floor(position.x), MathUtils.floor(position.y)).add(heading.scl(dt));
    }


    private float curl(int x, int y) {
        return 0;
    }

    @Override
    public Grid<Float> getDensity()
    {
        return density;
    }
    @Override
    public Grid<Vector2> getVelocity()
    {
        return velocity;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Iterable<Vector2> getParticles()
    {
        return Collections.emptyList();
    }
}
