package org.siframework.abbi.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;

public class IO {

	/**
	 * Copy the contents of one stream to another.  
	 * @param is The stream to copy
	 * @param os The stream which will be copied to
	 * @throws IOException If an exception occurs during the copy
	 */
	public static void copy(InputStream is, OutputStream os) throws IOException
	{
		byte[] buffer = new byte[4096];
		int length = 0;
		
		while ((length = is.read(buffer)) > 0)
			os.write(buffer, 0, length);
	}

	/**
	 * Copy the contents of a Reader to a Writer.  
	 * @param r The Reader to copy from
	 * @param w The Writer which will be copied to
	 * @throws IOException If an exception occurs during the copy
	 */
	public static void copy(Reader r, Writer w) throws IOException 
	{
		char[] buffer = new char[4096];
		int length = 0;
		
		while ((length = r.read(buffer)) > 0)
			w.write(buffer, 0, length);
	}

	public static byte[] toByteArray(InputStream s)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int length = 0;
		
		try
		{
			while ((length = s.read(buffer)) > 0)
				bos.write(buffer, 0, length);
		}
		catch (IOException ioex)
		{
			return null;
		}
		return bos.toByteArray();
	}

	public static String toString(InputStream s)
	{
		
		try
		{
			byte[] buffer = toByteArray(s);
			// TODO For now, assume all content encoded in UTF-8
			// we could detect the encoding from the byte array
			return new String(buffer, "UTF-8");
		}
		catch (UnsupportedEncodingException ex)
		{
			throw new Error("UTF-8 Not Supported in this VM", ex);
		}
	}

	public static String toString(Reader r)
	{
		StringWriter w = new StringWriter();
		
		try {
			IO.copy(r, w);
			return w.toString();
		} catch (IOException e) {
			return null;
		}
	}
	
	public static byte[] toUTF8(String r)
	{
		try
		{	return r.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// This should never happen
			throw new Error("UTF-8 Not Supported by JVM", e);
		}
	}
	
	public static InputStream toUTF8Stream(String r)
	{
		return new ByteArrayInputStream(toUTF8(r));
	}
	
	public static class SequenceReader extends Reader {
		Iterator<Reader> r = null;
		Reader current = null;
		
		public SequenceReader(Iterator<Reader> r)
		{
			this.r = r;
		}

		@Override
		public int read(char[] cbuf, int off, int len) throws IOException 
		{
			int read = 0;
			do
			{	
				while (current == null)
				{	if (!r.hasNext())	// Nothing more to read
						return -1;
					current = r.next();
				}
				// Try to read from the current reader.
				read = current.read(cbuf, off, len);
			
				// If you failed or read nothing, set current to null
				if (read < 1)
					current = null;
				
				// Repeat when current = null
			}	while (read < 0);
			
			return read;
		}

		@Override
		public void close() throws IOException {
			r = null;
			current = null;
		}
	}
}
