package org.ccci.util.contract;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;

/**
 * Useful strategy for unit tests, where sometimes we want to skip some {@link SoftPreconditions soft preconditions},
 * but not all.  To skip a certain {@link Check}, call {@link #skip(CheckSkip)} with an appropriate {@link CheckSkip}.
 * 
 * @author Matt Drees
 *
 */
public class CheckSkippingStrategy implements CheckingStrategy
{

    private static final CheckSkippingStrategy INSTANCE = new CheckSkippingStrategy();

    static
    {
        SoftPreconditions.replaceCheckingStrategy(INSTANCE);
    }
    
    private final Set<CheckSkip> checkSkips = Sets.newSetFromMap(new ConcurrentHashMap<CheckSkip, Boolean>());
; 
    
    /**
     * {@inheritDoc}
     * 
     * Will be performed by calling {@link CheckSkip#shouldSkip(Check)} on each {@link CheckSkip} added
     * via {@link #skip(CheckSkip)}
     */
    @Override
    public boolean shouldCheck(Check check)
    {
        for (CheckSkip checkSkip : checkSkips)
        {
            if (checkSkip.shouldSkip(check))
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Add the given {@link CheckSkip} to the set of {@link CheckSkip}s to evaluate when performing {@link #shouldCheck(Check)}
     * @param checkSkip
     */
    public static void skip(CheckSkip checkSkip)
    {
        INSTANCE.checkSkips.add(checkSkip);
    }
    
    /**
     * removes all {@link CheckSkip}s added by {@link #skip(CheckSkip)}
     */
    public static void clear()
    {
        INSTANCE.checkSkips.clear();
    }

}
