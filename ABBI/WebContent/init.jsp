<?xml version="1.0"?>
<%@page import="org.scribe.builder.*, org.scribe.builder.api.*, org.scribe.model.*, org.scribe.oauth.*, java.util.*"%>
<%@page import="org.siframework.abbi.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Service Initialization</title>
<script type='text/javascript' src='/ABBI/script/ga.js'></script>
</head>
<body>
<%
	// Tokens are stored in a weak hash map.  Occasionally we may run into cases
	// where the token is gc'd before the request gets back to the application.
	// In those cases, the user can just log in again.
	
	application.setAttribute("OAuthTokenMap", new HashMap<String, Token>());
	
	// Allow swapping of OAuth provider
	boolean useTwitter = request.getParameter("useTwitter") != null;
	// Create the Twitter Authorization Service
	OAuthService service = new ServiceBuilder()
                                .provider(useTwitter ? TwitterApi.class : MyAuthApi.class)
                                .apiKey("YLQguzhR2dR6y5M9vnA5m_bJ")
                                .apiSecret("LaM68B1Pt3DpjAMl9B0.uviY")
                                .callback("http://localhost:8080/ABBI/authenticate.jsp")
								.debugStream(System.err)
                                .build();
// YLQguzhR2dR6y5M9vnA5m_bJ , LaM68B1Pt3DpjAMl9B0.uviY
	
	application.setAttribute("OAuthService.Twitter", service);
	application.setAttribute("account.info.request", 
			useTwitter ? "https://api.twitter.com/1.1/account/verify_credentials.json" :
				"http://localhost:8080/OAuthFilter/api/verifyCredentials.jsp");
	String redirect = request.getParameter("redirect");
	if (redirect != null && redirect.length() != 0)
		response.sendRedirect(redirect);

%>
</body>
</html>