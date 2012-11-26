package org.siframework.abbi.atom;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

/**
 * Implements the interface to a {@link <a href='http://www.irt.org/rfc/rfc4287.htm#page_13'>Entry</a>} in an Atom Feed
 * 
 * @author Keith W. Boone
 */
public interface Entry {

	/**	Get a URI representing the id associated with this Atom Entry 
	 *  @return A URI representing the id associated with this Atom Entry
	 */
	public String getId();
	/**	Set The URI representing the id associated with this Atom Entry 
	 *  @param id The URI representing the id associated with this Atom Entry
	 */
	public void setId(String id);
	
	/**
	 * Get a string identifying the generator of this entry.
	 * @return A string identifying the generator of this entry.
	 */
	public String getGenerator();
	/**
	 * Set the string identifying the generator of this entry.
	 * @param generator The string identifying the generator of this entry.
	 */
	public void setGenerator(String generator);
	
	/**
	 * Get a mutable collection representing the links for this entry.  Modifying this collection
	 * modifies the links.
	 * @return A Collection of {@link Link} objects for this entry.
	 */
	public Collection<Link> getLinks();
	/**
	 * Set the the links for this entry.  
	 * @param links A Collection of {@link Link} objects for this entry.
	 */
	public void setLinks(Collection<Link> links);

	/**
	 * Get the link whose relationship is "self" from the collection of links for this entry.
	 * @return A {@link Link} representing the entry. 
	 */
	public Link getSelf();
	/**
	 * Set the link whose relationship is "self" for the collection of links for this entry.
	 * @param self The {@link Link} representing the entry.
	 */
	public void setSelf(Link self);
	
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
	 * Get a mutable collection representing the contributors to this entry.  Modifying this collection
	 * modifies the contributors.
	 * @return A Collection of {@link Person} objects representing contributors to this entry.
	 */
	public Collection<Person> getContributors();
	/**
	 * Set the the contributors to this entry.  
	 * @param contributors A Collection of {@link Person} objects representing contributors to this entry.
	 */
	public void setContributors(Collection<Person> contributors);

	/**
	 * Get the title for this entry.
	 * @return The title for this entry.
	 */
	public String getTitle();
	/**
	 * Set the title for this entry.
	 * @param title The title for this entry.
	 */
	public void setTitle(String title);
	
	/**
	 * Get the summary for this entry.
	 * @return The summary for this entry.
	 */
	public String getSummary();
	/**
	 * Set the summary for this entry.
	 * @param summary The summary for this entry.
	 */
	public void setSummary(String summary);
	
	/**
	 * Get the date that this entry was first published.
	 * @return The date that this entry was first published.
	 */
	public Date getPublished();
	/**
	 * Set the date that this entry was first published.
	 * @param published The date that this entry was first published.
	 */
	public void setPublished(Date published);

	/**
	 * Get the date that this entry was last updated.
	 * @return The date that this entry was last updated.
	 */
	public Date getUpdated();
	/**
	 * Set the date that this entry was last updated.
	 * @param updated The date that this entry was last updated.
	 */
	public void setUpdated(Date updated);
	
	/**
	 * Get a mutable collection representing the authors of this entry.  Modifying this collection
	 * modifies the authors.
	 * @return A Collection of {@link Person} objects representing contributors to this entry.
	 */
	public Collection<Person> getAuthors();
	/**
	 * Set the authors of this entry.  
	 * @param author A Collection of {@link Person} objects representing authors of this entry.
	 */
	public void setAuthors(Collection<Person> author);

	/**
	 * Get a mutable collection representing the categories associated with this entry.  Modifying this collection
	 * modifies the categories associated with the entry.
	 * @return A Collection of {@link Category} objects associated with this entry.
	 */
	public Collection<Category> getCategories();
	/**
	 * Set the categories associated with this entry.  
	 * @param categories The collection of {@link Category} objects to associate with this entry.
	 */
	public void setCategories(Collection<Category> categories);
	

	/** 
	 * Get the MIME Content Type of the entry.
	 * @return  The MIME Content Type of the entry.
	 */
	public String getContentType();
	/** 
	 * Set the MIME Content Type of the entry.
	 * @param  contentType The MIME Content Type of the entry.
	 */
	public void setContentType(String contentType);

	/**
	 * Get the URL where the content for this entry can be retrieved.
	 * @return The URL where the content for this entry can be retrieved.
	 */
	public String getContentSrc();
	/**
	 * Set the URL where the content for this entry can be retrieved.
	 * @param contentSrc The URL where the content for this entry can be retrieved.
	 */
	public void setContentSrc(String contentSrc);
	
	
	/**
	 * Get an InputStream to read the content for this entry.
	 * @return An InputStream containing the content of this entry.
	 */
	public InputStream getContent();
	/**
	 * Set the InputStream containing the content for this entry.
	 * @param content The InputStream containing the content of this entry.
	 */
	public void setContent(InputStream content);
	
};
	
