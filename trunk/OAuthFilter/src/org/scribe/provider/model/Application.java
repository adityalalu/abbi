package org.scribe.provider.model;

import java.security.Principal;

/**
 * This class represents an Application that has been or can be authorized by a user.
 * @author Keith W. Boone
 */
public interface Application extends Principal {
	
	/** 
	 * Get the client token assigned to the application.  This operation
	 * is only permitted to be performed by the user who owns the application. 
	 * @return The client token assigned to the application
	 */
	public Token getClientToken();
	
	/**
	 * Get the user who is the owner of this application.
	 * @return The user who is the owner of this application
	 */
	public User getOwner();
}
