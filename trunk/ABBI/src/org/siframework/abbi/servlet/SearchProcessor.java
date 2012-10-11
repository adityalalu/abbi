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
import java.util.Enumeration;
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
		PATIENTID = "patientid",
		MIMETYPE = "mimetype",
		FORMAT = "format",
		CLASS = "class",
		CONTENT = "content",
		SERVICESTARTTIMEFROM = "service.start-after",
		SERVICESTARTTIMETO = "service.start-before",
		SERVICESTARTTIME = "service.start",
		SERVICESTOPTIMEFROM = "service.stop-after",
		SERVICESTOPTIMETO = "service.stop-before",
		SERVICESTOPTIME = "service.stop",
		CREATIONTIMEFROM = "created-before",
		CREATIONTIMETO = "created-after",
		CREATIONTIME = "created",
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
	private boolean hasErrors = false;
	
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
	
	public List<String> getRequestParameters(String name)
	{
		List<String> values = new ArrayList<String>();
		Enumeration<String> paramNames = (Enumeration<String>)request.getParameterNames();
		while (paramNames.hasMoreElements())
		{   String paramName = paramNames.nextElement();
		 	if (paramName.equalsIgnoreCase(name))
			{	String[] paramValues = request.getParameterValues(paramName);
				values.addAll(Arrays.asList(paramValues));
			}
		}
		return values;
	}
	
	/**
	 * Gets a parameter that should appear only once by name.  Sends an SC_BAD_REQUEST
	 * error when running in STRICT mode if the parameter appears more than once.
	 * @param name The name of the parameter to retrieve.
	 * @return The parameter, or null if the parameter is not present.
	 */
	public String getParameter(String name)
	{
		List<String> values = getRequestParameters(name);
		if (values.size() == 0)
			return null;
		
		if (values.size() > 1 && isStrict())
		{	sendError(HttpServletResponse.SC_BAD_REQUEST,
				"Only one value allowed for " + name);
			return null;
		}
		
		return values.get(0);
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
	
	public Date[] getDateRangeParameter(String name, boolean isISO)
	{
		Date d[] = new Date[2];
		
		Date both = isISO ? getISODateParameter(name) : getDateParameter(name);
		String fromName = name + "-after";
		Date from = isISO ? getISODateParameter(fromName) : getDateParameter(fromName);
		String toName = name + "-before";
		Date to = isISO ? getISODateParameter(toName) : getDateParameter(toName);
		
		d[0] = from;
		d[1] = to;
		
		if (both != null)
		{	if (isStrict() && (from != null || to != null))
			{	sendError(HttpServletResponse.SC_BAD_REQUEST,
					name + " cannot be used with " + fromName + " or " + toName);
			}
			d[0] = both;
			d[1] = both;
		}
		
		return d;
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
		List<String> results = getRequestParameters(name);
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
		return results.toArray(new String[results.size()]);
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
			hasErrors = true;
			response.sendError(sc, message);
		}
		catch (IOException ex)
		{
			log.log("sendError", ex);
		}
	}
	
	public SearchParameters getCommonAPIParameters(boolean isISO)
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
		
		Date d[] = getDateRangeParameter(SERVICESTARTTIME, isISO);
		p.setServiceStartTimeFrom(d[0]);
		p.setServiceStartTimeTo(d[1]);
		d = getDateRangeParameter(SERVICESTOPTIME, isISO);
		p.setServiceStopTimeFrom(d[0]);
		p.setServiceStopTimeTo(d[1]);
		d  = getDateRangeParameter(CREATIONTIME, isISO);
		p.setCreationTimeFrom(d[0]);
		p.setCreationTimeTo(d[1]);
		return p;
		
	}
	
	
	public SearchParameters getABBIAPIParameters()
	{
		return getCommonAPIParameters(true);
	}
	
	/** Get the search parameters, per the FHIR xdsentry specification.
	 * Extracts the search parameters from the query string.
	 * n : integer	Starting offset of the first record to return in the search set
	 * count : integer	Number of return records requested. The server is not bound to conform
	 * id : token	The id of the resource [NOT SUPPORTED]
	 * repositoryId : string	repository - logical or literal url  [NOT SUPPORTED]
	 * mimeType : string	mime type of document
	 * format : qtoken	format (urn:.. Following rules)
	 * class : qtoken	particular kind of document
	 * type : qtoken	precise kind of document
	 * documentId : string	document id - logical or literal url [NOT SUPPORTED]
	 * availability : string	Approved | Deprecated [NOT SUPPORTED]
	 * confidentiality : qtoken	as defined by Affinty Domain
	 * created : date	date equal to time author created document
	 * created-before : date	date before or equal to time author created document
	 * created-after : date	date after or equal to time author created document
	 * event : qtoken	main clinical act(s)
	 * language : string	human language (RFC 3066)
	 * folderId : qtoken	folders this document is in
	 * patientId : qtoken	subject of care of the document
	 * patientInfo : qtoken	demographic details
	 * author.name : string	name of human/machine
	 * author.id : qtoken	id of human/machine
	 * facilityType : qtoken	type of organizational setting
	 * practiceSetting : qtoken	clinical speciality of the act
	 * homeCommunity : string	globally unique community id
	 * service.start : date	date equal to Start time
	 * service.start-before : date	date before or equal to Start time
	 * service.start-after : date	date after or equal to Start time
	 * service.stop : date	date equal to Stop time
	 * service.stop-before : date	date before or equal to Stop time
	 * service.stop-after : date	date after or equal to Stop time
	 * comments : string	comments as specified by affinity domain
	 * @return
	 */
	public SearchParameters getFHIRAPIParameters()
	{
		return getCommonAPIParameters(false);
	}
	
	/** Get search parameters for MHD from the query and return them
	 * typeCode
	 * practiceSettingCode
	 * creationTimeFrom
	 * creationTimeTo
	 * serviceStartTimeFrom
	 * serviceStartTimeTo
	 * serviceStopTimeFrom
	 * serviceStopTimeTo
	 * formatCode
	 * mimeType (not an MHD parameter)
	 * @return the Search parameters found.
	 */
	public SearchParameters getMHDAPIParameters()
	{
		SearchParameters p = new SearchParameters();
		
		// Now we get the various parameters
		p.setPatientID(getParameter(PATIENTID));

		// Get the content parameter.  If it is equal to "include"
		// then mark it as included, otherwise mark it as not included.
		
		p.setContentIncluded("include".equals(getParameter(CONTENT)));
		// Get coded parameters
		
		p.setClasses(getArrayParameter("classCode", searchAPI.getSupportedClasses()));
		p.setFormat(getArrayParameter("formatCode", searchAPI.getSupportedFormats()));
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
		p.setServiceStartTimeFrom(getISODateParameter("serviceStartTimeFrom"));
		p.setServiceStartTimeTo(getISODateParameter("serviceStartTimeTo"));
		p.setServiceStopTimeFrom(getISODateParameter("serviceStopTimeFrom"));
		p.setServiceStopTimeTo(getISODateParameter("serviceStopTimeTo"));
		p.setCreationTimeFrom(getISODateParameter("creationTimeFrom")); 
		p.setCreationTimeTo(getISODateParameter("creationTimeTo"));
		
		return p;
	}

	public Feed processQuery(API.Mode searchMode, String outputFormat)
	{
		SearchParameters p = null;
		
		switch (searchMode) {
		case ABBI:
			p = getABBIAPIParameters();
			break;
		case MHD:
			p = getMHDAPIParameters();
			break;
		case FHIR:
			p = getFHIRAPIParameters();
			break;
		}
		if (p.getOutputFormat() == null)
			p.setOutputFormat(outputFormat);
		
		// Haven't implemented pagination yet
		if (isStrict() && (p.getIndex() > 0 || p.getCount() > 0))
		{	sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not Implemented");		
			return null;
		}
		
		if (isStrict() && hasErrors)
			return null;
		
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
