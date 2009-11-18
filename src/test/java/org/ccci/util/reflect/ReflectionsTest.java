package org.ccci.util.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ReflectionsTest
{

    public static class Victim
    {
        private String foo = "foo";
        
        public Object payload;
        
        private boolean storePayload(Object payload)
        {
            this.payload = payload;
            return true;
        }
        
        @SuppressWarnings("unused")
        private boolean storeStringPayload(String payload)
        {
            return storePayload(payload);
        }
        
    }
    
    public static class SubVictim extends Victim
    {
        
    }
    
    @Test
    public void testSetField()
    {
        Victim victim = new Victim();
        Reflections.setField(victim, Victim.class, "foo", "bar");
        Assert.assertEquals(victim.foo, "bar");
    }

    @Test
    public void testSetFieldFromSuperClass()
    {
        Victim victim = new SubVictim();
        Reflections.setField(victim, Victim.class, "foo", "bar");
        Assert.assertEquals(victim.foo, "bar");
    }

    @Test
    public void testGetFieldFromSuperClass()
    {
        performGetFieldTest(new SubVictim());
    }


    private void performGetFieldTest(Victim victim)
    {
        victim.foo = "bar";
        String value = Reflections.getField(victim, Victim.class, "foo");
        Assert.assertEquals(value, "bar");
    }
    
    @Test
    public void testGetField()
    {
        performGetFieldTest(new Victim());
    }
    
    
    @Test
    public void testInvokeSpecificMethod()
    {
        performInvokeSpecificMethodTest(new Victim());
    }
    
    @Test
    public void testInvokeSpecificMethodFromSuperClass()
    {
        performInvokeSpecificMethodTest(new SubVictim());
    }

    private void performInvokeSpecificMethodTest(Victim victim)
    {
        boolean stored = Reflections.<Boolean>invokeSpecificMethod(victim, Victim.class, "storePayload", Object.class)
            .withArguments("foo");
        Assert.assertTrue(stored);
        Assert.assertEquals(victim.payload, "foo");
    }

    @Test
    public void testInvokeMethod()
    {
        Victim victim = new Victim();
        boolean stored = Reflections.invokeMethod(victim, Victim.class, "storeStringPayload", "foo");
        Assert.assertTrue(stored);
        Assert.assertEquals(victim.payload, "foo");
    }
}
