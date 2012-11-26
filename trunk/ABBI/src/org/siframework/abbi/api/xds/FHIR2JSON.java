package org.siframework.abbi.api.xds;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.siframework.abbi.utility.XML;
import org.w3c.dom.*;
import com.google.gson.*;

/** 
 * This class converts a FHIR xdsentry in XML format to JSON format
 * @author 212042380
 *
 */
public class FHIR2JSON 
{
	
	private static class ResourceModel {
		protected String resourceName;
		protected ResourceModel[] children = new ResourceModel[0];
		protected Collection<String> repeatable;
	
		public ResourceModel(String resourceName, Collection<String> repeatable)
		{
			this.resourceName = resourceName;
			this.repeatable = repeatable;
		}
		public ResourceModel(String resourceName, ResourceModel children[], Collection<String> repeatable)
		{
			this.resourceName = resourceName;
			this.children = children;
			this.repeatable = repeatable;
		}
		
		public ResourceModel getChildModel(String name)
		{	for (ResourceModel child: children)
				if (name.equals(child.resourceName))
					return child;
			return null;
		}
		
		public String getName()
		{
			return resourceName;
		}
		
		public boolean isRepeatable(String name)
		{
			return repeatable.contains(name);
		}
	};
	private static String xdsentryRepeatable[] = {
		"confidentiality", "folder", "author", "extension"
	};
	private static String authorRepeatable[] = {
		"role", "specialty", "institution", "contact" 
	};
	
	private static String datatypeRepeatable[] = {
		"part"
	};
	
	private static Set<String> NONE = Collections.emptySet();
	private static Collection<String> repeatableDatatypes = Arrays.asList(datatypeRepeatable);
	
	private static ResourceModel INSTITUTION = new ResourceModel("institution", NONE);
	private static ResourceModel AUTHORCHILDREN[] = { INSTITUTION };
	private static ResourceModel AUTHOR = 
		new ResourceModel("author", AUTHORCHILDREN, Arrays.asList(authorRepeatable));
	private static ResourceModel AUTHENTICATOR = new ResourceModel("authenticator", NONE);
	private static ResourceModel SERVICE = new ResourceModel("service", NONE);
	private static ResourceModel XDSENTRYCHILDREN[] = { AUTHOR, AUTHENTICATOR, SERVICE };
	private static ResourceModel XDSENTRY = 
		new ResourceModel("XdsEntry", XDSENTRYCHILDREN, Arrays.asList(xdsentryRepeatable));
	
	public static class ElementAdapter implements JsonSerializer<Element> {

		@Override
		public JsonElement serialize(Element e, Type typeOfSrc, JsonSerializationContext context) 
		{
			// This should only be called at the top of the document tree, so it should be
			// an XDSENTRY
			JsonObject obj = new JsonObject();
			obj.add(XDSENTRY.getName(), serialize(e, true, XDSENTRY));
			return obj;
		}
		
		public JsonElement serialize(Element e, boolean supportsId, ResourceModel model)
		{
			JsonObject obj = new JsonObject();
			
			// Handle the id attribute
			if (supportsId && e.hasAttribute("id"))
				obj.add("_id", new JsonPrimitive(e.getAttribute("id")));
		
			// Handle the dataAbsentReason attribute
			if (e.hasAttribute("dataAbsentReason"))
			{	obj.add("dataAbsentReason", new JsonPrimitive(e.getAttribute("dataAbsentReason")));
				supportsId = true;
			}
			
			if ("div".equals(e.getLocalName()))
			{	// Transform the div content to a string
				
				try {
					return new JsonPrimitive(XML.asString(e, XML.PRETTY));
				} catch (TransformerException e1) {
					throw new Error("Error transforming DIV", e1);
				}
			}
		
			// If it is a resource, then it has child elements (by definition), and it supports 
			// both id and dataAbsentReason, and each child element can also support id and 
			// data absent reason
			if (model != null)
			{	
				supportsId = true;
				
				// iterate over the elements and output each one
				for (Element child = XML.firstChildElement(e); child != null; child = XML.nextSiblingElement(child))
				{	String childName = child.getLocalName();
					ResourceModel childModel = model.getChildModel(childName);
					if (model.isRepeatable(childName))
					{
						Element lastChild = null;
						JsonArray array = new JsonArray();
						obj.add(childName, array);
						do
						{	array.add(serialize(child, true, childModel));
							lastChild = child;
							child = XML.nextSiblingElement(child);
						}	while (child != null && childName.equals(child.getLocalName()) );
						child = lastChild;
					}
					else
						obj.add(childName, serialize(child, true, childModel));
				}
			}
			else if (XML.hasChildElements(e))
			{	
				for (Element child = XML.firstChildElement(e); child != null; child = XML.nextSiblingElement(child))
				{	String childName = child.getLocalName();
					if (repeatableDatatypes.contains(childName))
					{	Element lastChild = null;
						JsonArray array = new JsonArray();
						obj.add(childName, array);
						do
						{	array.add(serialize(child, false, null));
							lastChild = child;
							child = XML.nextSiblingElement(child);
						}	while (child != null && childName.equals(child.getLocalName()) );
						child = lastChild;
					}
					else
						obj.add(childName, serialize(child, false, null));
				}
			}
			// otherwise this element has NO FHIR element children
			// append a value attribute containing the non-empty text content.
			else
			{
				String value = XML.getString(e);
				JsonPrimitive prim = new JsonPrimitive(value);
				
				// If supportsId is true here, then the text content of this element
				// needs to go into the value attribute.  Otherwise, it just becomes 
				// a primitive.

				if (!supportsId)
					return prim;
				
				obj.add("value", prim);
			}
			return obj;
		}
		
	};
	public static String toJson(Document d)
	{
		Gson gson = new GsonBuilder().
			registerTypeAdapter(Element.class, new ElementAdapter()).
			create();
		return gson.toJson(d.getDocumentElement(), Element.class);
	}
}
