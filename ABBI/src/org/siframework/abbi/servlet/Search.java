package org.siframework.abbi.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
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
import org.siframework.abbi.api.Context;
import org.siframework.abbi.api.SearchMode;
import org.siframework.abbi.api.SearchParameters;
import org.siframework.abbi.atom.Entry;
import org.siframework.abbi.atom.Feed;
import org.siframework.abbi.atom.JSONMarshaller;
import org.siframework.abbi.atom.XMLMarshaller;
import org.siframework.abbi.utility.IO;
import org.siframework.abbi.utility.XML;
import org.w3c.dom.Document;

/**
 * The Search class is an HttpServlet that implements the ABBI Protocol.  It serves as the front
 * end for the ABBI application.
 * 
 * To use this servlet, include the following lines in WEB-INF/web.xml in your application.
 * <servlet>
 *  	<servlet-name>SearchServlet</servlet-name>
 *   	<servlet-class>org.siframework.abbi.servlet.Search</servlet-class>
 *   	<load-on-startup>0</load-on-startup>
 *	</servlet>
 *
 * The servlet uses the URL pattern to figure out which protocol is being queried.  The default
 * protocol is the ABBI protocol, and operates on end-points that end with /search, /search.atom
 * or /search.json.  To enable these end-points, add the following servlet mappings to web.xml
 *	<servlet-mapping>
 *    	<servlet-name>SearchServlet</servlet-name>
 *    	<url-pattern>/search</url-pattern>
 *	</servlet-mapping>
 *
 * To support the IHE Mobile Access to Health Documents protocol, the end-point URL must 
 * contain /net.ihe/ in it.  To activate this protocol, add the following servlet mappings
 * to the web.xml file.
 * 	<servlet-mapping>
 *    	<servlet-name>SearchServlet</servlet-name>
 *    	<url-pattern>/net.ihe/DocumentDossier/search</url-pattern>
 *	</servlet-mapping>
 *	<servlet-mapping>
 *    	<servlet-name>SearchServlet</servlet-name>
 *    	<url-pattern>/net.ihe/Document/*</url-pattern>
 *	</servlet-mapping>
 *
 * To support the HL7 FHIR protocol, the end-point URL must contain /xdsentry/.  To activate
 * this protocol, add the following servlet mappings to the web.xml file. 
 *	<servlet-mapping>
 *    	<servlet-name>SearchServlet</servlet-name>
 *    	<url-pattern>/xdsentry/*</url-pattern>
 *	</servlet-mapping> 
 *
 * @author Keith W. Boone
 **/
public class Search extends HttpServlet implements Context {

	// Mime Types for the response
	public final static 
		String 	ATOM_MIMETYPE = "application/atom+xml",
				JSON_MIMETYPE = "application/json";

	private Properties properties = new Properties();
	private API searchAPI = null;
	
	public Search() {
	}
	
	@Override
	public void init() throws ServletException
	{	
		InputStream is = getServletContext().getResourceAsStream("/WEB-INF/search.api.mapping.properties");
		try {
			properties.load(is);
		} catch (IOException e) {
			log("Error loading /WEB-INF/search.api.mapping.properties", e);
		}
		
		String searchAPIClass = properties.getProperty("org.siframework.abbi.api");
		if (searchAPIClass == null)
		{
			throw new ServletException("No API class specified in /WEB-INF/search.api.mapping.properties");
		}
		
		try
		{	searchAPI = (API)Class.forName(searchAPIClass).newInstance();
			searchAPI.init(properties, this);
		}
		catch (Exception e)
		{
			throw new ServletException("Error creating " + searchAPIClass, e);
		}
	}
	
	/**
	 * Examine the request URL and from it, determine which protocol is being used.
	 * @param requestURL The URL which was used to perform the query	
	 * @return A value indicating which protocol is being used.
	 */
	public SearchMode getSearchMode(String requestURL)
	{
		// If the request contains /net.ihe/ then use the Mobile Access to Health Documents protocol 
		if (requestURL.contains("/net.ihe/"))
			return SearchMode.MHD;
		// If the request containts /xdsentry then use the HL7 FHIR protocol
		if (requestURL.contains("/xdsentry"))
			return SearchMode.FHIR;
		// Otherwise, use the ABBI protocol
		return SearchMode.ABBI;
	}

