package org.ccci.util.properties;

import junit.framework.Assert;

import org.testng.annotations.Test;

public class PropertiesTest
{
	@Test
	public void testProperties() throws Exception
	{
		String key = "123";
		Properties properties = new Properties("properties.xml",
				Properties.PropertySourceSemantics.CLASS_RESOURCE, Properties.SourceFormat.XML,
				new Properties.EncryptionData(key));

		Assert.assertEquals("value one", properties.getProperty("property1"));
		Assert.assertEquals("value two", properties.getProperty("property2"));
		Assert.assertEquals("value three", properties.getProperty("property3"));
	}
}
