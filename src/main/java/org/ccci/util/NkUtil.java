package org.ccci.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Nathan's classic Util class.
 * 
 * @author Nathan.Kopp
 *
 */
public class NkUtil
{
    /**
     * Determines if a string is "blank".  Blank means either null, or
     * trim().length()==0
     */
    public static boolean isBlank(String s)
    {
        if (s==null) return true;
        if (s.trim().isEmpty()) return true;
        return false;
    }

    /**
     * Determines if a string is "blank".  Blank means either null, or
     * trim().length()==0
     */
    public static boolean isBlank(Object o)
    {
        if (o==null) return true;
        return(isBlank(o.toString()));
    }

    /**
     * Determine if two objects are equal, taking into consideration that
     * either one or the other or both might be null.
     */
    public static boolean objectsEqual(Object s1, Object s2)
    {
        if (s1==s2) return true;
        if (s1==null || s2==null) return false;
        if (s1.equals(s2)) return true;
        return false;
    }
    
    public static String toUpper(String s)
    {
        if(s==null) return null;
        return s.toUpperCase();
    }
    
    static public boolean equal(Object val1, Object val2) {
        if(val1==val2) return true;
        if(val1==null || val2==null) return false;
        return (val1.equals(val2));
    }
    
    static public boolean equalBlanksEqual(Object val1, Object val2) {
        if(val1==val2) return true;
        if(isBlank(val1) && isBlank(val2)) return true;
        if(val1==null || val2==null) return false;
        return (val1.equals(val2));
    }
    
    static public Date parseDate(String s) throws ParseException
    {
        DateFormat df = new SimpleDateFormat("M/d/yy");
        if(isBlank(s)) return null;
        return df.parse(s);
    }
    
    static public String formatDate(Date d)
    {
        DateFormat df = new SimpleDateFormat("M/d/yyyy");
        if(d==null) return null;
        return df.format(d);
    }
	
	public static String getStringFromList(List list)
	{
		if (list == null || list.size() == 0)
			return "";
		String ret = (String)list.get(0);
		if (list.size() == 1)
			return ret;
		for(int i = 1; i < list.size()-1; i++)
		{
			ret += ", ";
			ret += (String)list.get(i);
		}
		ret += " and ";
		ret += (String)list.get(list.size()-1);
		return ret;
	}
	
}
