package org.ccci.testutils.persistence;

import java.lang.reflect.Field;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.base.Preconditions;

public class PersistenceContextReference
{

    private final Field field;
    private final Object testInstance;

    public PersistenceContextReference(Field field, Object testInstance)
    {
        Preconditions.checkNotNull(testInstance);
        Class<?> declaringClass = field.getDeclaringClass();
        Preconditions.checkArgument(
            declaringClass.isAnnotationPresent(PersistenceUnitBuiltBy.class), 
            "%s is not annotated @%s", 
            declaringClass, 
            PersistenceUnitBuiltBy.class.getSimpleName());
        this.field = field;
        this.testInstance = testInstance;
        field.setAccessible(true);
    }

    public void inject(EntityManager entityManager)
    {
        try
        {
            field.set(testInstance, entityManager);
        }
        catch (IllegalAccessException e)
        {
            throw assertNoIllegalAccessException();
        }
    }

    /**
     * The name will come directly from the {@link PersistenceContext#unitName() unitName}
     * attribute of the PersistenceContext annotation on the {@link EntityManager} field
     * that this reference is associated with.  The annotation may not
     * specify a name, in which case the empty string will be passed as the unitName.
     */
    public String getUnitName()
    {
        return field.getAnnotation(PersistenceContext.class).unitName();
    }

    public void clear()
    {
        try
        {
            field.set(testInstance, null);
        }
        catch (IllegalAccessException e)
        {
            throw assertNoIllegalAccessException();
        }
    }

    private AssertionError assertNoIllegalAccessException()
    {
        return new AssertionError("field should have been set accessible");
    }

    public EntityManager getEntityManager()
    {
        try
        {
            return (EntityManager) field.get(testInstance);
        }
        catch (IllegalAccessException e)
        {
            throw assertNoIllegalAccessException();
        }
    }

    public Field getField()
    {
        return field;
    }

    public Class<? extends PersistenceUnitFactory> getPersistenceUnitFactoryClass()
    {
        PersistenceUnitBuiltBy annotation = field.getDeclaringClass().getAnnotation(PersistenceUnitBuiltBy.class);
        return annotation.value();
    }

    public boolean automaticTransactionControl()
    {
        return !field.isAnnotationPresent(ManualTransactionControl.class);
    }

    public Object getTestInstance()
    {
        return testInstance;
    }

}
