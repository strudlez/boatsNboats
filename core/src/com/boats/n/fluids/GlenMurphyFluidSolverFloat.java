package com.boats.n.fluids;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.boats.n.containers.*;
import com.boats.n.utils.ListUtils;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by ben on 9/2/14.
 */
public class GlenMurphyFluidSolverFloat implements FloatFluidSolver
{
    private final FloatGrid vX;
    private final FloatGrid vY;

    private final FloatGrid bufPressureX;
    private final FloatGrid bufPressureY;

    private final FloatGrid color;

    private final List<Particle> particles;
    private final int height;
    private final int width;
    private final List<Triple<Integer, Integer, Vector2>> velocityBuffer;

    public GlenMurphyFluidSolverFloat(final int width, final int height, int numParticles)
    {
        vX = new FloatGrid(width + 1, height + 1);
        vY = new FloatGrid(width + 1, height + 1);
        bufPressureX = new FloatGrid(width + 1, height + 1);
        bufPressureY = new FloatGrid(width + 1, height + 1);
        color = new FloatGrid(width + 1, height + 1);

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
                float pressureX = getPressureX(vX.get(i - 1, j - 1),
                                            vX.get(i - 1, j),
                                            vX.get(i - 1, j + 1),
                                            vX.get(i, j + 1),
                                            vX.get(i + 1, j + 1),
                                            vX.get(i + 1, j),
                                            vX.get(i + 1, j - 1),
                                            vX.get(i, j - 1));

                float pressureY = getPressureY(vY.get(i - 1, j - 1),
                                            vY.get(i - 1, j),
                                            vY.get(i - 1, j + 1),
                                            vY.get(i, j + 1),
                                            vY.get(i + 1, j + 1),
                                            vY.get(i + 1, j),
                                            vY.get(i + 1, j - 1),
                                            vY.get(i, j - 1));

                bufPressureX.set(i, j, pressureX);
                bufPressureY.set(i, j, pressureY);
                color.set(i, j, 32);
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
            Vector2 velocity = triple.getThird().cpy().scl(dt);

            vX.add(i, j, velocity.x);
            vY.add(i, j, velocity.y);

        }
        velocityBuffer.clear();

        for (int i = 1; i < width - 1; i++)
        {
            for (int j = 1; j < height - 1; j ++)
            {
                vX.add(i, j, getBufferVelocityX(i, j));
                vY.add(i, j, getBufferVelocityY(i, j));
            }
        }
    }

    private float getPressureX(float upperLeft,
                                 float left,
                                 float lowerLeft,
                                 float below,
                                 float lowerRight,
                                 float right,
                                 float upperRight,
                                 float above) {
        return upperLeft * 0.5f +
                    left +
                    lowerLeft * 0.5f -
                    lowerRight * 0.5f -
                    right -
                    upperRight * 0.5f;
    }

    private float getPressureY(float upperLeft,
                                 float left,
                                 float lowerLeft,
                                 float below,
                                 float lowerRight,
                                 float right,
                                 float upperRight,
                                 float above) {
        return upperLeft * 0.5f +
                    above +
                    upperRight * 0.5f -
                    lowerRight * 0.5f -
                    below -
                    lowerLeft * 0.5f;
    }

    private float getBufferVelocityX(int i, int j) {
        return (getPressure1(i - 1, j - 1) * 0.5f +
                getPressure1(i - 1, j) +
                getPressure1(i - 1, j + 1) * 0.5f -
                getPressure1(i + 1, j - 1) * 0.5f -
                getPressure1(i + 1, j) -
                getPressure1(i + 1, j + 1) * 0.5f) * 0.25f;
    }

    private float getBufferVelocityY(int i, int j) {
        return (getPressure1(i - 1, j - 1) * 0.5f +
                getPressure1(i, j - 1) +
                getPressure1(i + 1, j - 1) * 0.5f -
                getPressure1(i - 1, j + 1) * 0.5f -
                getPressure1(i, j + 1) -
                getPressure1(i + 1, j + 1) * 0.5f) * 0.25f;
    }

    private float getPressure1(int i, int j) {
        return (bufPressureX.get(i, j) + bufPressureY.get(i, j)) * 0.25f;
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
    public FloatGrid getDensity()
    {
        return color;
    }

    @Override
    public FloatGrid getVelocityX()
    {
        return vX;
    }

    @Override
    public FloatGrid getVelocityY()
    {
        return vY;
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

                float ax = Math.abs(position.x - vi);
                float ay = Math.abs(position.y - vu);

                velocity.x += ((1 - ax) * vX.get(vi, vu) +
                        ax * vX.get(vi + 1, vu) +
                        ay * vX.get(vi, vu + 1)) * dt;

                velocity.y += ((1 - ay) * vY.get(vi, vu) +
                        ax * vY.get(vi + 1, vu) +
                        ay * vY.get(vi, vu + 1)) * dt;

                color.add(vi, vu, 4);

                position.add(velocity);
                velocity.scl(0.5f);
            }
        }

        public Vector2 getPosition()
        {
            return position;
        }
    }
}
