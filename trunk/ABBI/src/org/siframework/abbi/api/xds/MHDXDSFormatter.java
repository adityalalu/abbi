package org.siframework.abbi.api.xds;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.common.hl7v2.SourcePatientInfoType;
import org.openhealthtools.ihe.common.hl7v2.XCN;
import org.openhealthtools.ihe.common.hl7v2.XON;
import org.openhealthtools.ihe.xds.metadata.AuthorType;
import org.openhealthtools.ihe.xds.metadata.CodedMetadataType;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import org.openhealthtools.ihe.xds.response.DocumentEntryResponseType;
import org.siframework.abbi.api.Logger;
import org.siframework.abbi.api.SearchParameters;

import org.siframework.abbi.atom.Entry;
import org.siframework.abbi.atom.impl.EntryImpl;
import org.siframework.abbi.servlet.Search;

import com.google.gson.*;

public class MHDXDSFormatter implements XDSFormatter {

	public class AuthorAdapter implements JsonSerializer<AuthorType> {
	
		@Override
		public JsonElement serialize(AuthorType author, 
				Type typeOfSrc,
				JsonSerializationContext context) 
		{
			JsonObject obj = new JsonObject();
			
			List<String> v = author.getAuthorInstitution();
			if (v.size() != 0)
				obj.add("AuthorInstitution", context.serialize(v, List.class));
			
			if (author.getAuthorPerson() != null)
				obj.add("AuthorPerson", context.serialize(author.getAuthorPerson(), XCN.class));
			
			v = author.getAuthorRole();
			if (v.size() != 0)
				obj.add("AuthorRole", context.serialize(v, List.class));
			
			v = author.getAuthorSpeciality();
			if (v.size() != 0)
				obj.add("AuthorSpeciality", context.serialize(v, List.class));
			return obj;
		}
	}

	public class CodedMetadataAdapter implements JsonSerializer<CodedMetadataType> {
		public JsonElement serialize(CodedMetadataType code, 
				Type typeOfSrc,
				JsonSerializationContext context) 
		{
			JsonObject obj = new JsonObject();
			if (code.getCode() != null)
				obj.add("code", new JsonPrimitive(code.getCode()));
			if (code.getDisplayName() != null)
				obj.add("codeName", new JsonPrimitive(MetadataUtil.asString(code.getDisplayName())));
			if (code.getSchemeName() != null)
				obj.add("codingScheme", new JsonPrimitive(code.getSchemeName()));
			if (code.getSchemeUUID() != null)
				obj.add("schemeUUID", new JsonPrimitive(code.getSchemeUUID()));
			return obj;
		}
	}

	public class CXAdapter implements JsonSerializer<CX> {
	
		@Override
		public JsonElement serialize(CX src, Type typeOfSrc, JsonSerializationContext context) 
		{
			// This is certainly one way to do it, but how ugly
			// A better way to represent this would be
			// { id: "", assigningAuthority: { name: "", id: "", idType: "" }}
			String s = s(src.getIdNumber()) + "^^^" + s(src.getAssigningAuthorityName()) + "&" + 
				s(src.getAssigningAuthorityUniversalId()) + "&" + s(src.getAssigningAuthorityUniversalIdType());
			return new JsonPrimitive(s);
		}
	}

