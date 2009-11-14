package org.ccci.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ConstructionTest
{
    static class Foo extends ValueObject
    {

        private final String value;
        
        public Foo(String value)
        {
            if (value == null) throw new NullPointerException();
            if (value.equals("bad")) throw new IllegalArgumentException();
            this.value = value;
        }

        @Override
        public String toString()
        {
            return value;
        }

        @Override
        protected Object[] getComponents()
        {
            return new Object[]{value};
        }
        
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithBadValueThrowsOriginalRuntimeException1()
    {
        Construction.getFactory(Foo.class).valueOf("bad");
    }

    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorWithBadValueThrowsOriginalRuntimeException2()
    {
        Construction.getFactory(Foo.class).valueOf(null);
    }

    @Test
    public void testConstructorWithValidValueConstructsCorrectly()
    {
        Foo created = Construction.getFactory(Foo.class).valueOf("foo");
        Assert.assertEquals(created, new Foo("foo"));
    }
    


    static class Bar extends ValueObject
    {

        private final Integer value;
        
        @ConstructsFromString
        public static Bar valueOf(String string)
        {
            if (string == null) throw new NullPointerException();
            if (string.equals("bad")) throw new IllegalArgumentException();
            return new Bar(Integer.parseInt(string));
        }
        
        public Bar(Integer i)
        {
            this.value = i;
        }

        @Override
        public String toString()
        {
            return value.toString();
        }

        @Override
        protected Object[] getComponents()
        {
            return new Object[]{value};
        }
        
    }


    @Test
    public void testStaticFactoryWithValidValueConstructsCorrectly()
    {
        Bar created = Construction.getFactory(Bar.class).valueOf("42");
        Assert.assertEquals(created, new Bar(42));
    }
 
    

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testStaticFactoryWithBadValueThrowsOriginalRuntimeException1()
    {
        Construction.getFactory(Bar.class).valueOf("bad");
    }

    
    @Test(expectedExceptions = NullPointerException.class)
    public void testStaticFactoryWithBadValueThrowsOriginalRuntimeException2()
    {
        Construction.getFactory(Bar.class).valueOf(null);
    }

}
