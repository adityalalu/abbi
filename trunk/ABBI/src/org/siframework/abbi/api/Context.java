package org.siframework.abbi.api;

import java.io.InputStream;

/** The Context interface is passed to the back-end to provide it with access to a few critical
 * services, such as logging, and access to resources by name.
 * @author Keith W. Boone
 **/
public interface Context {
	/**
	 * This method generates a message in the log
	 * @param message  The message to print.
	 */
	public void log(String message);
	/**
	 * This method generates a message and a stack trace in the log
	 * @param message The message to generate (may be null)
	 * @param t	A throwable whose stack trace will be written to the log
	 */
	public void log(String message, Throwable t);
	
	/**
	 * This method provides access to a file resource as a stream.
	 * @param name The name of the resource to access.
	 * @return	An InputStream that can be used to access the resource.
	 */
	public InputStream getResource(String name);
}
