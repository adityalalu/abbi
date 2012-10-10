package org.siframework.abbi.api;

import org.siframework.abbi.atom.Entry;
import org.siframework.abbi.atom.Feed;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * The API Interface describes the interface used to respond to search queries and document
 * retrieval requests by the back end data holder.  Implement a class using this interface
 * to provide support for a back end data holder.  The implementing class must have a no
 * argument constructor.  The init() method will be called before any other API methods are
 * accessed.  It may be called again to reinitialize the data store.  This class should be
 * thread-safe as methods may be called by multiple threads.
 * @author Keith W. Boone
 *
 */
public interface API {
	public enum Mode {
		ABBI, FHIR, MHD
	};
	
	public interface Content {
		public InputStream getContent();
		public String getDocumentIdentifier();
	};
	/**
	 * Initialize the API from the specified initialization properties.
	 * @param p	The properties to use for initialization
	 * @param log	A logger to report errors during initialization
	 * @throws Exception An exception describing any errors found during initilization
	 */
	public void init(Properties p, Logger log) throws Exception;
	
	/**
	 * Search for the requested content and return an Atom feed containing
	 * the results, sorted in the appropriate order.
	 * @param search	The parameters to use when searching
	 * @return The atom feed to return to the caller
	 */
	public List<Entry> search(SearchParameters search, Mode searchMode, Logger log);
	
	/**
	 * Returns the list of formats supported by the implementation
	 * @return The list of supported formats.
	 */
	public Set<String> getSupportedFormats();
	
	/**
	 * Returns the list of classes supported by the implementation
	 * @return The list of supported classes.
	 */
	public Set<String> getSupportedClasses();
	
	/**
	 * Returns an identifier that uniquely identifies the specified entry
	 * suitable for use in a URL Path.
	 * @param entry	The feed entry for which the URL is being requested
	 * @return A string that can be used in the API Path to identify the document
	 */
	public String getDocumentIdentifier(Entry entry);
	
	/**
	 * Given an array of document identifiers, return an interface providing
	 * access to the document contents.  Log any retrieval errors using the 
	 * specified logger.  The returned array may be shorter than the set of
	 * documents requested if there are retrieval errors.
	 * @param documentIdentifier	The document identifiers to retrieve	
	 * @return	An array of objects implementing the Content interface
	 */
	public Content[] getDocumentContent(String documentIdentifier[], String patientId, Logger log);
}