	/**
	 * Compute the output format based on the URL, or the Accept Header.  Query parameters
	 * can override this selection, but this establishes the default format.
	 * @return "application/atom+xml" or "application/json"
	 */
	public String getOutputFormat(HttpServletRequest request, HttpServletResponse response)
	{	String theURL = request.getRequestURL().toString();
		String outputFormat = Search.ATOM_MIMETYPE;	// Our default format is atom+xml
		// Deal with output format.
		// TODO: Need to refactor this, because it is in two places right now
		String fmt = request.getParameter(SearchProcessor.OUTPUTFORMAT);
		if (fmt != null && fmt.contains("json"))
			outputFormat = JSON_MIMETYPE;
		else if (fmt != null && fmt.contains("atom"))
			outputFormat = ATOM_MIMETYPE;
		else if (theURL.endsWith(".json"))
			outputFormat = Search.JSON_MIMETYPE;
		else if (theURL.endsWith(".atom"))
			outputFormat = Search.ATOM_MIMETYPE;
		else if (theURL.endsWith(".xml"))
			outputFormat = Search.ATOM_MIMETYPE;
		else
		{	// Validate contents of the Accept header.
			String accept = request.getHeader("Accept");
			if (accept != null)
			{	if (accept.contains(Search.JSON_MIMETYPE) && accept.contains(Search.ATOM_MIMETYPE))
				{	// If it accepts both JSON and ATOM, we need to detect which is preferable.
					// TODO: For now, we cheat by using order, but we should actually deal with the q parameter.
					if (accept.indexOf(Search.JSON_MIMETYPE) < accept.indexOf(Search.ATOM_MIMETYPE))
						outputFormat = Search.JSON_MIMETYPE;
				}
				else if (accept.contains(Search.JSON_MIMETYPE))
					outputFormat = Search.JSON_MIMETYPE;
				else if (!accept.contains("application/*") && !accept.contains("*/*"))
				{
					// Generates an error if we don't recognized an 
					// acceptable format in STRICT mode.  In normal
					// mode, we just return application/atom+xml
					return null;
				}
			}
		}
		return outputFormat;
	}
	
	/**
	 * Write content in MHD Format
	 * @param results The Atom Feed containing the MHD Results
	 * @param w The Output Stream to which is being written to.
	 * @throws Exception If an error occurs during output
	 */
	public void writeMHD(Feed results, OutputStream w) throws Exception
	{
		w.write("documentEntry:[\n".getBytes());
		boolean first = true;
		for (Entry e : results.getEntries())
		{	if (!first)
				w.write(',');
			IO.copy(e.getContent(), w);
			first = false;
		}
		w.write("]\n".getBytes());
	}
	
	/**
	 * Write content in Atom Format
	 * @param results The Atom Feed containing the Results
	 * @param w The Servlet Response which is being written to.
	 * @throws Exception If an error occurs during output
	 */
	public void writeAtom(Feed results, String outputFormat, OutputStream w) throws Exception
	{
		if (outputFormat.equals(Search.JSON_MIMETYPE))
		{	String json = JSONMarshaller.toJson(results);
			w.write(IO.toUTF8(json));
		}
		else
		{	
			Document x = XMLMarshaller.toXML(results);
			Transformer t = XML.getTransformer();
			t.setOutputProperty("indent", "yes");
			StreamResult r = new StreamResult(w);
			t.transform(new DOMSource(x), r);
			w.flush();
		}
	}
	
	/**
	 * Handle post as if they were a Get to support applications that use forms and the
	 * POST method to perform queries.  In the future, this may also handle updates for
	 * document resources.
	 * @param request	The request to process
	 * @param response	Where to put the response.
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		doGet(request, response);
	}
	
	/**
	 * Process GET requests to search for lists of or access individual documents.
	 * @param request	The request to process
	 * @param response	Where to put the response.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{

		// Create a search processor to handle this transaction
		SearchProcessor search = new SearchProcessor(request, response, getServletContext(), 
				properties, searchAPI, this);
		
		// Determine the search mode
		SearchMode searchMode = getSearchMode(request.getRequestURL().toString());
		
		// And default output format
		String outputFormat = searchMode == SearchMode.MHD ? JSON_MIMETYPE : getOutputFormat(request, response);
		
		// If we have API checking turned on (isString), and searchMode wasn't determined, then
		// we barf.
		if (search.isStrict() && searchMode == null)
		{	try
			{
				response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, 
					"This service only generates application/atom+xml or application/json output");
			}
			catch (IOException ioex)
			{
				log("Output Error", ioex);
			}
			return;
		}
		SearchParameters p = new SearchParameters();
		p.setOutputFormat(outputFormat);
		p.setSearchMode(searchMode);
		// 
		Feed results = search.processQuery(p);
		
		response.setHeader("Content-Type", p.getOutputFormat());
		try {
			OutputStream w = response.getOutputStream();
			switch (searchMode) {
			case FHIR:
			case ABBI:
				writeAtom(results, outputFormat, w);
				break;
			case MHD:
				writeMHD(results, w);
				break;
			}
		} catch (Exception e) {
			log("Exception", e);
		}

	}

	public Properties getProperties()
	{
		return properties;
	}
	
	public API getAPI()
	{
		return searchAPI;
	}
	
	// Context Implementation
	@Override
	public void log(String message)
	{
		System.err.println(message);
	}
	
	@Override
	public void log(String message, Throwable t)
	{
		System.err.println(message);
		t.printStackTrace(System.err);
	}

	@Override
	public InputStream getResource(String name) {
		return this.getServletContext().getResourceAsStream(name);
	}
}

