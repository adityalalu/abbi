package org.siframework.abbi;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class MyAuthApi extends DefaultApi10a
{
	// private static final String HOST = "http://localhost:8080";
	private static final String HOST = "https://abbi-motorcycleguy.rhcloud.com";	// For SSL Testing
	// private static final String HOST = "http://abbi-motorcycleguy.rhcloud.com";	// For Testing in a way that can be Captured
	private static final String AUTHORIZE_URL = "/ABBI-Auth/Authorize.jsp?oauth_token=%s";
	private static final String REQUEST_TOKEN_RESOURCE = "/ABBI-Auth/oauth/request";
	private static final String ACCESS_TOKEN_RESOURCE = "/ABBI-Auth/oauth/access";
	public String getAccessTokenEndpoint()
	{
	    return HOST + ACCESS_TOKEN_RESOURCE;
	}

	@Override
	public String getRequestTokenEndpoint()
	{
		return HOST + REQUEST_TOKEN_RESOURCE;
	}

	@Override
	public String getAuthorizationUrl(Token requestToken)
	{
	    return String.format(HOST + AUTHORIZE_URL, requestToken.getToken());
	}

}
