package org.siframework.abbi.api.xds;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;

import javax.servlet.ServletException;

import org.eclipse.emf.common.util.EList;
import org.openhealthtools.ihe.atna.auditor.XDSConsumerAuditor;
import org.openhealthtools.ihe.atna.auditor.context.AuditorModuleConfig;
import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.common.hl7v2.Hl7v2Factory;
import org.openhealthtools.ihe.xds.consumer.B_Consumer;
import org.openhealthtools.ihe.xds.consumer.query.DateTimeRange;
import org.openhealthtools.ihe.xds.consumer.query.MalformedQueryException;
import org.openhealthtools.ihe.xds.consumer.retrieve.DocumentRequestType;
import org.openhealthtools.ihe.xds.consumer.retrieve.RetrieveDocumentSetRequestType;
import org.openhealthtools.ihe.xds.consumer.retrieve.RetrieveFactory;
import org.openhealthtools.ihe.xds.consumer.storedquery.FindDocumentsQuery;
import org.openhealthtools.ihe.xds.consumer.storedquery.GetDocumentsQuery;
import org.openhealthtools.ihe.xds.consumer.storedquery.MalformedStoredQueryException;
import org.openhealthtools.ihe.xds.document.XDSDocument;
import org.openhealthtools.ihe.xds.metadata.AuthorType;
import org.openhealthtools.ihe.xds.metadata.AvailabilityStatusType;
import org.openhealthtools.ihe.xds.metadata.CodedMetadataType;
import org.openhealthtools.ihe.xds.response.DocumentEntryResponseType;
import org.openhealthtools.ihe.xds.response.XDSQueryResponseType;
import org.openhealthtools.ihe.xds.response.XDSRetrieveResponseType;

import org.siframework.abbi.api.API;
import org.siframework.abbi.api.SearchParameters;
import org.siframework.abbi.api.Context;
import org.siframework.abbi.atom.Entry;
import org.siframework.abbi.atom.Person;
import org.siframework.abbi.atom.impl.CategoryImpl;
import org.siframework.abbi.atom.impl.EntryImpl;
import org.siframework.abbi.atom.impl.PersonImpl;
import org.siframework.abbi.utility.DT;

public class XDSImpl implements API {
	private B_Consumer registry = null;
	public String idSource = null;
	
	// Stores mappings from API specified format and class values to CodedMetadataType values
	public HashMap<String,List<String>> 
		formatMap = new HashMap<String,List<String>>(),
		classMap = new HashMap<String,List<String>>();
	
	// Stores the reverse mapping from CodedMetadataType values to API specified format codes.
	public HashMap<String, String> 
		inverseFormatMap = new HashMap<String, String>(),
		inverseClassMap = new HashMap<String, String>();
	
	// OHT requires both parts of a date range to be specified.  These two constants are
	// valid time stamps that can be used for the lower and upper bound of time ranges
	// when these values are not supplied.
	private final static Date 
		YESTERYEAR = new Date(0),				  	// The ePoch Date (before the internet)
		TOMORROWLAND = new Date(15778800000000l);  	// 450+ years into the future
	
	// This stores the Audit configuration for the AuditorModule.
	private AuditorModuleConfig amc = null;

	// The default availability Status of documents to retrieve
	// This implementation ONLY retrieves approved documents.
	public static final AvailabilityStatusType APPROVED[] = { 
		AvailabilityStatusType.APPROVED_LITERAL
	};
	
	// TODO: API Maps should be handled by the Front-End, not the back end.
	/**
	 * Read through the properties keys, and build mappings from
	 * API inputs to XDS code values
	 * @param properties The initialization properties
	 */
	private void buildAPIMaps(Properties properties)
	{
		for (Map.Entry<Object, Object> p: properties.entrySet())
		{	
			String prop = (String) p.getKey();
			String value = (String)p.getValue();
			
			if (value != null)
			{	
				List<String> sList = null;
				String key = null;
				Map<String, List<String>> map = null;
				Map<String, String> iMap = null;
				// Get the API parameter value that needs to be mapped, and
				// set maps to be updated.
				if (prop.startsWith("format."))
				{	key = prop.substring(7);
				 	map = formatMap;
				 	iMap = inverseFormatMap;
				}
				else if (prop.startsWith("class."))
				{	key = prop.substring(6);
					map = classMap;
					iMap = inverseClassMap;
				}
				
				// If there was a key value, update the maps
				if (key != null)
				{	sList = Arrays.asList(value.split(" *, *"));
					map.put(key, sList);
					for (String s : sList)
						iMap.put(s, key);
				}
			}
		}
	}
	
