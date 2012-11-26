package org.siframework.abbi.atom.impl;

import java.net.URI;

import org.siframework.abbi.atom.Category;

/**
 * A simple Java Bean Implementation of the Category Interface.
 * @author Keith W. Boone
 */
public class CategoryImpl implements Category {
	
	private String term = null, label = null, text = null;
	private String scheme = null;

	/**
	 * Construct a category from the string representing a coded term
	 * @param term The coded term to initialize the category with.
	 */
	public CategoryImpl(String term)
	{
		this(term, null, null, null);
	}
	
	/**
	 * Construct a category from the string representing a coded term from 
	 * a terminology scheme
	 * @param term The coded term to initialize the category with.
	 * @param scheme The terminology scheme to use with the category.
	 */
	public CategoryImpl(String term, String scheme)
	{
		this(term, null, scheme, null);
	}
	
	/**
	 * Construct a category from the string representing a coded term and its label from 
	 * a terminology scheme.  
	 * @param term The coded term to initialize the category with.
	 * @param label The label to use for the coded term.
	 * @param scheme The terminology scheme to use with the category.
	 */
	public CategoryImpl(String term, String label, String scheme)
	{
		this(term, label, scheme, null);
	}
	
	/**
	 * Construct a category from the string representing a coded term and its label from 
	 * a terminology scheme and its name.  
	 * @param term The coded term to initialize the category with.
	 * @param label The label to use for the coded term.
	 * @param scheme The terminology scheme to use with the category.
	 * @param text A human readable name for the terminology scheme
	 */
	public CategoryImpl(String term, String label, String scheme, String text)
	{
		this.term = term;
		this.label = label;
		this.scheme = scheme;
		this.text = text;
	}

	@Override
	public String getTerm() {
		return term;
	}

	@Override
	public void setTerm(String term) {
		this.term = term;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String getScheme() {
		return scheme;
	}

	@Override
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
}
