package utils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class NumUtils {

	public static TimeZone timezone = TimeZone.getTimeZone("GMT+0");

	public static void main(String[] args){
		//		System.out.println(convertFullDateToMilliSeconds("10/22/2013 10:00:00"));
		//		System.out.println(convertMilliSecondsToFullDate(1386349284360.0));
		double timezoneOffset = 8;

//		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
//		Date date = new Date();
//
//		String timezone = "-8:00";
//		TimeZone zone = TimeZone.getTimeZone("GMT"+timezone);
//
//		df.setTimeZone(zone);	
//		timezoneOffset = zone.getOffset(date.getTime())/3600000.0;		
//
//
//		String startDay = df.format(date);	
//		System.out.println(startDay + "\t timezoneOffset: "+timezoneOffset);
//
//		double referenceStartDayTime = 0;
//
//		try {
//			System.out.println(df.parse(startDay).getTime());
//		} catch (ParseException e2) {
//			e2.printStackTrace();
//		}
		
		System.out.println(convertMilliSecondsToSimpleTime(System.currentTimeMillis()));

	}


	public static double getDoublefromString(String NumberString){

		double Number = 0.0;


		try {

			Number = Double.parseDouble(NumberString);

		} catch (NumberFormatException e){



			if (NumberString.isEmpty()){
				// it is ok to return 0.0 if the string is empty
				return 0.0;

			} else {
				System.err.println("Cannot parse a double from "+NumberString);
				e.printStackTrace();

				return Double.NaN;

			}			

		} catch (NullPointerException e){

			System.err.println("Cannot parse a double from a Null String");
			e.printStackTrace();
			return Double.NaN;

		}

		return Number;		

	}

	public static int getIntegerfromString(String number){

		if (!number.isEmpty()){
			return Integer.parseInt(number);
		} else{
			return 0;
		}

	}

	public static boolean getBooleanfromString(String value){

		if (!value.isEmpty()){

			if (value.contains("T") || value.contains("true")){
				return true;
			} else if (value.contains("F") || value.contains("false")) {
				return false;
			} else {
				return false;
			}

		} else{
			return false;
		}

	}

	public static double roundTwoDecimals(double d) {

		if (Double.POSITIVE_INFINITY != d && Double.NEGATIVE_INFINITY != d){

			DecimalFormat twoDForm = new DecimalFormat("#.##");
			return Double.valueOf(twoDForm.format(d));
		} else {
			return d;
		}        
	}

	public static double roundFourDecimals(double d) {

		if (Double.POSITIVE_INFINITY != d && Double.NEGATIVE_INFINITY != d){

			DecimalFormat twoDForm = new DecimalFormat("#.####");
			return Double.valueOf(twoDForm.format(d));

		} else {
			return d;
		}        
	}
	
	public static double roundFiveDecimals(double d) {

		if (Double.POSITIVE_INFINITY != d && Double.NEGATIVE_INFINITY != d){

			DecimalFormat twoDForm = new DecimalFormat("#.#####");
			return Double.valueOf(twoDForm.format(d));

		} else {
			return d;
		}        
	}

	public static String convertMilliSecondsToFullDate(double milliSeconds)
	{
		// Create a DateFormatter object for displaying date in specified format.
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS");
		formatter.setTimeZone(timezone);
		// Create a calendar object that will convert the date and time value in milliseconds to date. 
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis((long) milliSeconds);

		return formatter.format(calendar.getTime());
	}

	public static String convertMilliSecondsToSimpleDate(double milliSeconds)
	{
		// Create a DateFormatter object for displaying date in specified format.
		DateFormat formatter = new SimpleDateFormat("MM/dd HH:mm:ss");
		formatter.setTimeZone(timezone);

		// Create a calendar object that will convert the date and time value in milliseconds to date. 
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis((long) milliSeconds);

		return formatter.format(calendar.getTime());
	}

	public static String convertMilliSecondsToTime(double milliSeconds)
	{
		// Create a DateFormatter object for displaying date in specified format.
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		formatter.setTimeZone(timezone);

		// Create a calendar object that will convert the date and time value in milliseconds to date. 
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis((long) milliSeconds);

		return formatter.format(calendar.getTime());
	}

	public static String convertMilliSecondsToSimpleTime(double milliSeconds)
	{
		// Create a DateFormatter object for displaying date in specified format.
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		formatter.setTimeZone(timezone);

		// Create a calendar object that will convert the date and time value in milliseconds to date. 
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis((long) milliSeconds);

		return formatter.format(calendar.getTime());
	}

	public static String convertMilliSecondsToCustomDate(double milliSeconds, String format)
	{
		// Create a DateFormatter object for displaying date in specified format.
		DateFormat formatter = new SimpleDateFormat(format);
		formatter.setTimeZone(timezone);

		// Create a calendar object that will convert the date and time value in milliseconds to date. 
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis((long) milliSeconds);

		return formatter.format(calendar.getTime());
	}

	public static long convertFullDateToMilliSeconds(String Date)
	{
		// Create a DateFormatter object for displaying date in specified format.
		long Time = 0;
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		format.setTimeZone(timezone);

		try {

			Date date = format.parse(Date);
			// Earliest full hour before the start event
			Time = date.getTime();

		} catch (ParseException e) {
			System.err.println("Cannot convert " + Date + " to millisecond.");
		}

		return Time;
	}


	public static double convertMinsToMs(double minutes)
	{ 
		return roundTwoDecimals(minutes*1000.0*60.0);
	}

	public static double convertMsToMins(double ms)
	{ 
		return roundTwoDecimals(ms/1000.0/60.0);
	}

	//	public static double convertMilliSecondsToHours(double milliSeconds)
	//	{
	//
	//		if (milliSeconds/1000.0/60.0/60.0 > 1){
	//		     return roundTwoDecimals(milliSeconds/1000.0/60.0/60.0);
	//		} else {
	//			return milliSeconds/1000.0/60.0/60.0;		
	//		}		
	//
	//	}

	public static int roundInteger(double d) {

		return (int) Math.ceil(d);

	}

	public static int ceilInteger(double d) {

		return (int) Math.ceil(d);

	}

	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

}
