package org.ccci.util.xml;

import com.google.common.base.Throwables;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * These are convenience methods for in-memory writing and parsing of XML documents
 * (XML Document -> OutputStream) & (InputStream -> XML Document)
 *
 * Created by ryancarlson on 3/25/14.
 */
public class XmlDocumentStreamConverter
{
	/**
	 * Writes an XML Document to a ByteArrayOutputStream.
	 *
	 * This method utilizes a javax.xml.transform Transfomer to write a document to the appropriate stream.	 *
	 */
    public static ByteArrayOutputStream writeToByteArrayStream(Document xmlFile)
    {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        try
        {
            Source source = new DOMSource(xmlFile);
            Result result = new StreamResult(byteStream);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(source, result);
        }
        catch(Exception e)
        {
            throw Throwables.propagate(e);
        }
        return byteStream;
    }

	/**
	 * Reads an XML Document from an InputStream
	 *
	 */
	public static Document readFromInputStream(InputStream inputStream)
	{
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			return builder.parse(inputStream);
		}
		catch(Exception e)
		{
			throw Throwables.propagate(e);
		}
	}
}
