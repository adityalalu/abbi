package org.scribe.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.model.OAuthConstants;
import org.scribe.provider.InvalidOAuthRequestException;
import org.scribe.provider.MemoryRepository;
import org.scribe.provider.OAuthProvider;
import org.scribe.provider.model.Application;
import org.scribe.provider.model.OAuthData;
import org.scribe.provider.model.Repository;
import org.scribe.provider.model.Token;

/**
 * This class implements an HTTP Servlet Filter to support applications wanting to
 * user OAuth.
 * @author Keith W. Boone
 */
public class OAuthFilter implements Filter {

	/**
	 * The OAuth Provider configured to work with this filter.
	 */
	private OAuthProvider provider = null;
	/**
	 * The Repository configured to work with this filter.
	 */
	private Repository repository = null;
	
	/**
	 * The default realm to use for requests needing authorization.
	 */
	private String realm = "OAuth";
	
	/**
	 * The name of the Authorization Page
	 */
	private String authorizationPage = "/Authorize.jsp";
	
	private final static String 
		DEFAULT_PROVIDER_CLASS = org.scribe.provider.OAuthProvider10a.class.getName();
	private final static String
		DEFAULT_REPOSITORY_CLASS = org.scribe.provider.MemoryRepository.class.getName();

	public final static String
		REPOSITORY_INIT_NAME = "Repository",
		PROVIDER_INIT_NAME = "Provider",
		REALM_INIT_NAME = "Realm",
		PAGE_INIT_NAME = "AuthorizationPage";


	@Override
	public void destroy() {
		provider = null;
		repository = null;
	}

	@Override
	public void doFilter(
		ServletRequest sRequest, ServletResponse sResponse, FilterChain chain) 
			throws IOException, ServletException 
	{
		if (!(sRequest instanceof HttpServletRequest) ||
			!(sResponse instanceof HttpServletResponse))
			throw new ServletException("This filter only runs on HttpRequests");
		
		HttpServletRequest request = (HttpServletRequest)sRequest;
		HttpServletResponse response = (HttpServletResponse)sResponse;
		
		// If this request is for the authorization page, then just
		// set the OAuth.Provider header attribute and continue
		if (request.getRequestURL().toString().endsWith(authorizationPage))
		{	request.setAttribute("OAuth.Provider", provider);
			chain.doFilter(request, response);
			return;
		}
		
		// If there was no OAuth Header then fail.
		String oAuthHeader = request.getHeader("Authorization");
		if (oAuthHeader == null || oAuthHeader.length() == 0)
		{	response.setHeader("WWW-Authenticate", "OAuth realm=\"" + realm + "\"");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Not Detected");
			return;
		}
		else if (!oAuthHeader.startsWith("OAuth"))
		{	response.setHeader("WWW-Authenticate", "OAuth realm=\"" + realm + "\"");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Use OAuth Authentication");
			return;
		}
		
		OAuthData data = null;
		try {
			data = provider.validate(request); 
		} catch (InvalidOAuthRequestException e) {
			response.setHeader("WWW-Authenticate", "OAuth realm=\"" + realm + "\"");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not Authorized");
			return;
		}
		
		OAuthUser u = new OAuthUser(data.getToken(), data.getParameters().get(OAuthConstants.CALLBACK));
		OAuthRequestWrapper w = new OAuthRequestWrapper(request, u);
		request.setAttribute("OAuth.Provider", provider);
		chain.doFilter(w, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Get the Repository and Provider Class Names
		String tkrClassName = filterConfig.getInitParameter(REPOSITORY_INIT_NAME);
		if (tkrClassName == null || tkrClassName.length() == 0)
			tkrClassName = DEFAULT_REPOSITORY_CLASS;
		
		String providerName = filterConfig.getInitParameter(PROVIDER_INIT_NAME);
		if (providerName == null || providerName.length() == 0)
			providerName = DEFAULT_PROVIDER_CLASS;
		
		String theRealm = filterConfig.getInitParameter(REALM_INIT_NAME);
		if (theRealm != null)
			realm = theRealm;
		
		String thePage = filterConfig.getInitParameter(PAGE_INIT_NAME);
		if (thePage != null)
			authorizationPage = thePage;
		
		try {
			repository = (Repository)Class.forName(tkrClassName).newInstance();
			provider = (OAuthProvider)Class.forName(providerName).newInstance();
			provider.setRepository(repository);

			MemoryRepository r = (MemoryRepository)repository;
			Application app = r.createApplication("TestApp", r.createUser("kwboone"));
			r.createUser("test");
			System.err.println(app.getClientToken());
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new ServletException("Exception instantiating Filter", e);
		}
	}

}
