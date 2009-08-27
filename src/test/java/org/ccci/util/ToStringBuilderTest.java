package org.ccci.util;

import org.ccci.util.strings.ToStringProperty;
import org.ccci.util.strings.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ToStringBuilderTest
{

    public static class Foo
    {
        @ToStringProperty
        final String name;

        @ToStringProperty
        final int age;

        final String position;
        
        @ToStringProperty
        public String getPosition()
        {
            return position;
        }
        
        public Foo(String name, int age, String position)
        {
            this.name = name;
            this.age = age;
            this.position = position;
        }
    }
    
    public static class Bar extends Foo
    {
        
        @ToStringProperty
        final String color;
        
        public Bar(String name, int age, String position, String color)
        {
            super(name, age, position);
            this.color = color;
        }
    }

    @Test
    public void testSimpleToString()
    {
        Foo foo = new Foo("Nathan", 45, "the boss");
        Assert.assertEquals(Foo.class.getSimpleName() + "@" + Integer.toHexString(foo.hashCode()) + "[name: Nathan, age: 45, position: the boss]",
            new ToStringBuilder(foo).toString());
    }

    @Test
    public void testHierarchyToString()
    {
        Bar foo = new Bar("Matt", 26, "the grunt", "blue");
        Assert.assertEquals(Bar.class.getSimpleName() + "@" + Integer.toHexString(foo.hashCode()) + "[name: Matt, age: 26, position: the grunt, color: blue]",
            new ToStringBuilder(foo).toString());
    }

}
