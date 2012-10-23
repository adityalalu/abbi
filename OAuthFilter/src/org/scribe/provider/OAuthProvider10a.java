package org.scribe.provider;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.provider.model.Application;
import org.scribe.provider.model.OAuthData;
import org.scribe.provider.model.Repository;
import org.scribe.provider.model.Token;
import org.scribe.provider.model.User;
import org.scribe.utils.Preconditions;

public class OAuthProvider10a implements OAuthProvider {

	private Repository repository = null;
	private static final boolean DEBUG = true;
	private PrintStream log = System.err;
	
	public OAuthProvider10a()
	{
	}
	
	public OAuthProvider10a(Repository rep)
	{
		Preconditions.checkNotNull(rep, "repository cannot be null.");
		setRepository(rep);
	}

	public void setRepository(Repository rep)
	{
		if (repository != null)
			throw new IllegalStateException("Repository already set");
		Preconditions.checkNotNull(rep, "repository cannot be null.");
		repository = rep;
	}
	
	public OAuthData validate(HttpServletRequest req) throws InvalidOAuthRequestException
	{
		log("Validating Request", null);
		try
		{
			final OAuthRequest r = Util.createRequest(req);
			log("OAuth Parameters: " + r.getOauthParameters(), null);
			final Token t = Util.validate(r, getRepository());
			log("Request is valid", null);
			
			return new OAuthData() {
				public Token getToken() {
					return t;
				}
				public Map<String, String> getParameters() {
					return r.getOauthParameters();
				}
			};
		}
		catch (InvalidOAuthRequestException ex)
		{
			log(null, ex);
			throw ex;
		}
	}
	
	@Override
	public Token getRequestToken(Token clientToken, String callback)
	{
		log("Performing getRequestToken", null);
		Token t = repository.createRequestToken(clientToken.getApplication(), callback);
		log("Request Token: " + t, null);
		return t;
	}
	
	@Override
	public String authorizeApplication(HttpServletRequest req, Collection<String> roles) 
	{
		log("Performing authorizeUser", null);
		
		String tokenKey = req.getParameter(OAuthConstants.TOKEN);
		Token requestToken = repository.getToken(tokenKey);
		String callback = null;
		try
		{
			// If the token doesn't exist, then throw an exception
			if (requestToken == null)
			 	throw new InvalidOAuthRequestException("Token Invalid or Expired");
			
			if (requestToken.getType() != Token.TokenType.REQUEST_TOKEN)
				throw new InvalidOAuthRequestException("Illegal Token");
	
			String userId = req.getRemoteUser();
			if (userId == null)
				throw new InvalidOAuthRequestException("No User");
			
			User user = repository.getUser(userId);
			if (user == null)
				throw new InvalidOAuthRequestException("User " + userId + " not found");
			
			callback = requestToken.getCallback();
			// Make the request token a temporary token.
			repository.createTemporaryToken(requestToken, user, roles);
	
			log("Temporary Token: " + requestToken, null);
			
			if ("oob".equals(callback))
				callback = requestToken.getVerifier();
			else
			{	// If the callback URL already contains the Query parameter, then add &
				callback += callback.contains("?") ? "&" : "?";
				callback += OAuthConstants.TOKEN + "=" + requestToken.getToken() + "&" +
					OAuthConstants.VERIFIER + "=" + requestToken.getVerifier();
			}
		}
		catch (InvalidOAuthRequestException oex)
		{
			log("Authorization failure", oex);
			callback = requestToken != null ? requestToken.getCallback() : "";
			if ("oob".equals(callback))
				callback = "";
		}

		log("Callback: " + callback, null);
		return callback;
	}
	

	@Override
	public String checkAuthorization(HttpServletRequest req) {
		log("Performing checkAuthorization", null);
		
		String tokenKey = req.getParameter(OAuthConstants.TOKEN);
		Token requestToken = repository.getToken(tokenKey);
		String callback = null;
		try
		{
			// If the token doesn't exist, then throw an exception
			if (requestToken == null)
			 	throw new InvalidOAuthRequestException("Token Invalid or Expired");
			
			if (requestToken.getType() != Token.TokenType.REQUEST_TOKEN)
				throw new InvalidOAuthRequestException("Illegal Token");
	
			String userId = req.getRemoteUser();
			if (userId == null)
				throw new InvalidOAuthRequestException("No User");
			
			User user = repository.getUser(userId);
			if (user == null)
				throw new InvalidOAuthRequestException("User " + userId + " not found");
			
			Application app = requestToken.getApplication();
			
			// locate the authorization token for this user
			Token authToken = user.getAuthorization(app);
			
			// This application was not authorized.
			if (authToken == null)
			{
				log("Application " + app.getName() + " has not been authorized previously", null);
				return null;
			}
			else
			{	log("Application " + app.getName() + " previously authorized with roles " + 
					authToken.getRoles(), null);
			}
			
			// Application was authorized, but want to get a new authorization because it is nearing
			// expiration anyway.
			if (authToken.getExpirationDate().getTime() < (new Date().getTime() + Repository.REAUTHORIZATION_AGE*1000))
			{	log("Authorization near expiration date", null);
				return null;
			}
			
			callback = requestToken.getCallback();
			repository.createTemporaryToken(requestToken, user, authToken.getRoles());
			
			log("Temporary Token: " + requestToken, null);
			
			if ("oob".equals(callback))
				callback = requestToken.getVerifier();
			else
			{	// If the callback URL already contains the Query parameter, then add &
				callback += callback.contains("?") ? "&" : "?";
				callback += OAuthConstants.TOKEN + "=" + requestToken.getToken() + "&" +
					OAuthConstants.VERIFIER + "=" + requestToken.getVerifier();
			}
			return callback;
		}
		catch (InvalidOAuthRequestException oex)
		{	log("Authorization failure", oex);
			return null;
		}
	}

	@Override
	public Repository getRepository() {
		return repository;
	}

	@Override
	public Token getAccessToken(Token tempToken)
	{
		
		log("Performing getAccessToken", null);

		// Locate any existing authorization token.
		Token t = tempToken.getUser().getAuthorization(tempToken.getApplication());
		
		// If the current authorization is nearing expiration, get a new one.
		if (t != null && t.getExpirationDate().getTime() < (new Date().getTime() + Repository.REAUTHORIZATION_AGE*1000))
		{	log("Authorization near expiration date", null);
			t = null;
		}
		
		// If an existing authorization token was not found, return a new one.
		if (t == null)
			t = repository.createAccessToken(tempToken.getApplication(), tempToken.getUser(), tempToken.getRoles());
		
		log("Access Token: " + t, null);
		
		// We've used up this temporary token, so remove it.
		repository.remove(tempToken);
		// and return it.
		return t;	
	}

	public void log(String message, Exception ex)
	{
		if (!DEBUG)
			return;
		if (log == null)
			log = System.err;
		if (message != null)
			log.println(message);
		if (ex != null)
			ex.printStackTrace(log);
	}

	@Override
	public void setLog(OutputStream s) {
		if (s instanceof PrintStream)
			log = (PrintStream)s;
		else
			log = new PrintStream(s);
	}
}
