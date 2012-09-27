package org.scribe.provider;

import org.apache.commons.codec.binary.Base64;
import org.scribe.model.Token;
import org.scribe.utils.Preconditions;

import java.util.HashMap;
import java.lang.Math;

/**
 * This class implements a token repository suitable for testing, but not deployment,
 * as there is no backing store for tokens.  It can be used as the base class for an 
 * implementation that provides a backing store.
 * 
 * @author Keith W. Boone
 *
 */
public class MemoryTokenRepositoryImpl implements TokenRepository 
{
	private HashMap<String, Token> tokens = new HashMap<String, Token>();
	private java.util.Random generator = new java.util.Random();
	
	@Override
	public Token get(TokenType type, String key) {
		Preconditions.checkNotNull(type, "Type cannot be null");
		Preconditions.checkNotNull(key, "Key cannot be null");
		Token t = tokens.get(type.toString() + ":" + key);
		return t;
	}
	

	@Override
	public void put(TokenType type, Token t) {
		Preconditions.checkNotNull(type, "Type cannot be null");
		Preconditions.checkNotNull(t, "Token cannot be null");
		// TODO Auto-generated method stub
		tokens.put(type.toString() + ":" + t.getToken(), t);
	}

	@Override
	public Token remove(TokenType type, String key) {
		Preconditions.checkNotNull(type, "Type cannot be null");
		Preconditions.checkNotNull(key, "Key cannot be null");
		return tokens.remove(type.toString() + ":" + key);
	}

	@Override
	public Token create(String data) {
		// TODO Auto-generated method stub
		byte key[] = new byte[24];
		generator.nextBytes(key);
		String result = new String(Base64.encodeBase64(key));
		// Fix for double decode bug where + in token gets converted to
		// spaces.  This fix doesn't eliminate the bug.  It just prevents
		// it from being triggered.
		result = result.replace('+', '.').replace('/', '_');
		Token t = new Token(result.substring(0, 20), result.substring(20), data);
		return t;
	}
	
	@Override
	public boolean isNonceUsed(String nonce) 
	{
		Preconditions.checkNotNull(nonce, "Nonce cannot be null");
		return tokens.containsKey("Nonce:" + nonce);
	}

	@Override
	public void saveNonce(String nonce) 
	{
		Preconditions.checkNotNull(nonce, "Nonce cannot be null");
		tokens.put("Nonce:" + nonce, null);
	}
	
	public static void main(String args[])
	{
		MemoryTokenRepositoryImpl impl = new MemoryTokenRepositoryImpl();
		System.out.println("Token: " + impl.create(null));
	}
}
