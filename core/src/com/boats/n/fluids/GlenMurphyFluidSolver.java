package com.boats.n.fluids;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.boats.n.containers.*;
import com.boats.n.functions.Isomorphism;
import com.boats.n.utils.ListUtils;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by ben on 9/2/14.
 */
public class GlenMurphyFluidSolver implements FluidSolver
{
    private final Grid<VSquare> v;
    private final Grid<VBuffer> vbuf;
    private final List<Particle> particles;
    private final int height;
    private final int width;
    private final List<Triple<Integer, Integer, Vector2>> velocityBuffer;

    public GlenMurphyFluidSolver(final int width, final int height, int numParticles)
    {
        vbuf = ArrayTableGrid.create(width, height, new Function<Pair<Integer, Integer>, VBuffer>()
        {
            @Override
            public VBuffer apply(Pair<Integer, Integer> pos)
            {
                int x = pos.getFirst();
                int y = pos.getSecond();

                return new VBuffer(x, y);
            }
        });

        v = ArrayTableGrid.create(width, height, new Function<Pair<Integer, Integer>, VSquare>()
        {
            @Override
            public VSquare apply(Pair<Integer, Integer> pos)
            {
                int x = pos.getFirst();
                int y = pos.getSecond();

                return new VSquare(x, y);
            }
        });

        particles = ListUtils.nInstances(numParticles, new Supplier<Particle>()
        {
            @Override
            public Particle get()
            {
                return new Particle(MathUtils.random(0.0f, width), MathUtils.random(0.0f, height));
            }
        });
        this.width = width;
        this.height = height;
        velocityBuffer = Lists.newArrayList();
    }

    @Override
    public void step(float dt)
    {
        for (int i = 1; i < width - 1; i++)
        {
            for (int j = 1; j < height - 1; j++)
            {
                vbuf.get(i, j).updateBuffer(v.get(i - 1, j - 1),
                                            v.get(i - 1, j),
                                            v.get(i - 1, j + 1),
                                            v.get(i, j + 1),
                                            v.get(i + 1, j + 1),
                                            v.get(i + 1, j),
                                            v.get(i + 1, j - 1),
                                            v.get(i, j - 1));
                v.get(i, j).setCol(32);
            }
        }

        for (Particle p : particles)
        {
            p.updatepos(dt);
        }

        for (Triple<Integer, Integer, Vector2> triple : velocityBuffer)
        {
            int i = triple.getFirst();
            int j = triple.getSecond();
            Vector2 velocity = triple.getThird();
            v.get(i, j).getVelocity().add(velocity.scl(dt));

        }

        for (int i = 1; i < width - 1; i++)
        {
            for (int j = 1; j < height - 1; j ++)
            {
                v.get(i, j).addBuffer(i, j);
            }
        }
    }

    @Override
    public void addVelocity(int x, int y, float dx, float dy)
    {
        addVelocity(x, y, new Vector2(dx, dy));
    }

    @Override
    public void addVelocity(int x, int y, Vector2 delta)
    {
        velocityBuffer.add(Triple.of(x, y, delta));
    }

    @Override
    public Grid<Float> getDensity()
    {
        return Grids.transform(v, new Isomorphism<VSquare, Float>()
        {
            @Override
            public Float forward(VSquare from)
            {
                return from.getTCol();
            }

            @Override
            public VSquare backward(Float to)
            {
                throw new UnsupportedOperationException("Don't edit density");
            }
        });
    }

