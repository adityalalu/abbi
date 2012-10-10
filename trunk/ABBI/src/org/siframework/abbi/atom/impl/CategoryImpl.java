package org.siframework.abbi.atom.impl;

import java.net.URI;

import org.siframework.abbi.atom.Category;

public class CategoryImpl implements Category {
	
	private String term = null, label = null, text = null;
	private URI scheme = null;
	
	public CategoryImpl(String term)
	{
		this(term, null, null, null);
	}
	
	public CategoryImpl(String term, URI scheme)
	{
		this(term, null, scheme, null);
	}
	
	public CategoryImpl(String term, String label, URI scheme)
	{
		this(term, label, scheme, null);
	}
	
	public CategoryImpl(String term, String label, URI scheme, String text)
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
	public URI getScheme() {
		return scheme;
	}

	@Override
	public void setScheme(URI scheme) {
		this.scheme = scheme;
	}
}
