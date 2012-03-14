package org.ccci.servlet;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Properties;

/**
 *  Used to do substitution of ${varible} variables in a String, deferencing the variable from Properties
 *  
 *  From http://www.coderanch.com/t/79094/Websphere/environment-variable-referance-Web-xml
 *  Code by M Simpson 
 */
public class ParameterSubstituter {

    /**
     * Same as dereference(String, Properties) but assumes System properties
     * @param value on which to do the substitution
     * @return value with substitutions
     */
    public static String dereference(final String value) {
        return dereference(value, System.getProperties());
    }

    /**
     * Input a string value with variables in the form of ${var-name} and it will lookup the var-name in props and
     * replace ${var-name} with it.
     * e.g. given properties "greeting=hello" and "name=sam" if the input value is "${greeting} ${name}, how are you?"
     * the the output would be "hello sam, how are you?"
     * <p>
     * Parameter names must be alphanumeric but can also have characters "-" and ".".
     * <p>
     * Also: it does multiple passes until all variables are resovled, e.g.
     *  <code>
     *  System.setProperty("first", "${second}");
     *  System.setProperty("second", "${third}");
     *  System.setProperty("third", "fourth");
     *  System.out.println(dereference("${first} ${second} ${third}"));
     * </code>
     * Prints "fourth fourth fourth"
     * @param value an optionally parameterized input string
     * @param props are the Properties to search for replacements
     * @return value with substitutions made
     */
    public static String dereference(String value, final Properties props) {
        if (value == null) {
            return null;
        }
        String initialValue = null;
        final Pattern p = Pattern.compile("\\$\\{([\\p{Alnum}|\\.|\\-]*)\\}");
        while (!value.equals(initialValue)) {
            initialValue = value;
            final Matcher m = p.matcher(value);
            while (m.find()) {
                final String propName = m.group(1);
                final String propValue = props.getProperty(propName, null);
                if (propValue != null) {
                    value = value.replaceAll("\\$\\{" + propName + "\\}", Matcher.quoteReplacement(propValue));
                }
            }
        }
        return value;
    }

}
