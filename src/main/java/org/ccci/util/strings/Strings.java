package org.ccci.util.strings;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.ccci.util.Assertions;
import org.ccci.util.contract.Preconditions;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class Strings
{
    /**
     * Returns true if the given string is null or (after trimming)
     * is the empty string.
     * @param string
     * @return
     */
    public static boolean isEmpty(String string)
    {
        if (string == null || string.isEmpty()) return true;
        return string.trim().isEmpty();
    }

    /**
     * Turns, for example, "FIRST_NAME" into "First Name"
     * 
     * @param name
     *            can be null
     * @return null if <tt>name</tt> is null
     */
    public static String capitalsAndUnderscoresToLabel(String name)
    {
        if (name == null) { return null; }
        String[] words = name.split("_");
        List<String> converted = Lists.newArrayList();
        for (String word : words)
        {
            if (!word.isEmpty())
            {
                converted.add(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
            }
        }
        return Joiner.on(" ").join(converted);
    }
    
    /**
     * If the string is too long, truncate it and append <tt>tail</tt>;
     * otherwise return the string
     * @param string
     * @param length
     * @param tail
     * @return
     */
    public static String truncate(String string, int length, String tail)
    {
        String truncated;
        if (string.length() > length) {
            truncated = string.substring(0, length) + tail;
        } else {
            truncated = string;
        }
        return truncated;
    }


    /**
     * Returns the concatenation of {@code n} spaces 
     * @param n how many spaces to concatenate
     */
    public static String nSpaces(int n)
    {
        return repeat(" ", n);
    }

    /**
     * Returns the last {@code n} characters of the given {@code string}
     * @param string
     * @param n
     * @return
     */
    public static String tail(String string, int n)
    {
        Preconditions.checkNotNull(string, "string is null");
        Preconditions.checkArgument(n >= 0, "n is negative: %s", n);
        Preconditions.checkArgument(n <= string.length(), "n is too big (%s); should be less than or equal to %s", n, string.length());
        
        return string.substring(string.length() - n, string.length());
    }

    /**
     * Adds sufficiently many copies of {@code padding} to the front of {@code string} such that the resulting {@link String}'s
     * length will equal {@code requiredLength}, and returns this resulting String.
     * 
     * @param string
     * @param requiredLength
     * @param padding
     * @return the padded string
     * @throws NullPointerException if {@code string} is null
     * @throws IllegalArgumentException if {@code requiredLength} is less than {@code string}'s length
     */
    public static String leftPad(String string, int requiredLength, char padding) throws IllegalArgumentException
    {
        checkPadArguments(string, requiredLength);
        
        if (string.length() == requiredLength) return string;
        StringBuilder builder = new StringBuilder(requiredLength);
        for (int i = 0; i < requiredLength - string.length(); i++)
        {
            builder.append(padding);
        }
        builder.append(string);
        return builder.toString();
    }

    /**
     * Adds sufficiently many copies of {@code padding} to the back of {@code string} such that the resulting {@link String}'s
     * length will equal {@code requiredLength}, and returns this resulting String.
     * 
     * @param string
     * @param requiredLength
     * @param padding
     * @return the padded string
     * @throws NullPointerException if {@code string} is null
     * @throws IllegalArgumentException if {@code requiredLength} is less than {@code string}'s length
     */
    public static String rightPad(String string, int requiredLength, char padding) throws IllegalArgumentException
    {
        checkPadArguments(string, requiredLength);
        
        if (string.length() == requiredLength) return string;
        StringBuilder builder = new StringBuilder(requiredLength);
        builder.append(string);
        for (int i = 0; i < requiredLength - string.length(); i++)
        {
            builder.append(padding);
        }
        return builder.toString();
    }

    static void checkPadArguments(String string, int requiredLength)
    {
        Preconditions.checkNotNull(string, "string is null");
        Preconditions.checkArgument(requiredLength >= 0, "requiredLength is negative: %s", requiredLength);
        Preconditions.checkArgument(requiredLength >= string.length(),
            "requiredLength is too small (%s); should be greater than or equal to %s", requiredLength, string.length());
    }

    /**
     * Returns a String that is composed of n copies of the given string
     * @param string the string to be repeated
     * @param n number of times to repeat
     */
    public static String repeat(String string, int n)
    {
        Preconditions.checkNotNull(string, "string is null");
        Preconditions.checkArgument(n >= 0, "n is negative: %s", n);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n; i++)
        {
            builder.append(string);
        }
        return builder.toString();
    }
    

    /** 
     * Combines the given iterable together into a String, separating them with {@code separator}, except the last two, 
     * which are separated by {@code finalSeparator}.
     * 
     * That is, this is very similar to {@link Joiner#on(char)}.{@link Joiner#join(Iterable) join(Iterable)},
     * except for the addition of {@code finalSeparator}.
     * 
     */
    public static String join(Iterable<?> iterable, String separator, String finalSeparator)
    {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iterator = iterable.iterator();
        Object first = iterator.next();
        builder.append(first);
        while (iterator.hasNext())
        {
            Object next = iterator.next();
            if (iterator.hasNext())
            {
                builder.append(separator).append(next);
            }
            else
            {
                builder.append(finalSeparator).append(next);
            }
        }
        return builder.toString();
    }

    /**
     * Returns the {@link #toString()} value of the given object, if the given object is not null.  Otherwise, returns null.
     * @param object
     */
    public static String safeToString(Object object)
    {
        return object == null ? null : object.toString();
    }

    /**
     * Builds a {@link String} from the given byte array using the UTF-8 character encoding scheme (a charset that should
     * be present on any compliant JVM).
     */
    public static String toStringAsUtf8(byte[] bytes)
    {
        return new String(bytes, Charsets.UTF_8);
    }

    /**
     * Builds a {@code byte} array by calling {@link Object#toString() toString()} on the given object, and then encoding the result
     * with UTF-8. 
     * 
     * @param object
     */
    public static byte[] toBytesAsUtf8(Object object)
    {
        return object.toString().getBytes(Charsets.UTF_8);
    }

}
