package org.ccci.util;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public class Iterables
{

    /**
     * not efficient for large Iterables.  Internally creates a new list.
     * @param <T>
     * @param iterable
     * @return
     */
    public static <T> Iterable<T> reverse(Iterable<T> iterable)
    {
        List<T> reversed = Lists.newArrayList(iterable);
        Collections.reverse(reversed);
        return reversed;
    }
    
}
