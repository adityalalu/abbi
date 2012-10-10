package org.siframework.abbi.atom;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Date;

public interface Entry {

	public URI getId();
	public void setId(URI id);
	
	public String getGenerator();
	public void setGenerator(String generator);
	
	public Collection<Link> getLinks();
	public void setLinks(Collection<Link> links);

	public Link getSelf();
	public void setSelf(Link self);
	
	public String getRights();
	public void setRights(String rights);
	
	public Collection<Person> getContributors();
	public void setContributors(Collection<Person> author);

	public String getTitle();
	public void setTitle(String title);
	
	public String getSummary();
	public void setSummary(String summary);
	
	public Date getPublished();
	public void setPublished(Date updated);

	public Date getUpdated();
	public void setUpdated(Date updated);
	
	public Collection<Person> getAuthors();
	public void setAuthors(Collection<Person> author);

	public Collection<Category> getCategories();
	public void setCategories(Collection<Category> categories);
	

	public String getContentType();
	public void setContentType(String contentType);

	public URI getContentSrc();
	public void setContentSrc(URI contentSrc);
	
	public InputStream getContent();
	public void setContent(InputStream content);
	
};
	
