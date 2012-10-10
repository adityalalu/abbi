package org.siframework.abbi.atom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.codec.binary.Base64;
import org.siframework.abbi.atom.impl.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML is a utility class to convert Feeds to XML and back.
 * @author Keith W. Boone
 *
 */
public class XML {
	// The Atom Namespace
	public static final String ATOMNS = "http://www.w3.org/2005/Atom";

	// The FHIR Namespace
	public static final String FHIRNS = "http://hl7.org/fhir";
	
	// The format for converting timestamps in Atom
	public static final String ISO8601TS = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	/** Convert a feed into XML via a Result
	 * @param feed	The feed to convert
	 * @param r Where to pu the resulting converted output.
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public static void toXML(Feed feed, Result r) 
		throws ParserConfigurationException, TransformerException
	{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer();
		
		DOMSource source = new DOMSource(toXML(feed));
		t.transform(source, r);
	}
	
	public static Document createDocument() throws ParserConfigurationException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.newDocument();
	}
	/**
	 * Convert an Atom feed to an XML DOM Document
	 * @param feed	The feed to convert
	 * @return	A Document containing the XML
	 * @throws ParserConfigurationException	If the XML Parser couldn't be built
	 */
	public static Document toXML(Feed feed) throws ParserConfigurationException
	{
		Document d = createDocument();
		createFeed(d, feed);
		return d;
	}
	
	
	/**
	 * Convert an XML DOM Document into a Feed
	 * @param d The document to convert
	 * @return	The feed resulting from the conversion
	 * @throws ParseException	If timestamps are incorrectly formatted.
	 * @throws URISyntaxException	If a URI is incorrectly formatted.
	 */
	public static Feed fromXML(Document d) throws ParseException, URISyntaxException
	{
		return fromXML(d.getDocumentElement());
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
	 * Converts an element containing an ISO8601 (Schema) Timestamp into a Date
	 * @param e The element to convert
	 * @return A Date object representing this date
	 * @throws ParseException If the string is not a valid timestamp
	 */
	public static Date getDate(Element e) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat(ISO8601TS);
		return sdf.parse(getString(e));
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
	 * Converts an element containing a person into a Person object
	 * @param e	The element representing the person
	 * @return	The Person
	 * @throws URISyntaxException
	 */
	public static Person parsePerson(Element e) throws URISyntaxException
	{
		String name = null, uri = null, email = null;
		for (Node c = e.getFirstChild(); c != null; c = c.getNextSibling())
		{	if (c.getNodeType() == Node.ELEMENT_NODE &&
				ATOMNS.equals(c.getNamespaceURI())
			)
			{	e = (Element)c;
				
				if ("name".equals(e.getLocalName()))
					name = getString(e);
				else if ("uri".equals(e.getLocalName()))
					uri = getString(e);
				else if ("email".equals(e.getLocalName()))
					email = getString(e);
			}
		}
		Person p = new PersonImpl(name, email);
		if (!isEmpty(uri))
			p.setWebSite(new URI(uri));
		
		return p;
	}
	
	/**
	 * Convert an element containing a link into a link
	 * @param e The element containing the link
	 * @return A Link
	 * @throws URISyntaxException if the URI is not valid
	 */
	public static Link parseLink(Element e) throws URISyntaxException
	{
		String
			href = e.getAttribute("href"),
			language = e.getAttribute("language"),
			mimeType = e.getAttribute("type"),
			rel = e.getAttribute("rel"),
			title = e.getAttribute("title"),
			length = e.getAttribute("length");
		
		Link l = new LinkImpl(new URI(href), rel, mimeType);
		if (!isEmpty(length))
			l.setLength(Integer.parseInt(length));
		if (!isEmpty(language))
			l.setLanguage(language);
		if (!isEmpty(title))
			l.setTitle(title);
		return l;
		
	}
	
	/**
	 * Convert an element containing a category into a Category object
	 * @param e The element containing the category
	 * @return	The Category
	 * @throws URISyntaxException If the scheme URI is not valid
	 */
	public static Category parseCategory(Element e) throws URISyntaxException
	{
		String term = e.getAttribute("term"),
			   scheme = e.getAttribute("scheme"),
			   label = e.getAttribute("label"),
			   text = getString(e);
		
		Category c = new CategoryImpl(term, label, new URI(scheme));
		if (!isEmpty(text))
			c.setText(text);
		return c;
	}
	
	/**
	 * Convert an element containing an Entry in an atom feed into an Entry object
	 * @param e	The element containing the entry
	 * @return	The Entry object
	 * @throws ParseException	If a timestamp is incorrectly structured
	 * @throws URISyntaxException	If a URI is not valid.
	 */
	public static Entry parseEntry(Element e) throws ParseException, URISyntaxException
	{
		Entry entry = new EntryImpl();
		
		for (Node c = e.getFirstChild(); c != null; c = c.getNextSibling())
		{
			if (c.getNodeType() == Node.ELEMENT_NODE)
			{
				e = (Element)c;
				
				if (ATOMNS.equals(e.getNamespaceURI()))
				{
					String name = e.getLocalName();
					// This is an element in the atom namespace, it should be one of the following
					if ("author".equals(name))
						entry.getAuthors().add(parsePerson(e));
					else if ("category".equals(name))
						entry.getCategories().add(parseCategory(e));
					else if ("contributor".equals(name))
						entry.getContributors().add(parsePerson(e));
					else if ("generator".equals(name))
						entry.setGenerator(getString(e));
					else if ("id".equals(name))
						entry.setId(getURI(e));
					else if ("link".equals(name))
						entry.getLinks().add(parseLink(e));
					else if ("rights".equals(name))
						entry.setRights(getString(e));
					else if ("title".equals(name))
						entry.setTitle(getString(e));
					else if ("summary".equals(name))
						entry.setSummary(getString(e));
					else if ("updated".equals(name))
						entry.setUpdated(getDate(e));
					else if ("published".equals(name))
						entry.setUpdated(getDate(e));
					/*
			                 & atomContent?
			                 & atomSource?
			        */       
				}
			}
			
		}
		return entry;
	}
	
	/**
	 * Convert an element containing an atom feed into a Feed object
	 * @param e The element containing the feed
	 * @return The feed object
	 * @throws ParseException	If a timestamp is incorrectly structured
	 * @throws URISyntaxException	If a URI is not valid.
	 */
	public static Feed fromXML(Element e) throws ParseException, URISyntaxException 
	{
		if (!ATOMNS.equals(e.getNamespaceURI()) ||
			!"feed".equals(e.getLocalName()) )
			return null;
		
		Feed feed = new FeedImpl();

		for (Node c = e.getFirstChild(); c != null; c = c.getNextSibling())
		{
			if (c.getNodeType() == Node.ELEMENT_NODE)
			{
				e = (Element)c;
				
				if (ATOMNS.equals(e.getNamespaceURI()))
				{
					String name = e.getLocalName();
					// This is an element in the atom namespace, it should be one of the following
					if ("author".equals(name))
						feed.getAuthors().add(parsePerson(e));
					else if ("category".equals(name))
						feed.getCategories().add(parseCategory(e));
					else if ("contributor".equals(name))
						feed.getContributors().add(parsePerson(e));
					else if ("generator".equals(name))
						feed.setGenerator(getString(e));
					else if ("icon".equals(name))
						feed.setIcon(getURI(e));
					else if ("id".equals(name))
						feed.setId(getURI(e));
					else if ("link".equals(name))
						feed.getLinks().add(parseLink(e));
					else if ("logo".equals(name))
						feed.setLogo(getURI(e));
					else if ("rights".equals(name))
						feed.setRights(getString(e));
					else if ("title".equals(name))
						feed.setTitle(getString(e));
					else if ("subtitle".equals(name))
						feed.setSubtitle(getString(e));
					else if ("updated".equals(name))
						feed.setUpdated(getDate(e));
					else if ("entry".equals(name))
						feed.getEntries().add(parseEntry(e));
				}
			}
			
		}
		return feed;
	}

	/**
	 * Returns true if the string is null or empty
	 * @param string	The string to test
	 * @return	true if the string is null or empty, false otherwise
	 */
	public final static boolean isEmpty(String string)
	{
		return string == null || string.length() == 0;
	}
	
	/**
	 * Create a simple ATOM element containing text.
	 * @param d	The document where the element should live
	 * @param elemName	The name of the element
	 * @param value The value to store in the element
	 * @return	A DOM element with the given name containing a 
	 * text child with the specified value
	 */
	
	public final static Element createSimple(Document d, String elemName, Object value)
	{
		if (value == null)
			return null;
		
		Element e = d.createElementNS(d.getDocumentElement().getNamespaceURI(), elemName);
		if (value instanceof Date)
		{	SimpleDateFormat sdf = new SimpleDateFormat(ISO8601TS);
			e.appendChild(d.createTextNode(sdf.format((Date)value)));
		}
		else
			e.appendChild(d.createTextNode(value.toString()));
		return e;
	}
	
	/**
	 * Create an element representing a Person
	 * @param d	The document where the element will be created
	 * @param name	The name of the element to create
	 * @param p	The person to represent
	 * @return	An element representing that person
	 */
	public static Element createPerson(Document d, String name, Person p)
	{
		if (p == null)
			return null;
		
		Element e = d.createElementNS(ATOMNS, name);
		append(e, createSimple(d, "name", p.getName()));
		append(e, createSimple(d, "uri", p.getWebSite()));
		append(e, createSimple(d, "email", p.getEmail()));
		return e;
	}
	/**
	 * Create an element representing a Category
	 * @param d	The document where the element will be created
	 * @param c The category to represent
	 * @return An element representing that category.
	 */
	public static Element createCategory(Document d, Category c)
	{
		if (c == null)
			return null;
		
		Element e = d.createElementNS(ATOMNS, "category");
		if (!isEmpty(c.getTerm()) )
			e.setAttribute("term", c.getTerm());
		if (!isEmpty(c.getLabel()) )
			e.setAttribute("label", c.getLabel());
		if (c.getScheme() != null)
			e.setAttribute("scheme", c.getScheme().toString());
		if (!isEmpty(c.getText()) )
			e.appendChild(d.createTextNode(c.getText()));
		return e;
	}
	
	/**
	 * Create an element representing a link
	 * @param d	The document where the element will be created
	 * @param l The link to represent
	 * @return An element representing that link.
	 */
	public static Element createLink(Document d, Link l)
	{
		if (l == null)
			return null;
		
		Element e = d.createElementNS(ATOMNS, "link");
		
		if (l.getHref() != null)
			e.setAttribute("href", l.getHref().toString());
		if (!isEmpty(l.getRel()))
			e.setAttribute("rel", l.getRel());
		if (!isEmpty(l.getMimeType()))
			e.setAttribute("type", l.getMimeType());
		if (!isEmpty(l.getLanguage()))
			e.setAttribute("hreflang", l.getLanguage());
		if (!isEmpty(l.getTitle()))
			e.setAttribute("title", l.getTitle());
		if (l.getLength() != 0)
			e.setAttribute("length", "" + l.getLength());
		
		return e;
	}

	
	/**
	 * Create an element representing a feed
	 * @param d	The document where the element will be created
	 * @param feed The feed to represent
	 * @return An element representing that feed.
	 */
	public static Element createFeed(Document d, Feed feed)
	{
		if (feed == null)
			return null;
		
		Element e;
		e = d.createElementNS(ATOMNS, "feed");
		e.setAttribute("xmlns", ATOMNS);
		d.appendChild(e);
		
		append(e, createSimple(d, "id", feed.getId()));
		append(e, createSimple(d, "title", feed.getTitle()));
		append(e, createSimple(d, "subtitle", feed.getSubtitle()));
		append(e, createSimple(d, "icon", feed.getIcon()));
		append(e, createSimple(d, "logo", feed.getLogo()));
		append(e, createSimple(d, "generator", feed.getGenerator()));
		append(e, createSimple(d, "rights", feed.getRights()));
		append(e, createSimple(d, "updated", feed.getUpdated()));
		
		for (Link l: feed.getLinks())
			append(e, createLink(d, l));
		
		for (Person p : feed.getAuthors())
			append(e, createPerson(d, "author", p));
		for (Person p : feed.getContributors())
			append(e, createPerson(d, "contributor", p));

		for (Category c: feed.getCategories())
			append(e, createCategory(d, c));
		
		for (Entry entry: feed.getEntries())
			append(e, createEntry(d, entry));

		return e;
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
	
	public static Element createContent(Document d, Entry entry)
	{
		Element content = d.createElementNS(ATOMNS, "content");
		InputStream contentBody  = entry.getContent();
		URI contentSrc  = entry.getContentSrc();
		String contentType = entry.getContentType();
		content.setAttribute("type", contentType);
		
		if (contentBody == null)
		{	if (contentSrc != null)
				content.setAttribute("src", contentSrc.toString());
		}
		else
		{	if (contentType.equals("text") || contentType.equals("html"))
				content.appendChild(d.createTextNode(toString(contentBody)));
			else if (contentType.equals("xhtml"))
			{	// TODO: This could be better implemented
				Vector<InputStream> streams = new Vector<InputStream>();
				streams.add(new ByteArrayInputStream("<div xmlns='http://www.w3.org/1999/xhtml'>".getBytes()));
				streams.add(contentBody);
				streams.add(new ByteArrayInputStream("</div>".getBytes()));
				Document doc2 = getDocument(new SequenceInputStream(streams.elements()));
				content.appendChild(d.importNode(doc2, true));
			}
			else if (contentType.endsWith("/xml") || contentType.endsWith("+xml"))
			{	Document doc2 = getDocument(contentBody);
				content.appendChild(d.importNode(doc2.getDocumentElement(), true));
			}
			else if (contentType.startsWith("text/"))
				content.appendChild(d.createTextNode(toString(contentBody)));
			else
				content.appendChild(d.createTextNode(Base64.encodeBase64String(toByteArray(contentBody))));
		}
		return content;
	}
	
	/**
	 * Create an element representing a atom entry
	 * @param d	The document in which to create the element
	 * @param entry	The entry to represent
	 * @return An element representing that atom feed entry
	 */
	public static Element createEntry(Document d, Entry entry) 
	{
		Element e = d.createElementNS(ATOMNS, "entry");
		append(e, createSimple(d, "id", entry.getId()));
		append(e, createSimple(d, "title", entry.getTitle()));
		append(e, createSimple(d, "summary", entry.getSummary()));
		append(e, createSimple(d, "generator", entry.getGenerator()));
		append(e, createSimple(d, "rights", entry.getRights()));
		append(e, createSimple(d, "updated", entry.getUpdated()));
		append(e, createSimple(d, "published", entry.getPublished()));

		// We create the content element here, because it could
		// result in the generation of a new link if there
		// is also a content link.  But we'll add it at the
		// end of the atom feed.  There's no particular order
		// of elements requirement in atom, so we could add it 
		// here, but I like content to appear at the end.
		Element content = createContent(d, entry);
		
		// If we happen to have both content and a link, then create
		// a <link type="contentType" rel="alternate" src="contentURI"/> 
		// element as well to support robust output.
		if (entry.getContentSrc() != null && entry.getContent() != null)
		{	LinkImpl l = new LinkImpl(entry.getContentSrc(), "alternate", entry.getContentType());
			append(e, createLink(d, l));
		}
		
		for (Link l: entry.getLinks())
			append(e, createLink(d, l));

		for (Person p : entry.getAuthors())
			append(e, createPerson(d, "author", p));
		for (Person p : entry.getContributors())
			append(e, createPerson(d, "contributor", p));

		for (Category c: entry.getCategories())
			append(e, createCategory(d, c));
		
		e.appendChild(content);
		
		return e;
	}
	
	public static Document getDocument(InputSource s)
	{
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			return db.parse(s);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO: Fix Error Handling
		return null;
	}
	
	public static Document getDocument(InputStream is)
	{
		return getDocument(new InputSource(is));
	}
	
	public static Document getDocument(byte[] data)
	{
		return getDocument(new InputSource(new ByteArrayInputStream(data)));
	}
	
	public static Document getDocument(String s)
	{
		return getDocument(new InputSource(new StringReader(s)));
	}
	
	public static byte[] toByteArray(InputStream s)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int length = 0;
		
		try
		{
			while ((length = s.read(buffer)) > 0)
				bos.write(buffer, 0, length);
		}
		catch (IOException ioex)
		{
			return null;
		}
		return bos.toByteArray();
	}
	
	public static String toString(InputStream s)
	{
		
		try
		{
			byte[] buffer = toByteArray(s);
			// TODO For now, assume all content encoded in UTF-8
			// we could detect the encoding from the byte array
			return new String(buffer, "UTF-8");
		}
		catch (UnsupportedEncodingException ex)
		{
			throw new Error("UTF-8 Not Supported in this VM", ex);
		}
	}
}
