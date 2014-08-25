package org.ccci.util.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.google.common.base.Throwables;
import org.ccci.util.properties.CcciProperties.PropertyEncryptionSetup;
import org.ccci.util.properties.CcciProperties.PropertySourceSemantics;
import org.ccci.util.properties.CcciProperties.SourceFormat;

public class PropertiesWithFallback extends Properties
{
    private static final long serialVersionUID = 1L;
    private List<Properties> props = new ArrayList<Properties>();
    
    public PropertiesWithFallback(boolean firstSourceOnly, String... sources)
    {
        this(null, firstSourceOnly, sources);
    }
    
    public PropertiesWithFallback(PropertyEncryptionSetup encryptionData, boolean firstSourceOnly, String... sources)
    {
        for(String source : sources)
        {
            SourceFormat format = source.toLowerCase().endsWith(".xml")?SourceFormat.XML:SourceFormat.KEYPAIR;
            try
            {
                Properties properties = new CcciProperties(source, PropertySourceSemantics.FILENAME, format, encryptionData);
                props.add(properties);
//                System.out.println("found file: " + source);
                if(firstSourceOnly) break;
            }
			// only FileNotFoundException should trigger loading the file as a resource. other types of exceptions should be thrown out.
			// for example an exception parsing XML got effectively swallowed and I spent several hours on file paths/permissions b/c
			// the behavior suggested the file wasn't being loaded. that wasn't at all the case.
            catch (FileNotFoundException e)
            {
                try
                {
                    Properties properties = new CcciProperties(source, PropertySourceSemantics.CLASS_RESOURCE, format, encryptionData);
                    props.add(properties);
//                    System.out.println("found resource: " + source);
                    if(firstSourceOnly) break;
                }
                catch (Exception e2)
                {
//                    System.out.println("Could not load as file or resource: " + source);
					throw Throwables.propagate(e2);
                }
            }
        }
    }
    
    public void clear()
    {
        props = new ArrayList<Properties>();
    }
    public Object clone()
    {
        throw new RuntimeException("not implemented");
    }
    public boolean contains(Object arg0)
    {
        for(Properties props2 : props)
            if (props2.contains(arg0)) return true;
        return false;
    }
    public boolean containsKey(Object arg0)
    {
        for(Properties props2 : props)
            if (props2.containsKey(arg0)) return true;
        return false;
    }
    public boolean containsValue(Object arg0)
    {
        for(Properties props2 : props)
            if (props2.containsValue(arg0)) return true;
        return false;
    }
    public Set<java.util.Map.Entry<Object, Object>> entrySet()
    {
        Set<java.util.Map.Entry<Object, Object>> retVal = new HashSet<java.util.Map.Entry<Object, Object>>();
        for(Properties props2 : props) retVal.addAll(props2.entrySet());
        return retVal;
    }
    public boolean equals(Object arg0)
    {
        return false;
    }
    public Object get(Object arg0)
    {
        for(Properties props2 : props)
            if (props2.get(arg0)!=null) return props2.get(arg0);
        return null;
    }
    public String getProperty(String key, String defaultValue)
    {
        for(Properties props2 : props)
            if (props2.getProperty(key)!=null) return translate(props2.getProperty(key));
        return defaultValue;
    }
    public String getProperty(String key)
    {
        for(Properties props2 : props)
            if (props2.getProperty(key)!=null) return translate(props2.getProperty(key));
        return null;
    }
    private String translate(String property)
    {
        if(property.equals("//empty//")) return null;
        return property;
    }

    public int hashCode()
    {
        return props.hashCode();
    }
    public boolean isEmpty()
    {
        for(Properties props2 : props)
            if (!props2.isEmpty()) return false;
        return true;
    }
    public Object setProperty(String key, String value)
    {
        return props.get(0).setProperty(key, value);
    }
    public Set<Object> keySet()
    {
        Set<Object> retVal = new HashSet<Object>();
        for(Properties props2 : props) retVal.addAll(props2.keySet());
        return retVal;
    }
    public Object put(Object arg0, Object arg1)
    {
        return props.get(0).put(arg0, arg1);
    }
    public void putAll(Map<? extends Object, ? extends Object> arg0)
    {
        props.get(0).putAll(arg0);
    }
    public Set<String> stringPropertyNames()
    {
        Set<String> retVal = new HashSet<String>();
        for(Properties props2 : props) retVal.addAll(props2.stringPropertyNames());
        return retVal;
    }
    public String toString()
    {
        return props.toString();
    }
    
    
    
    public Enumeration<Object> elements()
    {
        throw new RuntimeException("not implemented");
    }
    public Enumeration<Object> keys()
    {
        throw new RuntimeException("not implemented");
    }
    public void list(PrintStream out)
    {
        throw new RuntimeException("not implemented");
    }
    public void list(PrintWriter out)
    {
        throw new RuntimeException("not implemented");
    }
    public void load(InputStream inStream) throws IOException
    {
        throw new RuntimeException("not implemented");
    }
    public void load(Reader reader) throws IOException
    {
        throw new RuntimeException("not implemented");
    }
    public void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException
    {
        throw new RuntimeException("not implemented");
    }
    public Enumeration<?> propertyNames()
    {
        throw new RuntimeException("not implemented");
    }
    public Object remove(Object arg0)
    {
        throw new RuntimeException("not implemented");
    }
    public void save(OutputStream out, String comments)
    {
        throw new RuntimeException("not implemented");
    }
    public int size()
    {
        throw new RuntimeException("not implemented");
    }
    public void store(OutputStream out, String comments) throws IOException
    {
        throw new RuntimeException("not implemented");
    }
    public void store(Writer writer, String comments) throws IOException
    {
        throw new RuntimeException("not implemented");
    }
    public void storeToXML(OutputStream os, String comment, String encoding) throws IOException
    {
        throw new RuntimeException("not implemented");
    }
    public void storeToXML(OutputStream os, String comment) throws IOException
    {
        throw new RuntimeException("not implemented");
    }
    public Collection<Object> values()
    {
        throw new RuntimeException("not implemented");
    }
    
    
}
