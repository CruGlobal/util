package org.ccci.util;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


public class CollectionsExtension
{

    public static <T> boolean isSubset(Collection<T> c1, Collection<T> c2)
    {
        for (T t1 : c1)
        {
            if (!c2.contains(t1)) { return false; }
        }
        return true;
    }

    public static <T> boolean containEqualElements(Collection<T> c1, Collection<T> c2)
    {
        return c1.size() == c2.size() && isSubset(c1, c2) && isSubset(c2, c1);
    }
    
    public static <T> List<T> customEqualsList(EqualsOverride<T> equalsOverride)
    {
        return new CustomEqualsList<T>(equalsOverride);
    }
    
    public static <T> boolean containEqualElements(Collection<T> c1, Collection<T> c2, EqualsOverride<T> equalsOverride)
    {
        List<T> l1 = customEqualsList(equalsOverride);
        l1.addAll(c1);
        List<T> l2 = customEqualsList(equalsOverride);
        l2.addAll(c2);
        return containEqualElements(l1, l2);
    }

    public static <T> void assertContainsEqualElements(Collection<T> c1, Collection<T> c2,
                                                   EqualsOverride<T> equalsOverride)
    {
        List<T> l1 = customEqualsList(equalsOverride);
        l1.addAll(c1);
        List<T> l2 = customEqualsList(equalsOverride);
        l2.addAll(c2);
        assertContainsEqualElements(l1, l2);
    }
    
    public static <T> void assertContainsEqualElements(Collection<T> c1, Collection<T> c2)
    {
        assertIsSubset(c1, c2);
        assertIsSubset(c2, c1);
    }
    
    public static <T> void assertIsSubset(Collection<T> c1, Collection<T> c2)
    {
        for (T t1 : c1)
        {
            if (!c2.contains(t1)) 
            {
                throw new AssertionError("collection " + c1 + " is not a subset of collection " + c2
                    + ";  the second collection does not contain " + t1); 
            }
        }
    }
    
    /**
     * Returns a mutable {@link Set} of all the elements in the minuend that do not exist in the subtrahend
     * 
     * @param <T>
     * @param minuend
     * @param subtrahend
     * @return
     */
    public static <T> Set<T> difference(Set<T> minuend, Set<?> subtrahend)
    {
        Set<T> difference = Sets.newHashSet(minuend);
        difference.removeAll(subtrahend);
        return difference;
    }
    
    public static <T> List<T> difference(List<T> minuend, List<T> subtrahend)
    {
        List<T> difference = Lists.newArrayList(minuend);
        difference.removeAll(subtrahend);
        return difference;
    }

    public static void assertContains(Collection<?> collection, Object value)
    {
        if (!collection.contains(value)) {
            throw new AssertionError("collection " + collection + " does not contain " + value); 
        }
    }

    public static boolean containsUniqueElements(Collection<?> collection)
    {
        Set<Object> set = Sets.newHashSet();
        set.addAll(collection);
        return set.size() == collection.size();
    }

    public static <T> List<T> concat(Iterable<T> first, Iterable<T> second)
    {
        List<T> concatenation = Lists.newArrayList(first);
        Iterables.addAll(concatenation, second);
        return concatenation;
    }

}
