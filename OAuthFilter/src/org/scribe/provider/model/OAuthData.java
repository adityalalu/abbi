package org.scribe.provider.model;

import java.util.Map;

/**
 * This interface represents the data returned from the OAuth Provider Validation Service.
 * @author Keith W. Boone
 */
public interface OAuthData {
	/**
	 * Get the token authorizing the access.
	 * @return The token authorizing the access
	 */
	public Token getToken();
	
	/**
	 * Get a map representing the OAuth request parameters
	 * @return The OAuth Request Parameters
	 */
	public Map<String, String> getParameters();
}
