package org.ccci.util.contract;

public class Preconditions
{

    public static <T> T checkNotNull(T reference, String message, Object... parameters)
    {
        if (reference == null) throw new NullPointerException(String.format(message, parameters));
        return reference;
    }

    public static String checkNotNullOrEmpty(String string, String referenceName)
    {
        checkNotNull(string, "%s is null", referenceName);
        if (string.isEmpty()) illegalArg("%s is empty", referenceName);
        return string;
    }

    private static void illegalArg(String message, Object... parameters)
    {
        throw new IllegalArgumentException(String.format(message, parameters));
    }

    public static void checkSize(String string, int minLength, int maxSize, String referenceName)
    {
        checkMinSize(string, minLength, referenceName);
        checkMaxSize(string, maxSize, referenceName);
    }

    public static void checkMinSize(String string, int minLength, String referenceName)
    {
        if (string.length() < minLength) illegalArg("%s has less than %s character(s)", referenceName, minLength);
    }
    
    public static void checkMaxSize(String string, int maxSize, String referenceName)
    {
        if (string.length() > maxSize) illegalArg("%s has more than %s character(s)", referenceName, maxSize);
    }

    public static void checkArgument(boolean condition, String message, Object... parameters)
    {
        if (!condition) illegalArg(message, parameters);
    }

    /**
     * Verifies the given string can be used as a valid <a href="contract of http://www.w3.org/TR/xmlschema-2/#token">token</a>
     * @param string
     * @param referenceName
     * @return
     */
    public static String checkNonEmptyToken(String string, String referenceName)
    {
        checkNotNullOrEmpty(string, referenceName);
        if (string.startsWith(" ")) illegalArg("%s starts with a space", string);
        if (string.endsWith(" ")) illegalArg("%s ends with a space", string);
        if (string.contains("  ")) illegalArg("%s contains a sequence of two or more spaces", string);
        if (string.contains("\t")) illegalArg("%s contains a tab character", string);
        if (string.contains("\n")) illegalArg("%s contains a newline character", string);
        if (string.contains("\r")) illegalArg("%s contains a carriage return character", string);
        return string;
    }


}
