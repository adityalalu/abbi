package org.scribe.provider;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.scribe.extractors.BaseStringExtractorImpl;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;
import org.scribe.provider.model.Repository;
import org.scribe.provider.model.Token;
import org.scribe.provider.model.Token.TokenType;
import org.scribe.services.HMACSha1SignatureService;
import org.scribe.utils.OAuthEncoder;

public class Util {
	private static final String SIGHEADER = "X-OAuth-Signature";
	/**
	 * Translate the HttpServletRequest into an OAuthRequest for the purpose
	 * of validating and extracting parameters.  This method DOES not not 
	 * distinguish between body parameters in a POST, and query parameters in a GET 
	 * request.  The resulting request is not mean to be sent (in fact, it has
	 * been sent already ... to this application).
	 * 
	 * The Signature parameter is removed from the request and stored in 
	 * the X-OAuth-Signature header of the OAuthRequest.  This is done so that 
	 * when the signature is recomputed, the original signature can be verified.
	 * 
	 * @param req  The request to translate
	 * @return An OAuthRequest with header, body and query parameters set 
	 * and stored for signing.
	 */
	public static OAuthRequest createRequest(HttpServletRequest req)
	{
		OAuthRequest r = 
			new OAuthRequest(
					getVerb(req.getMethod()), 
					req.getRequestURL().toString());
		
		// It doesn't matter whether we add parameters as body or query 
		// parameters for the signing test.
		@SuppressWarnings("unchecked")
		Enumeration<String> e = (Enumeration<String>)req.getParameterNames();
		while (e.hasMoreElements())
		{	String param = e.nextElement();
			for (String value : req.getParameterValues(param))
			{
				if (param.startsWith(OAuthConstants.PARAM_PREFIX))
				{	if (param.equals(OAuthConstants.SIGNATURE))
						r.addHeader(SIGHEADER, param);
					else
						r.addOAuthParameter(param, value);
				}
				else if (param.equals(OAuthConstants.SCOPE))
					r.addOAuthParameter(param, value);
				else
					r.addQuerystringParameter(param, value);
			}
		}
		
		String oauthHeader = req.getHeader("Authorization");
		if (oauthHeader != null)
		{
			Map<String,String> params = parseHeader(oauthHeader);
			// Check to see that the header using OAuth as the scheme
			if (params.containsKey("OAuth") && params.get("OAuth").length() == 0)
			{
				params.remove("OAuth");
				String signature = params.remove(OAuthConstants.SIGNATURE);
				r.addHeader(SIGHEADER, signature == null ? "" : signature);
				// If it does, add the keys and values
				for (String k: params.keySet())
				 	r.addOAuthParameter(k, params.get(k));
			}
		}
		
		return r;
	}
	
	public static Map<String,String> parseHeader(String header)
	{
		HashMap<String,String> map = new HashMap<String,String>(10);
		
		String params[] = header.split("[,\\s]\\s*");
		for (int i = 0; i < params.length; i++)
		{	int pos = params[i].indexOf('=');
			if (pos < 0)
				map.put(OAuthEncoder.decode(params[i]),"");
			else
			{	String key = OAuthEncoder.decode(params[i].substring(0,pos));
				if (pos + 3 > params[i].length())
					throw new IllegalArgumentException("Header Improperly formatted: " + header);
				// Using pos+2 strips =" from the beginning of the value
				String value = params[i].substring(pos + 2);
				// Using value.length()-1 strips the trailing " from the value
				value = OAuthEncoder.decode(value.substring(0, value.length()-1));
				map.put(key, value);
			}
		}
		return map;
	}
	
	public static Verb getVerb(String method) 
	{
		if (Verb.GET.toString().equals(method))
			return Verb.GET;
		if (Verb.POST.toString().equals(method))
			return Verb.POST;
		return null;
	}

	public static Token validate(OAuthRequest r, Repository tokens) throws InvalidOAuthRequestException
	{
		Map<String,String> params = r.getOauthParameters();
		
		String 	timeStamp = params.get(OAuthConstants.TIMESTAMP),
				nonce = params.get(OAuthConstants.NONCE),
				version = params.get(OAuthConstants.VERSION);
		
		if (version != null && !version.equals("1.0"))
			throw new InvalidOAuthRequestException("Version must be 1.0");
		try
		{	long	tValue = (System.currentTimeMillis()/1000) - Long.parseLong(timeStamp);
			if (tValue > Repository.MAX_NONCE_AGE)
				throw new InvalidOAuthRequestException("Request is too old.");
		}
		catch (NumberFormatException ex)
		{	InvalidOAuthRequestException e = new InvalidOAuthRequestException("Timestamp invalid.");
			e.initCause(ex);
			throw e;
		}
		
		if (tokens.isNonceUsed(nonce))
			throw new InvalidOAuthRequestException("Nonce has already been used.");
		
		// Get either the consumer_key, or the token from the request.
		// If there is only a consumer key, token secret is null, and consumer_secret
		// is the only thing we use to sign the request.
		
		
		String 	consumerKey = params.get(OAuthConstants.CONSUMER_KEY),
				tokenKey = params.get(OAuthConstants.TOKEN);
		Token	clientToken = null,
				token = null;
		
		if (consumerKey != null)
		{	clientToken = tokens.getToken(consumerKey);
			if (clientToken == null)
				throw new InvalidOAuthRequestException("Consumer Key Invalid");
			if (clientToken.getType() != Token.TokenType.CLIENT_KEY)
				throw new InvalidOAuthRequestException("Invalid Consumer Key");
		}
		else 
			throw new InvalidOAuthRequestException("Missing Consumer Key.");

		
		if (tokenKey != null)
		{	token = tokens.getToken(tokenKey);
			if (token == null)
				throw new InvalidOAuthRequestException("Key Invalid");
			
			// Validate that this token is associated with the client
			// This test ensures that clients apps do not attempt to use 
			// authorization tokens associated with other client apps.
			// This ensures that a token assigned to a given client application
			// can only be used with that client and not other, even if
			// another application somehow got hold of it.
			if (!token.getApplication().getName().equals(clientToken.getApplication().getName()))
				throw new InvalidOAuthRequestException("Token / App Mismatch");
		}
		
		String signature = 
			getSignature(r, clientToken.getSecret(), 
				token == null ? "" : token.getSecret()
			);
		
		if (!signature.equals(r.getHeaders().get(SIGHEADER)) )
			throw new InvalidOAuthRequestException("Signature Invalid");
		
		if (token != null)
		{	if (token.getType() == TokenType.TEMP_TOKEN)
			{	String verifier = params.get(OAuthConstants.VERIFIER);
				if (verifier == null || !verifier.equals(token.getVerifier()))
					throw new InvalidOAuthRequestException("Verifier Invalid");
			}
		}
		// This is a valid request.
		return token == null ? clientToken : token;
	}
	
	public static String getSignature(OAuthRequest r, String consumerSecret, String tokenSecret)
	{
		BaseStringExtractorImpl extractor = new BaseStringExtractorImpl();
	    String baseString = extractor.extract(r);
	    
	    HMACSha1SignatureService signatureService = new HMACSha1SignatureService();
	    
	    String signature = 
	    	signatureService.getSignature(baseString, consumerSecret, tokenSecret);
	    
	    return signature;
	}
}
