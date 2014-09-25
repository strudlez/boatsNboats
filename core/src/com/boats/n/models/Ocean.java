package com.boats.n.models;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.boats.n.containers.FloatGrid;
import com.boats.n.fluids.FloatFluidSolver;
import com.boats.n.fluids.GlenMurphyFluidSolverFloat;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Created by ben on 8/26/14.
 */
public class Ocean
{
    private static final int CELL_SIZE = 10;

    private FloatFluidSolver fluidSolver;
    private final int widthCells;
    private final int heightCells;

    public Ocean(int width, int height)
    {

        widthCells = width / CELL_SIZE;
        heightCells = height / CELL_SIZE;

        // fluidSolver = new JosStamFluidSolver(widthCells, heightCells);
        fluidSolver = new GlenMurphyFluidSolverFloat(widthCells, heightCells, 30000);
    }

    public FloatFluidSolver getFluidSolver() {
        return fluidSolver;
    }

    public int getCellSize() {
        return CELL_SIZE;
    }

    public FloatGrid getDensity()
    {
        return fluidSolver.getDensity();
    }

    public FloatGrid getVelocityX()
    {
        return fluidSolver.getVelocityX();
    }

    public FloatGrid getVelocityY()
    {
        return fluidSolver.getVelocityY();
    }

    public Iterable<Vector2> getParticles()
    {
        return Iterables.transform(fluidSolver.getParticles(), new Function<Vector2, Vector2>()
        {
            @Override
            public Vector2 apply(Vector2 input)
            {
                return input.cpy().scl(CELL_SIZE);
            }
        });
    }

    public Vector2 getVelocityAt(Vector2 pos)
    {
        int x = MathUtils.clamp((int) (pos.x / CELL_SIZE), 0, widthCells);
        int y = MathUtils.clamp((int) (pos.y / CELL_SIZE), 0, heightCells);
        return new Vector2(fluidSolver.getVelocityX().get(x, y),
                fluidSolver.getVelocityY().get(x, y));
    }

    public void update(float dt)
    {
        this.fluidSolver.step(dt);
    }

    public void addVelocity(Vector2 position, Vector2 delta)
    {
        int x = Math.round(position.x / CELL_SIZE);
        int y =  Math.round(position.y / CELL_SIZE);
        for (int i = Math.max(0, x - 2); i < Math.min(x + 2, widthCells); i++)
        {
            for (int j = Math.max(0, y - 2); j < Math.min(y + 2, heightCells); j++)
            {
                fluidSolver.addVelocity(i, heightCells - j, -delta.x, delta.y);
            }
        }
    }

    public class Cell<T>
    {
        private final int x;
        private final int y;
        private final T value;

        private Cell(int x, int y, T value)
        {
            this.x = x;
            this.y = y;
            this.value = value;
        }

        public int getX()
        {
            return x * CELL_SIZE;
        }

        public int getY()
        {
            return y * CELL_SIZE;
        }

        public T getValue()
        {
            return value;
        }

        public int getSize()
        {
            return CELL_SIZE;
        }
    }
}
