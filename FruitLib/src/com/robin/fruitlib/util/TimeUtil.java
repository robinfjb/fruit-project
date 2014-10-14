package com.robin.fruitlib.util;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.format.Time;


public class TimeUtil {
	/**
	 * return current full time format as YYYYMMDDHHMMSS
	 * 
	 * @return long
	 */
	public static long getNowFullTime() {
		Time time = new Time();
		time.setToNow();
		String fullTime = time.format2445();
		return Long.parseLong(fullTime.substring(0, 8) + fullTime.substring(9));
	}

	/**
	 * return current time format as HHMMSS
	 * @return int
	 */
	public static int getNowTime() {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
		return Integer.parseInt(timeFormat.format(new Date()));
	}
	
	/**
	 * return current time format as HHMM
	 * @return
	 */
	public static String getNowTimeHHMM() {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		return timeFormat.format(new Date());
	}
	
	public static String getDelayTimeHHMM(long seconds) {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		Date date = new Date();
		long newTime = date.getTime() + seconds * 1000;
		return timeFormat.format(new Date(newTime));
	}
	
	/**
	 * return current date format as YYMMDD
	 * @return int
	 */
	public static int getNowDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
		return Integer.parseInt(dateFormat.format(new Date()));
	}
	
	/**
	 * return custom date base of today, format as YYYYMMDD
	 * @param day, the number of day after or before, 0 represents today
	 * @return int
	 */
	public static int getDateCompareToday(int day) {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		date = calendar.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return Integer.parseInt(dateFormat.format(date));
	}
	
	/**
	 * calculate the time distance between now and target time in millisecond
	 * @param day
	 * @param time
	 * @return millisecond
	 */
	public static long calculateBySecondsCompareNow(long timeday) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		ParsePosition pos = new ParsePosition(0);
		Date dt1 = formatter.parse(String.valueOf(timeday), pos);
		Date date = new Date();
		return dt1.getTime() - date.getTime();
		
	}
	
	public static String timeStamp2Date(String timestampString, String formats){
		 	Long timestamp = Long.parseLong(timestampString) * 1000 ;    
		  String date = new java.text.SimpleDateFormat(formats).format(new java.util.Date(timestamp));    
		  return date;    
	}
	
	public static String timeStamp2DateOnlyHour(String timestampString){
	 	Long timestamp = Long.parseLong(timestampString) * 1000 ;    
	  String date = new java.text.SimpleDateFormat("yyyy:mm:dd:HH:mm:ss").format(new java.util.Date(timestamp));    
	  return date.substring(11,16);    
}
}
