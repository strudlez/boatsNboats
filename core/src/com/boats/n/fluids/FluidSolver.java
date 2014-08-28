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
import com.google.common.base.Function;
import com.google.common.base.Supplier;

import java.util.Random;

/**
 * Created by ben on 8/18/14.
 */
public class FluidSolver
{
    private static final Supplier<Vector2> ZERO_VECTOR_SUPPLIER = new Supplier<Vector2>() {
        @Override
        public Vector2 get() {
            return Vector2.Zero.cpy();
        }
    };
    private static final float VISC = 0.0f;
    private static final float DIFF = 0.00f;

    float timeStep;
    int width;
    int height;

    private Grid<Float> density;
    private Grid<Vector2> velocity;
    private Grid<Float> curl;
    private DensitySolver densitySolver;
    private VelocitySolver velocitySolver;

    public FluidSolver(int w, int h, float dt)
    {
        timeStep = dt;
        width = w;
        height = h;

        densitySolver = new DensitySolver(width, height);
        velocitySolver = new VelocitySolver(width, height);

        density = new ArrayTableGrid<Float>(width + 2, height + 2, new Supplier<Float>()
        {
            @Override
            public Float get()
            {
                return new Random().nextFloat();
            }
        });

        velocity = new ArrayTableGrid<Vector2>(width + 2, height + 2, new Function<Pair<Integer, Integer>, Vector2>()
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
        });
        curl = new ArrayTableGrid<Float>(width + 2, height + 2, 0.0f);
    }

    public void step(float dt)
    {
        densitySolver.solve(density, DIFF, dt);
        velocitySolver.solve(velocity, VISC, dt);
    }

    public class DensitySolver
    {
        private Grid<Float> buffer;

        public DensitySolver(int width, int height)
        {
            buffer = new ArrayTableGrid<Float>(width + 2, height + 2, 0.0f);
        }

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
            }
        }

        public void advect(Grid<Float> buffer, Grid<Float> curDensity, float dt)
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

        public void solve(Grid<Float> curDensity, float diff, float dt)
        {
            diffuse(buffer, curDensity, diff, dt);
            advect(curDensity, buffer, dt);
        }
    }

    public class VelocitySolver
    {
        private Grid<Vector2> buffer;

        public VelocitySolver(int width, int height)
        {
            buffer = new ArrayTableGrid<Vector2>(width + 2, height + 2, ZERO_VECTOR_SUPPLIER);
        }

        public Grid<Vector2> solve(Grid<Vector2> curVelocity, float visc, float dt)
        {
            diffuse(curVelocity, buffer, visc, dt);
            project(curVelocity, buffer);

            advect(buffer, curVelocity, curVelocity);
            project(buffer, curVelocity);

            return buffer;
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
            }

            for (int x = 1; x <= width; x++)
            {
                for (int y = 1; y <= height; y++)
                {
                    curVelocity.get(x, y).x -= 0.5f * (buffer.get(x + 1, y).y - buffer.get(x - 1, y).y) / h;
                    curVelocity.get(x, y).y -= 0.5f * (buffer.get(x, y + 1).y - buffer.get(x, y - 1).y) / h;
                }
            }
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
            }
        }
    }


    /**
     * Calculate the curl at position (x, y) in the fluid grid.
     * Physically this represents the vortex strength at the
     * cell. Computed as follows: w = (del x U) where U is the
     * velocity vector at (x, y).
     *
     * @param x The x index of the cell.
     * @param y The y index of the cell.
     **/
    public float getCurl(int x, int y)
    {
        float du_dy = (velocity.get(x, y + 1).y - velocity.get(x, y - 1).y) * 0.5f;
        float dv_dx = (velocity.get(x + 1, y).x - velocity.get(x - 1, y).x) * 0.5f;
        return du_dy - dv_dx;
    }

    private Grid<Vector2> getVorticityConfinement()
    {
        Grid<Vector2> vorticityConfinement = new ArrayTableGrid<Vector2>(width, height, ZERO_VECTOR_SUPPLIER);

        for (int x = 1; x <= width; x++)
        {
            for (int y = 1; y <= height; y++)
            {
                curl.put(x, y, Math.abs(getCurl(x, y)));
            }
        }

        for (int x = 2; x < width; x++)
        {
            for (int y = 2; y < height; y++)
            {
                float dw_dx = (curl.get(x + 1, y) - curl.get(x - 1, y)) * 0.5f;
                float dw_dy = (curl.get(x, y + 1) - curl.get(x, y - 1)) * 0.5f;

                float length = (float) Math.sqrt(dw_dx * dw_dx + dw_dy * dw_dy) + 0.00001f;

                dw_dx /= length;
                dw_dy /= length;

                float v = getCurl(x, y);

                vorticityConfinement.put(x, y, new Vector2(dw_dy * -v, dw_dx * v));
            }
        }

        return vorticityConfinement;
    }

    public void solveVelocity()
    {
        Grid<Vector2> vorticity = getVorticityConfinement();
        for (int x : vorticity.getXs())
        {
            for (int y : vorticity.getYs())
            {
                addVelocity(x, y, vorticity.get(x, y));
            }
        }

        Grid<Vector2> buffer = vorticity;

        diffuse(0, buffer, velocity, VISC);

        Grid<Float> p = new ArrayTableGrid<Float>(width, height, 0.0f);
        Grid<Float> div = new ArrayTableGrid<Float>(width, height, 0.0f);

        project(buffer, p, div);

        Grid<Vector2> tmp = velocity;
        velocity = buffer;
        buffer = tmp;

        advect(velocity, buffer, buffer);

        Pair<? extends Grid<Vector1>, ? extends Grid<Vector1>> xsAndYs = Grids.split(Grids.transform(velocity, new Isomorphism<Vector2, Pair<Vector1, Vector1>>()
        {
            @Override
            public Pair<Vector1, Vector1> forward(Vector2 from)
            {
                return Pair.of(new Vector1(from.x), new Vector1(from.y));
            }

            @Override
            public Vector2 backward(Pair<Vector1, Vector1> to)
            {
                return new Vector2(to.getFirst().x, to.getSecond().x);
            }
        }));

        setBoundary(1, xsAndYs.getFirst());
        setBoundary(2, xsAndYs.getSecond());

        // project(velocity, buffer);
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
    private <T extends Vector<T>> void advect(Grid<T> to, Grid<T> from, Grid<Vector2> velocity)
    {
        float n = (float) Math.sqrt(width*width + height*height);
        float dt0 = timeStep * n;

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

                float s1 = x - left;
                float s0 = 1 - s1;
                float t1 = y - top;
                float t0 = 1 - t1;

                to.put(col, row, from.get(left, top).cpy()
                                     .scl(t0)
                                     .add(from.get(left, bottom).cpy()
                                              .scl(t1))
                                     .scl(s0).add(from.get(right, top).cpy()
                                                      .scl(t0)
                                                      .add(from.get(right, bottom).cpy()
                                                               .scl(t1))
                                                      .scl(s1)
                ));

            }
        }
    }

    /**
     * Use project() to make the velocity a mass conserving,
     * incompressible field. Achieved through a Hodge
     * decomposition. First we calculate the divergence field
     * of our velocity using the mean finite differnce approach,
     * and apply the linear solver to compute the Poisson
     * equation and obtain a "height" field. Now we subtract
     * the gradient of this field to obtain our mass conserving
     * velocity field.
     *
     * @param buffer the velocity buffer
     * @param p A temporary array we can use in the computation.
     * @param div Another temporary array we use to hold the
     * velocity divergence field.
     *
     **/
    private void project(Grid<Vector2> buffer, Grid<Float> p, Grid<Float> div)
    {
        float n = (float) Math.sqrt(width*width + height*height);

        for (int x = 1; x <= width; x++)
        {
            for (int y = 1; y <= height; y++)
            {
                float divValue = (velocity.get(x + 1, y).x - velocity.get(x - 1, y).x +
                                  velocity.get(x, y + 1).y - velocity.get(x, y - 1).y) * (-0.5f) / n;
                div.put(x, y, divValue);
            }
        }

        Isomorphism<Float, Vector1> floatVector1Isomorphism = new Isomorphism<Float, Vector1>()
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

        Grid<Vector1> divVec = Grids.transform(div, floatVector1Isomorphism);
        Grid<Vector1> pVec = Grids.transform(p, floatVector1Isomorphism);

        setBoundary(0, divVec);
        setBoundary(0, pVec);

        linearSolver(0, pVec, divVec, 1, 4);

        for (int x = 1; x <= width; x++)
        {
            for (int y = 1; y <= height; y++)
            {
                buffer.get(x, y).x -= 0.5f * n * (p.get(x + 1, y) - p.get(x - 1, y));
                buffer.get(x, y).y -= 0.5f * n * (p.get(x, y + 1) - p.get(x, y - 1));
            }
        }

        Pair<? extends Grid<Vector1>, ? extends Grid<Vector1>> xsAndYs = Grids.split(Grids.transform(buffer, new Isomorphism<Vector2, Pair<Vector1, Vector1>>()
        {
            @Override
            public Pair<Vector1, Vector1> forward(Vector2 from)
            {
                return Pair.of(new Vector1(from.x), new Vector1(from.y));
            }

            @Override
            public Vector2 backward(Pair<Vector1, Vector1> to)
            {
                return new Vector2(to.getFirst().x, to.getSecond().x);
            }
        }));

        setBoundary(1, xsAndYs.getFirst());
        setBoundary(2, xsAndYs.getSecond());
    }


    /**
     * Recalculate the input array with diffusion effects.
     * Here we consider a stable method of diffusion by
     * finding the densities, which when diffused backward
     * in time yield the same densities we started with.
     * This is achieved through use of a linear solver to
     * solve the sparse matrix built from this linear system.
     *  @param b Flag to specify how boundries should be handled.
     * @param to
     * @param from The input array on which we should compute
     * diffusion.
     * @param diff The factor of diffusion.
     **/
    private void diffuse(int b, Grid<Vector2> to, Grid<Vector2> from, float diff)
    {
        float a = timeStep * diff * width * height;
        linearSolver(b, to, from, a, 1 + 4 * a);
    }

    private <T extends Vector<T>> void linearSolver(int b, Grid<T> to, Grid<T> from, float a, float c)
    {
        for (int k = 0; k < 20; k++)
        {
            for (int x = 1; x <= width; x++)
            {
                for (int y = 1; y <= height; y++)
                {
                    to.put(x, y, from.get(x - 1, y).cpy()
                            .add(from.get(x + 1, y))
                            .add(from.get(x, y - 1))
                            .add(from.get(x, y + 1))
                            .scl(a)
                            .add(to.get(x, y)) // TODO: try from?
                            .scl(1 / c));
                }
            }
            setBoundary(b, velocity);
        }
    }

    private <T extends Vector<T>> void setBoundary(int b, Grid<T> grid)
    {
        for (int x = 1; x <= width; x++)
        {
            grid.put(x, 0, b == 2 ? grid.get(x, 1).cpy().scl(-1) : grid.get(x, 1).cpy());
            grid.put(x, height + 1, b == 2 ? grid.get(x, height).cpy().scl(-1) : grid.get(x, height).cpy());
        }

        for (int y = 1; y <= height; y++)
        {
            grid.put(0, y, b == 1 ? grid.get(1, y).cpy().scl(-1) : grid.get(1, y).cpy());
            grid.put(width + 1, 0, b == 1 ? grid.get(width, y).cpy().scl(-1) : grid.get(width, y).cpy());
        }

        grid.put(0, 0, grid.get(1, 0).cpy().add(grid.get(0, 1)).scl(0.5f));
        grid.put(0, height +1, grid.get(1, height + 1).cpy().add(grid.get(0, height)).scl(0.5f));
        grid.put(width + 1, 0, grid.get(width, 0).cpy().add(grid.get(height +1, 1)).scl(0.5f));
        grid.put(width +1, height +1, grid.get(width, height +1).cpy().add(grid.get(width +1, height)).scl(0.5f));
    }

    public void addVelocity(int x, int y, float dx, float dy)
    {
        velocity.get(x, y).add(dx*timeStep, dy*timeStep);
    }

    public void addVelocity(int x, int y, Vector2 delta)
    {
        velocity.get(x, y).add(delta);
    }


    private float curl(int x, int y) {
        return 0;
    }

    public Grid<Float> getDensity()
    {
        return density;
    }
    public Grid<Vector2> getVelocity()
    {
        return velocity;
    }
}
