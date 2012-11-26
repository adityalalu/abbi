package org.siframework.abbi.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * A utility package supporting Date and Time conversions.
 * @author Keith W. Boone
 */
public class DT {
	
	/**
	 * The unadorned ISO8601 Date format used by HL7 and IHE specifications.
	 */
	public static final String ISO8601 = "yyyyMMddHHmmss.SSSZ";
	/** 
	 * The format for converting dates in XML
	 */
	public static final String ISO8601XML = "yyyy-MM-dd'T'HH:mm:ssZ";

	/**
	 * A calendar to support conversions to UTC.
	 */
	public static final Calendar UTCCAL =
		new GregorianCalendar(TimeZone.getTimeZone("GMT"));
	
	/**
	 * Convert a Date in the current locale to a String representing that date
	 * in ISO8601 format (without punctuation). 
	 * @param date	The date to convert.
	 * @return A string representing the date in ISO8601 format.
	 */
	public static String asUTCString(Date date)
	{
		if (date == null)
			return null;
		
        SimpleDateFormat sdf = new SimpleDateFormat(ISO8601.substring(0,14));
        sdf.setCalendar(UTCCAL);
        return sdf.format(date);
	}
	
	/**
	 * Parse a string containing a date in ISO8601 format (with or without punctuation)
	 * into a Date object
	 * @param date A string containing the date in IS8601 format.
	 * @return A Date object representing that date/time.
	 */
	public static Date parseISODate(String date)
	{
		if (date == null)
			return null;

		// If the fourth character is -, assume XML format, otherwise, unadorned ISO8601 (e.g., HL7 and IHE)
		String format = (date.length() > 4 && date.charAt(4) == '-') ? ISO8601XML : ISO8601;
		
		// Adjust the format to deal with timezone presence or absence
		
		int pos = date.replace('+', '-').lastIndexOf('-');
		// if the position of the - is after position 10, then this indicates that a timezone offset is present.
		if (pos > 10)
			format = format.substring(0, pos) + "Z";  // truncate the format to input precision and add timezone
		else
			format = format.substring(0, date.length());	// truncate to the input precision
		
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date result = null;
		
		try
		{
			result = sdf.parse(date);
		}
		catch (ParseException ex)
		{
			// We don't care
		}
		return result;
	}
	
	/**
	 * Parse a date in long integer format representing a date as an offset in seconds
	 * from Midnight, January 1, 1970
	 * @param date A string containing the date specified as offset in seconds from the EPOCH
	 * @return A Date object representing that date
	 */
	public static Date parseDate(String date)
	{	
		try
		{
			long value = Long.parseLong(date);
			return new Date(value * 1000);
		}
		catch (NumberFormatException ex)
		{
			// We don't care
			return null;
		}
	}
	
	/**
	 * Format a date into ISO8601 format.  If isXML is true, use the XML form, otherwise, use
	 * the unpunctuated form preferred by HL7 and IHE.
	 * @param date	The date to convert.
	 * @param isXML true if punctuated format is to be used, false otherwise
	 * @return The formatted date
	 */
	public static String formatISODate(Date date, boolean isXML)
	{	if (date == null)
			return null;
		SimpleDateFormat format = new SimpleDateFormat(isXML ? ISO8601XML : ISO8601);
		return format.format(date);
	}
}
