package org.siframework.abbi.atom;

import java.net.URI;

public interface Link {
    public URI getHref();
	public void setHref(URI href);
	
	public String getRel();
	public void setRel(String rel);
	
	public String getMimeType();
	public void setMimeType(String mimeType);
	
	public String getLanguage();
	public void setLanguage(String language);
	
	public String getTitle();
	public void setTitle(String title);
	
	public int getLength();
	public void setLength(int length);
}
