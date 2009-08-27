package org.ccci.util;

import junit.framework.Assert;

import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ClassesTest
{
    
    static class Foo
    {
        
    }
    
    static class Bar extends Foo
    {
        
    }

    @Test
    public void testClassHierarchyOf()
    {
        Iterable<Class<?>> expected = Lists.<Class<?>>newArrayList(Bar.class, Foo.class);
        Iterable<Class<?>> actual = Classes.classHierarchyOf(Bar.class);
        System.out.println(Iterables.toString(actual));
        Assert.assertTrue(Iterables.elementsEqual(expected, actual));
    }
}
