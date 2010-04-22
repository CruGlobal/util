package org.ccci.util;

import org.ccci.util.contract.Preconditions;

public class Math
{

    /**
     * Java doesn't have a built in arithmetic modulo method, sadly (http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6282196).
     * See also http://en.wikipedia.org/wiki/Modular_arithmetic and http://en.wikipedia.org/wiki/Modulo_operator.
     * The modulo operator (%) is close, but it doesn't handle negative inputs the same way as modular arithmetic.
     * @param number
     * @param modulus must be positive
     * @return the arithmetic modulo, always in the range 0..{@code modulus}.
     */
    public static int mod(int number, int modulus)
    {
        return (number % modulus + modulus) % modulus;
    }
    
    /**
     * Like {@link #mod(int, int)}, but with longs
     * @param number
     * @param modulus
     * @return
     */
    public static long mod(long number, long modulus)
    {
        return (number % modulus + modulus) % modulus;
    }
    

    /**
     * returns {@code dividend} divided by {@code divisor}, except when {@code dividend} is 0; in this case,
     * returns 0.
     * @param dividend
     * @param divisor
     * @return
     */
    public static double safeDivide(double dividend, double divisor)
    {
        if (divisor == 0d)
            return 0d;
        else
        {
            return dividend / divisor;
        }
    }


    /**
     * Similar to {@link java.lang.Math#pow(double, double)}, except with longs.  Does not check for overflow.
     * @param base
     * @param exponent
     * @return
     */
    public static long pow(long base, long exponent)
    {
        Preconditions.checkArgument(base >= 0, "base is negative");
        Preconditions.checkArgument(exponent >= 0, "exponent is negative");
        Preconditions.checkArgument(exponent != 0 || base != 0, "zero raised to the zero power is undefined");
        long pow = 1;
        for (int i = 0; i < exponent; i++)
        {
            pow *= base;
        }
        return pow;
    }

}
