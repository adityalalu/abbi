package org.scribe.provider;

import java.io.OutputStream;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.scribe.provider.model.OAuthData;
import org.scribe.provider.model.Repository;
import org.scribe.provider.model.Token;

/**
 * The OAuthProvider interface defines the methods that an Authorization Provider makes available to
 * support Authorization of API users, and the OAuth Workflow.
 * 
 * <h3>The OAuth Workflow</h3>
 * The OAuth Workflow involves several steps.
 * <ol>
 * <li>In the first step, the client application authenticates to the data holder using its client token and secret and
 * is given a request token and secret to use in the next step.  It provides a callback address to use
 * when the authentication is complete to enable redirection to the authorized application.
 * </li>
 * <li>In the next step, the client application sends a request to the authorization page with both its
 * client token and secret, and the newly provided request token and secret.  The user authorizes the client
 * application in this step.  The data holder sends this temporary token and verification string to the callback
 * address specified in the first step</li>
 * <li>The client application converts this temporary token and verification string into an access token associated
 * with the user by passing these two and the client token and secret to the access token page. That page returns an 
 * access token associated with the user.</li>
 * <li>The client token can now use this access token to make API requests</li>
 * </ol>
 * @author Keith W. Boone
 *
 */
public interface OAuthProvider 
{
	/**
	 * Given a clientToken and callback address, create a new request token for the client to
	 * use in subsequent steps of the OAuth Workflow.
	 * @param clientToken	A token assigned to the client application.
	 * @param callback	The callback address specified by the application for this interchange.
	 * @return A Request Token enabling the client application to make an authoriszation request.
	 */
	public Token getRequestToken(Token clientToken, String callback);
	
	/**
	 * Set the stream used for logging provider results.
	 * @param s	The stream to use for logging activity.
	 */
	public void setLog(OutputStream s);
	
	public Token getAccessToken(Token tempToken);
	
	/**
	 * This method is used by the OAuthFilter to validate HttpServletRequests that have been
	 * made against any API pages protected by the filter.
	 * @param req	The HttpServletRequest being made.
	 * @return	Data needed to process requests in the OAuth Authentication Workflow
	 * @throws InvalidOAuthRequestException If there is a problem with the request.
	 */
	public OAuthData validate(HttpServletRequest req) 
		throws InvalidOAuthRequestException;
	
	/**
	 * This method is called by the Authorization page to generate a new Temporary Token.  It must
	 * validate that the token is of the appropriate type (a request token), verify that the token
	 * has not expired, and that the current remote user exists in the repository.  Upon completion
	 * it will return the callback or verifier string that the authorization page should redirect
	 * the user to (or display if a verifier).  Verifier strings do not contain the : character.
	 * If authorization failed (because the request was not correctly structured, or the token expired)
	 * it will return the callback address without any request parameters (or an empty verifier if 
	 * the request is to be verified out-of-band). 
	 * @param request	The HttpServletRequest authorizing an application.
	 * @param roles		The set of roles to allow for the application
	 * @return A callback URL (for redirects) or verifier string (for devices).
	 */
	public String authorizeApplication(HttpServletRequest request, Collection<String> roles);
	
	/**
	 * This method is used to determine if an application is already authorized.  If authorization
	 * has already been given, it will return the existing authorization token or verifier string
	 * to the caller.  If not, it will return null.  If the existing authorization token is nearing
	 * expiration, the provider may not return the current authorization token, but rather return
	 * null to ensure that a new authorization is provided.
	 * @param request	The HttpServletRequest which is requesting authorization.
	 * @return	An authorization token or verifier string if the application is already authorized, or null.
	 */
	public String checkAuthorization(HttpServletRequest request);
	
	public Repository getRepository();
	public void log(String message, Exception ex);
	public void setRepository(Repository repository);
}
