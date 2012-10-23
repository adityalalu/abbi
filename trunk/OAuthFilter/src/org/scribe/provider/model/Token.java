package org.scribe.provider.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;

public class Token extends org.scribe.model.Token {
	public enum TokenType {
		CLIENT_KEY,
		REQUEST_TOKEN,
		TEMP_TOKEN,
		ACCESS_TOKEN;
	}

	private TokenType type;
	private Date expiration = null;
	private Application application = null;
	private User user = null;
	private Set<String> roles = new HashSet<String>();
	private String verifier = null;
	
	public Token(TokenType type, String token, String secret, String data)
	{	super(token, secret, data);
		this.type = type;
	}
	public Token(Token oldToken, String token, String secret) {
		super(token, secret);
		this.type = getNextType(oldToken.getType());
		application = oldToken.getApplication();
	}
	public Token(Token oldToken, String token, String secret, String data) {
		super(token, secret, data);
		this.type = getNextType(oldToken.getType());
		application = oldToken.getApplication();
	}
	
	public Date getExpirationDate() 
	{
		return this.expiration;
	}
	public void setExpirationDate(Date expiration) 
	{
		this.expiration = expiration;
	}
	
	public TokenType getType()
	{
		return this.type;
	}
	
	public Application getApplication()
	{
		return application;
	}
	public void setApplication(Application app)
	{
		this.application = app;
	}
	
	public User getUser()
	{
		return user;
	}
	public void setUser(User user)
	{
		this.user = user;
	}
	
	
	public String getCallback()
	{
		if (type == TokenType.REQUEST_TOKEN)
			return getRawResponse();
		return null;
	}
	
	public void setVerifier(String verifier)
	{
		if (type == TokenType.REQUEST_TOKEN)
		{	this.verifier = verifier;
			this.type = TokenType.TEMP_TOKEN;
		}
	}
	
	public String getVerifier()
	{	if (type == TokenType.TEMP_TOKEN)
			return verifier;
		return null;
	}
	
	public Set<String> getRoles()
	{
		return roles;
	}
	
	public static TokenType getNextType(TokenType type)
	{
		switch (type) {
		case CLIENT_KEY: return TokenType.REQUEST_TOKEN;
		case REQUEST_TOKEN: return TokenType.TEMP_TOKEN;
		case TEMP_TOKEN: return TokenType.ACCESS_TOKEN;
		default: return null;
		}
	}

	/**
	 * A random bit generator for use in creating random keys and secrets
	 * of varying lengths.
	 */
	// TODO Remove Seed
	private static java.util.Random generator = new java.util.Random(0);
	
	/**
	 * A utility method to generate a key/secret pair of the specified lengths.
	 * @param keyLength
	 * @param secretLength
	 * @return An array containing two strings, the first containing a random
	 * string of characters representing a key, and the second containing a
	 * random string of characters representing a shared secret.
	 */
	public static String[] generateKeys(int keyLength, int secretLength)
	{
		// Each character represents 6 bits of data.
		// So we find the multiple of 8 that is greater than key
		int totalLength = (((keyLength + secretLength) * 6) / 8) + 1;
		String parts[] = new String[2];
		byte key[] = new byte[totalLength];
		generator.nextBytes(key);
		String result = new String(Base64.encodeBase64(key));

		// Fix for double decode bug where + in token gets converted to
		// spaces.  This fix doesn't eliminate the bug.  It just prevents
		// it from being triggered.
		result = result.replace('+', '.').replace('/', '_');
		parts[0] = result.substring(0, keyLength);
		parts[1] = result.substring(keyLength, keyLength + secretLength);
		return parts;
	}
}
