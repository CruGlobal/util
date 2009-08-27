package org.ccci.debug;

import org.jboss.seam.log.Log;

public class EmptyLog implements Log
{

    @Override
    public void debug(Object object, Object... params)
    {
    }

    @Override
    public void debug(Object object, Throwable t, Object... params)
    {
    }

    @Override
    public void error(Object object, Object... params)
    {
    }

    @Override
    public void error(Object object, Throwable t, Object... params)
    {
    }

    @Override
    public void fatal(Object object, Object... params)
    {
    }

    @Override
    public void fatal(Object object, Throwable t, Object... params)
    {
    }

    @Override
    public void info(Object object, Object... params)
    {
    }

    @Override
    public void info(Object object, Throwable t, Object... params)
    {
    }

    @Override
    public boolean isDebugEnabled()
    {
        return false;
    }

    @Override
    public boolean isErrorEnabled()
    {
        return false;
    }

    @Override
    public boolean isFatalEnabled()
    {
        return false;
    }

    @Override
    public boolean isInfoEnabled()
    {
        return false;
    }

    @Override
    public boolean isTraceEnabled()
    {
        return false;
    }

    @Override
    public boolean isWarnEnabled()
    {
        return false;
    }

    @Override
    public void trace(Object object, Object... params)
    {

    }

    @Override
    public void trace(Object object, Throwable t, Object... params)
    {

    }

    @Override
    public void warn(Object object, Object... params)
    {

    }

    @Override
    public void warn(Object object, Throwable t, Object... params)
    {

    }

}
