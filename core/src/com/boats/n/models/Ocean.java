package com.boats.n.models;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.boats.n.fluids.FluidSolver;
import com.boats.n.fluids.JosStamFluidSolver;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Table;

/**
 * Created by ben on 8/26/14.
 */
public class Ocean
{
    private static final int CELL_SIZE = 15;

    private FluidSolver fluidSolver;
    private final int widthCells;
    private final int heightCells;

    public Ocean(int width, int height)
    {

        widthCells = width / CELL_SIZE;
        heightCells = height / CELL_SIZE;

        fluidSolver = new JosStamFluidSolver(widthCells, heightCells);
    }

    public FluidSolver getFluidSolver() {
        return fluidSolver;
    }

    public int getCellSize() {
        return CELL_SIZE;
    }

    public Iterable<Cell<Float>> getDensityCells()
    {
        return Iterables.transform(fluidSolver.getDensity(), new Function<Table.Cell<Integer, Integer, Float>, Cell<Float>>()
        {
            @Override
            public Cell<Float> apply(Table.Cell<Integer, Integer, Float> tableCell)
            {
                return new Cell<Float>(tableCell.getColumnKey(), tableCell.getRowKey(), tableCell.getValue());
            }
        });
    }

    public Iterable<Cell<Vector2>> getVelocityCells()
    {
        return Iterables.transform(fluidSolver.getVelocity(), new Function<Table.Cell<Integer, Integer, Vector2>, Cell<Vector2>>()
        {
            @Override
            public Cell<Vector2> apply(Table.Cell<Integer, Integer, Vector2> tableCell)
            {
                return new Cell<Vector2>(tableCell.getColumnKey(), tableCell.getRowKey(), tableCell.getValue());
            }
        });
    }

    public Vector2 getVelocityAt(Vector2 pos)
    {
        int x = MathUtils.clamp((int) (pos.x / CELL_SIZE), 0, widthCells);
        int y = MathUtils.clamp((int) (pos.y / CELL_SIZE), 0, heightCells);
        return fluidSolver.getVelocity().get(x, y).cpy();
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
