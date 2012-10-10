package org.siframework.abbi;

import javax.script.*;
import java.util.*;

import org.openhealthtools.ihe.atna.auditor.XDSConsumerAuditor;
import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.common.hl7v2.Hl7v2Factory;
import org.openhealthtools.ihe.xds.consumer.B_Consumer;
import org.openhealthtools.ihe.xds.consumer.storedquery.FindDocumentsQuery;
import org.openhealthtools.ihe.xds.metadata.AvailabilityStatusType;
import org.openhealthtools.ihe.xds.response.XDSQueryResponseType;

import java.net.URI;

public class Test {
	public static final String REGISTRY = "http://ihexds.nist.gov:12080/tf6/services/xdsregistryb";
	
	public static final AvailabilityStatusType APPROVED[] = { 
		AvailabilityStatusType.APPROVED_LITERAL
	};
	
	public static void main(String args[])
	throws Exception
	{
		List<ScriptEngineFactory> l = new ScriptEngineManager().getEngineFactories();
		for (ScriptEngineFactory f: l)
		{
			System.out.println(f.getLanguageName() + ": " + f.getMimeTypes());
		}
	}
	public static void XDSTest(String args[]) throws Exception
	{
		URI uri = new URI(REGISTRY);
		B_Consumer c = new B_Consumer(uri);
		XDSConsumerAuditor.getAuditor().getConfig().setAuditorEnabled(false);
		// Set up the patient we are querying for
		CX patientId = Hl7v2Factory.eINSTANCE.createCX();
		patientId.setIdNumber("00ae33bb8f2b437");
		patientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2009.1.2.300");
		patientId.setAssigningAuthorityUniversalIdType("ISO");
		FindDocumentsQuery q = new FindDocumentsQuery(patientId, APPROVED);
		XDSQueryResponseType t = c.invokeStoredQuery(q, false);
		
	}
}
