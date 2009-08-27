package org.ccci.util.strings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.ccci.util.Annotations;
import org.ccci.util.Classes;
import org.ccci.util.Iterables;
import org.ccci.util.reflect.Accessor;
import org.ccci.util.reflect.FieldAccessor;
import org.ccci.util.reflect.MethodAccessor;

import com.google.common.base.Join;
import com.google.common.collect.Lists;

/**
 * Prints the <tt>@</tt>{@link ToStringProperty} fields of the given object.
 * Format is something like "Person@12b23c4[name: Joe]"
 * @author Matt Drees
 *
 */
public class ToStringBuilder
{

    private final Object object;

    public ToStringBuilder(Object object)
    {
        this.object = object;
    }

    @Override
    public String toString()
    {
        final List<Accessor> attributesToPrint = Lists.newArrayList();
        for (Class<?> clazz : Iterables.reverse(Classes.classHierarchyOf(object.getClass())))
        {
            for (Field field : Annotations.getFieldsAnnotated(clazz, ToStringProperty.class))
            {
                attributesToPrint.add(new FieldAccessor(field));
            }
            for (Method method : Annotations.getMethodsAnnotated(clazz, ToStringProperty.class))
            {
                attributesToPrint.add(new MethodAccessor(method));
            }
        }
        StringBuilder builder =
                new StringBuilder(object.getClass().getSimpleName()).append("@").append(
                    Integer.toHexString(object.hashCode())).append("[");

        Join.join(builder, ", ", new Iterator<String>()
        {

            Iterator<Accessor> iter = attributesToPrint.iterator();

            @Override
            public boolean hasNext()
            {
                return iter.hasNext();
            }

            @Override
            public String next()
            {
                Accessor accessor = iter.next();
                return accessor.getName() + ": " + accessor.get(object);
            }

            @Override
            public void remove()
            {
                iter.remove();
            }

        });
        builder.append("]");
        return builder.toString();
    }
    
    
}
