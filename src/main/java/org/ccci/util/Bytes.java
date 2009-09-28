package org.ccci.util;

import org.ccci.util.contract.Preconditions;

public class Bytes
{

    /**
     * from http://mindprod.com/jgloss/hex.html#BYTEARRAYTOSTRING
     * 
     * Fast convert a byte array to a hex string with possible leading zero.
     * 
     * @param b
     *            array of bytes to convert to string
     * @return hex representation, two chars per byte.
     */
    public static String toHexString(byte[] b)
    {
        Preconditions.checkNotNull(b, "b is null");
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++)
        {
            // look up high nibble char
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);

            // look up low nibble char
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * table to convert a nibble to a hex char.
     */
    static final char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

}
