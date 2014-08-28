package com.boats.n.functions;

import com.boats.n.functions.Isomorphism;

/**
 * Created by ben on 8/25/14.
 */
public class Isomorphisms
{
    public static <T1, T2> Isomorphism<T1, T2> reverse(final Isomorphism<T2, T1> iso)
    {
        return new Isomorphism<T1, T2>()
        {
            @Override
            public T2 forward(T1 from)
            {
                return iso.backward(from);
            }

            @Override
            public T1 backward(T2 to)
            {
                return iso.forward(to);
            }
        };
    }
}
