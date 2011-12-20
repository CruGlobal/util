package org.ccci.util.properties;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import org.ccci.util.strings.Strings;
import org.jasypt.util.text.TextEncryptor;

/**
 * Properties class extended to include support for reading in properties files.
 * 
 * Reads from specified properties source (class resource or file) and format
 * (xml or key pair).
 * 
 * Supports reading encrypted properties.
 * 
 * @author Lee Braddock
 */
public class CcciProperties extends java.util.Properties
{
	private static final long serialVersionUID = 1L;

	public CcciProperties(String classResource) throws RuntimeException
	{
		this(classResource, PropertySourceSemantics.CLASS_RESOURCE, SourceFormat.XML);
	}

	public enum PropertySourceSemantics
	{
		CLASS_RESOURCE, FILENAME
	}

	public CcciProperties(String classResource, PropertySourceSemantics propertySourceSemantics)
			throws RuntimeException
	{
		this(classResource, propertySourceSemantics, SourceFormat.XML);
	}

	public enum SourceFormat
	{
		KEYPAIR, XML
	}

	public CcciProperties(String propertiesSource, PropertySourceSemantics propertySourceSemantics,
			SourceFormat sourceFormat) throws RuntimeException
	{
		this(propertiesSource, propertySourceSemantics, sourceFormat, null);
	}

	private PropertyEncryptionSetup encryptionData;
	private TextEncryptor textEncryptor;

	static public class PropertyEncryptionSetup
	{
		private String key;
		private String prefix;

		public PropertyEncryptionSetup(String key)
		{
			this(key, "encryptedData:");
		}

		public PropertyEncryptionSetup(String key, String prefix)
		{
			this.key = key;
			this.prefix = prefix;
		}
	}

	public CcciProperties(String propertiesSource, PropertySourceSemantics propertySourceSemantic,
			SourceFormat sourceFormat, PropertyEncryptionSetup encryptionData) throws RuntimeException
	{
		if (propertySourceSemantic == PropertySourceSemantics.FILENAME)
			getPropertiesFromFilename(propertiesSource, sourceFormat);
		else
			getPropertiesFromClassResource(propertiesSource, sourceFormat);

		this.encryptionData = encryptionData;
		if (encryptionData != null)
			textEncryptor = getTextEncryptor(encryptionData.key);
	}

	@Override
	public String getProperty(String key, String defaultValue)
	{
		String property = getProperty(key);

		return Strings.isEmpty(property) ? defaultValue : property;
	}

	@Override
	public String getProperty(String key)
	{
		String property = super.getProperty(key);

		if (Strings.isEmpty(property))
			return property;

		if (encryptionData != null)
			if (property.startsWith(encryptionData.prefix))
				if (textEncryptor != null)
					return textEncryptor.decrypt(property.substring(encryptionData.prefix.length()));

		return property;
	}

	/**
	 * Convenience method for generating encrypted property values
	 *  
	 * @param key - encryption key
	 * @param value - value to encrypt
	 * @return encrypted value
	 */
	public String encrypt(String key, String value)
	{
		return getTextEncryptor(key).encrypt(value);
	}

	private TextEncryptor getTextEncryptor(String key)
	{
	    return new CcciPropsTextEncryptor(key, false);
	}

	private void getPropertiesFromClassResource(String classResource, SourceFormat sourceFormat)
	{
		InputStream in = null;
		try
		{
			in = CcciProperties.class.getClassLoader().getResourceAsStream(classResource);
			if (sourceFormat == SourceFormat.XML)
				loadFromXML(in);
			else
				load(in);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not load class resource " + classResource, e);
		}
		finally
		{
			try
			{
				if (in != null)
					in.close();
			}
			catch (Exception e)
			{
			}
		}
	}

	private void getPropertiesFromFilename(String fileName, SourceFormat sourceFormat) throws RuntimeException
	{
		FileInputStream fstream = null;
		DataInputStream in = null;

		try
		{
			fstream = new FileInputStream(fileName);
			in = new DataInputStream(fstream);
			if (sourceFormat == SourceFormat.XML)
				loadFromXML(in);
			else
				load(in);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not load properties file " + fileName, e);
		}
		finally
		{
			try
			{
				if (fstream != null)
					fstream.close();
				if (in != null)
					in.close();
			}
			catch (Exception exception)
			{
			}
		}
	}
}
