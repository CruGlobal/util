package org.ccci.util.properties;

import java.io.DataInputStream;
import java.io.FileInputStream;

/**
 * Properties class extended to include support for reading in properties files.
 * 
 * @author Lee Braddock
 */
public class Properties extends java.util.Properties {

	private static final long serialVersionUID = 1L;

	public Properties() {
	}

	/**
	 * Reads the specified properties file into this class.
	 */
	public void getProperties(String propertiesFileName)
			throws RuntimeException {
		FileInputStream fstream = null;
		DataInputStream in = null;

		try {
			fstream = new FileInputStream(propertiesFileName);
			in = new DataInputStream(fstream);
			loadFromXML(in);
		} catch (Exception e) {
			throw new RuntimeException("Could not load properties file "
					+ propertiesFileName, e);
		} finally {
			try {
				if (fstream != null)
					fstream.close();
				if (in != null)
					in.close();
			} catch (Exception e) {
			}
		}
	}
}
