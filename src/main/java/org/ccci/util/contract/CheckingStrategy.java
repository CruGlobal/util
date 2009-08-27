package org.ccci.util.contract;

public interface CheckingStrategy
{

    /**
     * Returns true if the given {@link Check} should be evaluated.  If not, then the precondition using
     * the {@code check} will be skipped
     * @param check
     * @return
     */
    boolean shouldCheck(Check check);

}
