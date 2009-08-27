package org.ccci.faces.convert.helper;

import javax.el.ELContext;
import javax.el.ValueExpression;

public class EmptyValueExpression extends ValueExpression
{

    private static final long serialVersionUID = 1L;

    @Override
    public Class<?> getExpectedType()
    {
        return null;
    }

    @Override
    public Class<?> getType(ELContext arg0)
    {
        return null;
    }

    @Override
    public Object getValue(ELContext arg0)
    {
        return null;
    }

    @Override
    public boolean isReadOnly(ELContext arg0)
    {
        return false;
    }

    @Override
    public void setValue(ELContext arg0, Object arg1)
    {

    }

    @Override
    public boolean equals(Object arg0)
    {
        return false;
    }

    @Override
    public String getExpressionString()
    {
        return null;
    }

    @Override
    public int hashCode()
    {
        return 0;
    }

    @Override
    public boolean isLiteralText()
    {
        return false;
    }

}
