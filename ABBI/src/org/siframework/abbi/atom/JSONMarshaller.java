package org.siframework.abbi.atom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import org.openhealthtools.ihe.common.hl7v2.CX;
import org.siframework.abbi.atom.impl.LinkImpl;
import org.siframework.abbi.utility.DT;
import org.siframework.abbi.utility.IO;
import org.siframework.abbi.utility.XML;
import org.w3c.dom.Element;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/** 
 * Support serialization of an Atom Feed to the 
 * {@link <a href='http://www.hl7.org/implement/standards/fhir/fhir-book.htm#json.json-bundles'>FHIR JSON 
 * Format</a>}.  This class uses the {@link <a href=''>Google Gson library</a>} to serialize to JSON.
 * 
 * @author Keith W. Boone
 */
public class JSONMarshaller {
	
	/**
	 * An adapter to serialize objects implementing the {@link Feed} interface to JSon. 
	 * @author Keith W. Boone
	 */
	private static class FeedAdapter implements JsonSerializer<Feed>
	{
		@Override
		public JsonElement serialize(Feed src, Type typeOfSrc, JsonSerializationContext context) 
		{
			JsonObject feed = new JsonObject();
			add(feed, "id", src.getId());
			
			add(feed, "id", src.getId());
			add(feed, "title", src.getTitle());
			add(feed, "subtitle", src.getSubtitle());
			add(feed, "icon", src.getIcon());
			add(feed, "logo", src.getLogo());
			add(feed, "generator", src.getGenerator());
			add(feed, "rights", src.getRights());
			add(feed, "updated", DT.formatISODate(src.getUpdated(), true));
			
			JsonArray array = null;
			feed.add("links", array = new JsonArray());
			for (Link l: src.getLinks())
				array.add(context.serialize(l, Link.class));
			
			feed.add("authors", array = new JsonArray());
			for (Person p : src.getAuthors())
				array.add(context.serialize(p, Person.class));

			feed.add("contributors", array = new JsonArray());
			for (Person p : src.getContributors())
				array.add(context.serialize(p, Person.class));

			feed.add("categories", array = new JsonArray());
			for (Category c: src.getCategories())
				array.add(context.serialize(c, Category.class));
			
			feed.add("entries", array = new JsonArray());
			for (Entry entry: src.getEntries())
				array.add(context.serialize(entry, Entry.class));
			
			return feed;
		}
		
	};
	/**
	 * An adapter to serialize objects implementing the {@link Entry} interface to JSon. 
	 * @author Keith W. Boone
	 */
	private static class EntryAdapter implements JsonSerializer<Entry>
	{

		@Override
		public JsonElement serialize(Entry src, Type typeOfSrc, JsonSerializationContext context) 
		{
			JsonObject entry = new JsonObject();

			add(entry, "id", src.getId());
			add(entry, "title", src.getTitle());
			add(entry, "summary", src.getSummary());
			add(entry, "generator", src.getGenerator());
			add(entry, "rights", src.getRights());
			add(entry, "updated", DT.formatISODate(src.getUpdated(), true));
			add(entry, "published", DT.formatISODate(src.getPublished(), true));
			
			JsonArray array = null;
			entry.add("links", array = new JsonArray());

			// If we happen to have both content and a link, then create
			// a <link type="contentType" rel="alternate" src="contentURI"/> 
			// element as well to support robust output.
			if (src.getContentSrc() != null && src.getContent() != null)
			{	LinkImpl l = new LinkImpl(src.getContentSrc(), "alternate", src.getContentType());
				array.add(context.serialize(l, Link.class));
			}
			for (Link l: src.getLinks())
				array.add(context.serialize(l, Link.class));
			
			entry.add("authors", array = new JsonArray());
			for (Person p : src.getAuthors())
				array.add(context.serialize(p, Person.class));

			entry.add("contributors", array = new JsonArray());
			for (Person p : src.getContributors())
				array.add(context.serialize(p, Person.class));

			entry.add("categories", array = new JsonArray());
			for (Category c: src.getCategories())
				array.add(context.serialize(c, Category.class));
			
			// This assumes Content is already in JSON, thus the back-end
			// needs to generate content in the appropriate output format
			InputStream is = src.getContent();
			if (is != null)
			{	JsonParser p = new JsonParser();
				JsonElement e = p.parse(new InputStreamReader(is));
				entry.add("content", e);
			}
			
			return entry;
		}
		
	};
	
