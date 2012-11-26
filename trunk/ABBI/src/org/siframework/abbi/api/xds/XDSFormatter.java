package org.siframework.abbi.api.xds;

import org.openhealthtools.ihe.xds.response.DocumentEntryResponseType;
import org.siframework.abbi.api.Context;
import org.siframework.abbi.atom.Entry;

public interface XDSFormatter {
	public Entry format(DocumentEntryResponseType der, Context log);
}
