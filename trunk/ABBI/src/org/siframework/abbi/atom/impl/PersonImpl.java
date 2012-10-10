package org.siframework.abbi.atom.impl;

import java.net.URI;

import org.siframework.abbi.atom.Person;

public class PersonImpl implements Person {

	private  String name = null, email = null;
	private  URI webSite = null;
	
	public PersonImpl(String name)
	{
		this(name, null, null);
	}
	
	public PersonImpl(String name, String email)
	{
		this(name, email, null);
	}
	
	public PersonImpl(String name, String email, URI webSite)
	{
		this.name = name;
		this.email = email;
		this.webSite = webSite;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public URI getWebSite() {
		return webSite;
	}

	@Override
	public void setWebSite(URI webSite) {
		this.webSite = webSite;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}
}