	/**
	 * An adapter to serialize objects implementing the {@link Person} interface to JSon. 
	 * @author Keith W. Boone
	 */
	private static class PersonAdapter implements JsonSerializer<Person>
	{
		@Override
		public JsonElement serialize(Person src, Type typeOfSrc, JsonSerializationContext context) 
		{
			JsonObject person = new JsonObject();
			add(person, "name", src.getName());
			add(person, "uri", src.getWebSite());
			add(person, "email", src.getEmail());
			return person;
		}
		
	};
	/**
	 * An adapter to serialize objects implementing the {@link Category} interface to JSon. 
	 * @author Keith W. Boone
	 */
	private static class CategoryAdapter implements JsonSerializer<Category>
	{
		@Override
		public JsonElement serialize(Category src, Type typeOfSrc, JsonSerializationContext context) 
		{
			JsonObject cat = new JsonObject();
			add(cat, "term", src.getTerm());
			add(cat, "label", src.getLabel());
			add(cat, "scheme", src.getScheme());
			add(cat, "value", src.getText());
			return cat;
		}
		
	};
	/**
	 * An adapter to serialize objects implementing the {@link Link} interface to JSon. 
	 * @author Keith W. Boone
	 */
	private static class LinkAdapter implements JsonSerializer<Link>
	{
		@Override
		public JsonElement serialize(Link l, Type typeOfSrc, JsonSerializationContext context) 
		{
			JsonObject link = new JsonObject();
			add(link, "href", l.getHref());
			add(link, "rel", l.getRel());
			add(link, "type", l.getMimeType());
			add(link, "hreflang", l.getLanguage());
			add(link, "title", l.getLength());
			if (l.getLength() != 0)
				link.add("title", new JsonPrimitive(l.getLength()));
			return link;
		}
		
	};
	
	/**
	 * Add a primitive named property to a JsonObject if it is non-null.
	 * @param obj	The JsonObject to add the property to
	 * @param name	The name of the property
	 * @param x		The Object to add to the tree when non-null (and non-empty).
	 */
	private static final void add(JsonObject obj, String name, Object x)
	{	if (x != null)
		{	String value = x.toString();
			if (value.length() != 0)
				obj.add(name, new JsonPrimitive(value));
		}
	}
	
	/** Convert a feed into JSON
	 * @param feed	The feed to convert
	 * @return A string containing the JSON format of this feed.
	 */
	public static String toJson(Feed feed) 
	{
		Gson gson = new GsonBuilder()
	     .registerTypeAdapter(Feed.class, new FeedAdapter())
	     .registerTypeAdapter(Entry.class, new EntryAdapter())
	     .registerTypeAdapter(Person.class, new PersonAdapter())
	     .registerTypeAdapter(Link.class, new LinkAdapter())
	     .registerTypeAdapter(Category.class, new CategoryAdapter())
	     .setPrettyPrinting()
	     .create();
		return gson.toJson(feed, Feed.class);	
	}
	
	/** Convert a feed into a JSON Object tree
	 * @param feed	The feed to convert
	 * @return A Json Object containing the information in this feed.
	 */
	public static JsonElement toJsonTree(Feed feed)
	{
		Gson gson = new GsonBuilder()
	     .registerTypeAdapter(Feed.class, new FeedAdapter())
	     .setPrettyPrinting()
	     .create();
		return gson.toJsonTree(feed, Feed.class);	
		
	}
}