	public Map<String,URI> getRepositoryMap(Properties properties, Context log)
	{
		Map<String,String> repositoryId = new HashMap<String,String>();
		Map<String,String> repositoryUrl = new HashMap<String,String>();
		Map<String,URI> repositoryMap = new HashMap<String, URI>();
		String repositoryKey = this.getClass().getName() + ".repository.";
		
		for (Map.Entry<Object, Object> p: properties.entrySet())
		{	
			String prop = (String) p.getKey();
			String value = (String)p.getValue();
			
			if (prop.startsWith(repositoryKey))
			{	prop = prop.substring(repositoryKey.length());
				String parts[] = prop.split("\\\\.");
				if (parts.length != 2)
					log.log("Invalid repository property: " + prop);
				else if ("id".equals(parts[0]))
					repositoryId.put(parts[1], value);
				else if ("url".equals(parts[0]))
					repositoryUrl.put(parts[1], value);
				else
					log.log("Invalid repository property syntax: " + prop);
			}
		}
		Set<String> s = new HashSet<String>();
		s.addAll(repositoryId.keySet());
		s.addAll(repositoryUrl.keySet());
		
		for (String key: s)
		{
			String id = repositoryId.get(key);
			String url = repositoryUrl.get(key);
			if (id == null)
				log.log("Missing "+repositoryKey+".id." + key + " value");
			else if (url == null)
				log.log("Missing "+repositoryKey+".url." + key + " value");
			else
			{	try
				{
					URI uri = new URI(url);
					repositoryMap.put(id, uri);
				}
				catch (URISyntaxException ex)
				{
					log.log("Invalid URL for repository.url." + key + ": " + url, ex);
				}
			}
		}
		
		return repositoryMap;
	}

	/**
	 * Initialize the registry, audit trail, and mappings from API parameters to XDS CodedMetadataType values
	 * from the supplied properties.
	 * @param properties	The properties from which to perform the initialization.
	 * @param log	The logger where errors should be reported.
	 */
	@Override
	public void init(Properties properties, Context log) throws ServletException
	{
		try {
			URI uri;
			String regURI = null;
			
			regURI = properties.getProperty(this.getClass().getName()+".registry");
			if (regURI == null)
				throw new ServletException("Missing registry parameter in properties.");
			uri = new URI(regURI);
			registry = new B_Consumer(uri);
			
			// If the auditor is configured
			if (properties.containsKey(AuditorModuleConfig.AUDITOR_AUDIT_REPOSITORY_HOST_KEY))
			{	// Configure it from the properties
				amc = new AuditorModuleConfig(properties);
				XDSConsumerAuditor.getAuditor().setConfig(amc);
			}
			else
			{	// Disable auditing
				amc = XDSConsumerAuditor.getAuditor().getConfig();
				amc.setAuditorEnabled(false);
			}
		} catch (URISyntaxException e) {
			log.log("Registry URI Not Valid", e);
		}
		
		// Set the map to repositories.
		registry.setRepositoryMap(getRepositoryMap(properties, log));
		
		idSource = properties.getProperty(this.getClass().getName()+".source");
		if (idSource == null)
			throw new ServletException("Missing source in properties");
		
		buildAPIMaps(properties);
	}
	
	@Override
	/** Search the registry using the specified parameters and return an
	 * atom feed with populated entries.
	 * @param search	The search parameters to apply.
	 * @param log	The logger to use for error reporting.
	 * @return A populated atom feed containing the relevant entries
	 */
	public List<Entry> search(SearchParameters search, Context log) 
	{
		// Perform the XDS Query
		XDSQueryResponseType response = doXDSQuery(search, log);
		XDSFormatter formatter = getFormatter(search);
		
		// If there was an error, return null
		if (response == null)
			return null;
		
		// Create a new list of entries to hold each response.
		LinkedList<Entry> entries = new LinkedList<Entry>();
		
		// For each document entry
		for (DocumentEntryResponseType der : response.getDocumentEntryResponses())
			entries.add(formatter.format(der, log));
		return entries;
	}
	
