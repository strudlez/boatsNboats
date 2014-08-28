package com.boats.n.containers;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.*;

import java.util.Iterator;

/**
 * Created by ben on 8/18/14.
 */
public class ArrayTableGrid<T> implements Grid<T>
{
    private final int width;
    private final int height;

    private ArrayTable<Integer, Integer, T> grid;

    public ArrayTableGrid(int width, int height, final T defaultValue)
    {
        this(width, height, Suppliers.ofInstance(defaultValue));
    }

    public ArrayTableGrid(int width, int height, Supplier<T> defaultValue)
    {
        this(width, height, Functions.forSupplier(defaultValue));
    }

    public ArrayTableGrid(int width, int height, Function<? super Pair<Integer, Integer>, T> valueGenerator)
    {
        this.width = width;
        this.height = height;

        ContiguousSet<Integer> widthRange = ContiguousSet.create(Range.closed(0, width), DiscreteDomain.integers());
        ContiguousSet<Integer> heightRange = ContiguousSet.create(Range.closed(0, height), DiscreteDomain.integers());

        grid = ArrayTable.create(heightRange, widthRange);

        for (int x : widthRange)
        {
            for (int y : heightRange)
            {
                grid.put(y, x, valueGenerator.apply(Pair.of(x, y)));
            }
        }
    }

    @Override
    public Iterable<Integer> getYs()
    {
        return grid.rowKeyList();
    }

    @Override
    public Iterable<Integer> getXs()
    {
        return grid.columnKeyList();
    }

    @Override
    public T get(int x, int y)
    {
        return grid.get(y, x);
    }

    @Override
    public void put(int x, int y, T value)
    {
        grid.put(y, x, value);
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
    public Iterator<Table.Cell<Integer, Integer, T>> iterator()
    {
        return grid.cellSet().iterator();
    }
}
