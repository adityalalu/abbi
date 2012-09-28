package org.scribe.provider;

import javax.servlet.http.HttpServletRequest;

import org.scribe.model.Token;

public interface OAuthProvider 
{
	public Token getRequestToken(HttpServletRequest req) throws InvalidOAuthRequestException;
	public String authorizeUser(HttpServletRequest req) throws InvalidOAuthRequestException;
	public String getCallback(HttpServletRequest req) throws InvalidOAuthRequestException;
	public Token getAccessToken(HttpServletRequest req)  throws InvalidOAuthRequestException;
	public void validate(HttpServletRequest req) throws InvalidOAuthRequestException;
	public TokenRepository getRepository();
	public void log(String message, Exception ex);
}
