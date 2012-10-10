package org.siframework.abbi.atom.impl;

import java.io.InputStream;
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
import org.siframework.abbi.atom.Link;
import org.siframework.abbi.atom.Person;

public class EntryImpl implements Entry  {

	private URI id = null, contentSrc = null;
	private String generator = null, title = null, rights = null, summary = null;
	private List<Link> links = new LinkedList<Link>();
	private HashSet<Person> authors = new HashSet<Person>(), contributors = new HashSet<Person>();
	private HashSet<Category> categories = new HashSet<Category>();
	private Date updated = null, published = null;
	private String contentType = null;
	private InputStream content = null;
	
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
	public String getSummary() {
		// TODO Auto-generated method stub
		return summary;
	}

	@Override
	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Override
	public Date getPublished() {
		// TODO Auto-generated method stub
		return published;
	}

	@Override
	public void setPublished(Date published) {
		this.published = published;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public URI getContentSrc() {
		return contentSrc;
	}

	@Override
	public void setContentSrc(URI contentSrc) {
		this.contentSrc = contentSrc;
	}
	
	@Override
	public InputStream getContent() {
		return content;
	}
	
	@Override
	public void setContent(InputStream content) {
		this.content = content;
	}
}
