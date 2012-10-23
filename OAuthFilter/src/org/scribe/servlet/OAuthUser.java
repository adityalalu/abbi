package org.scribe.servlet;

import java.security.Principal;

import org.scribe.provider.model.Token;
import org.scribe.provider.model.User;

public class OAuthUser implements Principal {

	private final Token authorizingToken;
	private final String callback;
	protected OAuthUser(Token authorizingToken, String callback)
	{
		this.authorizingToken = authorizingToken;
		this.callback = callback;
	}
	
	@Override
	public String getName() {
		return authorizingToken.getApplication().getName();
	}

	public String getImpersonating() {
		if (authorizingToken == null)
			return null;
		User u = authorizingToken.getUser();
		return u == null ? null : u.getName();
	}
	
	public static final String
	OAUTH_CLIENT_ROLE = "OAuth.Client",
	OAUTH_AUTHORIZER_ROLE = "OAuth.Authorizor",
	OAUTH_VERIFIER_ROLE = "OAuth.Verifier",
	OAUTH_USER_ROLE = "OAuth.User";
	
	protected Token getToken()
	{
		return authorizingToken;
	}
	
	protected boolean isUserInRole(String role)
	{
		switch (authorizingToken.getType()) {
		case CLIENT_KEY:
			// If just the client key is given, they a token request is being performed
			if (OAUTH_CLIENT_ROLE.equals(role))
				return true;
			break;
		case TEMP_TOKEN:
			// If a temporary token is given, then an access request is being performed. 
			if (OAUTH_VERIFIER_ROLE.equals(role))
				return true;
			break;
		case REQUEST_TOKEN:
			if (OAUTH_AUTHORIZER_ROLE.equals(role))
				return true;
			break;
		case ACCESS_TOKEN:
			if (OAUTH_USER_ROLE.equals(role))
				return true;
			break;
		}
		
		return authorizingToken != null && authorizingToken.getRoles().contains(role);
	}
	
	protected String getCallback()
	{
		return callback;
	}
}
