package org.ccci.util;

import java.io.Serializable;

import org.ccci.annotations.Immutable;
import org.ccci.util.contract.Check;
import org.ccci.util.contract.CheckSkip;
import org.ccci.util.contract.CheckSkippingStrategy;

import com.google.common.base.Preconditions;

@Immutable
public class SerializationCheck implements Check
{

    private final Class<?> type;
    private final String variableName;

    private SerializationCheck(Class<?> type, String variableName)
    {
        this.type = type;
        this.variableName = variableName;
    }

    @Override
    public boolean evaluate()
    {
        return Serializable.class.isAssignableFrom(type);
    }

    @Override
    public Object[] getArguments()
    {
        return new Object[]{type};
    }

    @Override
    public String getMessage()
    {
        return variableName + " (of type %s) is not Serializable";
    }

    public Class<?> getType()
    {
        return type;
    }

    public String getVariableName()
    {
        return variableName;
    }

    public static SerializationCheck check(Object object, String variableName)
    {
        Preconditions.checkNotNull(object);
        Preconditions.checkNotNull(variableName);
        return new SerializationCheck(object.getClass(), variableName);
    }
    
    public static SerializationCheck check(Class<?> type, String variableName)
    {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(variableName);
        return new SerializationCheck(type, variableName);
    }
    

    @Immutable
    private static class SerializationCheckSkip extends ValueObject implements CheckSkip
    {

        private final Class<?> type;
        private final String variableName;

        private SerializationCheckSkip(Class<?> type, String variableName)
        {
            this.type = type;
            this.variableName = variableName;
        }

        @Override
        public boolean shouldSkip(Check check)
        {
            if (check instanceof SerializationCheck == false) return false;
            SerializationCheck serializationCheck = (SerializationCheck) check;
            if (serializationCheck.type.equals(type) && 
                    serializationCheck.variableName.equals(variableName))
            {
                return true;
            }
            return false;
        }

        @Override
        protected Object[] getComponents()
        {
            return new Object[]{type, variableName};
        }

    }
    
    /**
     * For unit tests.  Indicates that {@link SerializationCheck}s should be ignored for the given type and 
     * variable name
     * @param type
     * @param variableName
     */
    public static void skipChecksFor(Class<?> type, String variableName)
    {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(variableName);
        CheckSkippingStrategy.skip(new SerializationCheckSkip(type, variableName));
    }
}