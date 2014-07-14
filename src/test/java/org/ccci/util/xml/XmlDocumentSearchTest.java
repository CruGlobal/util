package org.ccci.util.xml;

import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by ryancarlson on 7/14/14.
 */
public class XmlDocumentSearchTest
{
	/**
	 * Test foo, bar, baz and quox and check for expected results
	 *
	 * foo - 1 element
	 * bar - 3 elements
	 * baz - no elements
	 * quox - 2 elements
	 *
	 */
	@Test
	public void testSearchForElementsByName() throws URISyntaxException, FileNotFoundException
	{
		Document testFile = XmlDocumentStreamConverter.readFromInputStream(getTestFileStream());

		List<Element> fooElements = XmlDocumentSearchUtilities.findElements(testFile, "foo");

		Assert.assertEquals(fooElements.size(), 1);
		Assert.assertEquals(fooElements.get(0).getTagName(), "foo");

		List<Element> barElements = XmlDocumentSearchUtilities.findElements(testFile, "bar");

		Assert.assertEquals(barElements.size(), 3);
		for(Element bar : barElements)
		{
			Assert.assertEquals(bar.getTagName(), "bar");
		}

		List<Element> quxElements = XmlDocumentSearchUtilities.findElements(testFile, "qux");

		Assert.assertEquals(quxElements.size(), 2);
		for(Element qux : quxElements)
		{
			Assert.assertEquals(qux.getTagName(), "qux");
		}

		List<Element> bazElements = XmlDocumentSearchUtilities.findElements(testFile, "baz");

		Assert.assertTrue(bazElements.isEmpty());
	}

	@Test
	public void testSearchForSpecificElementsWithAttribute() throws URISyntaxException, FileNotFoundException
	{
		Document testFile = XmlDocumentStreamConverter.readFromInputStream(getTestFileStream());

		List<Element> barWithBazElements = XmlDocumentSearchUtilities.findElementsWithAttribute(testFile, "bar", "baz");

		Assert.assertEquals(barWithBazElements.size(), 3);

		for(Element barWithBaz : barWithBazElements)
		{
			Assert.assertTrue(barWithBaz.hasAttribute("baz"));
			Assert.assertNotNull(barWithBaz.getAttribute("baz"));
		}

		List<Element> quxWithLanguageElements = XmlDocumentSearchUtilities.findElementsWithAttribute(testFile, "qux", "language");

		Assert.assertEquals(quxWithLanguageElements.size(), 2);

		for(Element quxWithLanguage : quxWithLanguageElements)
		{
			Assert.assertTrue(quxWithLanguage.hasAttribute("language"));
			Assert.assertTrue(quxWithLanguage.getAttribute("language").equals("en") || quxWithLanguage.getAttribute("language").equals("fr") );
		}

		List<Element> barWithCorgeElements = XmlDocumentSearchUtilities.findElementsWithAttribute(testFile, "bar", "corge");

		Assert.assertTrue(barWithCorgeElements.isEmpty());
	}

	@Test
	public void testSearchForAnyElementWithAttribute() throws URISyntaxException, FileNotFoundException
	{
		Document testFile = XmlDocumentStreamConverter.readFromInputStream(getTestFileStream());

		List<Element> elementsWithBaz = XmlDocumentSearchUtilities.findElementsWithAttribute(testFile, "baz");

		Assert.assertEquals(elementsWithBaz.size(), 4);
		for(Element bazElement : elementsWithBaz)
		{
			Assert.assertTrue(bazElement.hasAttribute("baz"));
		}

		List<Element> elementsWithCorge = XmlDocumentSearchUtilities.findElementsWithAttribute(testFile, "corge");

		Assert.assertTrue(elementsWithCorge.isEmpty());
	}

	InputStream getTestFileStream() throws URISyntaxException
	{
		return this.getClass().getResourceAsStream("/xmlSearchTest.xml");
	}
}
