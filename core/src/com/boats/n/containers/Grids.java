package com.boats.n.containers;

import com.boats.n.functions.Isomorphism;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableTable;
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

    public static <T1, T2> Pair<? extends Grid<T1>, ? extends Grid<T2>> split(final Grid<Pair<T1, T2>> grid)
    {
        return Pair.of(new Grid<T1>()
                       {
                           @Override
                           public Iterable<Integer> getYs()
                           {
                               return grid.getYs();
                           }

                           @Override
                           public Iterable<Integer> getXs()
                           {
                               return grid.getXs();
                           }

                           @Override
                           public T1 get(int x, int y)
                           {
                               return grid.get(x, y).getFirst();
                           }

                           @Override
                           public void put(int x, int y, T1 value)
                           {
                               grid.put(x, y, Pair.of(value, grid.get(x, y).getSecond()));
                           }

                           @Override
                           public int getWidth()
                           {
                               return grid.getWidth();
                           }

                           @Override
                           public int getHeight()
                           {
                               return grid.getHeight();
                           }

                           @Override
                           public Iterator<Table.Cell<Integer, Integer, T1>> iterator()
                           {
                               return transformCellIterable(grid.iterator(), new Function<Pair<T1, T2>, T1>()
                               {
                                   @Override
                                   public T1 apply(Pair<T1, T2> input)
                                   {
                                       return input.getFirst();
                                   }
                               });
                           }
                       }, new Grid<T2>()
                       {
                           @Override
                           public Iterable<Integer> getYs()
                           {
                               return grid.getYs();
                           }

                           @Override
                           public Iterable<Integer> getXs()
                           {
                               return grid.getXs();
                           }

                           @Override
                           public T2 get(int x, int y)
                           {
                               return grid.get(x, y).getSecond();
                           }

                           @Override
                           public void put(int x, int y, T2 value)
                           {
                               grid.put(x, y, Pair.of(grid.get(x, y).getFirst(), value));
                           }

                           @Override
                           public int getWidth()
                           {
                               return grid.getWidth();
                           }

                           @Override
                           public int getHeight()
                           {
                               return grid.getHeight();
                           }

                           @Override
                           public Iterator<Table.Cell<Integer, Integer, T2>> iterator()
                           {
                               return transformCellIterable(grid.iterator(), new Function<Pair<T1, T2>, T2>()
                               {
                                   @Override
                                   public T2 apply(Pair<T1, T2> input)
                                   {
                                       return input.getSecond();
                                   }
                               });
                           }
                       }
        );
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
