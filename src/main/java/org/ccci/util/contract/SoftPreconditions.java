package org.ccci.util.contract;

import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.Preconditions;

/**
 * Similar in purpose to {@link Preconditions}, except may be skipped in unit tests.
 * @see CheckSkippingStrategy
 * @author Matt Drees
 *
 */
public class SoftPreconditions
{
    
	private static final CheckingStrategy DEFAULT_STRATEGY = new AlwaysCheckStrategy();
    private static final AtomicReference<CheckingStrategy> checkingStrategy = new AtomicReference<CheckingStrategy>(DEFAULT_STRATEGY);
    
    public static void checkArgument(Check check)
    {
        if (checkingStrategy.get().shouldCheck(check))
        {
            Preconditions.checkArgument(check.evaluate(), check.getMessage(), check.getArguments());
        }
    }
    
    public static void checkState(Check check)
    {
        if (checkingStrategy.get().shouldCheck(check))
        {
            Preconditions.checkState(check.evaluate(), check.getMessage(), check.getArguments());
        }
    }
    
    /**
     * Can only be replaced once per jvm lifetime.
     * This is for simplicity's sake; trying to support multiple replacements is hard to do safely
     * 
     * @param newCheckingStrategy
     * @throws IllegalStateException if the strategy has already been replaced
     */
    public static void replaceCheckingStrategy(CheckingStrategy newCheckingStrategy)
    {
        Preconditions.checkNotNull(newCheckingStrategy, "newCheckingStrategy is null");
        boolean updated = checkingStrategy.compareAndSet(DEFAULT_STRATEGY, newCheckingStrategy);
        if (!updated)
        {
            throw new IllegalStateException("strategy has already been replaced");
        }
    }
    
}
