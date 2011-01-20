package org.ccci.util.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.common.io.Closeables;
import com.google.common.io.Resources;

public class PropertiesLoading
{
    private PropertiesLoading() {}
    
    
    public static Properties loadFromClasspathResource(String resourceName) throws IOException
    {
        InputStream propertiesStream;
        try
        {
            propertiesStream = Resources.getResource(resourceName).openStream();
        }
        catch (IOException e)
        {
            throw new IOException("Can't open resource " + resourceName, e);
        }
        
        boolean threw = true;
        try
        {
            Properties properties = new Properties();
            properties.load(propertiesStream);
            threw = false;
            return properties;
        }
        finally
        {
            Closeables.close(propertiesStream, threw);
        }

    }
    
}
