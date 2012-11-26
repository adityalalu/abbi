package org.siframework.abbi.atom;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Implements the interface to an Atom Feed as specified by {@link <a href='http://www.irt.org/rfc/rfc4287.htm'>RFC 4287</a>}
 * 
 * @author Keith W. Boone
 */
public interface Feed {
	/**	Get a URI representing the id associated with this Atom Entry 
	 *  @return A URI representing the id associated with this Atom Entry
	 */
	public String getId();
	/**	Set The URI representing the id associated with this Atom Entry 
	 *  @param id The URI representing the id associated with this Atom Entry
	 */
	public void setId(String id);
	
	/**
	 * Get a string identifying the generator of this feed.
	 * @return A string identifying the generator of this feed.
	 */
	public String getGenerator();
	/**
	 * Set the string identifying the generator of this feed.
	 * @param generator The string identifying the generator of this feed.
	 */
	public void setGenerator(String generator);
	
	/**
	 * Get a mutable collection representing the links for this feed.  Modifying this collection
	 * modifies the links.
	 * @return A Collection of {@link Link} objects for this feed.
	 */
	public Collection<Link> getLinks();
	/**
	 * Set the the links for this feed.  
	 * @param links A Collection of {@link Link} objects for this feed.
	 */
	public void setLinks(Collection<Link> links);
	
	/**
	 * Get the link whose relationship is "self" from the collection of links for this feed.
	 * @return A {@link Link} representing the feed. 
	 */
	public Link getSelf();
	/**
	 * Set the link whose relationship is "self" for the collection of links for this feed.
	 * @param self The {@link Link} representing the feed.
	 */
	public void setSelf(Link self);

	/**
	 * Get the URL for the icon for this feed.
	 * @return The URL for the icon for this feed.
	 */
	public String getIcon();
	/**
	 * Set the URL for the icon for this feed.
	 * @param icon The URL for the icon for this feed.
	 */
	public void setIcon(String icon);
	
	/**
	 * Get the URL for the logo for this feed.
	 * @return The URL for the logo for this feed.
	 */
	public String getLogo();
	/**
	 * Set the URL for the logo for this feed.
	 * @param icon The URL for the logo for this feed.
	 */
	public void setLogo(String logo);

	/**
	 * Get a mutable collection representing the contributors to this feed.  Modifying this collection
	 * modifies the contributors.
	 * @return A Collection of {@link Person} objects representing contributors to this feed.
	 */
	public Collection<Person> getContributors();
	/**
	 * Set the the contributors to this feed.  
	 * @param contributors A Collection of {@link Person} objects representing contributors to this feed.
	 */
	public void setContributors(Collection<Person> author);

	/**
	 * Get the title for this feed.
	 * @return The title for this feed.
	 */
	public String getTitle();
	/**
	 * Set the title for this feed.
	 * @param title The title for this feed.
	 */
	public void setTitle(String title);
	
	/**
	 * Get the subtitle for this feed.
	 * @return The subtitle for this feed.
	 */
	public String getSubtitle();
	/**
	 * Set the subtitle for this feed.
	 * @param subtitle The subtitle for this feed.
	 */
	public void setSubtitle(String subtitle);
	
	/**
	 * Get the string indicating the rights to the content.
	 * @return The string indicating the rights to the content.
	 */
	public String getRights();
	/**
	 * Set the string indicating the rights to the content.
	 * @param rights The string indicating the rights to the content.
	 */
	public void setRights(String rights);
	
	/**
	 * Get the date that this feed was last updated.
	 * @return The date that this feed was last updated.
	 */
	public Date getUpdated();
	/**
	 * Set the date that this feed was last updated.
	 * @param updated The date that this feed was last updated.
	 */
	public void setUpdated(Date updated);
	
	/**
	 * Get a mutable collection representing the authors of this feed.  Modifying this collection
	 * modifies the authors.
	 * @return A Collection of {@link Person} objects representing contributors to this feed.
	 */
	public Collection<Person> getAuthors();
	/**
	 * Set the authors of this feed.  
	 * @param author A Collection of {@link Person} objects representing authors of this feed.
	 */
	public void setAuthors(Collection<Person> author);

	/**
	 * Get a mutable collection representing the categories associated with this feed.  Modifying this collection
	 * modifies the categories associated with the feed.
	 * @return A Collection of {@link Category} objects associated with this feed.
	 */
	public Collection<Category> getCategories();
	/**
	 * Set the categories associated with this feed.  
	 * @param categories The collection of {@link Category} objects to associate with this feed.
	 */
	public void setCategories(Collection<Category> categories);
	
	/**
	 * Get a mutable collection representing the entries in this atom feed.  Modifying this collection
	 * modifies the entries contained in it.
	 * @return  A mutable collection representing the entries in this atom feed
	 */
	public List<Entry> getEntries();
	/**
	 * Set the collection containing the entries of this atom feed.  
	 * @param entries  The collection of the entries in this atom feed
	 */
	public void setEntries(List<Entry> entries);
}
