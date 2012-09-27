<%@page import="org.scribe.provider.*" %><%	response.setHeader("Content-Type", "application/json"); 
	OAuthProvider provider = (OAuthProvider)application.getAttribute("OAuth.Provider");
	try
	{	provider.validate(request); }
	catch (InvalidOAuthRequestException ex)
	{	ex.printStackTrace();
		response.sendError(401);
	}
%>{ 
	"Test": "This is a test",
	"name": "Hi Keith",
	"screen_name": "Did this work?"
}
