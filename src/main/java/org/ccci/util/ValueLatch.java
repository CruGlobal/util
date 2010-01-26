package org.ccci.util;

import java.util.concurrent.atomic.AtomicReference;

import org.ccci.annotations.ThreadSafe;
import org.ccci.util.contract.Preconditions;

/**
 * An instance of this class acts like a variable of type <tt>T</tt>.
 * The initial state is empty, and it may be initialized to a non-null value once, but only once.
 * 
 * <T> the type of the contained value
 * 
 * @author Matt Drees
 */
@ThreadSafe
public class ValueLatch<T>
{
    private final AtomicReference<T> value = new AtomicReference<T>();

    public boolean isInitialized()
    {
        return value.get() != null;
    }
    
    /**
     * Initializes the variable to the given value.
     * @param value
     * @throws NullPointerException if value is null
     * @throws IllegalStateException if a value has already been set
     */
    public void set(T value)
    {
        Preconditions.checkNotNull(value, "value is null");
        boolean successful = this.value.compareAndSet(null, value);
        if (!successful) throw Exceptions.newIllegalStateException("value was already set to %s", this.value.get());
    }
    
    
    /**
     * Returns null if the value has not been initialized.
     * @return
     */
    public T get()
    {
        return this.value.get();
    }
    
}
