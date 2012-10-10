package org.siframework.abbi.atom;

import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface Feed {
	public URI getId();
	public void setId(URI id);
	
	public String getGenerator();
	public void setGenerator(String generator);
	
	public Collection<Link> getLinks();
	public void setLinks(Collection<Link> links);
	
	public Link getSelf();
	public void setSelf(Link self);

	public URI getIcon();
	public void setIcon(URI icon);
	
	public URI getLogo();
	public void setLogo(URI logo);
	
	public Collection<Person> getContributors();
	public void setContributors(Collection<Person> author);

	public String getTitle();
	public void setTitle(String title);
	
	public String getSubtitle();
	public void setSubtitle(String subtitle);
	
	public String getRights();
	public void setRights(String rights);
	
	public Date getUpdated();
	public void setUpdated(Date updated);
	
	public Collection<Person> getAuthors();
	public void setAuthors(Collection<Person> author);

	public Collection<Category> getCategories();
	public void setCategories(Collection<Category> categories);
	
	public List<Entry> getEntries();
	public void setEntries(List<Entry> entries);
}
