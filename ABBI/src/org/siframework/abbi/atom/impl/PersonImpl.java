package org.siframework.abbi.atom.impl;

import java.net.URI;

import org.siframework.abbi.atom.Person;

/**
 * A simple Java Bean implementing the Person interface
 * @author Keith W. Boone
 */
public class PersonImpl implements Person {

	private  String name = null, email = null;
	private  String webSite = null;
	
	/**
	 * Construct a person with the specified name.
	 * @param name	The name of the person
	 */
	public PersonImpl(String name)
	{
		this(name, null, null);
	}
	
	/**
	 * Construct a person with the specified name and email address.
	 * @param name	The name of the person
	 * @param email The email address for the person
	 */
	public PersonImpl(String name, String email)
	{
		this(name, email, null);
	}
	
	/**
	 * Construct a person with the specified name, email address, and website.
	 * @param name	The name of the person
	 * @param email The email address for the person
	 * @param website A website to associate with the person
	 */
	public PersonImpl(String name, String email, String webSite)
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
	public String getWebSite() {
		return webSite;
	}

	@Override
	public void setWebSite(String webSite) {
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
