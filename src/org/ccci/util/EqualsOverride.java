package org.ccci.util;

//TODO: is there a reason I didn't just use Comparator?  
public interface EqualsOverride<T>
{
    boolean isEqual(T t1, T t2);
}