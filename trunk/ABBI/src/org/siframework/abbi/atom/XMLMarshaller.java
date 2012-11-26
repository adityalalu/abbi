package org.siframework.abbi.atom;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.siframework.abbi.atom.impl.*;
import org.siframework.abbi.utility.DT;
import org.siframework.abbi.utility.IO;
import org.siframework.abbi.utility.XML;
import org.w3c.dom.*;

/**
 * A utility class to convert Feeds to XML and back.
 * @author Keith W. Boone
 * @see Feed
 */
public class XMLMarshaller {
	// The Atom Namespace
	public static final String ATOMNS = "http://www.w3.org/2005/Atom";

	/** Convert a feed into XML via a Result
	 * @param feed	The feed to convert
	 * @param r Where to pu the resulting converted output.
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public static void toXML(Feed feed, Result r) throws TransformerException 
	{
		DOMSource source = new DOMSource(toXML(feed));
		XML.getTransformer().transform(source, r);
	}
	
	/**
	 * Convert an Atom feed to an XML DOM Document
	 * @param feed	The feed to convert
	 * @return	A Document containing the XML
	 * @throws ParserConfigurationException	If the XML Parser couldn't be built
	 */
	public static Document toXML(Feed feed)
	{
		Document d = XML.createDocument();
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
	public static Feed fromXML(Document d) throws ParseException
	{
		return fromXML(d.getDocumentElement());
	}
	
	/**
	 * Converts an element containing an ISO8601 (Schema) Timestamp into a Date
	 * @param e The element to convert
	 * @return A Date object representing this date
	 * @throws ParseException If the string is not a valid timestamp
	 */
	public static Date getDate(Element e) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat(DT.ISO8601XML);
		return sdf.parse(XML.getString(e));
	}
	
	/**
	 * Converts an element containing a person into a Person object
	 * @param e	The element representing the person
	 * @return	The Person
	 */
	public static Person parsePerson(Element e)
	{
		String name = null, uri = null, email = null;
		for (Node c = e.getFirstChild(); c != null; c = c.getNextSibling())
		{	if (c.getNodeType() == Node.ELEMENT_NODE &&
				ATOMNS.equals(c.getNamespaceURI())
			)
			{	e = (Element)c;
				
				if ("name".equals(e.getLocalName()))
					name = XML.getString(e);
				else if ("uri".equals(e.getLocalName()))
					uri = XML.getString(e);
				else if ("email".equals(e.getLocalName()))
					email = XML.getString(e);
			}
		}
		Person p = new PersonImpl(name, email);
		if (!isEmpty(uri))
			p.setWebSite(uri);
		
		return p;
	}
	
	/**
	 * Convert an element containing a link into a link
	 * @param e The element containing the link
	 * @return A Link
	 */
	public static Link parseLink(Element e)
	{
		String
			href = e.getAttribute("href"),
			language = e.getAttribute("language"),
			mimeType = e.getAttribute("type"),
			rel = e.getAttribute("rel"),
			title = e.getAttribute("title"),
			length = e.getAttribute("length");
		
		Link l = new LinkImpl(href, rel, mimeType);
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
	 */
	public static Category parseCategory(Element e)
	{
		String term = e.getAttribute("term"),
			   scheme = e.getAttribute("scheme"),
			   label = e.getAttribute("label"),
			   text = XML.getString(e);
		
		Category c = new CategoryImpl(term, label, scheme);
		if (!isEmpty(text))
			c.setText(text);
		return c;
	}
	
	/**
	 * Convert an element containing an Entry in an atom feed into an Entry object
	 * @param e	The element containing the entry
	 * @return	The Entry object
	 * @throws ParseException	If a timestamp is incorrectly structured
	 */
	public static Entry parseEntry(Element e) throws ParseException
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
						entry.setGenerator(XML.getString(e));
					else if ("id".equals(name))
						entry.setId(XML.getString(e));
					else if ("link".equals(name))
						entry.getLinks().add(parseLink(e));
					else if ("rights".equals(name))
						entry.setRights(XML.getString(e));
					else if ("title".equals(name))
						entry.setTitle(XML.getString(e));
					else if ("summary".equals(name))
						entry.setSummary(XML.getString(e));
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
	 */
	public static Feed fromXML(Element e) throws ParseException
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
						feed.setGenerator(XML.getString(e));
					else if ("icon".equals(name))
						feed.setIcon(XML.getString(e));
					else if ("id".equals(name))
						feed.setId(XML.getString(e));
					else if ("link".equals(name))
						feed.getLinks().add(parseLink(e));
					else if ("logo".equals(name))
						feed.setLogo(XML.getString(e));
					else if ("rights".equals(name))
						feed.setRights(XML.getString(e));
					else if ("title".equals(name))
						feed.setTitle(XML.getString(e));
					else if ("subtitle".equals(name))
						feed.setSubtitle(XML.getString(e));
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
		{	SimpleDateFormat sdf = new SimpleDateFormat(DT.ISO8601XML);
			e.appendChild(d.createTextNode(sdf.format((Date)value)));
		}
		else 
		{	String s = value.toString();
			if (s.length() == 0)
				return null;
			e.appendChild(d.createTextNode(s));
		}
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
		XML.append(e, createSimple(d, "name", p.getName()));
		XML.append(e, createSimple(d, "uri", p.getWebSite()));
		XML.append(e, createSimple(d, "email", p.getEmail()));
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
		
		XML.append(e, createSimple(d, "id", feed.getId()));
		XML.append(e, createSimple(d, "title", feed.getTitle()));
		XML.append(e, createSimple(d, "subtitle", feed.getSubtitle()));
		XML.append(e, createSimple(d, "icon", feed.getIcon()));
		XML.append(e, createSimple(d, "logo", feed.getLogo()));
		XML.append(e, createSimple(d, "generator", feed.getGenerator()));
		XML.append(e, createSimple(d, "rights", feed.getRights()));
		XML.append(e, createSimple(d, "updated", feed.getUpdated()));
		
		for (Link l: feed.getLinks())
			XML.append(e, createLink(d, l));
		
		for (Person p : feed.getAuthors())
			XML.append(e, createPerson(d, "author", p));
		for (Person p : feed.getContributors())
			XML.append(e, createPerson(d, "contributor", p));

		for (Category c: feed.getCategories())
			XML.append(e, createCategory(d, c));
		
		for (Entry entry: feed.getEntries())
			XML.append(e, createEntry(d, entry));

		return e;
	}
	
