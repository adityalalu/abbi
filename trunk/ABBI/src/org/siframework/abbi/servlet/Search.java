package org.siframework.abbi.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.siframework.abbi.api.API.Mode;
import org.siframework.abbi.api.Logger;
import org.siframework.abbi.atom.Entry;
import org.siframework.abbi.atom.Feed;
import org.siframework.abbi.atom.XML;
import org.w3c.dom.Document;

public class Search extends HttpServlet implements Logger {

	// Mime Types for the response
	public final static 
		String 	ATOM = "application/atom+xml",
				JSON = "application/json";

	private Properties properties = new Properties();
	private API searchAPI = null;
	
	public Properties getProperties()
	{
		return properties;
	}
	
	public API getAPI()
	{
		return searchAPI;
	}
	
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
	
	public void init() throws ServletException
	{	
		InputStream is = getServletContext().getResourceAsStream("/WEB-INF/search.api.mapping.properties");
		try {
			properties.load(is);
		} catch (IOException e) {
			log("Error loading /WEB-INF/search.api.mapping.properties", e);
		}
		
		String searchAPIClass = properties.getProperty("API");
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
	
	public API.Mode getSearchMode(HttpServletRequest request)
	{
		String url = request.getRequestURL().toString();
		if (url.contains("/net.ihe/"))
			return API.Mode.MHD;
		if (url.contains("/xdsentry"))
			return API.Mode.FHIR;
		return API.Mode.ABBI;
	}

	/**
	 * Compute the output format based on the URL, or the Accept Header
	 * @return "application/atom+xml" or "application/json"
	 */
	public String getOutputFormat(HttpServletRequest request, HttpServletResponse response)
	{	String theURL = request.getRequestURL().toString();
		String outputFormat = Search.ATOM;	// Our default format is atom+xml
		// Deal with output format.
		if (theURL.endsWith(".json"))
			outputFormat = Search.JSON;
		else if (theURL.endsWith(".atom"))
			outputFormat = Search.ATOM;
		else if (theURL.endsWith(".xml"))
			outputFormat = Search.ATOM;
		else
		{	// Validate contents of the Accept header.
			String accept = request.getHeader("Accept");
			if (accept != null)
			{	if (accept.contains(Search.JSON) && accept.contains(Search.ATOM))
				{	// If it accepts both JSON and ATOM, we need to detect which is preferable.
					// TODO: For now, we cheat by using order, but we should actually deal with the q parameter.
					if (accept.indexOf(Search.JSON) < accept.indexOf(Search.ATOM))
						outputFormat = Search.JSON;
				}
				else if (accept.contains(Search.JSON))
					outputFormat = Search.JSON;
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
	
	public void copy(InputStream is, OutputStream os) throws IOException
	{
		byte[] buffer = new byte[4096];
		int length = 0;
		
		while ((length = is.read(buffer)) > 0)
			os.write(buffer, 0, length);
	}
	
	public void writeMHD(Feed results, HttpServletResponse response) throws Exception
	{
		OutputStream out = response.getOutputStream();
		out.write("documentEntry:[\n".getBytes());
		boolean first = true;
		for (Entry e : results.getEntries())
		{	if (!first)
				out.write(',');
			copy(e.getContent(), out);
			first = false;
		}
		out.write("]\n".getBytes());
	}
	
	public void writeABBI(Feed results, String outputFormat, HttpServletResponse response) throws Exception
	{
		Document x = XML.toXML(results);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = null;
		if (outputFormat.equals(Search.JSON))
		{	InputStream is = getServletContext().getResourceAsStream("/xsl/ABBItoJSON.xsl");
			StreamSource s = new StreamSource(is);
				t = tf.newTransformer(s);
		}
		else
		{	t = tf.newTransformer();
			t.setOutputProperty("indent", "yes");
		}
		OutputStream o = response.getOutputStream();
		StreamResult r = new StreamResult(o);
		t.transform(new DOMSource(x), r);
		o.flush();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{

		SearchProcessor search = new SearchProcessor(request, response, getServletContext(), properties, searchAPI, this);
		API.Mode searchMode = getSearchMode(request);
		String outputFormat = searchMode == Mode.MHD ? JSON : getOutputFormat(request, response);
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
		
		
		Feed results = search.processQuery(searchMode, outputFormat);
		
		results.setUpdated(new Date());
		results.setGenerator(properties.getProperty("feed.generator"));
		results.setTitle(properties.getProperty("feed.title"));
		results.setSubtitle(properties.getProperty("feed.subtitle"));
		
		try { 
			String requestUri = request.getRequestURL().toString();
			requestUri = requestUri.substring(0, requestUri.lastIndexOf('/'));
			results.setId(new URI(requestUri)); 
		} 
		catch (URISyntaxException e1) { log("setId", e1); } 
		
		String uri = properties.getProperty("feed.icon");
		try { if (uri != null) results.setIcon(new URI(uri)); } catch (URISyntaxException e1) {
			log("setIcon", e1);
		} 
		
		uri = properties.getProperty("feed.logo");
		try { if (uri != null) results.setLogo(new URI(uri)); } catch (URISyntaxException e1) {
			log("setLogo", e1);
		} 
		
		response.setHeader("Content-Type", outputFormat);
		try {
			switch (searchMode) {
			case FHIR:
			case ABBI:
				writeABBI(results, outputFormat, response);
				break;
			case MHD:
				writeMHD(results, response);
				break;
			}
		} catch (Exception e) {
			log("Exception", e);
		}

	}
}

