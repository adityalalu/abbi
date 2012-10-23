package org.scribe.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.model.Token;
import org.scribe.provider.InvalidOAuthRequestException;
import org.scribe.provider.OAuthProvider;

/**
 * This servlet implements the OAuth Token Request.  It presumes that
 * an OAuth Provider implementation has been stored as an application
 * attribute using the OAuth.Provider key.
 * To configure this servlet in your web application, use the following
 * declaration and mapping.
 * &lt;servlet&gt;
 *      &lt;servlet-name>TokenRequestServlet&lt;/servlet-name&gt;
 *      &lt;servlet-class>org.siframework.abbi.oauth.servlet.TokenRequest&lt;/servlet-class&gt;
 *	&lt;/servlet&gt;
 *	&lt;servlet-mapping&gt;
 *      &lt;servlet-name&gt;TokenRequestServlet&lt;/servlet-name&gt;
 *      &lt;url-pattern&gt;/oauth/request&lt;/url-pattern&gt;
 *	&lt;/servlet-mapping&gt;
 * 
 * @author Keith W. Boone
 */
public class OAuthServlet 
	extends HttpServlet 
{
	public final static String REQUEST_PATH = "/request", ACCESS_PATH = "/access";
	
	@Override
	public String getServletInfo()
	{
		return "";
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	{
		try {
			resp.sendError(404, "Not Found");
		} catch (IOException e) {
			log(null, e);
		}
	}
	
	public void log(String message, Throwable t)
	{
		if (message != null)
			System.err.println(message);
		if (t != null)
			t.printStackTrace(System.err);
	}
	
	public void log(String message)
	{
		log(message, null);
	}
	
	private final String RESPONSE = "oauth_token=%s&oauth_token_secret=%s&oauth_callback_confirmed=true";
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	{
		String reqURL = req.getRequestURL().toString();
		String operation = reqURL.substring(reqURL.lastIndexOf('/'));

		// Get the provider from the request.  The OAuthFilter does that for us.
		OAuthProvider p = (OAuthProvider)req.getAttribute("OAuth.Provider");
		
		if (p == null || req.getUserPrincipal() == null)
		{	try {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						p == null ? "Request not initialized." 
								: "Not Authorized");
			} catch (IOException e) {
				log(null, e);
			}
			return;
		}

		try {
			OAuthUser u = (OAuthUser) req.getUserPrincipal();
			Token t = null;
			
			if (REQUEST_PATH.equals(operation) && req.isUserInRole(OAuthUser.OAUTH_CLIENT_ROLE))
				t = p.getRequestToken(u.getToken(), u.getCallback());
			else if (ACCESS_PATH.equals(operation) && req.isUserInRole(OAuthUser.OAUTH_VERIFIER_ROLE))
				t = p.getAccessToken(u.getToken());
			else
			{	log("Invalid operation: " + operation, null);
				resp.sendError(HttpServletResponse.SC_NOT_FOUND, operation);
				return;
			}
			
			resp.setHeader("Content-Type", "application/x-www-form-urlencoded");
			PrintWriter pw = resp.getWriter();
			String response =
				String.format(RESPONSE, t.getToken(), t.getSecret());
			pw.print(response);
			try {
				log("Response:\n" + response, null);
			}
			catch (Throwable t2)
			{
				t2.printStackTrace(System.err);
			}
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (IOException e) {
			log("Error writing response", e);
		}
	}

}
