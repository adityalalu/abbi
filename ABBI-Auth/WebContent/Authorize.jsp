<%@page import="org.scribe.builder.*, org.scribe.builder.api.*, org.scribe.model.*, org.scribe.oauth.*, java.util.*"%>
<%@page import="org.scribe.provider.*, org.scribe.provider.TokenRepository.TokenType"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<body>
<%
	OAuthProvider provider = 
		(OAuthProvider)application.getAttribute("OAuth.Provider");

	if (request.getMethod().equals("POST"))
	{	// Validate the user login credentials. If valid, send a redirect 
		// to original application callback with the Temporary Token.
		String password = request.getParameter("password"); 
		String canceled = request.getParameter("Cancel");
		if (canceled != null && canceled.length() != 0)
		{
			String callback = provider.getCallback(request);	
			if (callback.contains(":"))
				response.sendRedirect(callback);
		}
		else if (password != null && password.length() != 0)
		{	try
			{
				String callback = provider.authorizeUser(request);
			
				if (callback.contains(":"))
					response.sendRedirect(callback);
				else
				{	%>You have successfully authorized your device.  Your temporary access code is:<br/>
					<h2><%= callback %></h2>
					Enter this access code on your device to complete the process.
					<%
				}
			}
			catch (InvalidOAuthRequestException e)
			{	// This exception is thrown if the token isn't valid
				response.sendError(401, "Invalid Token");
			}
		}
		else
		{	// If not valid, generate invalid username/password message and repeat
			// login attempt.
			%><p style='color: dark-red, font-weight: bold'>Invalid Username or Password</p><% 
		}
	}
%>
<div><p>By entering your name and password below and clicking on "I Agree", you agree to the following:</p>
<blockquote><i>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do 
eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, 
quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. 
Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu 
fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in 
culpa qui officia deserunt mollit anim id est laborum.</i></blockquote>
</div>
<form method="post">
	<input type="hidden" name="<%= OAuthConstants.TOKEN %>" value="<%=request.getParameter(OAuthConstants.TOKEN)%>"/>
	Username: <input type="text" name="username"/><br/>
	Password: <input type="text" name="password"/><br/>
	<input type="submit" value="I Agree"/><input type="submit" name="Cancel" value="Cancel"/>
</form>
</body>
</html>