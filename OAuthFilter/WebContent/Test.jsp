<%@page import="java.security.Principal"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head><title>Test Page</title></head>
	<body>
		<div><p>This is a test page.</p></div>
		<p>You are <%= request.getRemoteUser() %></p><% 
		Principal p = request.getUserPrincipal();
		%>
		<p><%= p.getClass().getName() %></p>
		<%
			if (request.getParameter("logout") != null)
			{	HttpSession s = request.getSession(false);
				if (s != null)
					s.invalidate();
			}
%><p><a href="Test.jsp?logout=true">Logout</a></p>
</body>
</html>