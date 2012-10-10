package org.siframework.abbi.atom;

import java.net.URI;

public interface Person {

	public String getName();
	public void setName(String name);
	public URI getWebSite();
	public void setWebSite(URI webSite);
	public String getEmail();
	public void setEmail(String email);
}
