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
     * Returns a hex string representing the SHA-1 digest of the given message (decoded using UTF-8)
     * 
     * See http://en.wikipedia.org/wiki/SHA_hash_functions for details
     */
    public static String sha1Digest(String message)
    {
        return digest(message, "SHA-1");
    }
    
    /**
     * Returns a hex string representing the MD5 digest of the given message (decoded using UTF-8)
     * 
     * See http://en.wikipedia.org/wiki/MD5 for details
     */
    public static String md5Digest(String message)
    {
        return digest(message, "MD5");
    }

    private static String digest(String message, String builtinAlgorithm) throws AssertionError
    {
        MessageDigest md;
        try
        {
            md = MessageDigest.getInstance(builtinAlgorithm);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException(builtinAlgorithm + " is unavailable.  It is built into Sun's cryptograpy provider; is a different provider being used here?");
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
        return Bytes.toHexString(md.digest());
    }
}
