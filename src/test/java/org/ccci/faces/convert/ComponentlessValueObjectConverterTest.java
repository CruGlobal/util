package org.ccci.faces.convert;

import javax.faces.convert.ConverterException;

import org.ccci.util.ValueObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ComponentlessValueObjectConverterTest
{

    public static class Foo extends ValueObject
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
    
    static class FooConverter extends ComponentlessValueObjectConverter<Foo>{}
    
    @Test
    public void testGetAsObjectWithValidString()
    {
        Object converted = new FooConverter().getAsObject(null, null, "foo");
        Assert.assertTrue(converted instanceof Foo);
        Assert.assertEquals(converted, new Foo("foo"));
    }
    
    @Test(expectedExceptions = ConverterException.class)
    public void testGetAsObjectThrowsConverterExceptionWithInvalidString()
    {
        new FooConverter().getAsObject(null, null, "bad");
    }
    
    @Test
    public void testGetAsString()
    {
        String converted = new FooConverter().getAsString(null, null, new Foo("foo"));
        Assert.assertEquals(converted, "foo");
    }
}
