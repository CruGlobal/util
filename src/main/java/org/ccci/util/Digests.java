package org.ccci.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A utility class to make hash/digest algorithms easy to use.
 * 
 * @author Matt Drees
 *
 */
public class Digests
{
    
    private Digests() {} //uninstantiable

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
            throw new IllegalStateException("SHA-1 is unavailable.  It is built into Sun's cryptograpy provider; is a different provider being used here?");
        }
        byte[] messageAsBytes;
        try
        {
            messageAsBytes = message.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new AssertionError("UTF-8 encoding should be part of a compliant JRE");
        }
        md.update(messageAsBytes);
        byte[] digest = md.digest();
        return Bytes.toHexString(digest);
    }
}
