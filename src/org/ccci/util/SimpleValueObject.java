package org.ccci.util;

import com.google.common.base.Join;
import com.google.common.base.Objects;

/**
 * A base class to make it easier to use value objects.  Implements {@link #equals(Object)}, {@link #hashCode()}, and {@link #toString()}
 * based on the array that {@link #getMembers()} returns.
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
 *      protected Object[] getMembers() {
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
public abstract class SimpleValueObject {

	/**
	 * return an array of the values that make up this value object.
	 * Should never return null.
	 */
	protected abstract Object[] getMembers();
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleValueObject other = (SimpleValueObject) obj;
		Object[] mine = getMembers();
		Object[] his = other.getMembers();
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
		return Objects.hashCode(getMembers());
	}
	

    @Override
    public String toString()
    {
    	Object[] members = getMembers();
    	StringBuilder builder = new StringBuilder();
    	builder.append(getClass().getSimpleName());
    	builder.append("[");
    	Join.join(builder, ", ", members);
    	builder.append("]");
    	return builder.toString();
    }

}
