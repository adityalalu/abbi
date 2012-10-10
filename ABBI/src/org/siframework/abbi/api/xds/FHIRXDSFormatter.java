package org.siframework.abbi.api.xds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.common.hl7v2.XCN;
import org.openhealthtools.ihe.common.hl7v2.XON;
import org.openhealthtools.ihe.xds.metadata.AuthorType;
import org.openhealthtools.ihe.xds.metadata.CodedMetadataType;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import org.openhealthtools.ihe.xds.response.DocumentEntryResponseType;
import org.siframework.abbi.api.Logger;
import org.siframework.abbi.api.SearchParameters;
import org.siframework.abbi.atom.Entry;
import org.siframework.abbi.atom.XML;
import org.siframework.abbi.atom.impl.CategoryImpl;
import org.siframework.abbi.atom.impl.EntryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FHIRXDSFormatter implements XDSFormatter {

	public static final String availabilityStatusMap[] = { 
		"Submitted", "Approved", "Deprecated", "Withdrawn"
	};


	public FHIRXDSFormatter(SearchParameters search) {
		// TODO Auto-generated constructor stub
	}


	@Override
	// TODO: entry must contain the document path
	public Entry format(DocumentEntryResponseType der, Logger log)
	{
		DocumentEntryType de = der.getDocumentEntry();
		EntryImpl e = new EntryImpl();
		
		Document d = null;
		try {
			d = XML.createDocument();
		} catch (ParserConfigurationException ex) {
			log.log("Parse Error", ex);
			return null;
		}
		
		String uuid = der.getDocumentEntry().getEntryUUID();
		try {
			e.setId(new URI(uuid));
			uuid = "/xdsentry/@" + URLEncoder.encode(uuid.substring(uuid.lastIndexOf(':')+1));
			e.setContentSrc(new URI(uuid));
		} catch (Exception ex) {
			log.log("Error setting entry id", ex);
		}
		
		e.setPublished(new Date());
		
		/* TODO
		 * Set the author to the author of the submission set, and updated to the
		 * timestamp on the submission set that updated this entry
		 */
		/*
			e.getAuthors().add(
				new PersonImpl()
			);
			e.setUpdated(null); // TODO Figure this out.
		*/
		
		try {
			e.getCategories().add(new CategoryImpl("XdsEntry", new URI("http://hl7.org/fhir/sid/fhir/resource-types")));
		} catch (URISyntaxException e3) {
			log.log("URI Malfunction", e3);
		}

		Element xdsEntry = d.createElementNS(XML.FHIRNS, "XdsEntry");
		Element te;
		xdsEntry.setAttribute("xmlns", XML.FHIRNS);   // Is this necessary?
		d.appendChild(xdsEntry);
		
		XML.append(xdsEntry, te = d.createElementNS(XML.FHIRNS, "id"));
		// The ID of this resource is the XDSDocumentEntryUUID
		te.appendChild(d.createTextNode(de.getEntryUUID()));
		try {
			e.setId(new URI(de.getEntryUUID()));
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			log.log("Bad Id: " + de.getEntryUUID(), e2);
		}
		XML.append(xdsEntry, XML.createSimple(d, "repositoryId", de.getRepositoryUniqueId()));

		XML.append(xdsEntry, XML.createSimple(d, "mimeType",de.getMimeType()));
		XML.append(xdsEntry, createCode(d, "format", de.getFormatCode()));
		XML.append(xdsEntry, createCode(d, "class", de.getClassCode()));
		XML.append(xdsEntry, createCode(d, "type", de.getTypeCode()));
		XML.append(xdsEntry, XML.createSimple(d, "title", MetadataUtil.asString(de.getTitle())));
		e.setTitle(MetadataUtil.asString(de.getTitle()));
		
		XML.append(xdsEntry, XML.createSimple(d, "documentId", de.getUniqueId()));
		XML.append(xdsEntry, XML.createSimple(d, "availability", 
			availabilityStatusMap[de.getAvailabilityStatus().getValue()]
		));
		
		for (CodedMetadataType code: (List<CodedMetadataType>)de.getConfidentialityCode())
			XML.append(xdsEntry, createCode(d, "confidentiality", code));
		
		XML.append(xdsEntry, XML.createSimple(d, "created", MetadataUtil.reformatISODate(de.getCreationTime())));

		for (CodedMetadataType code: (List<CodedMetadataType>)de.getEventCode())
			XML.append(xdsEntry, createCode(d, "event", code));

		XML.append(xdsEntry, XML.createSimple(d, "hash", de.getHash()));
		XML.append(xdsEntry, XML.createSimple(d, "size", de.getSize()));
		XML.append(xdsEntry, XML.createSimple(d, "language", de.getLanguageCode()));

		CX pid = de.getPatientId();
		XML.append(xdsEntry,
				createIdentifier(d, "patientId", 
						pid.getAssigningAuthorityUniversalIdType(),
						pid.getAssigningAuthorityUniversalId(),
						pid.getIdNumber()
				)
		);
		
		pid = de.getSourcePatientId();
		XML.append(xdsEntry,
				createIdentifier(d, "sourcePatientId", 
						pid.getAssigningAuthorityUniversalIdType(),
						pid.getAssigningAuthorityUniversalId(),
						pid.getIdNumber()
				)
		);
		// TODO Handle: de.getSourcePatientInfo();
		
		for (AuthorType author: (List<AuthorType>)de.getAuthors())
			XML.append(xdsEntry, createAuthor(d, author));
		
		XML.append(xdsEntry, createAuthenticator(d, de.getLegalAuthenticator()));
		
		XML.append(xdsEntry, createCode(d, "facilityType", de.getHealthCareFacilityTypeCode()));
		XML.append(xdsEntry, createCode(d, "practiceSetting", de.getPracticeSettingCode()));
		XML.append(xdsEntry, XML.createSimple(d, "homeCommunity", der.getHomeCommunityId()));
		Element service = d.createElementNS(XML.FHIRNS, "service");
		xdsEntry.appendChild(service);
		XML.append(service, XML.createSimple(d, "start", MetadataUtil.reformatISODate(de.getServiceStartTime())));
		XML.append(service, XML.createSimple(d, "stop", MetadataUtil.reformatISODate(de.getServiceStopTime())));
		XML.append(xdsEntry, XML.createSimple(d, "comments", MetadataUtil.asString(de.getComments())));
		//<text><!-- 1..1 Narrative Text summary of organization, fall back for human interpretation --></text>
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		StreamResult r = new StreamResult(bos);
		DOMSource s = new DOMSource(xdsEntry);
		
		Transformer t;
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			// TODO Generate <text> and <summary> content by applying
			// stylesheets to the existing resource content.  For now,
			// we just copy it.
			t = tf.newTransformer();
			t.setOutputProperty("omit-xml-declaration", "yes");
			t.transform(s, r);
			e.setContent(new ByteArrayInputStream(bos.toByteArray()));
			e.setContentType("text/xml");
		} 
		catch (Exception e1) 
		{
			log.log("Transformation Error", e1);
		}
		
		return e;
	}


	public Element createAuthenticator(Document d, XCN authenticator)
	{
		Element e = d.createElementNS(XML.FHIRNS, "authenticator");
		XML.append(e, createIdentifier(d, authenticator));
		XML.append(e, createName(d, authenticator));
		return e;
	}


	public Element createAuthor(Document d, AuthorType author)
	{
		/*
		<author>  <!-- 1..* human/machine that authored document -->
		  <name><!-- 0..1 HumanName name of human/machine --></name>
		  <id><!-- 0..1 Identifier id of human/machine --></id>
		  <role><!-- 0..* string role of author wrt to the patient --></role>
		  <specialty><!-- 0..* string speciality of facility per affinity domain --></specialty>
		  <institution>  <!-- 0..* facilty under which document authored -->
		   <id><!-- 0..1 Identifier id of facility --></id>
		   <name><!-- 0..1 string name of facility --></name>
		  </institution>
		  <contact><!-- 0..* Contact contact details for author --></contact>
		 </author>
		 */
		Element e = d.createElementNS(XML.FHIRNS, "author");
		XML.append(e, createName(d, author.getAuthorPerson()));
		XML.append(e, createIdentifier(d, author.getAuthorPerson()));
		for (String v: (List<String>)author.getAuthorRole())
			XML.append(e, XML.createSimple(d, "role", v));
		for (String v: (List<String>)author.getAuthorSpeciality())
			XML.append(e, XML.createSimple(d, "speciality", v));
		for (XON xon: (List<XON>)author.getAuthorInstitution())
			XML.append(e, createInstitution(d, xon));
		return e;
	}


	public Element createCode(Document d, String name, CodedMetadataType code)
	{
		Element e = d.createElementNS(XML.FHIRNS, name);
		XML.append(e, XML.createSimple(d, "code", code.getCode()));
		XML.append(e, XML.createSimple(d, "system", code.getSchemeUUID()));
		XML.append(e, XML.createSimple(d, "display", MetadataUtil.asString(code.getDisplayName())));
		return e;
	}


	public Element createIdentifier(Document d, String name, String idType, String system, String id)
	{
		if (id == null || id.length() == 0)
			return null;
		
		String pre = "";
		if ("ISO".equals(idType))
			pre = "urn:oid:";
		else if ("UUID".equals(idType) || "GUID".equals(idType))
			pre = "urn:uuid";
		else if ("DNS".equals(idType))
			pre = "socket:";
		
		Element e = d.createElementNS(XML.FHIRNS, name);
		XML.append(e, XML.createSimple(d, "system", pre + system));
		XML.append(e, XML.createSimple(d, "id", id));
		return e;
	}


	public Element createIdentifier(Document d, XCN person)
	{
		if (person == null)
			return null;
		
		String idType = person.getAssigningAuthorityUniversalIdType();
	
		return createIdentifier(d, "id",
				person.getAssigningAuthorityUniversalIdType(), 
				person.getAssigningAuthorityUniversalId(),
				person.getIdNumber());
	}


	public Element createIdentifier(Document d, XON inst)
	{

		return createIdentifier(d, "id",
				inst.getAssigningAuthorityUniversalIdType(),
				inst.getAssigningAuthorityUniversalId(),
				inst.getIdNumber()
		);
	}


	public Element createInstitution(Document d, XON inst)
	{
		Element e = d.createElementNS(XML.FHIRNS, "institution");
		XML.append(e, createIdentifier(d, inst));
		XML.append(e, XML.createSimple(d, "name", inst.getOrganizationName()));
		return e;
	}


	public Element createName(Document d, XCN person)
	{
		if (person == null)
			return null;
		
		Element name = d.createElementNS(XML.FHIRNS, "name");
		XML.append(name, createNamePart(d, "prefix", person.getPrefix()));
		XML.append(name, createNamePart(d, "given", person.getGivenName()));
		XML.append(name, createNamePart(d, "given", person.getOtherName()));
		XML.append(name, createNamePart(d, "family", person.getFamilyName()));
		XML.append(name, createNamePart(d, "suffix", person.getSuffix()));
		return name;
	}


	public Element createNamePart(Document d, String type, String value)
	{	if (type == null || value == null || value.length() == 0 || type.length() == 0)
			return null;
	
		Element part = d.createElementNS(XML.FHIRNS, "part");
		XML.append(part, XML.createSimple(d, "type", type));
		XML.append(part, XML.createSimple(d, "value", value));
		return part;
	}
}
