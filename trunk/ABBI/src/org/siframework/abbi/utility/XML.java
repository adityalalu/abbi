package org.siframework.abbi.utility;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XML {

	/**
	 * A set of properties to generate indented XML with no
	 * XML declaration.
	 */
	public static final Properties PRETTY = new Properties();
	static {
		PRETTY.setProperty(OutputKeys.INDENT, "yes");
		PRETTY.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	}
	
	public static Document getDocument(byte[] data)
	{
		return getDocument(new InputSource(new ByteArrayInputStream(data)));
	}

	public static Document getDocument(InputSource s)
	{
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			return db.parse(s);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO: Fix Error Handling
		return null;
	}

	public static Document getDocument(InputStream is)
	{
		return getDocument(new InputSource(is));
	}

	public static Document getDocument(Reader r)
	{
		return getDocument(new InputSource(r));
	}

	public static Document getDocument(String s)
	{
		return getDocument(new InputSource(new StringReader(s)));
	}

	public static Document createDocument()
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.newDocument();
		}
		catch (ParserConfigurationException ex)
		{
			throw new Error("XML Parser Configuration Error", ex);
		}
	}

	/**
	 * Convert an element into its string representation
	 * @param e	The element to convert
	 * @return	The string value of the element (see getTextContent() from DOM 3)
	 */
	public static String getString(Element e)
	{
		StringBuffer b = new StringBuffer();
		getString(e, b);
		return b.toString();
	}

	/**
	 * Append the string value of an element into a string buffer
	 * @param e	The element to convert
	 * @param b The string buffer to convert it into
	 */
	public static void getString(Element e, StringBuffer b)
	{
		for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling())
		{	if (n.getNodeType() == Node.CDATA_SECTION_NODE ||
				n.getNodeType() == Node.TEXT_NODE)
				b.append(n.getNodeValue());
			else if (n.getNodeType() == Node.ELEMENT_NODE)
				getString((Element)n, b);
		}
	}

	/**
	 * Converts an element containing a URI into a URI
	 * @param e The element containing the URI
	 * @return	A URI
	 * @throws URISyntaxException
	 */
	public static URI getURI(Element e) throws URISyntaxException
	{
		return new URI(getString(e));
	}

	/**
	 * Append node n to node e if it is non-null
	 * @param e	The node to append to
	 * @param n The node to append
	 */
	public static final void append(Node e, Node n)
	{
		if (n != null)
			e.appendChild(n);
	}
	
	/**
	 * Returns true if the element contains any element content, false otherwise
	 * @param e	The element to test for presence of element content
	 * @return	True if the element has element children, false otherwise
	 */
	public static boolean hasChildElements(Element e)
	{
		return firstChildElement(e) != null;
	}
	
	/**
	 * Get the first element child of an element.
	 * @param e The element to get the first element child of.
	 * @return The first element that is a child of e, or null if it has no element children.
	 */
	public static Element firstChildElement(Element e)
	{
		if (e == null)
			return null;
		if (e.getFirstChild().getNodeType() == Node.ELEMENT_NODE)
			return (Element)e.getFirstChild();
		return nextSiblingElement(e.getFirstChild());
	}
	
	/**
	 * Get the next element sibling following this node.
	 * @param n Node the node from which to start looking for elements.
	 * @return The next sibling of n that is an element, or null if it has no 
	 * following element siblings 
	 */
	public static Element nextSiblingElement(Node n)
	{
		while (true)
		{	n = n.getNextSibling();
			if (n == null)
				return null;
			if (n.getNodeType() == Node.ELEMENT_NODE)
				return (Element)n;
		}
	}
	
	/**
	 * Create a Transformer Factory for this class to use.
	 */
	private static TransformerFactory tf = TransformerFactory.newInstance();
	
	/**
	 * Construct a transformer from an input stream.
	 * @param transform The input stream containing the stylesheet
	 * @return A transformer that will implement the transform specified in the stylesheet.
	 */
	public static Transformer getTransformer(InputStream transform)
	{
		try {
			return tf.newTransformer(new StreamSource(transform));
		} catch (TransformerConfigurationException e) {
			throw new Error("XSL Configuration Error", e);
		}
	}
	
	/**
	 * Construct an identity transformer.
	 * @return An identity transformer.
	 */
	public final static Transformer getTransformer()
	{
		try {
			return tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new Error("XSL Configuration Error", e);
		}
	}

	/**
	 * Generate a string from the XML Node, using the specified output properties.
	 * @param n The node to transform
	 * @param p The output properties to use for the transformation
	 * @return A string representation of the XML
	 * @throws TransformerException if there was an error transforming the XML
	 */
	public static String asString(Node n, Properties p) throws TransformerException 
	{	Transformer t = getTransformer();
		StringWriter w = new StringWriter();
		if (p != null)
			t.setOutputProperties(p);
		t.transform(new DOMSource(n), new StreamResult(w));
		return w.getBuffer().toString();
	}
	
	/**
	 * Transform an XML Node into a String
	 * @param n The node to transform
	 * @return The string representation of that node.
	 * @throws TransformerException If an error occured during the transform.
	 */
	public static String asString(Node n) throws TransformerException
	{
		return asString(n, null);
	}

}
