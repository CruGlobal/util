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

	/**
	 * Reads the specified properties file into this class.
	 */
	public void getProperties(String propertiesFileName)
			throws RuntimeException {
		FileInputStream fstream = null;
		DataInputStream in = null;

		try {
			// find the file with the class loader
			loadFromXML(new Properties().getClass().getClassLoader()
					.getResourceAsStream(propertiesFileName));
		} catch (Exception e) {
			// find the file from the 'current directory' or absolute path name
			try {
				fstream = new FileInputStream(propertiesFileName);
				in = new DataInputStream(fstream);
				loadFromXML(in);
			} catch (Exception e2) {
				throw new RuntimeException("Could not load properties file "
						+ propertiesFileName, e2);
			} finally {
				try {
					if (fstream != null)
						fstream.close();
					if (in != null)
						in.close();
				} catch (Exception e3) {
				}
			}
		}
	}
}
