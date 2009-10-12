package org.ccci.util;

import org.ccci.util.contract.Preconditions;
import org.jboss.seam.util.Base64;

public class Bytes
{

    /**
     * from http://mindprod.com/jgloss/hex.html#BYTEARRAYTOSTRING
     * 
     * Fast convert a byte array to a hex string with possible leading zero.
     * 
     * @param bytes array of bytes to convert to string
     * @return hex representation, two chars per byte.
     */
    public static String toHexString(byte[] bytes)
    {
        Preconditions.checkNotNull(bytes, "bytes is null");
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++)
        {
            // look up high nibble char
            sb.append(hexChar[(bytes[i] & 0xf0) >>> 4]);

            // look up low nibble char
            sb.append(hexChar[bytes[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * table to convert a nibble to a hex char.
     */
    static final char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * Return a Base64 representation of the given byte string.
     * See http://en.wikipedia.org/wiki/Base64 for info on Base64.
     * @param bytes array of bytes to encode using Base64 encoding
     */
    public static String toBase64String(byte[] bytes)
    {
        Preconditions.checkNotNull(bytes, "bytes is null");
        //for now, use Base64 util inside Seam.  May in the future want to not depend on Seam for this.
        return Base64.encodeBytes(bytes);
    }
}
