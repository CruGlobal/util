package org.ccci.util;

import com.google.common.base.Preconditions;

/**
 * A helper class to make it easier to write value objects. It implements
 * {@link Object#equals(Object)} and {@link Object#hashCode()}. It requires that the client class be represented by a
 * single object, typically a String, that correctly implements {@link #equals(Object)} and
 * {@link #hashCode()}.  The client class must pass in an appropriate {@link ValueProvider} to this class's constructor, as well
 * as the client object itself.
 * 
 * @author Matt Drees
 * 
 */
public class ValueObjectHelper<T>
{
    public interface ValueProvider<U>
    {
        Object getValue(U instance);
    }
    
    private final ValueProvider<T> valueProvider;
    private final T instance;

    public ValueObjectHelper(ValueProvider<T> valueProvider, T instance)
    {
        this.instance = Preconditions.checkNotNull(instance, "instance is null");
        this.valueProvider = Preconditions.checkNotNull(valueProvider, "valueProvider is null");
    }

    @Override
    public String toString()
    {
        return String.valueOf(valueProvider.getValue(instance));
    }

    public int makeHashCode()
    {
        return (valueProvider.getValue(instance) == null) ? 0 : valueProvider.getValue(instance).hashCode();
    }

    public boolean checkEquals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (instance.getClass() != obj.getClass()) return false;
        @SuppressWarnings("unchecked") //the other's class is the same as instance's class, and instance is a T
        T other = (T) obj;
        Object thisValue = valueProvider.getValue(instance);
        Object otherValue = valueProvider.getValue(other);
        if (thisValue == null)
        {
            if (otherValue != null) return false;
        }
        else if (!thisValue.equals(otherValue)) return false;
        return true;
    }
}
