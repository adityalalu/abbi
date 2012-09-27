package org.siframework.abbi.oauth.servlet;

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
 * 
 * 
 * @author Keith W. Boone
 */

/* To configure this servlet in your web application, use the following
 * declaration and mapping.
 	<servlet>
 		<servlet-name>TokenRequestServlet</servlet-name>
 		<servlet-class>org.siframework.abbi.oauth.servlet.TokenRequest</servlet-class>
 	</servlet>
 	<servlet-mapping>
 		<servlet-name>TokenRequestServlet</servlet-name>
    	<url-pattern>/oauth/request</url-pattern>
 	</servlet-mapping>
 */
public class OAuthRequest 
	extends HttpServlet 
{
	@Override
	public String getServletInfo()
	{
		return "";
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	{
		if (req.getRequestURL().toString().contains("test"))
			doPost(req, resp);
		else
		{	try {
				resp.sendError(404, "Not Found");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private final String RESPONSE = "oauth_token=%s&oauth_token_secret=%s&oauth_callback_confirmed=true";
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	{
		String reqURL = req.getRequestURL().toString();
		String operation = reqURL.substring(reqURL.lastIndexOf('/'));

		OAuthProvider p = (OAuthProvider)getServletConfig().getServletContext().getAttribute("OAuth.Provider");
		try {
			
			Token t = null;
			
			if ("/request".equals(operation))
				t = p.getRequestToken(req);
			else if ("/access".equals(operation))
				t = p.getAccessToken(req);
			else
			{	resp.sendError(404, operation);
				return;
			}
			
			resp.setHeader("Content-Type", "application/x-www-form-urlencoded");
			PrintWriter pw = resp.getWriter();
			String response =
				String.format(RESPONSE, t.getToken(), t.getSecret());
			pw.print(response);
			resp.setStatus(200);
		} catch (InvalidOAuthRequestException e) {
			e.printStackTrace();
			try {
				resp.sendError(401, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
