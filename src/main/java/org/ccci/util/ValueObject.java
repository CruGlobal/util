package org.ccci.util;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

/**
 * A base class to make it easier to use value objects.  Implements {@link #equals(Object)}, {@link #hashCode()}, and {@link #toString()}
 * based on the array that {@link #getComponents()} returns.
 * 
 * Usage example:
 * <pre>
 * public class Person extends SimpleValueObject
 *   {
 *      private String name;
 *      private String rank;
 *
 *      ...
 *
 *      @Override
 *      protected Object[] getComponents() {
 *         return new Object[]{name, rank};
 *      }
 *
 *   }
 * </pre>
 * 
 * Doesn't use reflection, for whatever that's worth.
 * 
 * @author Matt Drees
 *
 */
public abstract class ValueObject {

	/**
	 * return an array of the values that make up this value object.
	 * Should never return null.
	 */
	protected abstract Object[] getComponents();
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValueObject other = (ValueObject) obj;
		Object[] mine = getComponents();
		Object[] his = other.getComponents();
		assert mine.length == his.length;
		for (int i = 0; i < mine.length; i++)
		{
			if (!Objects.equal(mine[i], his[i]))
				return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getComponents());
	}
	

    @Override
    public String toString()
    {
    	Object[] members = getComponents();
    	StringBuilder builder = new StringBuilder();
    	builder.append(getClass().getSimpleName());
    	builder.append("[");
    	Joiner.on(", ").appendTo(builder, members);
    	builder.append("]");
    	return builder.toString();
    }

}
