package org.ccci.util.properties;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

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

	public enum SourceFormat {
		KEYPAIR, XML
	}

	public Properties(String classResource) throws RuntimeException {
		this(classResource, PropertySourceSemantics.CLASS_RESOURCE,
				SourceFormat.XML);
	}

	public Properties(String classResource,
			PropertySourceSemantics propertySourceSemantics)
			throws RuntimeException {
		this(classResource, propertySourceSemantics, SourceFormat.XML);
	}

	public Properties(String propertiesSource,
			PropertySourceSemantics propertySourceSemantic,
			SourceFormat sourceFormat) throws RuntimeException {
		if (propertySourceSemantic == PropertySourceSemantics.FILENAME)
			getPropertiesFromFilename(propertiesSource, sourceFormat);
		else
			getPropertiesFromClassResource(propertiesSource, sourceFormat);
	}

	private void getPropertiesFromClassResource(String classResource,
			SourceFormat sourceFormat) {
		InputStream in = null;
		try {
			in = Properties.class.getClassLoader().getResourceAsStream(
					classResource);
			if (sourceFormat == SourceFormat.XML)
				loadFromXML(in);
			else
				load(in);
		} catch (Exception e) {
			throw new RuntimeException("Could not load class resource "
					+ classResource, e);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
			}
		}
	}

	private void getPropertiesFromFilename(String fileName,
			SourceFormat sourceFormat) throws RuntimeException {
		FileInputStream fstream = null;
		DataInputStream in = null;

		try {
			fstream = new FileInputStream(fileName);
			in = new DataInputStream(fstream);
			if (sourceFormat == SourceFormat.XML)
				loadFromXML(in);
			else
				load(in);
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
