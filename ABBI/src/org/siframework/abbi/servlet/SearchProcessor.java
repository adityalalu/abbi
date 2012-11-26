package org.siframework.abbi.servlet;

import java.io.IOException;

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

import org.siframework.abbi.api.API;
import org.siframework.abbi.api.Context;
import org.siframework.abbi.api.SearchParameters;
import org.siframework.abbi.atom.Entry;
import org.siframework.abbi.atom.Feed;
import org.siframework.abbi.atom.Link;
import org.siframework.abbi.atom.impl.CategoryImpl;
import org.siframework.abbi.atom.impl.EntryImpl;
import org.siframework.abbi.atom.impl.FeedImpl;
import org.siframework.abbi.atom.impl.LinkImpl;
import org.siframework.abbi.utility.DT;

/**
 * This class implements the basic framework of a search processor.  It contains a number
 * of utility methods to get and validate request parameters.
 * @author 212042380
 *
 */
public class SearchProcessor {
	
	// Parameter Names to use.
	protected final static String
		PATIENTID = "patientid",
		MIMETYPE = "mimetype",
		FORMAT = "format",
		CLASS = "class",
		CONTENT = "content",
		SERVICESTARTTIME = "service.start",
		SERVICESTOPTIME = "service.stop",
		CREATIONTIME = "created",
		COUNT = "count",
		N = "n",
		OUTPUTFORMAT = "$format";
	
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
	private final Context log;
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
		Context log)
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

	/**
	 * Returns true if strict API checking should be performed, false otherwise
	 * @return True if strict API checking should be performed, false otherwise
	 */
	public boolean isStrict()
	{
		return strict;
	}
	
	/**
	 * Set whether Strict API checking should be used or not.
	 * @param strict A boolean value indicating whether strict API checking should be used (true) or not (false).
	 */
	public void setStrict(boolean strict)
	{
		this.strict = strict;
	}

	/**
	 * Get a list of parameter values used for the given request parameter name.
	 * @param name	The name of the parameter to check.
	 * @return	A list of values set for that parameter name.
	 */
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
		
		Date date = DT.parseISODate(value);
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
	 * Gets the date range associated with a request parameter.  
	 * @param name The name of the request parameter to check.
	 * @param isISO	A boolean value to indicate how dates are represented.  If true, dates are represented
	 * in ISO format (YYYYMMDDhhmmss.SSSZ or YYYY-MM-DDThh:mm:ss.SSSZ).  If false, they are represented
	 * as an integer offset in seconds from the Epoch (Midnight, January 1, 1970).
	 * @return A pair of Date objects representing the lower and upper bounds of the range.
	 */
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
		if (value == null)
			return null;
		
		Date date = DT.parseDate(value);
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
	
	/**
	 * Set the API Parameters common to ABBI and FHIR.
	 * @param isISO	true if using ISO Date Format (ABBI) or false otherwise (FHIR)
	 * @return	The parsed search parameters given in the request.
	 */
	public void setCommonAPIParameters(boolean isISO, SearchParameters p)
	{
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
	}
	
	/**
	 * Sets the search parameters when using the ABBI protocol
	 * @return 
	 * @return The parsed search parameters
	 */
	public void setABBIAPIParameters(SearchParameters p)
	{
		setCommonAPIParameters(true, p);
	}
	
	/** Set the search parameters, per the FHIR xdsentry specification.
	 * Extracts the search parameters from the query string.<br/>
	 * n : integer	Starting offset of the first record to return in the search set<br/>
	 * count : integer	Number of return records requested. The server is not bound to conform<br/>
	 * id : token	The id of the resource [NOT SUPPORTED]<br/>
	 * repositoryId : string	repository - logical or literal url  [NOT SUPPORTED]<br/>
	 * mimeType : string	mime type of document<br/>
	 * format : qtoken	format (urn:.. Following rules)<br/>
	 * class : qtoken	particular kind of document<br/>
	 * type : qtoken	precise kind of document [NOT SUPPORTED]<br/>
	 * documentId : string	document id - logical or literal url [NOT SUPPORTED]<br/>
	 * availability : string	Approved | Deprecated [NOT SUPPORTED]<br/>
	 * confidentiality : qtoken	as defined by Affinty Domain<br/>
	 * created : date	date equal to time author created document<br/>
	 * created-before : date	date before or equal to time author created document<br/>
	 * created-after : date	date after or equal to time author created document<br/>
	 * event : qtoken	main clinical act(s)[NOT SUPPORTED]<br/>
	 * language : string	human language (RFC 3066) [NOT SUPPORTED]<br/>
	 * folderId : qtoken	folders this document is in [NOT SUPPORTED]<br/>
	 * patientId : qtoken	subject of care of the document<br/>
	 * patientInfo : qtoken	demographic details [NOT SUPPORTED]<br/>
	 * author.name : string	name of human/machine [NOT SUPPORTED]<br/>
	 * author.id : qtoken	id of human/machine [NOT SUPPORTED]<br/>
	 * facilityType : qtoken	type of organizational setting [NOT SUPPORTED]<br/>
	 * practiceSetting : qtoken	clinical speciality of the act [NOT SUPPORTED]<br/>
	 * homeCommunity : string	globally unique community id [NOT SUPPORTED]<br/>
	 * service.start : date	date equal to Start time<br/>
	 * service.start-before : date	date before or equal to Start time<br/>
	 * service.start-after : date	date after or equal to Start time<br/>
	 * service.stop : date	date equal to Stop time<br/>
	 * service.stop-before : date	date before or equal to Stop time<br/>
	 * service.stop-after : date	date after or equal to Stop time<br/>
	 * comments : string	comments as specified by affinity domain [NOT SUPPORTED]<br/>
	 * @return 
	 * @return The API Parameters filled in from the Query String
	 */
	public void setFHIRAPIParameters(SearchParameters p)
	{
		setCommonAPIParameters(false, p);
		String fmt = getParameter(OUTPUTFORMAT);
		if (fmt != null)
		{	if (fmt.contains("json"))
				p.setOutputFormat(Search.JSON_MIMETYPE);
			else if (fmt.contains("atom"))
				p.setOutputFormat(Search.ATOM_MIMETYPE);
		}
	}
	
	/** Get search parameters for MHD from the query and return them
	 * typeCode<br/>
	 * practiceSettingCode<br/>
	 * creationTimeFrom<br/>
	 * creationTimeTo<br/>
	 * serviceStartTimeFrom<br/>
	 * serviceStartTimeTo<br/>
	 * serviceStopTimeFrom<br/>
	 * serviceStopTimeTo<br/>
	 * formatCode<br/>
	 * mimeType (not an MHD parameter)
	 * @return the Search parameters found.
	 */
	public void setMHDAPIParameters(SearchParameters p)
	{
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
		
	}
	
	/**
	 * Initialize a new Feed for return by a search request
	 * @param outputFormat 
	 * @return A new feed, initialized from properties and business rules
	 */
	public Feed initializeFeed(String outputFormat)
	{
		Feed results = new FeedImpl();
		
		// Property (Configuration) based initializations
		results.setGenerator(properties.getProperty("feed.generator"));
		results.setTitle(properties.getProperty("feed.title"));
		results.setSubtitle(properties.getProperty("feed.subtitle"));
		String uri = properties.getProperty("feed.icon");
		if (uri != null) results.setIcon(uri);
		uri = properties.getProperty("feed.logo");
		if (uri != null) results.setLogo(uri);
		
		// Business rule initializations
		String requestUri = request.getRequestURL().toString();
		requestUri = requestUri.substring(0, requestUri.lastIndexOf('/'));
		
		results.setId(requestUri); 
		String self = request.getRequestURL().toString();
		if (request.getQueryString() != null)
			self = self + "?" + request.getQueryString();
		
		// Self link should represent appropriate output format
		Link lSelf = new LinkImpl(self, "self", outputFormat);
		lSelf.setTitle(results.getTitle());
		results.setSelf(lSelf);
		results.setUpdated(new Date());

		return results;
	}

	/**
	 * Process a query<br/>
	 * 1.  Get the Query Parameters<br/>
	 * 2.  Send them to the back end to get results as Atom Entries<br/>
	 * 3.  Filter the pertinent results<br/>
	 * 4.  Attach the entries to an Atom Feed and return it to the caller<br/>
	 * @param searchMode The API mode to use when parsing query parameters
	 * @param outputFormat How the output will be generated
	 * @return An Atom Feed containing the search results.
	 */
	public Feed processQuery(SearchParameters p)
	{
		// Get the query parameters
		switch (p.getSearchMode()) {
		case ABBI:
			setABBIAPIParameters(p);
			break;
		case MHD:
			setMHDAPIParameters(p);
			break;
		case FHIR:
			setFHIRAPIParameters(p);
			break;
		}
		
		// Set the base URL for the service as a parameter
		String requestURI = request.getRequestURL().toString();
		if (requestURI.contains("/net.ihe/"))
			requestURI = requestURI.substring(0, requestURI.lastIndexOf("/net.ihe/")+9);
		else if (requestURI.contains("/xdsentry/"))
			requestURI = requestURI.substring(0, requestURI.lastIndexOf("/xdsentry"));
		else
			requestURI = requestURI.substring(0, requestURI.lastIndexOf('/'));
		p.setBaseURL(requestURI);
		
		// Haven't implemented pagination yet
		if (isStrict() && (p.getIndex() > 0 || p.getCount() > 0))
		{	sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not Implemented");		
			return null;
		}
		
		// If API checking is enabled, and there are errors, return null
		if (isStrict() && hasErrors)
			return null;
		
		// TODO: At this point, we need to get the patientID from the 
		// authenticated principal we've identified based on the
		// OAuth process flow, and map it to the patient ID
		// used by the XDS Registry.  For now, we are using
		// a fixed patient ID.
		p.setPatientID("00ae33bb8f2b437");
		
		// Perform the query
		List<Entry> entries = searchAPI.search(p, log);
		
		// If there were no entries, generate an error and return
		if (entries == null)
		{	sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during query");
			return null;
		}
		// Initialize the feed
		Feed results = initializeFeed(p.getOutputFormat());

		// Set the entries
		results.setEntries(filterResults(entries, p.getMimeType()));
		
		// Add a few categories to the feed based on query parameters used.
		for (String category : p.getClasses())
			results.getCategories().add(new CategoryImpl(category, ABBI + "/class"));
		for (String category : p.getFormat())
			results.getCategories().add(new CategoryImpl(category, ABBI + "/format"));
		
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
	
	/**
	 * Return a set of strings indicating what outputs can be generated from content in the 
	 * passed in mimeType
	 * @param mimeType The mimeType of the source document
	 * @return A set of mimeTypes that the source document can be transformed to.
	 */
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
		Collections.sort(results, EntryImpl.ENTRYCOMPARATOR);

		return results;
	}
}
