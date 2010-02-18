package org.ccci.util.properties;

/**
* Property accessor for xml properties file of the following format:
 
<?xml version="1.0" encoding="UTF-8"?> 
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"> 
<properties> 
<entry key="services">value1 value2 value3</entry> 
</properties>

Applications can sub class this class as follows:

public class MyProperties extends ApplicationProperties {
    public static String SERVICES_PROPERTY_NAME = "services";
    static { setPropertyFileName("the application properties filename"); }
}

And get properties as follows:
	MyProperties.getProperty(MyProperties.SERVICES_PROPERTY_NAME);

 * @author Lee Braddock
 */
public class ApplicationProperties {
	public static String getProperty(String property) {
		return (getProperties().getProperty(property) == null) ? EMPTY_STRING
				: getProperties().getProperty(property);
	}

	public static void setPropertyFileName(String propertyFileName) {
		ApplicationProperties.propertyFileName = propertyFileName;
	}

	private static Object initSynch = new Object();

	private static Properties properties = null;

	private static String EMPTY_STRING = "";

	private static String propertyFileName = EMPTY_STRING;

	private static Properties getProperties() {

		if (properties != null)
			return properties;

		synchronized (initSynch) {

			try {

				properties = new Properties();

				properties.getProperties(propertyFileName);

				return properties;

			} catch (Exception e) {

				properties = null;

				throw new RuntimeException(e);
			}
		}
	}
}
