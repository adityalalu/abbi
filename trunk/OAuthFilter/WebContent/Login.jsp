<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head><title>Login</title></head>
	<body>
		<%
			if (request.getParameter("invalid") != null)
			{
				%>
				<p style='color:red; font-weight:bold'>Invalid Username or Password</p>
				<%
			}
		%>
		<div><p>Please log in to your account.</p></div>
		<form method="post" action="j_security_check">
			Username: <input type="text" name="j_username"/><br/>
			Password: <input type="text" name="j_password"/><br/>
			<input type="submit"/>
		</form>
	</body>
</html>