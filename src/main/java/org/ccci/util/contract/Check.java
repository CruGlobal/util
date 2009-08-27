package org.ccci.util.contract;

public interface Check
{
    boolean evaluate();
    
    String getMessage();
    
    Object[] getArguments();
}
