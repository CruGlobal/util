package org.ccci.util;

import java.util.List;

import com.google.common.collect.Lists;

public class Generics
{

    /**
     * The idea is that you can verify that an unchecked List of objects that should contain {@code T}s actually
     * does contain {@code T}'s. This way, an exception is thrown now where the error occurs instead of later,
     * when an instance with a bad type is pulled out of the list.
     * 
     * This will not work for nested Lists; only one level is supported.
     * 
     * @param <T>
     * @param targetType the desired class or interface
     * @param list
     * @return the same list passed in
     */
    public static <T> List<T> checkList(Class<T> targetType, List<?> list)
    {
        List<Object> badObjects = Lists.newLinkedList();
        for (Object object : list)
        {
            if (!targetType.isInstance(object))
            {
                badObjects.add(object);
            }
        }
        if (!badObjects.isEmpty())
        {
            throw Exceptions.newIllegalStateException("List contains objects that are not instances of %s: %s", targetType, badObjects);
        }
        //we've just verified that all instances in the list are of type T
        @SuppressWarnings("unchecked")
        List<T> checked = (List<T>) list;
        return checked;
    }

    /**
     * Expects a list of object arrays of equal length, and verifies that each row contains objects
     * of the expected type.  
     * @param list
     * @param expectedRowTypePattern
     * @return the input list, except more strongly typed
     */
    public static List<Object[]> checkObjectArrayList(List<?> list, Class<?>... expectedRowTypePattern)
    {

        for (Object object : list)
        {
            if (object instanceof Object[] == false)
            {
                throw Exceptions.newIllegalStateException("List contains a row that is not an instance of Object[]: %s", object);
            }
            Object[] row = (Object[]) object;
            if (row.length != expectedRowTypePattern.length)
            {
                throw Exceptions.newIllegalStateException("List contains a row that does not contain %s columns: %s", expectedRowTypePattern.length, row.length);
            }
            for (int i = 0; i < row.length; i++)
            {
                if (!expectedRowTypePattern[i].isInstance(row[i]))
                {
                    throw Exceptions.newIllegalStateException("List contains a row with a field at index %s that is not an instance of %s: %s", i, expectedRowTypePattern[i], row[i]);
                }
            }
        }
        @SuppressWarnings("unchecked")
        List<Object[]> checked = (List<Object[]>) list;
        return checked;
    }

}
