package com.boats.n.fluids;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.boats.n.containers.ArrayTableGrid;
import com.boats.n.containers.Grid;
import com.boats.n.containers.Pair;
import com.boats.n.utils.ListUtils;
import com.google.common.base.Function;
import com.google.common.base.Supplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ben on 9/2/14.
 */
public class GlenMurphyFluidSolver implements FluidSolver
{
    private final Grid<VSquare> v;
    private final Grid<VBuffer> vbuf;
    private final List<Particle> particles;

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
    }

    @Override
    public void step(float dt)
    {

    }

    @Override
    public void addVelocity(int x, int y, float dx, float dy)
    {

    }

    @Override
    public void addVelocity(int x, int y, Vector2 delta)
    {

    }

    @Override
    public Grid<Float> getDensity()
    {
        return null;
    }

    @Override
    public Grid<Vector2> getVelocity()
    {
        return null;
    }

    @Override
    public int getWidth()
    {
        return 0;
    }

    @Override
    public int getHeight()
    {
        return 0;
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
    }
}
