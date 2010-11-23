package org.ccci.util.strings;

import java.util.StringTokenizer;

/**
 * This class convert the strings to all upper, lower or sentence.
 * Also, place '%' symbols at the beginning and ending of a string
 * in preparation for SQL usage with the LIKE keyword. 
 * 
 * @author Lael.Watkins
 * 
 */
public class QueryStringCaseConverter
{
    private String stringAllUppercase ;
    private String stringAllLowercase;
    private String stringUppercaseFirstChar;
    private String stringSentencecase;

    public QueryStringCaseConverter(String textString)
    {
        convertStringAllUppercase(textString);
        convertStringAllLowercase(textString);
        convertStringUppercaseFirstChar(textString);        
        convertStringSentencecase(textString);
    }    
    
    /**
     * This method takes a string of space delimted words and changes the
     * first character of each one to uppercase.  Thus, changing the string
     * to sentence case.
     * 
     * @param textString
     * @return String
     */
    private void sentenceCaser(String textString)
    {
        String firstChar = "";   
        String newPhrase = "";
        String word;            
       
        textString = textString.toLowerCase();
        StringTokenizer st = new StringTokenizer(textString, " ");
        String[] stringer = new String[st.countTokens()];           
        
        int i = 0;
        while(st.hasMoreTokens()&& i<stringer.length)
            stringer[i++]= st.nextToken();
        
        for(int t=0;t<stringer.length;t++)
        {
            word = stringer[t];
            if(word!=null)
            {
            firstChar = word.substring(0,1);
            }
            word = word.replaceFirst(firstChar, firstChar.toUpperCase());
            newPhrase+=word.concat(" ");
        }
        
        this.stringSentencecase =  "%" + newPhrase.trim() + "%";              
    }

    public String getStringAllUppercase()
    {
        return stringAllUppercase;
    }

    public void convertStringAllUppercase(String stringAllUppercase)
    {
        this.stringAllUppercase = "%" + stringAllUppercase.toUpperCase() + "%";
    }

    public String getStringAllLowercase()
    {
        return stringAllLowercase;
    }

    public void convertStringAllLowercase(String stringAllLowercase)
    {        
        this.stringAllLowercase = "%" + stringAllLowercase.toLowerCase() + "%";
    }

    public String getStringUppercaseFirstChar()
    {
        return stringUppercaseFirstChar;
    }

    /**
     * Convert the first characater only to uppercase.
     * 
     * @param stringUppercaseFirstChar
     */
    public void convertStringUppercaseFirstChar(String stringUppercaseFirstChar)
    {
        stringUppercaseFirstChar = stringUppercaseFirstChar.toLowerCase();
        String firstChar = stringUppercaseFirstChar.substring(0,1);
        stringUppercaseFirstChar = stringUppercaseFirstChar.replaceFirst(firstChar, firstChar.toUpperCase());
        this.stringUppercaseFirstChar = "%" + stringUppercaseFirstChar + "%";
    }

    public String getStringSentencecase()
    {
        return stringSentencecase;
    }

    public void convertStringSentencecase(String stringSentencecase)
    {
        sentenceCaser(stringSentencecase);       
    }
}
