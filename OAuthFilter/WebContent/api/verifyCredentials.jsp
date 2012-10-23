<%@page import="java.security.Principal, org.scribe.servlet.OAuthUser"%>
<%	response.setHeader("Content-Type", "application/json"); 
	OAuthUser u = null;
	Principal p = request.getUserPrincipal();
	if (p instanceof OAuthUser)
	{
		u = (OAuthUser)p;
	}
%>{ 
	"Test": "This is a test",
	"name": "<%= request.getRemoteUser() %>",
	"screen_name": "<%= u.getImpersonating() %>"
}
