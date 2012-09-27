<?xml version="1.0"?>
<%@page import="org.scribe.builder.*, org.scribe.builder.api.*, org.scribe.model.*, org.scribe.oauth.*, java.util.*"%>
<%@page import="org.scribe.provider.*, org.scribe.provider.TokenRepository.TokenType"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Service Initialization</title>
</head>
<body>
<%
	MemoryTokenRepositoryImpl rep = new MemoryTokenRepositoryImpl();
	Token t = new Token("E5yCcZlqEh9qTBZAqQcAg","A3P1FdX6mwSY1VF6FXoKq1p9ISpKgYBTb7BiViPeU48");
	rep.put(TokenType.CLIENT_KEY, t);
	
	OAuthProvider10aImpl providerImpl = new OAuthProvider10aImpl(rep);
	
	application.setAttribute("OAuth.Provider", providerImpl);
	
	String redirect = request.getParameter("redirect");
	if (redirect != null && redirect.length() != 0)
		response.sendRedirect(redirect);

%>
</body>
</html>