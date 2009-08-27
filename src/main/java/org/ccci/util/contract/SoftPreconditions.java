package org.ccci.util.contract;

import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.base.Preconditions;

/**
 * Similar in purpose to {@link Preconditions}, except may be skipped in unit tests.
 * @see CheckSkippingStrategy
 * @author Matt Drees
 *
 */
public class SoftPreconditions
{
    
    private static volatile CheckingStrategy checkingStrategy = new AlwaysCheckStrategy();
    
    public static void checkArgument(Check check)
    {
        if (checkingStrategy.shouldCheck(check))
        {
            Preconditions.checkArgument(check.evaluate(), check.getMessage(), check.getArguments());
        }
    }
    
    public static void checkState(Check check)
    {
        if (checkingStrategy.shouldCheck(check))
        {
            Preconditions.checkState(check.evaluate(), check.getMessage(), check.getArguments());
        }
    }

    /** used to enforced limit of one {@link CheckingStrategy} replacement only  */
    private static final AtomicBoolean replaced = new AtomicBoolean(false);
    
    /**
     * Can only be replaced once per jvm lifetime.
     * This is for simplicity's sake; trying to support multiple replacements is hard to do safely
     * 
     * @param newCheckingStrategy
     */
    public static void replaceCheckingStrategy(CheckingStrategy newCheckingStrategy)
    {
        Preconditions.checkNotNull(newCheckingStrategy, "newCheckingStrategy is null");
        if (!replaced.compareAndSet(false, true))
        {
            throw new IllegalStateException("strategy has already been replaced");
        }
        checkingStrategy = newCheckingStrategy;
    }
    
}
