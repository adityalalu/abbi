package org.siframework.abbi.atom;

import java.net.URI;

public interface Category {

	public String getTerm();
	public void setTerm(String term);
	public String getLabel();
	public void setLabel(String label);
	public String getText();
	public void setText(String text);
	public URI getScheme();
	public void setScheme(URI scheme);
}
