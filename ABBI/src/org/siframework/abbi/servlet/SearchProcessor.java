package org.siframework.abbi.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.siframework.abbi.api.API;
import org.siframework.abbi.api.Logger;
import org.siframework.abbi.api.SearchParameters;
import org.siframework.abbi.api.Util;
import org.siframework.abbi.atom.Entry;
import org.siframework.abbi.atom.Feed;
import org.siframework.abbi.atom.Link;
import org.siframework.abbi.atom.XML;
import org.siframework.abbi.atom.impl.CategoryImpl;
import org.siframework.abbi.atom.impl.FeedImpl;
import org.siframework.abbi.atom.impl.LinkImpl;
import org.w3c.dom.Document;

/**
 * This class implements the basic framework of a search processor.  It contains a number
 * of utility methods to get and validate request parameters.
 * @author 212042380
 *
 */
public class SearchProcessor {
	
	// Parameter Names to use.
	private final static String
		PATIENTID = "patientID",
		MIMETYPE = "mimeType",
		FORMAT = "format",
		CLASS = "class",
		CONTENT = "content",
		SERVICESTARTTIMEFROM = "serviceStartTimeFrom",
		SERVICESTARTTIMETO = "serviceStartTimeTo",
		SERVICESTOPTIMEFROM = "serviceStopTimeFrom",
		SERVICESTOPTIMETO = "serviceStopTimeTo",
		CREATIONTIMEFROM = "creationTimeFrom",
		CREATIONTIMETO = "creationTimeTo",
		COUNT = "count",
		N = "n";
	
	public final static String _XML_CONVERTS_2[] = { "text/html", "text/plain" };
	public final static HashSet<String> 
			XML_CONVERTS_2 = 
				new HashSet<String>(Arrays.asList(_XML_CONVERTS_2));
	
	// Set STRICT to false to turn off strict API checking
	// STRICT mode is used for Client testing
	private boolean strict = true;

	public final static String ABBI = "http://www.siframework.org/ABBI";
	private final static String EMPTY[] = new String[0];

	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final ServletContext context;
	private final Logger log;
	private final API searchAPI;
	private final Properties properties;

	private Map<String, List<String>> mimeTypeMap = new HashMap<String,List<String>>();
	
	public SearchProcessor(
		HttpServletRequest request, 
		HttpServletResponse response,
		ServletContext context,
		Properties properties,
		API searchAPI, 
		Logger log)
	{
		this.request = request;
		this.response = response;
		this.context = context;
		this.properties = properties;
		this.log = log;
		this.searchAPI = searchAPI;
		
		for (java.util.Map.Entry<Object, Object> p: properties.entrySet())
		{	
			String prop = (String) p.getKey();
			String key = null, value = (String)p.getValue();
			
			if (prop.startsWith("mimeType."))
			{	key = prop.substring(9);
				mimeTypeMap.put(key, Arrays.asList(value.split(" *, *")));
			}
		}
	}

	public boolean isStrict()
	{
		return strict;
	}
	
	public void setStrict(boolean strict)
	{
		this.strict = strict;
	}
	

	/**
	 * Gets a parameter that should appear only once by name.  Sends an SC_BAD_REQUEST
	 * error when running in STRICT mode if the parameter appears more than once.
	 * @param name The name of the parameter to retrieve.
	 * @return The parameter, or null if the parameter is not present.
	 */
	public String getParameter(String name)
	{	String values[] = request.getParameterValues(name);
	
		if (values == null)
			return null;
		
		// Generate an error in STRICT mode.  In normal mode,
		// just use the first value and ignore others
		if (isStrict() && values.length > 1)
			sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Only one value allowed for " + name);
		return values[0];
	}
	
	/**
	 * Gets a date parameter.  Uses getParameter() because date parameters can only be
	 * specified once.  Converts it to a Date value.  In STRICT mode, if the date value 
	 * is not correctly formatted, will generate an SC_BAD_REQUEST error.
	 * @param name  The name of the date parameter.
	 * @return The date parameter, or null if not present.
	 */
	public Date getISODateParameter(String name)
	{
		String value = getParameter(name);
		if (value == null)
			return null;
		
		Date date = Util.parseISODate(value);
		// In strict mode we check the date format.
		// Otherwise, we just pass it on to the back end
		// and let it deal with it.
		if (isStrict() && date == null)
			sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Date value "+name+" is malformed: " + value
			);
		
