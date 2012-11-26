<?xml version="1.0"?>
<%@page import="org.scribe.builder.*, org.scribe.builder.api.*, org.scribe.model.*, org.scribe.oauth.*, com.google.gson.*, java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Authentication Complete</title>
<script type='text/javascript' src='/ABBI/script/ga.js'></script>
</head>
<%
	response.setHeader("Pragma", "no-cache");
	response.setHeader("Cache-Control", "no-cache");
	
	// Tokens are stored in a weak hash map.  Occasionally we may run into cases
	// where the token is gc'd before the request gets back to the application.
	// In those cases, the user can just log in again.
	HashMap<String, Token> m = (HashMap<String, Token>)application.getAttribute("OAuthTokenMap");
	OAuthService service = (OAuthService)application.getAttribute("OAuthService.Twitter");

	if (m == null || service == null)
		response.sendRedirect("init.jsp?redirect=login.jsp");
	String name = null, handle = null;
	String verifier = request.getParameter("oauth_verifier");
	if (verifier != null)
	{	Token requestToken = m.get(request.getParameter("oauth_token")); 
		if (requestToken != null)
		{	Verifier v = new Verifier(verifier);
			Token accessToken = service.getAccessToken(requestToken, v);
			session.setAttribute("OAuthAccessToken", accessToken);
			
			OAuthRequest req = new OAuthRequest(Verb.GET, 
				(String)application.getAttribute("account.info.request")
			);
		    service.signRequest(accessToken, req);
		    Response resp = req.send();
		    
		    JsonParser p = new JsonParser();
		    
		    String result = resp.getBody();
		    
		    System.err.println("Response:\n"+result);
		    JsonObject user = (JsonObject)p.parse(result);
		    
		    name = user.get("name").toString();
		    handle = user.get("screen_name").toString();
		    
		    System.err.println("name: " + name );
		    System.err.println("handle: " + handle);
		    
		    // OK, now I have a user name, and an access token.
		}
		
	    
	}
%>
<p> Hello <%= handle %>! (p.s., I know you really are <%= name %>)</p>
</html>