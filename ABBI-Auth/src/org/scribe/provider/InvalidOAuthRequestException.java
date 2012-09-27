package org.scribe.provider;

public class InvalidOAuthRequestException extends Exception {

	public InvalidOAuthRequestException(String reason)
	{
		super(reason);
	}
}
