<%@page import="java.util.*, org.scribe.model.OAuthConstants, org.scribe.provider.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<body>
<%
/*
	This page must be protected in web.xml, so that users must authenticate to get here.  This page uses
	whatever authentication method is already supported in your web application.  
	request.getUserPrincipal() must return a non-null principal where principal.getName() is the user id in your API 
	and is equal to request.getRemoteUser();
*/
	OAuthProvider provider = (OAuthProvider)request.getAttribute("OAuth.Provider");
	String callback = null;

	if ("POST".equals(request.getMethod()))
	{	
		String[] roles = request.getParameterValues("Role");
		if (roles == null)
			callback = "";
		else 
			callback = provider.authorizeApplication(request, Arrays.asList(roles));

		// Defend against providers returning a null callback string.
		if (callback == null)
			callback = "";
	}
	else if ("GET".equals(request.getMethod()))
	{	// Check to see if this application is already authorized.
		callback = provider.checkAuthorization(request); 
	}
	else
	{	response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, request.getMethod() + " method not allowed");
		return;
	}
	
	if (callback != null)
	{	if (callback.contains(":"))
			response.sendRedirect(callback);
		else if (callback.length() == 0) 
		{
			%><p>Your device was not authorized.</p><%
		}
		else
		{
		%>
			<p>You have successfully authorized your device.  Your temporary access code is:</p>
			<h2><%= callback %></h2>
			<p>Enter this access code on your device to complete the process.</p>
		<%
		}
		return;
	}
%>
<div><p>By clicking on "I Agree", you agree to the following:</p>
<blockquote><i>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do 
eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, 
quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. 
Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu 
fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in 
culpa qui officia deserunt mollit anim id est laborum.</i></blockquote>
</div>
<form method="post">
	<input type="hidden" name="<%= OAuthConstants.TOKEN %>" value="<%=request.getParameter(OAuthConstants.TOKEN)%>"/>
	<input type="checkbox" name="Role" value="Emergency"/>Access to Emergency Health Data<br/>
	<input type="checkbox" name="Role" value="Private"/>Access to private Health Data<br/>
	<input type="checkbox" name="Role" value="Restricted"/>Access to Health Data you have marked as restricted<br/>
	<input type="submit" value="I Agree"/>
</form>
</body>
</html>