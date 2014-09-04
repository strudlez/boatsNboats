package com.boats.n.utils;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

import java.util.ArrayList;

/**
 * Created by ben on 9/2/14.
 */
public class ListUtils
{
    public static <T> ArrayList<T> nInstances(int n, Supplier<T> supplier)
    {
        ArrayList<T> toReturn = Lists.newArrayListWithCapacity(n);
        for (int i = 0; i < n; i++)
        {
            toReturn.add(supplier.get());
        }
        return toReturn;
    }
}