	public class DocumentEntryResponseAdapter 
			implements JsonSerializer<DocumentEntryResponseType> {
		public JsonElement serialize(DocumentEntryResponseType der, 
				Type typeOfSrc,
				JsonSerializationContext context) 
		{
			JsonObject obj = new JsonObject();
			DocumentEntryType de = der.getDocumentEntry();
			List<AuthorType> authors = de.getAuthors();
			if (authors.size() == 1)
				obj.add("author", context.serialize(authors.get(0), AuthorType.class));
			else
			{	JsonArray arr = new JsonArray();
				for (AuthorType author : authors)
					arr.add(context.serialize(author, AuthorType.class));
				obj.add("author", arr);
			}
			
			obj.add("availabilityStatus", context.serialize(de.getAvailabilityStatus()));
			obj.add("classCode", context.serialize(de.getClassCode(),CodedMetadataType.class));
			if (de.getComments() != null)
				obj.add("comments", context.serialize(MetadataUtil.asString(de.getComments())));
			obj.add("confidentialityCode", context.serialize(de.getConfidentialityCode(),List.class));
			if (de.getCreationTime() != null)
				obj.add("creationTime", context.serialize(de.getCreationTime()));
			if (de.getEntryUUID() != null)
				obj.add("entryUUID", context.serialize(de.getEntryUUID()));
			if (de.getEventCode() != null)
				obj.add("eventCodeList", context.serialize(de.getEventCode(),List.class));
			if (de.getFormatCode() != null)
				obj.add("formatCode", context.serialize(de.getFormatCode(),CodedMetadataType.class));
			if (de.getHash() != null)
				obj.add("hash", context.serialize(de.getHash()));
			if (de.getHealthCareFacilityTypeCode() != null)
				obj.add("healthcareFacilityTypeCode", context.serialize(de.getHealthCareFacilityTypeCode(),CodedMetadataType.class));
			if (der.getHomeCommunityId() != null)
				obj.add("homeCommunityId", context.serialize(der.getHomeCommunityId()));
			if (de.getLanguageCode() != null)
				obj.add("languageCode", context.serialize(de.getLanguageCode()));
			if (de.getLegalAuthenticator() != null)
				obj.add("legalAuthenticator", context.serialize(de.getLegalAuthenticator(), CX.class));
			if (de.getMimeType() != null)
				obj.add("mimeType", context.serialize(de.getMimeType()));
			if (de.getPatientId() != null)
				obj.add("patientId", context.serialize(de.getPatientId(), CX.class));
			if (de.getPracticeSettingCode() != null)
				obj.add("practiceSettingCode", context.serialize(de.getPracticeSettingCode(),CodedMetadataType.class));
			if (de.getRepositoryUniqueId() != null)
				obj.add("repositoryUniqueId", context.serialize(de.getRepositoryUniqueId()));
			if (de.getServiceStartTime() != null)
				obj.add("serviceStartTime", context.serialize(de.getServiceStartTime()));
			if (de.getServiceStopTime() != null)
				obj.add("serviceStopTime", context.serialize(de.getServiceStopTime()));
			if (de.getSize() != null)
				obj.add("size", context.serialize(de.getSize()));
			if (de.getSourcePatientId() != null)
				obj.add("sourcePatientId", context.serialize(de.getSourcePatientId(), CX.class));
			if (de.getSourcePatientInfo() != null)
				obj.add("sourcePatientInfo", context.serialize(de.getSourcePatientInfo().toString()));
			if (de.getTitle() != null)
				obj.add("title", context.serialize(MetadataUtil.asString(de.getTitle())));
			if (de.getTypeCode() != null)
				obj.add("typeCode", context.serialize(de.getTypeCode(),CodedMetadataType.class));
			if (de.getUniqueId() != null)
				obj.add("uniqueId", context.serialize(de.getUniqueId()));
	
			return obj;
		}
	}

	public class ListAdapter implements JsonSerializer<List> {
		public JsonElement serialize(List sList, 
				Type typeOfSrc,
				JsonSerializationContext context) 
		{
			JsonArray arr = new JsonArray();
			for (Object s: sList)
			{	if (s != null)
				{	if (s instanceof CodedMetadataType)
						arr.add(context.serialize(s, CodedMetadataType.class));
					else if (s instanceof XON)
						arr.add(context.serialize(s, XON.class));
					else
						arr.add(new JsonPrimitive(s.toString()));
				}
			}
			return arr;
		}
	}

	public class SourcePatientInfoAdapter implements JsonSerializer<SourcePatientInfoType> {
	
