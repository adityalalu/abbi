package org.siframework.abbi.api;

public interface Logger {

	public void log(String message);
	public void log(String message, Throwable t);
}