	/**
	 * Maps codes in the specified code list to CodedMetadataTypes initialized from the 
	 * specified map.
	 * @param codeList	An array of codes being searched for.
	 * @param map	A map from one code system to another, supporting one to many mappings
	 * @return	An array of codes to search for in the back end.
	 */
	protected ArrayList<CodedMetadataType> mapCodes(String[] codeList, HashMap<String,List<String>> map)
	{
		ArrayList<CodedMetadataType> list = new ArrayList<CodedMetadataType>(codeList.length);
		
		for (String code: codeList)
		{
			List<String> mapTo = map.get(code);
			if (mapTo != null)
			{
				for (String m: mapTo)
				{	String parts[] = m.split("[|]");
					if (parts.length > 0)
						list.add(
							MetadataUtil.asCodedMetadataType(
								parts[0],
								parts.length > 1 ? parts[1] : null,
								parts.length > 2 ? parts[2] : null,
								parts.length > 3 ? parts[3] : null
							)
						);
				}
			}
			// Silently ignore cases where we don't know how to map.
			// Previous API checks will catch these!
		}
		return list;
	}
	
	protected DateTimeRange getTimeRange(String name, Date from, Date to) throws MalformedQueryException
	{
		if (from == null && to == null)
			return null;
		if (from == null)
			from = YESTERYEAR;
		if (to == null)
			to = TOMORROWLAND;
		
		return new DateTimeRange(name, DT.asUTCString(from), DT.asUTCString(to));
	}
	
	/**
	 * Perform the XDS Query based on the specified parameters
	 * @param p	The parameters to search for.
	 * @param log	A place to write messages.
	 * @return	The Query result as an XDSQueryResponseType
	 */
	protected XDSQueryResponseType doXDSQuery(SearchParameters p, Context log)
	{
		// A list of dateTimeRanges to use in searching.
        ArrayList<DateTimeRange> dateTimeRanges = 
        	new ArrayList<DateTimeRange>();
        
        // The formatCodes to search for, after mapping from p.getFormat()
        ArrayList<CodedMetadataType> formatCodes = mapCodes(p.getFormat(), formatMap);
        
        // The classCodes to search for, after mapping from p.getClasses()
        ArrayList<CodedMetadataType> classCodes = mapCodes(p.getClasses(), classMap);

        try
        {
        	DateTimeRange r = null;
        	
        	// Set up time ranges for the search.
        	r = getTimeRange("serviceStartTime", p.getServiceStartTimeFrom(), p.getServiceStartTimeTo());
        	if (r != null) dateTimeRanges.add(r);
        	r = getTimeRange("serviceStopTime", p.getServiceStopTimeFrom(), p.getServiceStopTimeTo());
            if (r != null) dateTimeRanges.add(r);
            r = getTimeRange("creationTime", p.getCreationTimeFrom(), p.getCreationTimeTo());
            if (r != null) dateTimeRanges.add(r);
            
			// Set up the patient we are querying for
            CX patientId = getPatientId(p.getPatientID());
			
			// Generate the query from the converted parameters
			FindDocumentsQuery q = new FindDocumentsQuery(
				patientId, 
				classCodes.toArray(new CodedMetadataType[classCodes.size()]), 
				dateTimeRanges.toArray(new DateTimeRange[dateTimeRanges.size()]), 
				(CodedMetadataType[])null,
				(CodedMetadataType[])null, 
				(CodedMetadataType[])null, 
				(CodedMetadataType[])null, 
				formatCodes.toArray(new CodedMetadataType[formatCodes.size()]), 
				null, // authorPerson added by IHE 2008 CP 357
				APPROVED);
					
			// Invoke it.
			XDSQueryResponseType t = registry.invokeStoredQuery(q, false);
			
			return t;
        }
        catch (MalformedStoredQueryException ex1)
        {
        	log.log("Malformed Stored Query", ex1);
        }
        catch (MalformedQueryException ex2)
        {
        	log.log("Malformed Query", ex2);
        } 
        catch (Exception ex3) 
        {
        	log.log("Query Invocation Error", ex3);
		}
        return null;
	}