	/**
	 * Create the content element in the Atom feed.
	 * @param d The XML Document being updated.
	 * @param entry  The Entry containing the content
	 * @return An XML Element containing a &lt;content&gt; tag for use in the ATOM results.
	 */
	public static Element createContent(Document d, Entry entry)
	{
		Element content = d.createElementNS(ATOMNS, "content");
		InputStream contentBody  = entry.getContent();
		String contentSrc  = entry.getContentSrc();
		String contentType = entry.getContentType();
		content.setAttribute("type", contentType);
		
		if (contentBody == null)
		{	if (contentSrc != null)
				content.setAttribute("src", contentSrc.toString());
		}
		else
		{	if (contentType.equals("text") || contentType.equals("html"))
				content.appendChild(d.createTextNode(IO.toString(contentBody)));
			else if (contentType.equals("xhtml"))
			{	
				Vector<InputStream> r = new Vector<InputStream>();
				r.add(IO.toUTF8Stream("<div xmlns='http://www.w3.org/1999/xhtml'>"));
				r.add(contentBody);
				r.add(IO.toUTF8Stream("</div>"));
				Document doc2 = XML.getDocument(new SequenceInputStream(r.elements()));
				content.appendChild(d.importNode(doc2, true));
			}
			else if (contentType.endsWith("/xml") || contentType.endsWith("+xml"))
			{	Document doc2 = XML.getDocument(contentBody);
				content.appendChild(d.importNode(doc2.getDocumentElement(), true));
			}
			else if (contentType.startsWith("text/"))
				content.appendChild(d.createTextNode(IO.toString(contentBody)));
			else
			{	// We'll have to assume binary content on a feed is already
				// base 64 encoded.
				content.appendChild(d.createTextNode(IO.toString(contentBody)));
			}
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
		XML.append(e, createSimple(d, "id", entry.getId()));
		XML.append(e, createSimple(d, "title", entry.getTitle()));
		XML.append(e, createSimple(d, "summary", entry.getSummary()));
		XML.append(e, createSimple(d, "generator", entry.getGenerator()));
		XML.append(e, createSimple(d, "rights", entry.getRights()));
		XML.append(e, createSimple(d, "updated", entry.getUpdated()));
		XML.append(e, createSimple(d, "published", entry.getPublished()));

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
			XML.append(e, createLink(d, l));
		}
		
		for (Link l: entry.getLinks())
			XML.append(e, createLink(d, l));

		for (Person p : entry.getAuthors())
			XML.append(e, createPerson(d, "author", p));
		for (Person p : entry.getContributors())
			XML.append(e, createPerson(d, "contributor", p));

		for (Category c: entry.getCategories())
			XML.append(e, createCategory(d, c));
		
		e.appendChild(content);
		
		return e;
	}
}