		@Override
		public JsonElement serialize(SourcePatientInfoType src, Type typeOfSrc, JsonSerializationContext context) 
		{
			return new JsonPrimitive("");
		}
	}

	public class XCNAdapter implements JsonSerializer<XCN> {
	
		@Override
		public JsonElement serialize(XCN src, Type typeOfSrc, JsonSerializationContext context) 
		{
			// 11375^Welby^Marcus^J^Jr. MD^Dr^^^&1.2.840.113619.6.197&ISO
			// Again icky
			// { id: 11375, name: { family: Welby, given: Marcus, other: J, prefix: "Jr. MD", suffix: "Dr" },
			//	 assigningAuthority: { name : "", id: 1.2.840.113619.6.197, type: "ISO" }}
			// or some such
			String s =
				s(src.getIdNumber()) + "^" + 
				s(src.getFamilyName()) + "^" + 
				s(src.getGivenName()) + "^" + 
				s(src.getOtherName()) + "^" +
				s(src.getSuffix()) + "^" + 
				s(src.getPrefix()) + 
				s(src.getAssigningAuthorityName()) + "&" + 
				s(src.getAssigningAuthorityUniversalId()) + "&" +
				s(src.getAssigningAuthorityUniversalIdType());
			return new JsonPrimitive(s);
		}
	}

	public class XONAdapter implements JsonSerializer<XON> {
	
		@Override
		public JsonElement serialize(XON src, Type typeOfSrc, JsonSerializationContext context) 
		{
			// Some Hospital^^^^^&1.2.3.4.5.6.7.8.9.1789&ISO^^^^45
			// better would be
			// { name: "", id: { id: 45, assigningAuthority { name: "", id: "1.2.3.4.5.6.7.8.9.1789", type: "ISO" };
			// or some such
			String s =
				s(src.getOrganizationName()) + "^^^^^" + 
				s(src.getAssigningAuthorityName()) + "&" + 
				s(src.getAssigningAuthorityUniversalId()) + "&" +
				s(src.getAssigningAuthorityUniversalIdType()) + "^^^^" + s(src.getIdNumber());
			return new JsonPrimitive(s);
		}
	}

	public MHDXDSFormatter(SearchParameters search) {
		// TODO Auto-generated constructor stub
	}

	// TODO: entry must contain the document path
	@Override
	public Entry format(DocumentEntryResponseType der, Logger log)
	{
		StringBuffer b = new StringBuffer();
		String json = null;
		Entry e = new EntryImpl();
		try {
			String uuid = der.getDocumentEntry().getEntryUUID();
			e.setId(new URI(uuid));
			uuid = "/Document/" + uuid.substring(uuid.lastIndexOf(':')+1);
			e.setContentSrc(new URI(uuid));
		} catch (URISyntaxException e2) {
			log.log("Error generating document id", e2);
		}
		try {
			Gson gson = new GsonBuilder()
		     .registerTypeAdapter(CX.class, new CXAdapter())
		     .registerTypeAdapter(XCN.class, new XCNAdapter())
		     .registerTypeAdapter(XON.class, new XONAdapter())
		     .registerTypeAdapter(List.class, new ListAdapter())
		     .registerTypeAdapter(AuthorType.class, new AuthorAdapter())
		     .registerTypeAdapter(CodedMetadataType.class, new CodedMetadataAdapter())
		     .registerTypeAdapter(DocumentEntryResponseType.class, new DocumentEntryResponseAdapter())
		     .setPrettyPrinting()
		     .create();
			json = gson.toJson(der, DocumentEntryResponseType.class);
			e.setContentType(Search.JSON);
			
		}
		catch (Throwable t)
		{
			log.log("Not sure what happened here: ", t);
		}
		byte data[];
		
		try {
			data = json.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			throw new Error("UTF-8 Not Supported", e1);
		}
		e.setContent(new ByteArrayInputStream(data));
		return e;
	}

	public final static String s(String s)
	{
		return s == null ? "" : s;
	}	
}
