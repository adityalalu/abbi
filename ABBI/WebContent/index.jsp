<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ABBI</title>
<script type='text/javascript' src='/ABBI/script/ga.js'></script>
</head>
<body>
	<h1>Welcome to the ABBI Prototype Home Page</h1>
	<p>At this stage, the prototype demonstrates the use of three different protocols.  
	You can <a href="test.jsp">test</a> the service, or read 
	<a href="api/doc/index.htm">service documentation</a>.  All data is being served for the
	same sample patient by the
	<a href="http://ihexds.nist.gov/">NIST IHE Document Sharing/Public Registry Test Facility</a>.  
	(If that service is not available, this service will not work.)</p>
	<p>The service at present just implements retrieval of metadata, and does not yet support 
	retrieval of documents.  The ABBI protocol supports the many of the specified search 
	parameters.  Feel free to try various combinations.  Note that due to very sparse test data,
	currently loaded, it isn't necessarily possible to get a feel for what the different search
	parameters do.</p>
	<h2>Architecture</h2>
	<p>The prototype acts as a gateway to an XDS Registry using the Query and Document Retrieval APIs.
	I'm using <a href='https://www.projects.openhealthtools.org/sf/sfmain/do/viewProject/projects.iheprofiles'>OHT's 
	IHE Profiles Project</a> to support retrieval from the back end.  The 
	<a href='/ABBI/api/javadoc/?/ABBI/api/javadoc/org/siframework/abbi/api/xds/XDSImpl.html'>back end</a> has 
	an <a href='/ABBI/api/javadoc/?/ABBI/api/javadoc/org/siframework/abbi/api/API.html'>API</a> and 
	is pluggable in my prototype so that other systems can also be used.  The reason for 
	supporting XDS/XCA from the start is to enable ABBI to eventually work 
	with <a href='http://www.connectopensource.org/product/connect-nhin-specs'>NwHIN Exchange</a> 
	protocols which are based on XDS and XCA.  The <a href='/ABBI/api/javadoc/?/ABBI/api/javadoc/org/siframework/abbi/servlet/Search.html'>front end 
	servlet</a> retrieves <a href='/ABBI/api/javadoc/?/ABBI/api/javadoc/org/siframework/abbi/api/SearchParameters.html'>query parameters</a>, sends them to the back end
	and generates the output in Atom format.  The back end processes the query parameters, turns them into an
	XDS Stored Query Request, and sends that request to the NIST Registry.  It then converts the retrieved results
	into an an array of Atom <a href="/ABBI/api/javadoc/?/ABBI/api/javadoc/org/siframework/abbi/atom/Entry.html">Entries</a> which 
	it attaches to an Atom <a href='/ABBI/api/javadoc/?/ABBI/api/javadoc/org/siframework/abbi/atom/Feed.html'>Feed</a>.
	</p>
	<p>The front-end servlet is configured through the <a href="/ABBI/api/doc/search.api.mapping.properties.htm">search.api.mapping.properties</a> file 
	residing in the WEB-INF folder of the application.</p>
	<h2>Source Code</h2>
	<p>Source Code resides (for now) in an SVN Repository in <a href='http://code.google.com/p/abbi/'>Google Code</a></p>
	<p>Java Doc for the code can be found <a href='/ABBI/api/javadoc'>here</a> (a lot needs to be written).</p>
	<h2>To Do List</h2>
	<p>There is a fairly extensive list of things to do.  If you are familiar with Java, Tomcat, 
	and/or OHT, I could use help with these.  Feel free to 
	<a href="mailto:keith.boone@ge.com">e-mail me</a>.</p>
	<ul>
	<li>Access Document Content through different protocols</li>
	<li><strike>Add Search Parameters to MHD and FHIR Protocols</strike></li>
	<li><strike>Support the JSON Output format for the FHIR Protocol</strike></li>
	<li>Support for pagination (and result caching) in the API</li>
	<li>Updated Documentation</li>
	<li>Document Architecture or lack thereof ;-)</li>
	</ul>
</body>
</html>