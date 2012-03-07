package org.ccci.util.properties;

import junit.framework.Assert;

import org.testng.annotations.Test;

public class PropertiesTest
{
	@Test
	public void testProperties() throws Exception
	{
		String key = "123";
		CcciProperties properties = new CcciProperties("/properties.xml",
		    CcciProperties.PropertySourceSemantics.CLASS_RESOURCE, CcciProperties.SourceFormat.XML,
				new CcciProperties.PropertyEncryptionSetup(key));

		Assert.assertEquals("value one", properties.getProperty("property1"));
		Assert.assertEquals("value two", properties.getProperty("property2"));
		Assert.assertEquals("value three", properties.getProperty("property3"));
	}
}
