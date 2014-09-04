package com.boats.n.containers;

/**
 * Created by ben on 9/3/14.
 */
public class Triple<A, B, C>
{
    private final A first;
    private final B second;
    private final C third;

    public Triple(A first, B second, C third)
    {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <A, B, C> Triple<A, B, C> of(A first, B second, C third)
    {
        return new Triple<A, B, C>(first, second, third);
    }

    public A getFirst()
    {
        return first;
    }

    public B getSecond()
    {
        return second;
    }

    public C getThird()
    {
        return third;
    }
}