    @Override
    public Grid<Vector2> getVelocity()
    {
        return Grids.transform(v, new Isomorphism<VSquare, Vector2>()
        {
            @Override
            public Vector2 forward(VSquare from)
            {
                return from.getVelocity();
            }

            @Override
            public VSquare backward(Vector2 to)
            {
                throw new UnsupportedOperationException("Don't edit velocity");
            }
        });
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public Iterable<Vector2> getParticles()
    {
        return Iterables.transform(particles, new Function<Particle, Vector2>()
        {
            @Override
            public Vector2 apply(Particle input)
            {
                return input.getPosition();
            }
        });
    }

    private class VSquare
    {
        private final int x;
        private final int y;
        private final Vector2 velocity;
        private float col;

        public VSquare(int x, int y)
        {
            this.x = x;
            this.y = y;
            velocity = new Vector2(0, 0);
            col = 0;
        }

        public Vector2 getVelocity()
        {
            return velocity;
        }

        public float getCol()
        {
            return col;
        }

        public void setCol(float col)
        {
            this.col = col;
        }

        public void addBuffer(int i, int j)
        {
            velocity.x += (vbuf.get(i - 1, j - 1).getPressure1() * 0.5 +
                    vbuf.get(i - 1, j).getPressure1() +
                    vbuf.get(i - 1, j + 1).getPressure1() * 0.5 -
                    vbuf.get(i + 1, j - 1).getPressure1() * 0.5 -
                    vbuf.get(i + 1, j).getPressure1() -
                    vbuf.get(i + 1, j + 1).getPressure1() * 0.5) * 0.25;
            velocity.y += (vbuf.get(i - 1, j - 1).getPressure1() * 0.5 +
                    vbuf.get(i, j - 1).getPressure1() +
                    vbuf.get(i + 1, j - 1).getPressure1() * 0.5 -
                    vbuf.get(i - 1, j + 1).getPressure1() * 0.5 -
                    vbuf.get(i, j + 1).getPressure1() -
                    vbuf.get(i + 1, j + 1).getPressure1() * 0.5) * 0.25;
        }

        public Float getTCol()
        {
            if (col > 255)
            {
                col = 255;
            }

            if (x > 0 && x < width && y > 0 && y < height)
            {
                return (v.get(x, y + 1).col + v.get(x + 1, y).col + v.get(x + 1, y + 1).col * 0.4f) + col * 0.5f;
            }
            else
            {
                return col;
            }
        }
    }

    private class VBuffer
    {
        private final int x;
        private final int y;
        private final Vector2 velocity;
        private final Vector2 pressure;

        public VBuffer(int x, int y)
        {
            this.x = x;
            this.y = y;
            velocity = new Vector2(0, 0);
            pressure = new Vector2(0, 0);
        }

        public void updateBuffer(VSquare upperLeft,
                                 VSquare left,
                                 VSquare lowerLeft,
                                 VSquare below,
                                 VSquare lowerRight,
                                 VSquare right,
                                 VSquare upperRight,
                                 VSquare above)
        {
            pressure.x = upperLeft.getVelocity().x * 0.5f +
                    left.getVelocity().x +
                    lowerLeft.getVelocity().x * 0.5f -
                    lowerRight.getVelocity().x * 0.5f -
                    right.getVelocity().x -
                    upperRight.getVelocity().x * 0.5f;
            pressure.y = upperLeft.getVelocity().y * 0.5f +
                    above.getVelocity().y +
                    upperRight.getVelocity().y * 0.5f -
                    lowerRight.getVelocity().y * 0.5f -
                    below.getVelocity().y -
                    lowerLeft.getVelocity().y * 0.5f;

        }

        public Vector2 getPressure2()
        {
            return pressure;
        }

        public Vector2 getVelocity()
        {
            return velocity;
        }

        public float getPressure1()
        {
            return (pressure.x + pressure.y)*0.25f;
        }
    }

    private class Particle
    {
        private final Vector2 position;
        private final Vector2 velocity;

        public Particle(float x, float y)
        {
            position = new Vector2(x, y);
            velocity = new Vector2(0, 0);
        }

        public void updatepos(float dt)
        {
            if (position.x > 0 && position.x < width && position.y > 0 && position.y < height)
            {
                int vi = (int) position.x;
                int vu = (int) position.y;

                VSquare o = v.get(vi, vu);

                float ax = Math.abs(position.x - vi);
                float ay = Math.abs(position.y - vu);

                velocity.x += ((1 - ax) * v.get(vi, vu).getVelocity().x +
                        ax * v.get(vi + 1, vu).getVelocity().x +
                        ay * v.get(vi, vu + 1).getVelocity().x) * dt;

                velocity.y += ((1 - ay) * v.get(vi, vu).getVelocity().y +
                        ax * v.get(vi + 1, vu).getVelocity().y +
                        ay * v.get(vi, vu + 1).getVelocity().y) * dt;

                o.setCol(o.getCol() + 4);

                position.add(velocity);
                velocity.scl(0.5f);
            }
        }

        public Vector2 getVelocity()
        {
            return velocity;
        }

        public Vector2 getPosition()
        {
            return position;
        }
    }
}
