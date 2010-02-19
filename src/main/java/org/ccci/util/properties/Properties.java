package org.ccci.util.properties;

import java.io.DataInputStream;
import java.io.FileInputStream;

/**
 * Properties class extended to include support for reading in properties files.
 * 
 * Loads properties from properties source, depending on specified properties
 * source semantics - class resource is default.
 * 
 * @author Lee Braddock
 */
public class Properties extends java.util.Properties {

	private static final long serialVersionUID = 1L;

	public enum PropertySourceSemantics {
		CLASS_RESOURCE, FILENAME
	}

	public Properties(String classResource) throws RuntimeException {
		this(classResource, PropertySourceSemantics.CLASS_RESOURCE);
	}

	public Properties(String propertiesSource,
			PropertySourceSemantics propertyType) throws RuntimeException {
		if (propertyType == PropertySourceSemantics.FILENAME)
			getPropertiesFromFilename(propertiesSource);
		else
			getPropertiesFromClassResource(propertiesSource);
	}

	private void getPropertiesFromClassResource(String classResource) {
		try {
			loadFromXML(new java.util.Properties().getClass()
					.getClassLoader().getResourceAsStream(classResource));
		} catch (Exception e) {
			throw new RuntimeException("Could not load class resource "
					+ classResource, e);
		}
	}

	private void getPropertiesFromFilename(String fileName)
			throws RuntimeException {
		FileInputStream fstream = null;
		DataInputStream in = null;

		try {
			fstream = new FileInputStream(fileName);
			in = new DataInputStream(fstream);
			loadFromXML(in);
		} catch (Exception e) {
			throw new RuntimeException("Could not load properties file "
					+ fileName, e);
		} finally {
			try {
				if (fstream != null)
					fstream.close();
				if (in != null)
					in.close();
			} catch (Exception exception) {
			}
		}
	}
}
