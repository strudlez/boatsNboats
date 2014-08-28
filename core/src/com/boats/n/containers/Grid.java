package com.boats.n.containers;

import com.google.common.collect.Table;

/**
 * Created by ben on 8/25/14.
 */
public interface Grid<T> extends Iterable<Table.Cell<Integer, Integer, T>>
{
    Iterable<Integer> getYs();

    Iterable<Integer> getXs();

    T get(int x, int y);

    void put(int x, int y, T value);

    public int getWidth();

    public int getHeight();
}
