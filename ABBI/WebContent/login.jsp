<?xml version="1.0"?>
<%@page import="org.scribe.builder.*, org.scribe.builder.api.*, org.scribe.model.*, org.scribe.oauth.*, java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Login</title>
<script type='text/javascript' src='/ABBI/script/ga.js'></script>
</head>
<body>
<%
	// Tokens are stored in a weak hash map.  Occasionally we may run into cases
	// where the token is gc'd before the request gets back to the application.
	// In those cases, the user can just log in again.
	
	HashMap<String, Token> m = (HashMap<String, Token>)application.getAttribute("OAuthTokenMap");
	OAuthService service = (OAuthService)application.getAttribute("OAuthService.Twitter");
	if (m == null || service == null)
		response.sendRedirect("init.jsp?redirect=login.jsp");
	
	Token requestToken = null;
	String loginUrl = null;

	try
	{	requestToken = service.getRequestToken();
		loginUrl = service.getAuthorizationUrl(requestToken);
		System.err.println("Request Token: " + requestToken);
		System.err.println("loginUrl: " + loginUrl);
		
		application.getAttribute("OAuthTokenMap");
			
		m.put(requestToken.getToken(),requestToken);
%><a href='<%= loginUrl%>'><img alt='Sign in with twitter' src="images/sign-in-with-twitter-l.png"/></a><%
	}
	catch (Exception e)
	{	e.printStackTrace();
%><p>We're sorry.  Twitter does not seem to be responding right now.  Refresh the page to try again.
</p><%
	}
%>
</body>
</html>