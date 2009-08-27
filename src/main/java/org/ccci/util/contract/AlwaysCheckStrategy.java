package org.ccci.util.contract;

public class AlwaysCheckStrategy implements CheckingStrategy
{

    @Override
    public boolean shouldCheck(Check check)
    {
        return true;
    }

}
