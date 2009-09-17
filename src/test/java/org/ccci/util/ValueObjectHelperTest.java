package org.ccci.util;

import org.ccci.util.ValueObjectHelper.ValueProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ValueObjectHelperTest
{

    public static class Name
    {
        public String name;

        public Name(String name)
        {
            this.name = name;
        }
        
        private final ValueObjectHelper<Name> objectHelper = new ValueObjectHelper<Name>(new ValueProvider<Name>()
        {
            @Override
            public Object getValue(Name name)
            {
                return name.name;
            }
        }, this);
        
        @Override
        public int hashCode()
        {
            return objectHelper.makeHashCode();
        }
        
        @Override
        public boolean equals(Object object)
        {
            return objectHelper.checkEquals(object);
        }
        
        @Override
        public String toString() {
        	return objectHelper.toString();
        }
        
    }
    
    @Test
    public void testMakeHashCode()
    {
        String matt = "Matt";
        Name myName = new Name(matt);
        Assert.assertEquals(matt.hashCode(), myName.hashCode());
    }
    
    @Test
    public void testMakeHashCodeWithNullValueDoesntBomb()
    {
        Name myName = new Name(null);
        myName.hashCode();
    }
    

    @Test
    public void testCheckEqualsWithSameValue()
    {
        String matt = "Matt";
        Name myName = new Name(matt);
        Name myName2 = new Name(new String(matt));
        Assert.assertTrue(myName.equals(myName2));
    }

    @Test
    public void testCheckEqualsWithSameValueWhenNull()
    {
        Name nullName = new Name(null);
        Name nullName2 = new Name(null);
        Assert.assertTrue(nullName.equals(nullName2));
    }
    
    @Test
    public void testCheckEqualsWithDifferentValue()
    {
        Name myName = new Name("Matt");
        Name myName2 = new Name("Lee");
        Assert.assertFalse(myName.equals(myName2));
    }
    
    @Test
    public void testCheckEqualsWithDifferentValueWhenNull()
    {
        Name myName = new Name(null);
        Assert.assertFalse(myName.equals(new Object()));
    }
    

}
