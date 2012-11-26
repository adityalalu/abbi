package org.siframework.abbi.atom;

import java.net.URI;

/**
 * Implements the interface to a {@link <a href='http://www.irt.org/rfc/rfc4287.htm#page_18'>Category</a>} in an Atom Feed
 * 
 * HL7 V3 and XDS use four parts to describe a coded element, including a code (term),
 * display name (label), code system OID (scheme) and code system name (text).  This interface
 * supports the Atom Category using the propery names: term, label, and scheme.  It adds a text
 * property which would be represented in Atom as the content of the Category element.
 * <br/>
 * <h3>Atom Representation</h3>
 * &lt;category term="<i>term</i>" label="<i>label</i>" scheme="<i>schemeURI</i>"&gt;<i>text</i>&lt;/category&gt; 
 * 
 * @author Keith W. Boone
 */
public interface Category {

	/**
	 * Get the term associated with category
	 * @return The term associated with the category in the feed.
	 */
	public String getTerm();
	/**
	 * Set the term associated with a category
	 * @param term The new term value
	 */
	public void setTerm(String term);
	
	/**
	 * Get the label (display name) associated with the category
	 * @return The label (display name) associated with the category
	 */
	public String getLabel();
	/**
	 * Set the label (display name) associated with a category
	 * @param label The new label value
	 */
	public void setLabel(String label);

	/**
	 * Get the URI representing the terminology scheme used for the category
	 * @return The URI representing the terminology scheme used for the category
	 */
	public String getScheme();

	/**
	 * Set the URI representing the terminology scheme used for the category
	 * @param scheme A URI representing the terminology scheme used for the category
	 */
	public void setScheme(String scheme);

	/**
	 * Get the text appearing inside the category element (undefined by Atom).
	 * @return The text appearing inside the category element
	 */
	public String getText();
	/** 
	 * Set the text appearing inside the category element (undefined by Atom).
	 * @param text The text to appear inside the category element
	 */
	public void setText(String text);
	
	
}
