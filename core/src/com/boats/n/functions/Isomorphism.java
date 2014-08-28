package com.boats.n.functions;

import com.google.common.base.Function;

/**
 * Created by ben on 8/25/14.
 */
public abstract class Isomorphism<T1, T2>
{
    public abstract T2 forward(T1 from);

    public abstract T1 backward(T2 to);

    public Function<? super T1,? extends T2> forwardFunction()
    {
        return new Function<T1, T2>()
        {
            @Override
            public T2 apply(T1 input)
            {
                return forward(input);
            }
        };
    }
}
