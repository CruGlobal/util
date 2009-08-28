package org.ccci;

import junit.framework.Assert;

import org.ccci.util.ValueObject;
import org.testng.annotations.Test;

public class SimpleValueObjectTest {

	public static class Person extends ValueObject
	{
		private String name;
		private String rank;
		
		public Person(String name, String rank) {
			this.name = name;
			this.rank = rank;
		}

		@Override
		protected Object[] getComponents() {
			return new Object[]{name, rank};
		}
		
	}
	
	@Test
	public void test()
	{
		Person joe = new Person("joe", "emperor");
		Person bill = new Person("bill", "king");
		Person joe2 = new Person("joe", "duke");
		Person joeClone = new Person("joe", "emperor");
		
		Assert.assertFalse(joe.equals(bill));
		Assert.assertFalse(joe.equals(joe2));
		Assert.assertTrue(joe.equals(joeClone));
		
		Assert.assertEquals(joe.hashCode(), joeClone.hashCode());
		Assert.assertEquals(joe.toString(), "Person[joe, emperor]");
		
	}
}
