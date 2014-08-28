package com.boats.n.containers;

import com.boats.n.functions.Isomorphism;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Table;

import java.util.Iterator;

/**
 * Created by ben on 8/25/14.
 */
public class Grids
{
    public static <F, T> Grid<T> transform(final Grid<F> from, final Isomorphism<F, T> iso)
    {
        return new Grid<T>()
        {
            @Override
            public Iterable<Integer> getYs()
            {
                return from.getYs();
            }

            @Override
            public Iterable<Integer> getXs()
            {
                return from.getXs();
            }

            @Override
            public T get(int x, int y)
            {
                return iso.forward(from.get(x, y));
            }

            @Override
            public void put(int x, int y, T value)
            {
                from.put(x, y, iso.backward(value));
            }

            @Override
            public int getWidth()
            {
                return from.getWidth();
            }

            @Override
            public int getHeight()
            {
                return from.getHeight();
            }

            @Override
            public Iterator<Table.Cell<Integer, Integer, T>> iterator()
            {
                return transformCellIterable(from.iterator(), iso.forwardFunction());
            }
        };
    }

    private static <F, T> Iterator<Table.Cell<Integer, Integer, T>> transformCellIterable(Iterator<Table.Cell<Integer, Integer, F>> iterator, final Function<? super F, ? extends T> function)
    {
        return Iterators.transform(iterator, new Function<Table.Cell<Integer, Integer, F>, Table.Cell<Integer, Integer, T>>()
        {
            @Override
            public Table.Cell<Integer, Integer, T> apply(final Table.Cell<Integer, Integer, F> input)
            {
                return new Table.Cell<Integer, Integer, T>()
                {
                    @Override
                    public Integer getRowKey()
                    {
                        return input.getRowKey();
                    }

                    @Override
                    public Integer getColumnKey()
                    {
                        return input.getColumnKey();
                    }

                    @Override
                    public T getValue()
                    {
                        return function.apply(input.getValue());
                    }
                };
            }
        });
    }
}
