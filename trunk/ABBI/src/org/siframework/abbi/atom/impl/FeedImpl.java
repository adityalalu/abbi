package org.siframework.abbi.atom.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.siframework.abbi.atom.Category;
import org.siframework.abbi.atom.Entry;
import org.siframework.abbi.atom.Feed;
import org.siframework.abbi.atom.Link;
import org.siframework.abbi.atom.Person;

public class FeedImpl implements Feed  {

	private URI id = null, icon = null, logo = null;
	private String generator = null, title = null, subtitle = null, rights = null;
	private List<Link> links = new LinkedList<Link>(); 
	private HashSet<Person> authors = new HashSet<Person>(), contributors = new HashSet<Person>();
	private HashSet<Category> categories = new HashSet<Category>();
	private Date updated = null;
	private List<Entry> entries = new ArrayList<Entry>();
	
	@Override
	public URI getId() {
		return id;
	}

	@Override
	public void setId(URI id) {
		this.id = id;
	}

	@Override
	public String getGenerator() {
		return generator;
	}

	@Override
	public void setGenerator(String generator) {
		this.generator = generator;
	}

	@Override
	public Collection<Link> getLinks() {
		return links;
	}

	@Override
	public void setLinks(Collection<Link> links) 
	{	
		this.links = new ArrayList<Link>();
		this.links.addAll(links);
	}

	@Override
	public Link getSelf() 
	{	for (Link link : links)
			if ("self".equals(link.getRel()))
				return link;
		return null;
	}

	@Override
	public void setSelf(Link self) 
	{	
		Link oldSelf = getSelf();
		
		if (oldSelf != null)
			links.remove(self);
		
		links.add(self);
	}

	@Override
	public URI getIcon() {
		return icon;
	}

	@Override
	public void setIcon(URI icon) {
		this.icon = icon;
	}

	@Override
	public URI getLogo() {
		return logo;
	}

	@Override
	public void setLogo(URI logo) {
		this.logo = logo;
	}

	@Override
	public Collection<Person> getContributors() {
		return contributors;
	}

	@Override
	public void setContributors(Collection<Person> contributors) {
		contributors = new HashSet<Person>(contributors);
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
		
	}

	@Override
	public String getSubtitle() {
		return subtitle;
	}

	@Override
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	@Override
	public String getRights() {
		return rights;
	}

	@Override
	public void setRights(String rights) {
		this.rights = rights;
	}

	@Override
	public Date getUpdated() {
		return updated;
	}

	@Override
	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@Override
	public Collection<Person> getAuthors() {
		return authors;
	}

	@Override
	public void setAuthors(Collection<Person> authors) {
		this.authors = new HashSet<Person>(authors);
	}

	@Override
	public Collection<Category> getCategories() {
		return categories;
	}

	@Override
	public void setCategories(Collection<Category> categories) {
		this.categories = new HashSet<Category>(categories);
	}

	@Override
	public List<Entry> getEntries() {
		return entries;
	}

	@Override
	public void setEntries(List<Entry> entries) {
		this.entries = new ArrayList<Entry>(entries);
	}

}