	@Override
	public Set<String> getSupportedFormats() {
		return formatMap.keySet();
	}


	@Override
	public Set<String> getSupportedClasses() {
		return classMap.keySet();
	}
	
	@Override
	public String getDocumentIdentifier(Entry entry)
	{
		String uuid = entry.getId();
		if (uuid == null || uuid.length() == 0)
			return null;
		// Return everything after the last :
		return uuid.substring(uuid.lastIndexOf(':')+1);
	}

	WeakHashMap<String, DocumentRequestType> documentRequestCache = new WeakHashMap<String, DocumentRequestType>();
	public List<DocumentRequestType> fetchDocumentInfo(String documentUuid[], Context log) 
	{
		ArrayList<DocumentRequestType> rq = new ArrayList<DocumentRequestType>();
		try
		{
			GetDocumentsQuery query = new GetDocumentsQuery(documentUuid, true);
			XDSQueryResponseType
				queryResponse = registry.invokeStoredQuery(query, false);
			for (DocumentEntryResponseType docEntry : 
					queryResponse.getDocumentEntryResponses())
			{
				DocumentRequestType docRequest =
					RetrieveFactory.eINSTANCE.createDocumentRequestType();
				docRequest.setDocumentUniqueId(
					docEntry.getDocumentEntry().getUniqueId()
				);
				docRequest.setRepositoryUniqueId(
					docEntry.getDocumentEntry().getRepositoryUniqueId()
				);
				docRequest.setHomeCommunityId(
					docEntry.getHomeCommunityId()
				);
				
				// Cache this information for future use
				documentRequestCache.put(
					docEntry.getDocumentEntry().getEntryUUID(),
					docRequest
				);
				
				rq.add(docRequest);
			}
		}
		catch (Exception e)
		{
			log.log("Error Fetching Document Information ", e);
		}
		
		return rq;
	}
	
	public CX getPatientId(String patientId)
	{
		// This should really be doing a PIX/PDQ Lookup
		CX pid = Hl7v2Factory.eINSTANCE.createCX();
		pid.setIdNumber(patientId);
		
		// Set Assigning Authority ID and Type
		pid.setAssigningAuthorityUniversalId(idSource);
		pid.setAssigningAuthorityUniversalIdType("ISO");
		
		return pid;
	}
	
	@Override
	public Content[] getDocumentContent(String[] documentIdentifiers, String patientId, Context log) {
		CX pid = getPatientId(patientId);
		
		// TODO Implement this.  The challenge is that we must know
		// The Home Community ID?, the repositoryUniqueID, and the documentUniqueId
		RetrieveDocumentSetRequestType request = RetrieveFactory.eINSTANCE.createRetrieveDocumentSetRequestType();
		
		ArrayList<String> fetch = new ArrayList<String>();
		
		// Lookup what we need to get documents, saving any we don't find for a 
		// subsequent fetch operation.
		for (String docId: documentIdentifiers)
		{	DocumentRequestType dr = documentRequestCache.get(docId);
			if (dr != null)
				request.getDocumentRequest().add(dr);
			else
				fetch.add(docId);
		}
		// If there's anything we need to fetch, go do it.
		if (fetch.size() != 0)
		{	List<DocumentRequestType> fetched = fetchDocumentInfo(fetch.toArray(new String[fetch.size()]), log);
			request.getDocumentRequest().addAll(fetched);
		}
		
		List<Content> contents = new ArrayList<Content>();
		XDSRetrieveResponseType rds = registry.retrieveDocumentSet(request, pid);
		for (XDSDocument docEntry : rds.getAttachments())
		{
			final XDSDocument e = docEntry;
			Content content = new Content() {
				private final XDSDocument entry = e;

				@Override
				public InputStream getContent() {
					return entry.getStream();
				}

				@Override
				public String getDocumentIdentifier() {
					return entry.getDocumentEntryUUID();
				}
				
			};
			contents.add(content);
		}
		return contents.toArray(new Content[contents.size()]);
	}

	public XDSFormatter getFormatter(SearchParameters search) {
		switch (search.getSearchMode()) {
		case ABBI:
			return new ABBIXDSFormatter(search);
		case MHD:
			return new MHDXDSFormatter(search);
		case FHIR:
			return new FHIRXDSFormatter(search);
		}
		return null;
	}

}
