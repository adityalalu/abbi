<?xml version="1.0"?>
<%@page import="org.scribe.builder.*, org.scribe.builder.api.*, org.scribe.model.*, org.scribe.oauth.*, java.util.*"%>
<%@page import="org.siframework.abbi.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Service Initialization</title>
</head>
<body>
<%
	// Tokens are stored in a weak hash map.  Occasionally we may run into cases
	// where the token is gc'd before the request gets back to the application.
	// In those cases, the user can just log in again.
	
	application.setAttribute("OAuthTokenMap", new WeakHashMap<String, Token>());
	
	// Allow swapping of OAuth provider
	boolean useTwitter = request.getParameter("useTwitter") != null;
	// Create the Twitter Authorization Service
	OAuthService service = new ServiceBuilder()
                                .provider(useTwitter ? TwitterApi.class : MyAuthApi.class)
                                .apiKey("E5yCcZlqEh9qTBZAqQcAg")
                                .apiSecret("A3P1FdX6mwSY1VF6FXoKq1p9ISpKgYBTb7BiViPeU48")
                                .callback("http://localhost:8080/ABBI/authenticate.jsp")
								.debugStream(System.err)
                                .build();

	
	application.setAttribute("OAuthService.Twitter", service);
	application.setAttribute("account.info.request", 
			useTwitter ? "https://api.twitter.com/1.1/account/verify_credentials.json" :
				"http://abbi-motorcycleguy.rhcloud.com//ABBI-Auth/verifyCredentials.jsp");
	String redirect = request.getParameter("redirect");
	if (redirect != null && redirect.length() != 0)
		response.sendRedirect(redirect);

%>
</body>
</html>