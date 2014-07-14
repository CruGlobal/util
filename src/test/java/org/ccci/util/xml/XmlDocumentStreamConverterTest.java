package org.ccci.util.xml;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * Created by ryancarlson on 7/14/14.
 */
public class XmlDocumentStreamConverterTest
{
	@Test
	public void testReadFile() throws URISyntaxException
	{
		Assert.assertNotNull(XmlDocumentStreamConverter.readFromInputStream(getTestFileStream()));
	}

	@Test
	public void testWriteStream() throws URISyntaxException, UnsupportedEncodingException
	{
		Document document = XmlDocumentStreamConverter.readFromInputStream(getTestFileStream());

		ByteArrayOutputStream outputStream = XmlDocumentStreamConverter.writeToByteArrayStream(document);

		String xmlString = outputStream.toString("UTF-8");

		Assert.assertTrue(xmlString.contains("<foo>"));
	}

	InputStream getTestFileStream() throws URISyntaxException
	{
		return this.getClass().getResourceAsStream("/xmlSearchTest.xml");
	}
}
