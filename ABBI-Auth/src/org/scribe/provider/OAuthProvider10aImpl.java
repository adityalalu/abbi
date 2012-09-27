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
	
	public OAuthProvider10aImpl(TokenRepository rep)
	{
		Preconditions.checkNotNull(rep, "repository cannot be null.");
		repository = rep;
	}
	
	public void validate(HttpServletRequest req) throws InvalidOAuthRequestException
	{
		Util.validate(Util.createRequest(req), getRepository());
	}
	
	@Override
	public Token getRequestToken(HttpServletRequest req) throws InvalidOAuthRequestException
	{
		String callback = null;
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
		
		// and return it.
		return t;
	}
	
	@Override
	public String getCallback(HttpServletRequest req) throws InvalidOAuthRequestException 
	{
		String tokenKey = req.getParameter(OAuthConstants.TOKEN);
		TokenRepository rep = getRepository();
		Token t = rep.get(TokenType.REQUEST_TOKEN, tokenKey);
		if (t == null)
		 	throw new InvalidOAuthRequestException("Token Invalid");
		return t.getRawResponse();
	}

	@Override
	public String authorizeUser(HttpServletRequest req) throws InvalidOAuthRequestException 
	{
		
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
		
		String callback = t.getRawResponse();
		if ("oob".equals(callback))
			return verifier;
		else
		{	// If the callback URL already contains the Query parameter, then add &
			callback += callback.contains("?") ? "&" : "?";
			callback += OAuthConstants.TOKEN + "=" + tempToken.getToken() + "&" +
				OAuthConstants.VERIFIER + "=" + verifier;
			return callback;
		}
	}

	@Override
	public TokenRepository getRepository() {
		return repository;
	}

	@Override
	public Token getAccessToken(HttpServletRequest req) throws InvalidOAuthRequestException 
	{
		String verifier = null;
		OAuthRequest r = Util.createRequest(req);
		Map<String,String> oauthParams = r.getOauthParameters();

		TokenRepository repository = getRepository();
	
		Token t = repository.get(TokenType.TEMP_TOKEN, oauthParams.get(OAuthConstants.TOKEN));
		verifier = oauthParams.get(OAuthConstants.VERIFIER);
		
		Util.validate(r, repository);
		if (t.getRawResponse() == null || !t.getRawResponse().equals(verifier))
			throw new InvalidOAuthRequestException("Verifier does not match.");
		
		t = repository.create(null);

		// Store it.
		repository.put(TokenType.ACCESS_TOKEN, t);
		
		// and return it.
		return t;	
	}

}