		return date;
	}
	
	/**
	 * Gets a date parameter.  Uses getParameter() because date parameters can only be
	 * specified once.  Converts it to a Date value by assuming it is an integer offset
	 * in seconds from January 1, 1970.  In STRICT mode, if the date value 
	 * is not correctly formatted, will generate an SC_BAD_REQUEST error.
	 * @param name  The name of the date parameter.
	 * @return The date parameter, or null if not present.
	 * @return A Date Object initialized from the parameter
	 */
	public Date getDateParameter(String name)
	{
		String value = getParameter(name);
		Date date = Util.parseDate(value);
		if (isStrict() && date == null)
			sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Date value "+name+" is malformed: " + value
			);
		return date;
	}
	
	/**
	 * Get an integer parameter, using getParameter() because integer parameters
	 * can only be specified once.  Converts to an integer value.  In STRICT mode
	 * if this is not a valid integer, generates an SC_BAD_REQUEST error.
	 * @param name  The name of the parameter
	 * @return The integer value of the parameter, or 0 if not present.
	 */
	public long getIntParameter(String name)
	{
		long value = -1;
		
		try
		{
			String sValue = getParameter(name);
			if (sValue == null)
				return 0;
			value = Long.parseLong(sValue);
		}
		catch (NumberFormatException ex)
		{
			if (isStrict())
			{	sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						"Integer value "+name+" is malformed: " + value
					);
				return -1;
			}
		}
		return value;
	}

	/**
	 * Get a set of parameters from a fixed list of values
	 * @param name The name of the parameter to retrieve
	 * @param values The set of legal values, or null if all values are OK.
	 * @return An array containing the values returned.
	 */
	public String[] getArrayParameter(String name, Set<String> values)
	{
		String[] results = request.getParameterValues(name);
		if (results == null)
			return EMPTY;
		
		if (isStrict() && values != null)
		{	for (String r : results)
			{	if (!values.contains(r))
				{	sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						name + " value " + r + " not in " + values
					);
					return null;
				}
			}
		}
		return results;
	}
	
	/**
	 * Send the specified error to the user agent in response to a request.
	 * @param sc The error code to return
	 * @param message A text message explaining the error.
	 */
	public void sendError(int sc, String message)
	{
		try
		{
			response.sendError(sc, message);
		}
		catch (IOException ex)
		{
			log.log("sendError", ex);
		}
	}
	
	public SearchParameters getAPIParameters()
	{
		SearchParameters p = new SearchParameters();
		// Now we get the various parameters
		p.setPatientID(getParameter(PATIENTID));

		// Get the content parameter.  If it is equal to "include"
		// then mark it as included, otherwise mark it as not included.
		
		p.setContentIncluded("include".equals(getParameter(CONTENT)));
		// Get coded parameters
		
		p.setClasses(getArrayParameter(CLASS, searchAPI.getSupportedClasses()));
		p.setFormat(getArrayParameter(FORMAT, searchAPI.getSupportedFormats()));
		p.setMimeType(getArrayParameter(MIMETYPE, null));
		
		// translate mimeType shortcuts.  These are always passed through
		// in the long form to the back end.
		String mimeType[] = p.getMimeType();
		if (mimeType != null)
			for (int i = 0; i < mimeType.length; i++)
			{	if (mimeTypeMap.containsKey(mimeType [i]))
				{	List<String> s = mimeTypeMap.get(mimeType[i]);
					mimeType[i] = s.get(0);
				}
			}
		
		// Get paging parameters
		p.setIndex((int)getIntParameter(N));
		p.setCount((int)getIntParameter(COUNT));
		
		// Get time parameters
		p.setServiceStartTimeFrom(getISODateParameter(SERVICESTARTTIMEFROM));
		p.setServiceStopTimeFrom(getISODateParameter(SERVICESTOPTIMEFROM));
		p.setServiceStartTimeTo(getISODateParameter(SERVICESTARTTIMETO));
		p.setServiceStopTimeTo(getISODateParameter(SERVICESTOPTIMETO));
		p.setCreationTimeFrom(getISODateParameter(CREATIONTIMEFROM)); 
		p.setCreationTimeTo(getISODateParameter(CREATIONTIMETO));
		
		return p;
	}
	
	public Feed processQuery(API.Mode searchMode, String outputFormat)
	{
		SearchParameters p = getAPIParameters();
		if (p.getOutputFormat() == null)
			p.setOutputFormat(outputFormat);
		
		// Haven't implemented pagination yet
		if (isStrict() && (p.getIndex() > 0 || p.getCount() > 0))
		{	sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not Implemented");		
			return null;
		}
		
		// TODO: At this point, we need to get the patientID from the 
		// authenticated principal we've identified based on the
		// OAuth process flow, and map it to the patient ID
		// used by the XDS Registry.  For now, we are using
		// a fixed patient ID.
		p.setPatientID("00ae33bb8f2b437");
		
		List<Entry> entries = searchAPI.search(p, searchMode, log);
		Feed results = new FeedImpl();
		
		// If there were no entries, generate an error and return
		if (entries == null)
		{	sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during query");
			return null;
		}
		
		results.setEntries(entries);
		String self = request.getRequestURL().toString();
		if (request.getQueryString() != null)
			self = self + "?" + request.getQueryString();
		
		try {
			Link lSelf = new LinkImpl(new URI(self), "self", Search.ATOM);
			lSelf.setTitle(results.getTitle());
			results.setSelf(lSelf);
		} catch (URISyntaxException e2) {
			log.log("Bad URI: " + self, e2);
		}
		results.setEntries(filterResults(results.getEntries(), p.getMimeType()));
		URI classesURI = null, mimeTypesURI = null, formatsURI = null;
		try
		{	classesURI = new URI(ABBI + "/class");
			formatsURI = new URI(ABBI + "/format");
		}
		catch (URISyntaxException e1)
		{ 
			log.log("URI", e1);
		}
		
		for (String category : p.getClasses())
			results.getCategories().add(new CategoryImpl(category, classesURI));
		for (String category : p.getFormat())
			results.getCategories().add(new CategoryImpl(category, formatsURI));

		List<String> targetMimeTypes = p.getMimeType() != null ?
				Arrays.asList(p.getMimeType()) : 
				(List<String>)Collections.EMPTY_LIST; 
				
		String requestURI = request.getRequestURI().toString();
		requestURI = requestURI.substring(0, requestURI.lastIndexOf('/'));

		for (Entry entry: results.getEntries())
		{	
			String path = null;
			try {
				// TODO: Ensure format() call generates entry in appropriate format!
				path = requestURI + entry.getContentSrc().toString();
				entry.setContentSrc(new URI(path));
			} catch (URISyntaxException e) {
				log.log("Set Content Path Error", e);
			}
		}

		return results;
	}

	/**
	 * Given a source mime type, and a set of possible target mime types,
	 * returns the first target mimeType to which the source can be converted.
	 * 
	 * @param sourceType	The mime type of the found document
	 * @param targetList	The requested set of target mime types
	 * @return	One of the mimeType values in targetList, or null if none are applicable
	 */
	public static String getTargetMimeType(String sourceType, String targetList[])
	{
		if (sourceType.equals("text/xml"))
		{	List<String> targets = Arrays.asList(targetList);
			for (String xformType: _XML_CONVERTS_2)
				if (targets.contains(xformType))
					return xformType;
		}
		return null;
	}
	
	public static Set<String> transformableTo(String mimeType)
	{
		if (mimeType.equals("text/xml"))
		{
			return XML_CONVERTS_2;
		}
		return Collections.emptySet();
	}
	
	/**
	 * Filter a list of entries by mimeType values.  Removes any entries that are
	 * neither in the specified mimeType nor can be mapped to it.
	 * @param results	The results to filter
	 * @param mimeTypes	The set of mimeTypes to filter by
	 * @return	A list of entries that are either in the specified format, or can
	 * be mapped to it.
	 */
	protected List<Entry> filterResults(
		List<Entry> results, String mimeTypes[] 
	)
	{
		// filter the list based on mimeType
		if (mimeTypes != null && mimeTypes.length != 0)
		{	HashSet<String> mimeTypeSet = new HashSet<String>(Arrays.asList(mimeTypes));
			ArrayList<Entry> s = new ArrayList<Entry>();
			for (Entry e : results)
			{	String mt = e.getContentType();
				// if the original content type matches, or can be transformed to
				// then keep this one.
				if (mimeTypeSet.contains(mt) || getTargetMimeType(mt, mimeTypes) != null)
					s.add(e);
			}
			results = s;
		}
		
		// Sort the list in order
		Collections.sort(results, Util.ENTRYCOMPARATOR);
		
		// TODO: Handle the mime Type filtering
		return results;
	}
}
