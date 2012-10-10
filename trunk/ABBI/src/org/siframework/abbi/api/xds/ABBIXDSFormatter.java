package org.siframework.abbi.api.xds;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.emf.common.util.EList;
import org.openhealthtools.ihe.xds.metadata.AuthorType;
import org.openhealthtools.ihe.xds.metadata.CodedMetadataType;
import org.openhealthtools.ihe.xds.response.DocumentEntryResponseType;
import org.siframework.abbi.api.Logger;
import org.siframework.abbi.api.SearchParameters;
import org.siframework.abbi.api.Util;
import org.siframework.abbi.atom.Entry;
import org.siframework.abbi.atom.Feed;
import org.siframework.abbi.atom.Person;
import org.siframework.abbi.atom.XML;
import org.siframework.abbi.atom.impl.CategoryImpl;
import org.siframework.abbi.atom.impl.EntryImpl;
import org.siframework.abbi.atom.impl.PersonImpl;
import org.siframework.abbi.servlet.Search;
import org.w3c.dom.Document;

public class ABBIXDSFormatter implements XDSFormatter {

	String outputFormat = null;
	public ABBIXDSFormatter(SearchParameters search) {
		// TODO Auto-generated constructor stub
		outputFormat = Search.ATOM;
	}

	@Override
	// TODO: entry must contain the document path
	public Entry format(DocumentEntryResponseType der, Logger log)
	{
		// Create a new feed entry
		Entry e = new EntryImpl();
		
		String uuid = der.getDocumentEntry().getEntryUUID();
		
		try {
			e.setId(new URI(uuid));
			e.setContentSrc(new URI("/Document/" + uuid.substring(uuid.lastIndexOf(':')+1)));
		} catch (Exception ex) {
			log.log("Error setting entry id", ex);
		}
		
		// Set the title to the document title
		e.setTitle(MetadataUtil.asString(der.getDocumentEntry().getTitle()));
		
		// Set the summary to the document comments (if present) 
		e.setSummary(
				MetadataUtil.asString(der.getDocumentEntry().getComments())
			);

		// Set the author names
		EList<AuthorType> eAuthors = (EList<AuthorType>)der.getDocumentEntry().getAuthors();
		ArrayList<Person> al = new ArrayList<Person>();
		for (AuthorType auth : eAuthors)
			al.add(new PersonImpl(MetadataUtil.getName(auth.getAuthorPerson())));
		e.setAuthors(al);
		
		// We retain original categories from the registry for added value.
		CodedMetadataType classCode = der.getDocumentEntry().getClassCode();
		try {
			CategoryImpl classCategory;
			classCategory = new CategoryImpl(
				classCode.getCode(), 
				MetadataUtil.asString(classCode.getDisplayName()), 
				MetadataUtil.asURI(classCode.getSchemeUUID()), 
				classCode.getSchemeName()
			);
			e.getCategories().add(classCategory);
		} catch (URISyntaxException e1) {
			log.log("Cannot create class category for " + MetadataUtil.asString(classCode), e1);
		}
		
		// Store the formatCode as a category
		CodedMetadataType formatCode = der.getDocumentEntry().getFormatCode();
		try {
			CategoryImpl formatCategory;
			formatCategory = new CategoryImpl(
				formatCode.getCode(), 
				MetadataUtil.asString(formatCode.getDisplayName()), 
				MetadataUtil.asURI(formatCode.getSchemeUUID()), 
				formatCode.getSchemeName()
			);
			e.getCategories().add(formatCategory);
		} catch (URISyntaxException e1) {
			log.log("Cannot create format category for " + MetadataUtil.asString(formatCode), e1);
		}

		String mimeType = der.getDocumentEntry().getMimeType();
		e.setContentType(mimeType);
		String entryUUID = der.getDocumentEntry().getEntryUUID();
		
		// TODO: Categories based on classCode and formatCode should be mapped back into API Values.
		
		// Sent the entry Id to the uuid of the document
		try {
			URI uri = MetadataUtil.asURI(entryUUID);
			e.setId(uri);
		} catch (URISyntaxException e1) {
			log.log("Error setting content entry UUID " + der.getDocumentEntry().getEntryUUID(), e1);
		}
		
		// Set the creation and published time for this document.
		Date creationTime = Util.parseDate(der.getDocumentEntry().getCreationTime());
		e.setUpdated(creationTime);
		e.setPublished(creationTime);
		
		return e;
	}	
	
	public String getDocumentPath(Entry entry, Logger log)
	{
		URI uuid = entry.getId();
		if (uuid == null)
			return null;
		String sUUID = uuid.toString();
		
		// Return everything after the last :
		sUUID = sUUID.substring(sUUID.lastIndexOf(':')+1);
		// TODO: Ensure contentType in the entry is the appropriate kind of content
		// based on the target content type.
		return "/Document/" + sUUID + "/?mimeType=" + entry.getContentType();
	}

}
