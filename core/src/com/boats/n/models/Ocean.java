package com.boats.n.models;

import com.badlogic.gdx.math.Vector2;
import com.boats.n.fluids.FluidSolver;
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

    public Ocean(int width, int height)
    {

        int widthCells = width / CELL_SIZE;
        int heightCells = height / CELL_SIZE;

        fluidSolver = new FluidSolver(widthCells, heightCells, 0.1f);
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

    public void update(float dt)
    {
        this.fluidSolver.step(dt);
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
