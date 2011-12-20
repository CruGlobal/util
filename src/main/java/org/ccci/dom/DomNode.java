package org.ccci.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class DomNode implements Node
{
    private Node wrappedNode = null;
    
    public DomNode(Node wrappedNode)
    {
        super();
        this.wrappedNode = wrappedNode;
    }
    
    public DomNode getFirstNodeByName(String name)
    {
        NodeList nodeList = getChildNodes();
        if(nodeList==null) return null;

        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeName().equals(name))
            {
                return new DomNode(childNode);
            }
        }
        return null;
    }
    
    public DomNode getFirstNodeByNameAndAttrib(String name, String attribName, String attribValue)
    {
        NodeList nodeList = getChildNodes();
        if(nodeList==null) return null;

        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeName().equals(name))
            {
                Node attrib = childNode.getAttributes().getNamedItem(attribName);
                if(attrib!=null && attrib.getNodeValue().equals(attribValue)) return new DomNode(childNode);
            }
        }
        return null;
    }

    public Node appendChild(Node newChild) throws DOMException
    {
        return wrappedNode.appendChild(newChild);
    }

    public Node cloneNode(boolean deep)
    {
        return wrappedNode.cloneNode(deep);
    }

    public short compareDocumentPosition(Node other) throws DOMException
    {
        return wrappedNode.compareDocumentPosition(other);
    }

    public NamedNodeMap getAttributes()
    {
        return wrappedNode.getAttributes();
    }

    public String getBaseURI()
    {
        return wrappedNode.getBaseURI();
    }

    public NodeList getChildNodes()
    {
        return wrappedNode.getChildNodes();
    }

    public Object getFeature(String feature, String version)
    {
        return wrappedNode.getFeature(feature, version);
    }

    public Node getFirstChild()
    {
        return wrappedNode.getFirstChild();
    }

    public Node getLastChild()
    {
        return wrappedNode.getLastChild();
    }

    public String getLocalName()
    {
        return wrappedNode.getLocalName();
    }

    public String getNamespaceURI()
    {
        return wrappedNode.getNamespaceURI();
    }

    public Node getNextSibling()
    {
        return wrappedNode.getNextSibling();
    }

    public String getNodeName()
    {
        return wrappedNode.getNodeName();
    }

    public short getNodeType()
    {
        return wrappedNode.getNodeType();
    }

    public String getNodeValue() throws DOMException
    {
        return wrappedNode.getNodeValue();
    }

    public Document getOwnerDocument()
    {
        return wrappedNode.getOwnerDocument();
    }

    public Node getParentNode()
    {
        return wrappedNode.getParentNode();
    }

    public String getPrefix()
    {
        return wrappedNode.getPrefix();
    }

    public Node getPreviousSibling()
    {
        return wrappedNode.getPreviousSibling();
    }

    public String getTextContent() throws DOMException
    {
        return wrappedNode.getTextContent();
    }

    public Object getUserData(String key)
    {
        return wrappedNode.getUserData(key);
    }

    public boolean hasAttributes()
    {
        return wrappedNode.hasAttributes();
    }

    public boolean hasChildNodes()
    {
        return wrappedNode.hasChildNodes();
    }

    public Node insertBefore(Node newChild, Node refChild) throws DOMException
    {
        return wrappedNode.insertBefore(newChild, refChild);
    }

    public boolean isDefaultNamespace(String namespaceURI)
    {
        return wrappedNode.isDefaultNamespace(namespaceURI);
    }

    public boolean isEqualNode(Node arg)
    {
        return wrappedNode.isEqualNode(arg);
    }

    public boolean isSameNode(Node other)
    {
        return wrappedNode.isSameNode(other);
    }

    public boolean isSupported(String feature, String version)
    {
        return wrappedNode.isSupported(feature, version);
    }

    public String lookupNamespaceURI(String prefix)
    {
        return wrappedNode.lookupNamespaceURI(prefix);
    }

    public String lookupPrefix(String namespaceURI)
    {
        return wrappedNode.lookupPrefix(namespaceURI);
    }

    public void normalize()
    {
        wrappedNode.normalize();
    }

    public Node removeChild(Node oldChild) throws DOMException
    {
        return wrappedNode.removeChild(oldChild);
    }

    public Node replaceChild(Node newChild, Node oldChild) throws DOMException
    {
        return wrappedNode.replaceChild(newChild, oldChild);
    }

    public void setNodeValue(String nodeValue) throws DOMException
    {
        wrappedNode.setNodeValue(nodeValue);
    }

    public void setPrefix(String prefix) throws DOMException
    {
        wrappedNode.setPrefix(prefix);
    }

    public void setTextContent(String textContent) throws DOMException
    {
        wrappedNode.setTextContent(textContent);
    }

    public Object setUserData(String key, Object data, UserDataHandler handler)
    {
        return wrappedNode.setUserData(key, data, handler);
    }
}
