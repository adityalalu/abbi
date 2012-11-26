/**
 * 
 */
package org.siframework.abbi.atom.impl;

import org.siframework.abbi.atom.Link;

/**
 * A simple Java Bean implementing a Link
 * @author Keith W. Boone
 */
public class LinkImpl implements Link {

	private String href = null;
	private String rel = null, title = null, language = null, mimeType = null;
	private int length = 0;
	
	/**
	 * Construct a new Link
	 * @param href	The URL to which the link goes
	 * @param rel	The relationship of the link
	 * @param mimeType	The content type of the link
	 */
	public LinkImpl(String href, String rel, String mimeType)
	{	this.href = href;
		this.rel = rel;
		this.mimeType = mimeType;
	}
	
	/* (non-Javadoc)
	 * @see org.siframework.abbi.atom.Link#getHref(java.net.URI)
	 */
	@Override
	public String getHref() {
		return href;
	}

	/* (non-Javadoc)
	 * @see org.siframework.abbi.atom.Link#setHref(java.net.URI)
	 */
	@Override
	public void setHref(String href) {
		this.href = href;
	}

	/* (non-Javadoc)
	 * @see org.siframework.abbi.atom.Link#getRel()
	 */
	@Override
	public String getRel() {
		return rel;
	}

	/* (non-Javadoc)
	 * @see org.siframework.abbi.atom.Link#setRel(java.lang.String)
	 */
	@Override
	public void setRel(String rel) {
		this.rel = rel;
	}

	/* (non-Javadoc)
	 * @see org.siframework.abbi.atom.Link#getMimeType()
	 */
	@Override
	public String getMimeType() {
		return mimeType;
	}

	/* (non-Javadoc)
	 * @see org.siframework.abbi.atom.Link#setMimeType(java.lang.String)
	 */
	@Override
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/* (non-Javadoc)
	 * @see org.siframework.abbi.atom.Link#getLanguage()
	 */
	@Override
	public String getLanguage() {
		return language;
	}

	/* (non-Javadoc)
	 * @see org.siframework.abbi.atom.Link#setLanguage(java.lang.String)
	 */
	@Override
	public void setLanguage(String language) {
		this.language = language;
	}

	/* (non-Javadoc)
	 * @see org.siframework.abbi.atom.Link#getTitle()
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/* (non-Javadoc)
	 * @see org.siframework.abbi.atom.Link#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	/* (non-Javadoc)
	 * @see org.siframework.abbi.atom.Link#getLength()
	 */
	@Override
	public int getLength() {
		return length;
	}

	/* (non-Javadoc)
	 * @see org.siframework.abbi.atom.Link#setLength(int)
	 */
	@Override
	public void setLength(int length) {
		this.length = length;
	}

}
