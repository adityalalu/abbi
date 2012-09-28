package org.scribe.provider;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.provider.TokenRepository.TokenType;
import org.scribe.utils.Preconditions;

public class OAuthProvider10aImpl implements OAuthProvider {

	private TokenRepository repository = null;
	private static final boolean DEBUG = true;
	
	public OAuthProvider10aImpl(TokenRepository rep)
	{
		Preconditions.checkNotNull(rep, "repository cannot be null.");
		repository = rep;
	}
	
	public void validate(HttpServletRequest req) throws InvalidOAuthRequestException
	{
		log("Validating Request", null);
		try
		{
			OAuthRequest r = Util.createRequest(req);
			log("OAuth Parameters: " + r.getOauthParameters(), null);
			Util.validate(r, getRepository());
		}
		catch (InvalidOAuthRequestException ex)
		{
			log(null, ex);
		}
		log("Request is valid", null);
	}
	
	@Override
	public Token getRequestToken(HttpServletRequest req) throws InvalidOAuthRequestException
	{
		String callback = null;
		log("Performing getRequestToken", null);
		
		OAuthRequest r = Util.createRequest(req);
		Map<String,String> oauthParams = r.getOauthParameters();

		TokenRepository repository = getRepository();
		
		callback = oauthParams.get(OAuthConstants.CALLBACK);
		
		Util.validate(r, repository);
		
		// Validate ensures that the client key exists, so we don't need to worry about that.
		// So we create the request token
		Token t = repository.create(callback);

		// Store it.
		repository.put(TokenType.REQUEST_TOKEN, t);
		
		log("Request Token: " + t, null);
		
		// and return it.
		return t;
	}
	
	@Override
	public String getCallback(HttpServletRequest req) throws InvalidOAuthRequestException 
	{
		log("performing getCallback", null);
		String tokenKey = req.getParameter(OAuthConstants.TOKEN);
		TokenRepository rep = getRepository();
		Token t = rep.get(TokenType.REQUEST_TOKEN, tokenKey);
		if (t == null)
		{	log("Token Invalid", null);
		 	throw new InvalidOAuthRequestException("Token Invalid");
		}
		log("Callback: " + t.getRawResponse(), null);
		return t.getRawResponse();
	}

	@Override
	public String authorizeUser(HttpServletRequest req) throws InvalidOAuthRequestException 
	{
		log("Performing authorizeUser", null);
		
		String tokenKey = req.getParameter(OAuthConstants.TOKEN);
		TokenRepository rep = getRepository();
		
		Token t = rep.remove(TokenType.REQUEST_TOKEN, tokenKey);

		// If the token doesn't exist, then throw an exception
		if (t == null)
		 	throw new InvalidOAuthRequestException("Token Invalid");

		// Make a temporary token to from it, storing verifier with it.
		String verifier = String.format("%08d", (int)(java.lang.Math.random() * 100000000));
		Token tempToken = new Token(t.getToken(), t.getSecret(), verifier);
		rep.put(TokenType.TEMP_TOKEN, tempToken);

		log("Temporary Token: " + tempToken, null);
		String callback = t.getRawResponse();
		if ("oob".equals(callback))
			callback = verifier;
		else
		{	// If the callback URL already contains the Query parameter, then add &
			callback += callback.contains("?") ? "&" : "?";
			callback += OAuthConstants.TOKEN + "=" + tempToken.getToken() + "&" +
				OAuthConstants.VERIFIER + "=" + verifier;
		}
		
		log("Callback: " + callback, null);
		return callback;
	}

	@Override
	public TokenRepository getRepository() {
		return repository;
	}

	@Override
	public Token getAccessToken(HttpServletRequest req) throws InvalidOAuthRequestException 
	{
		log("Performing getAccessToken", null);
		String verifier = null;
		OAuthRequest r = Util.createRequest(req);
		Map<String,String> oauthParams = r.getOauthParameters();

		TokenRepository repository = getRepository();
	
		Token t = repository.get(TokenType.TEMP_TOKEN, oauthParams.get(OAuthConstants.TOKEN));
		verifier = oauthParams.get(OAuthConstants.VERIFIER);
		
		Util.validate(r, repository);
		
		if (t.getRawResponse() == null || !t.getRawResponse().equals(verifier))
		{
			log("Verifier does not match", null);
			throw new InvalidOAuthRequestException("Verifier does not match.");
		}
		
		t = repository.create(null);

		// Store it.
		repository.put(TokenType.ACCESS_TOKEN, t);

		log("Access Token: " + t, null);
		// and return it.
		return t;	
	}

	public void log(String message, Exception ex)
	{
		if (!DEBUG)
			return;
		
		if (message != null)
		{
			System.err.println(message);
		}
		if (ex != null)
		{
			ex.printStackTrace(System.err);
		}
	}
}
