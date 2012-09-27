package org.scribe.provider;

import org.scribe.model.Token;

public interface TokenRepository {
	enum TokenType {
		CLIENT_KEY,
		REQUEST_TOKEN,
		TEMP_TOKEN,
		ACCESS_TOKEN
	};
	public Token get(TokenType type, String key);
	public void put(TokenType type, Token t);
	public Token remove(TokenType type, String key);
	public Token create(String data);
	public boolean isNonceUsed(String nonce);
	public void saveNonce(String nonce);
}
