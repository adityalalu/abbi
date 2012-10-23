package org.scribe.servlet;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.scribe.provider.model.Application;
import org.scribe.provider.model.Token;

public class OAuthRequestWrapper extends HttpServletRequestWrapper {

	OAuthUser oauthUser;
	
	public OAuthRequestWrapper(HttpServletRequest request, OAuthUser oauthUser) 
	{	super(request);
		this.oauthUser = oauthUser;
	}
	
	@Override
	public Principal getUserPrincipal()
	{
		return oauthUser;
	}
	
	@Override
	public String getRemoteUser()
	{
		return oauthUser.getName();
	}
	
	@Override
	public boolean isUserInRole(String rolename)
	{
		return oauthUser.isUserInRole(rolename);
	}
}
