package org.siframework.abbi.api.xds;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.emf.common.util.EList;
import org.openhealthtools.ihe.xds.metadata.AuthorType;
import org.openhealthtools.ihe.xds.metadata.CodedMetadataType;
import org.openhealthtools.ihe.xds.response.DocumentEntryResponseType;
import org.siframework.abbi.api.Context;
import org.siframework.abbi.api.SearchParameters;
import org.siframework.abbi.atom.Entry;
import org.siframework.abbi.atom.Person;

import org.siframework.abbi.atom.impl.CategoryImpl;
import org.siframework.abbi.atom.impl.EntryImpl;
import org.siframework.abbi.atom.impl.PersonImpl;
import org.siframework.abbi.servlet.Search;
import org.siframework.abbi.utility.DT;


public class ABBIXDSFormatter implements XDSFormatter {

	String outputFormat = null;
	String baseURL = null;
	
	public ABBIXDSFormatter(SearchParameters search) {
		outputFormat = Search.ATOM_MIMETYPE;
		baseURL = search.getBaseURL();
	}

	@Override
	public Entry format(DocumentEntryResponseType der, Context log)
	{
		// Create a new feed entry
		Entry e = new EntryImpl();
		
		String uuid = der.getDocumentEntry().getEntryUUID();
		e.setId(uuid);
		e.setContentSrc(baseURL + "/Document/" + uuid.substring(uuid.lastIndexOf(':')+1));
		
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
		CategoryImpl classCategory;
		classCategory = new CategoryImpl(
			classCode.getCode(), 
			MetadataUtil.asString(classCode.getDisplayName()), 
			classCode.getSchemeUUID(), 
			classCode.getSchemeName()
		);
		e.getCategories().add(classCategory);
		
		// Store the formatCode as a category
		CodedMetadataType formatCode = der.getDocumentEntry().getFormatCode();
		CategoryImpl formatCategory;
		formatCategory = new CategoryImpl(
			formatCode.getCode(), 
			MetadataUtil.asString(formatCode.getDisplayName()), 
			formatCode.getSchemeUUID(), 
			formatCode.getSchemeName()
		);
		e.getCategories().add(formatCategory);

		String mimeType = der.getDocumentEntry().getMimeType();
		e.setContentType(mimeType);
		String entryUUID = der.getDocumentEntry().getEntryUUID();
		
		// TODO: Categories based on classCode and formatCode should be mapped back into API Values.
		
		// Sent the entry Id to the uuid of the document
		e.setId(entryUUID);
		
		// Set the creation and published time for this document.
		Date creationTime = DT.parseDate(der.getDocumentEntry().getCreationTime());
		e.setUpdated(creationTime);
		e.setPublished(creationTime);
		
		return e;
	}	
	
	public String getDocumentPath(Entry entry, Context log)
	{
		String uuid = entry.getId();
		if (uuid == null)
			return null;
		
		// Return everything after the last :
		uuid = uuid.substring(uuid.lastIndexOf(':')+1);
		
		// TODO: Ensure contentType in the entry is the appropriate kind of content
		// based on the target content type.
		return "/Document/" + uuid + "/?mimeType=" + entry.getContentType();
	}

}
