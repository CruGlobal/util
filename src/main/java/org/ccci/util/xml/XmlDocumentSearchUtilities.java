package org.ccci.util.xml;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * Created by ryancarlson on 3/19/14.
 */

public class XmlDocumentSearchUtilities
{
	/**
	 * This method will scan the document for all elements named "elementName".  If those elements have an attribute
	 * named "attributeName", it will return a list of all of those elements.
	 */
	public static List<Element> findElementsWithAttribute(Document document, String elementName, String attributeName)
	{
		List<Element> list = Lists.newArrayList();

		NodeList nodes = document.getElementsByTagName(elementName);

		for(int i = 0; i < nodes.getLength(); i++)
		{
			Element element = (Element) nodes.item(i);
			if(Strings.isNullOrEmpty(element.getAttribute(attributeName))) continue;

			list.add(element);
		}

		return list;
	}

	/**
	 * This method will scan the document for all elements with an attribute named @param attribute.
	 * Returns a list of all matching results
	 *
	 */
	public static List<Element> findElementsWithAttribute(Document document, String attribute)
	{
		List<Element> list = Lists.newArrayList();

		recursiveDocumentSearch(list, document.getDocumentElement(), attribute);

		return list;
	}

	private static void recursiveDocumentSearch(List<Element> list, Node baseNode, String attribute)
	{
		NodeList children = baseNode.getChildNodes();

		for(int i = 0; i < children.getLength(); i++)
		{
			Node node = children.item(i);

			// if we have an element, then check to see if it has the attribute we want.
			// if not, keep moving and dive deeper into the document.
			if(node instanceof Element)
			{
				Element currentElement = (Element) node;
				if(!Strings.isNullOrEmpty(currentElement.getAttribute(attribute)))
				{
					list.add((Element)children.item(i));
				}
			}
			recursiveDocumentSearch(list, node, attribute);
		}
	}

	/**
	 * Retrieves a list of all elements matching elementName.
	 */
	public static List<Element> findElements(Document document, String elementName)
	{
		List<Element> list = Lists.newArrayList();

		NodeList nodes = document.getElementsByTagName(elementName);

		for(int i = 0; i < nodes.getLength(); i++)
		{
			Node node = nodes.item(i);

			if(node instanceof Element)
			{
				list.add((Element) node);
			}
		}

		return list;
	}
}
