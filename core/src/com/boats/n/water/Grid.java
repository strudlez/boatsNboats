package com.boats.n.water;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;

import java.util.Iterator;

/**
 * Created by ben on 8/18/14.
 */
public class Grid<T>
{
    private final int width;
    private final int height;

    private ArrayTable<Integer, Integer, T> grid;

    public Grid(int width, int height, final T defaultValue)
    {
        this(width, height, Suppliers.ofInstance(defaultValue));
    }

    public Grid(int width, int height, Supplier<T> defaultValue)
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
                grid.put(y, x, defaultValue.get());
            }
        }
    }

    public Iterable<Integer> getYs()
    {
        return grid.rowKeyList();
    }

    public Iterable<Integer> getXs()
    {
        return grid.columnKeyList();
    }

    public T get(int x, int y)
    {
        return grid.get(y, x);
    }

    public void put(int x, int y, T value)
    {
        grid.put(y, x, value);
    }
}
