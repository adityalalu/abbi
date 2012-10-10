package org.siframework.abbi.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.siframework.abbi.atom.Entry;

public class Util {
	
	public static final String ISO8601 = "yyyyMMddHHmmss.SSSZ";

	// Used to convert dateTime to GMT/UTC
	public static final Calendar GMTCAL =
		new GregorianCalendar(TimeZone.getTimeZone("GMT"));
	
	public static final Comparator<Entry> ENTRYCOMPARATOR =
		new Comparator<Entry>() {
		public int compare(Entry o1, Entry o2) {
			int result = o1.getPublished().compareTo(o2.getPublished());
			if (result == 0)
				result = o1.getId().compareTo(o2.getId());
			return result;
		}
	};
	
	// Format a date as in a String  as ISO8601 using UTC Time Zone
	public static String asUTCString(Date s)
	{
		if (s == null)
			return null;
		
        SimpleDateFormat sdf = new SimpleDateFormat(ISO8601.substring(0,14));
        sdf.setCalendar(GMTCAL);
        return sdf.format(s);
	}
	
	public static Date parseISODate(String date)
	{
		String format;
		int pos = date.replace('-', '+').indexOf('+');
		if (pos > 0)
			format = ISO8601.substring(0, pos) + "Z";
		else
			format = ISO8601.substring(0, date.length());
		
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
}
