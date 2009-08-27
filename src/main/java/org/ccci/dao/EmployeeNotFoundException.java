package org.ccci.dao;

public class EmployeeNotFoundException extends RuntimeException
{

    public EmployeeNotFoundException()
    {
        super();
    }

    public EmployeeNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EmployeeNotFoundException(String message)
    {
        super(message);
    }

    public EmployeeNotFoundException(Throwable cause)
    {
        super(cause);
    }

    private static final long serialVersionUID = 1L;
}
