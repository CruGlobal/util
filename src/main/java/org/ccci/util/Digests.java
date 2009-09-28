package org.ccci.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Digests
{

    /**
     * Returns a hex string representing the SHA-1 digest of the given message
     * 
     * See http://en.wikipedia.org/wiki/SHA_hash_functions for details
     * 
     * @param message
     * @return
     */
    public static String sha1Digest(String message)
    {
        MessageDigest md;
        try
        {
            md = MessageDigest.getInstance("SHA-1");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new AssertionError("SHA-1 algorithm should be part of JRE");
        }
        byte[] messageAsBytes;
        try
        {
            messageAsBytes = message.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new AssertionError("UTF-8 encoding should be part of JRE");
        }
        md.update(messageAsBytes);
        byte[] digest = md.digest();
        return Bytes.toHexString(digest);
    }
}
