<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Protocol Test Page</title>
</head>
<body>
	<h1>Prototype Test Page</h1>
	<p>The following URLs can be used to test the service.  They will produce output in the 
	formats specified below.  Clicking on the links below will populate the frame with results 
	from the service according to the specifications for the protocol. Depending on how your browser
	is configured to deal with the content types, the frame may be populated in a variety of ways.
	For example, IE will display the Atom feeds using it's Atom feed viewer, and will ask you to
	save the application/json output rather than displaying it.  Using Chrome without any extensions
	will display the content in the frame below, but if you have extensions enabled to deal with 
	Atom feeds (or application/json content), those extensions will display in the frame.
	</p>
	<ul>
		<li>The ABBI Protocol: <a href="/ABBI/search.atom" 
			onclick="DocURL.value=this.href" target="results">/ABBI/search.atom</a></li>
		<li>The IHE MHD Protocol: <a href="/ABBI/net.ihe/DocumentDossier/search" 
			onclick="DocURL.value=this.href" target="results">/ABBI/net.ihe/DocumentDossier/search</a></li>
		<li>The HL7 FHIR Protocol: <a href="/ABBI/xdsentry/search" 
			onclick="DocURL.value=this.href" target="results">/ABBI/xdsentry/search</a></li>
	</ul>
	<textarea rows="1" cols="98" id="DocURL" disabled='true'></textarea><br/>
	<iframe name="results" id="results" width="800" height="400" scrolling="yes"></iframe>
</body>
</html>