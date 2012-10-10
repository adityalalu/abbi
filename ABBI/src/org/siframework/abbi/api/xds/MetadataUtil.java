package org.siframework.abbi.api.xds;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import org.eclipse.emf.common.util.EList;
import org.openhealthtools.ihe.common.hl7v2.XCN;
import org.openhealthtools.ihe.xds.metadata.CodedMetadataType;
import org.openhealthtools.ihe.xds.metadata.InternationalStringType;
import org.openhealthtools.ihe.xds.metadata.LocalizedStringType;
import org.openhealthtools.ihe.xds.metadata.MetadataFactory;

/**
 * This class provides a variety of utility methods to translate between XDS metadata types
 * supported in OHT and native java types.
 * @author Keith W. Boone
 *
 */
public class MetadataUtil {

	public static final boolean isEmpty(String x)
	{
		return x == null || x.length() == 0;
	}
	
	/**
	 * Translate a name from XCN Format to a string suitable for display.  It assumes that
	 * name order is Western (given family).
	 * @param name	The name to translate
	 * @return A string suitable for displaying the specified name
	 */
	public static String getName(XCN name)
	{
		if (name == null) return "";
		
		String given = name.getGivenName();
		String family = name.getFamilyName();
		String middle = name.getOtherName();
		String prefix = name.getPrefix();
		String suffix = name.getSuffix();
		
		StringBuffer b = new StringBuffer();
		
		if (!isEmpty(prefix))
		 	b.append(prefix).append(' ');
		if (!isEmpty(given))
		 	b.append(given).append(' ');
		if (!isEmpty(middle))
		 	b.append(middle).append(' ');
		if (!isEmpty(family))
		 	b.append(family).append(' ');
		if (!isEmpty(suffix))
		 	b.append(suffix).append(' ');
		
		// remove terminal space
		if (b.length() > 1)
			b.setLength(b.length()-1);
		
		return b.toString();
	}
	
	/**
	 * Convert a String into an InternationalStringType
	 * @param s	The string to convert
	 * @param lang	The language to use in the conversion.  If null, the InternationalStringType 
	 * has no language specified.
	 * @return An InternationalStringType representing the specified string.
	 */
	public static InternationalStringType asInternationalString(String s, String lang)
	{
		// Should really set language
		LocalizedStringType dName = MetadataFactory.eINSTANCE.createLocalizedStringType();
		dName.setValue(s);
		if (lang != null)
			dName.setLang(lang);
		
		InternationalStringType display = MetadataFactory.eINSTANCE.createInternationalStringType();
		display.getLocalizedString().add(dName);
		return display;
	}
	
	/**
	 * Convert a UUID into a URI.
	 * @param uuid	The UUID to convert
	 * @return A URI representing the specified UUID
	 * @throws URISyntaxException	If the URI format is invalid, usually meaning that uuid is incorrectly formatted
	 */
	public static URI asURI(String uuid) throws URISyntaxException
	{
		return isEmpty(uuid) ? null : new URI(uuid);
	}

	/**
	 * Get an appropriate string to display for the specified InternationalStringType.
	 * @param iString  The InternationStringType from which a display string is selected. 
	 * @return The appropriate string to display for the default Locale for this JVM.
	 */
	public static String asString(InternationalStringType iString) 
	{	
		return iString == null ? null : asString(iString, null);
	}
	
	/**
	 * Get an appropriate string to display for the specified InternationalStringType and Locale.
	 * NOTE: Right now, this just selects the first string from the InternationalStringType, and
	 * doesn't do any locale matching.
	 * @param iString  The InternationStringType from which a display string is selected.
	 * @param l	The locale to use when selecting the String, or null if the default locale should 
	 * be used.
	 * @return The appropriate string to display for the specified locale.
	 */
	public static String asString(InternationalStringType iString, Locale l)
	{
		if (iString == null || iString.getLocalizedString().isEmpty())
			return null;
		
		EList<LocalizedStringType> lc = iString.getLocalizedString();
		if (l == null)
			return lc.get(0).getValue();

		// TODO: Return the best string based on locale
		return lc.get(0).getValue();
	}
	
	/**
	 * Convert a CodedMetadataType to a String.  This function puts the components of the
	 * CodedMetadataType into a string in the order code|SchemeUUID|displayName|SchemeName
	 * @param code	The CodedMetadataType to convert to a string
	 * @return A string representing the CodedMetadataType
	 */
	public static String asString(CodedMetadataType code)
	{
		if (code == null)
			return null;
		
		StringBuffer b = new StringBuffer();
		b.append(code.getCode()).append('|');
		b.append(code.getSchemeUUID()).append('|');
		b.append(asString(code.getDisplayName())).append('|');
		b.append(code.getSchemeName());
		return b.toString();
	}
	
	/** Construct a new coded metadata type from the given parameters.
	 * 
	 * @param code	A string representing the code
	 * @param scheme	The UUID for the coding scheme
	 * @param displayName	A display name for the string in the default locale
	 * @param schemeName	The scheme name for the scheme
	 * @return	A CodedMetadataType representing these values
	 */
	public static CodedMetadataType asCodedMetadataType(String code, String scheme, String displayName, String schemeName)
	{
		CodedMetadataType t = MetadataFactory.eINSTANCE.createCodedMetadataType();
		if (code != null && code.length() != 0)
			t.setCode(code);
		if (scheme != null && scheme.length() != 0)
			t.setSchemeUUID(scheme);
		if (displayName != null && displayName.length() != 0)
			t.setDisplayName(MetadataUtil.asInternationalString(displayName, null));
		if (schemeName != null && schemeName.length() != 0)
			t.setSchemeName(schemeName);
		
		return t;
	}

	public static String reformatISODate(String date)
	{
		StringBuffer b = new StringBuffer();
		int length = date.length();
		
		if (length >= 4)
			b.append(date.substring(0, 4));
		if (length >= 6);
			b.append('-').append(date.substring(4, 6));
		if (length >= 8)
			b.append('-').append(date.substring(6, 8));
		
		if (length >= 10)
			b.append('T').append(date.substring(8,10));
		if (length >= 12)
			b.append(':').append(date.substring(10, 12));
		if (length > 12)
			b.append(':').append(date.substring(12));
		return b.toString();
	}
	
	public final static String s(String s)
	{
		return s == null ? "" : s;
	}
}
