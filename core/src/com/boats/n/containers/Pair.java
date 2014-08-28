package com.boats.n.containers;

/**
 * Created by ben on 8/25/14.
 */
public class Pair<T1, T2>
{
    private final T1 first;
    private final T2 second;

    public static <T1, T2> Pair<T1, T2> of(T1 first, T2 second)
    {
        return new Pair<T1, T2>(first, second);
    }

    public Pair(T1 first, T2 second)
    {
        this.first = first;
        this.second = second;
    }

    public T1 getFirst()
    {
        return first;
    }

    public T2 getSecond()
    {
        return second;
    }
}
