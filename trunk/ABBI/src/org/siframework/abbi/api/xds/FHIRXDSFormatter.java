package org.siframework.abbi.api.xds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.common.hl7v2.XCN;
import org.openhealthtools.ihe.common.hl7v2.XON;
import org.openhealthtools.ihe.xds.metadata.AuthorType;
import org.openhealthtools.ihe.xds.metadata.CodedMetadataType;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import org.openhealthtools.ihe.xds.response.DocumentEntryResponseType;
import org.siframework.abbi.api.Context;
import org.siframework.abbi.api.SearchParameters;
import org.siframework.abbi.atom.Entry;
import org.siframework.abbi.atom.XMLMarshaller;
import org.siframework.abbi.atom.impl.CategoryImpl;
import org.siframework.abbi.atom.impl.EntryImpl;
import org.siframework.abbi.servlet.Search;
import org.siframework.abbi.utility.IO;
import org.siframework.abbi.utility.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FHIRXDSFormatter implements XDSFormatter {

	public static final String availabilityStatusMap[] = { 
		"Submitted", "Approved", "Deprecated", "Withdrawn"
	};
	// The FHIR Namespace
	public static final String FHIRNS = "http://hl7.org/fhir";
	public static final String XHTMLNS = "http://www.w3.org/1999/xhtml";

	String outputFormat = null;
	String baseURL = null;
	public FHIRXDSFormatter(SearchParameters search) {
		outputFormat = search.getOutputFormat();
		baseURL = search.getBaseURL();
	}


	@Override
	public Entry format(DocumentEntryResponseType der, Context ctx)
	{
		DocumentEntryType de = der.getDocumentEntry();
		EntryImpl e = new EntryImpl();
		
		
		String uuid = der.getDocumentEntry().getEntryUUID();
		e.setId(uuid);
		uuid = URLEncoder.encode(uuid.substring(uuid.lastIndexOf(':')+1));
		e.setContentSrc(baseURL + "/xdsentry/@" + uuid);
		
		e.setPublished(new Date());
		
		/* TODO Set the author to the author of the submission set, and updated to the
		 * timestamp on the submission set that updated this entry
		 */
		/*
			e.getAuthors().add(
				new PersonImpl()
			);
			e.setUpdated(null); // TODO Figure this out.
		*/
		
		e.getCategories().add(new CategoryImpl("XdsEntry", "http://hl7.org/fhir/sid/fhir/resource-types"));

		Document d = XML.createDocument();
		Element xdsEntry = d.createElementNS(FHIRNS, "XdsEntry");
		Element te;
		xdsEntry.setAttribute("xmlns", FHIRNS);   // Is this necessary?
		d.appendChild(xdsEntry);
		
		XML.append(xdsEntry, te = d.createElementNS(FHIRNS, "id"));
		// The ID of this resource is the XDSDocumentEntryUUID
		te.appendChild(d.createTextNode(de.getEntryUUID()));
		e.setId(de.getEntryUUID());
		
		XML.append(xdsEntry, XMLMarshaller.createSimple(d, "repositoryId", de.getRepositoryUniqueId()));

		XML.append(xdsEntry, XMLMarshaller.createSimple(d, "mimeType",de.getMimeType()));
		XML.append(xdsEntry, createCode(d, "format", de.getFormatCode()));
		XML.append(xdsEntry, createCode(d, "class", de.getClassCode()));
		XML.append(xdsEntry, createCode(d, "type", de.getTypeCode()));
		XML.append(xdsEntry, XMLMarshaller.createSimple(d, "title", MetadataUtil.asString(de.getTitle())));
		e.setTitle(MetadataUtil.asString(de.getTitle()));
		
		XML.append(xdsEntry, XMLMarshaller.createSimple(d, "documentId", de.getUniqueId()));
		XML.append(xdsEntry, XMLMarshaller.createSimple(d, "availability", 
			availabilityStatusMap[de.getAvailabilityStatus().getValue()]
		));
		
		for (CodedMetadataType code: (List<CodedMetadataType>)de.getConfidentialityCode())
			XML.append(xdsEntry, createCode(d, "confidentiality", code));
		
		XML.append(xdsEntry, XMLMarshaller.createSimple(d, "created", MetadataUtil.reformatISODate(de.getCreationTime())));

		for (CodedMetadataType code: (List<CodedMetadataType>)de.getEventCode())
			XML.append(xdsEntry, createCode(d, "event", code));

		XML.append(xdsEntry, XMLMarshaller.createSimple(d, "hash", de.getHash()));
		XML.append(xdsEntry, XMLMarshaller.createSimple(d, "size", de.getSize()));
		XML.append(xdsEntry, XMLMarshaller.createSimple(d, "language", de.getLanguageCode()));

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
		XML.append(xdsEntry, XMLMarshaller.createSimple(d, "homeCommunity", der.getHomeCommunityId()));
		Element service = d.createElementNS(FHIRNS, "service");
		xdsEntry.appendChild(service);
		XML.append(service, XMLMarshaller.createSimple(d, "start", MetadataUtil.reformatISODate(de.getServiceStartTime())));
		XML.append(service, XMLMarshaller.createSimple(d, "stop", MetadataUtil.reformatISODate(de.getServiceStopTime())));
		XML.append(xdsEntry, XMLMarshaller.createSimple(d, "comments", MetadataUtil.asString(de.getComments())));
		
		Element attachment = d.createElementNS(FHIRNS, "attachment");
		XML.append(attachment, XMLMarshaller.createSimple(d, "mimeType", de.getMimeType()));
		
		XML.append(attachment, XMLMarshaller.createSimple(d, "url", baseURL + "/Document/"+uuid));

		// TODO Should be ISO-639-3, but I don't like that
		XML.append(attachment, XMLMarshaller.createSimple(d, "lang", de.getLanguageCode()));
		
		xdsEntry.appendChild(attachment);
		
		//<text><!-- 1..1 Narrative Text summary of organization, fall back for human interpretation --></text>
		
		DOMSource unfinishedXdsEntry = new DOMSource(xdsEntry);
		
		Transformer t;
		try {
			DOMResult finishedXdsEntry = new DOMResult();
			TransformerFactory tf = TransformerFactory.newInstance();
			InputStream is = ctx.getResource("/WEB-INF/xsl/xdsentryText.xsl");
			t = tf.newTransformer(new StreamSource(is));
			t.setOutputProperty("omit-xml-declaration", "yes");
			t.setOutputProperty("indent", "yes");
			t.transform(unfinishedXdsEntry, finishedXdsEntry);
			
			t.transform(unfinishedXdsEntry, new StreamResult(System.err));
			
			// Then find the <div> element inside of it.
			Document finishedDoc = (Document)finishedXdsEntry.getNode();
			NodeList divList = finishedDoc.getElementsByTagNameNS(XHTMLNS, "div");

			// Create an identity transformer
			t = tf.newTransformer();
			t.setOutputProperty("omit-xml-declaration", "yes");
			t.setOutputProperty("indent", "yes");

			// If there was one, write it out to a string
			if (divList.getLength() != 0)
			{	DOMSource divElement = new DOMSource(divList.item(0));
				StringWriter w = new StringWriter();
				t.transform(divElement, new StreamResult(w));
				// And set that string as the summary
				e.setSummary(w.getBuffer().toString());
				System.err.println(w.getBuffer().toString());
			}
			

			if (Search.ATOM_MIMETYPE.equals(outputFormat))
			{	// copy the xdsEntryResult to a byte array
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				t.transform(new DOMSource(finishedDoc), new StreamResult(bos));
				e.setContent(new ByteArrayInputStream(bos.toByteArray()));
				e.setContentType("text/xml");
			}
			else if (Search.JSON_MIMETYPE.equals(outputFormat))
			{	String json = FHIR2JSON.toJson(finishedDoc);
				e.setContent(new ByteArrayInputStream(IO.toUTF8(json)));
				e.setContentType("application/json");
			}
		} 
		catch (Exception e1) 
		{
			ctx.log("Transformation Error", e1);
		}
		
		return e;
	}


	public Element createAuthenticator(Document d, XCN authenticator)
	{
		Element e = d.createElementNS(FHIRNS, "authenticator");
		XML.append(e, createIdentifier(d, authenticator));
		XML.append(e, createName(d, authenticator));
		return e.hasChildNodes() ? e : null;
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
		Element e = d.createElementNS(FHIRNS, "author");
		XML.append(e, createName(d, author.getAuthorPerson()));
		XML.append(e, createIdentifier(d, author.getAuthorPerson()));
		for (String v: (List<String>)author.getAuthorRole())
			XML.append(e, XMLMarshaller.createSimple(d, "role", v));
		for (String v: (List<String>)author.getAuthorSpeciality())
			XML.append(e, XMLMarshaller.createSimple(d, "speciality", v));
		for (XON xon: (List<XON>)author.getAuthorInstitution())
			XML.append(e, createInstitution(d, xon));
		return e;
	}


	public Element createCode(Document d, String name, CodedMetadataType code)
	{
		Element e = d.createElementNS(FHIRNS, name);
		XML.append(e, XMLMarshaller.createSimple(d, "code", code.getCode()));
		XML.append(e, XMLMarshaller.createSimple(d, "system", code.getSchemeUUID()));
		XML.append(e, XMLMarshaller.createSimple(d, "display", MetadataUtil.asString(code.getDisplayName())));
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
		
		Element e = d.createElementNS(FHIRNS, name);
		XML.append(e, XMLMarshaller.createSimple(d, "system", pre + system));
		XML.append(e, XMLMarshaller.createSimple(d, "id", id));
		return e;
	}


	public Element createIdentifier(Document d, XCN person)
	{
		if (person == null)
			return null;
	
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
		Element e = d.createElementNS(FHIRNS, "institution");
		XML.append(e, createIdentifier(d, inst));
		XML.append(e, XMLMarshaller.createSimple(d, "name", inst.getOrganizationName()));
		return e;
	}


	public Element createName(Document d, XCN person)
	{
		if (person == null)
			return null;
		
		Element name = d.createElementNS(FHIRNS, "name");
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
	
		Element part = d.createElementNS(FHIRNS, "part");
		XML.append(part, XMLMarshaller.createSimple(d, "type", type));
		XML.append(part, XMLMarshaller.createSimple(d, "value", value));
		return part;
	}
}
