package org.scribe.provider.model;

import java.security.Principal;
import java.util.Set;

/**
 * This class represents a system user that can own applications or who has authorized 
 * applications to operate on their behalf.
 * @author Keith W. Boone
 */
public interface User extends Principal {
	
	/**
	 * Return an enumeration listing the applications owned by this user.
	 * @return The set of applications owned by this user.  Never null, possibly empty.
	 */
	Set<Application> getOwnedApplications();
	
	/**
	 * Return an set listing the Authorization tokens given to applications
	 * provided by this user.
	 * @return The set of tokens authorizing applications.
	 */
	Set<Token> getAuthorization();
	
	/** Return the authorization token for the specified application.
	 * @param app The application whose authorization token is to be returned.
	 * @return	The authorization token for the specified application
	 */
	Token getAuthorization(Application app);
}
