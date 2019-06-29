package com.android.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class DateUtils {

	public static long timeDifference(String startTime, String endTime, String pattern){
		
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			long start = sdf.parse(startTime).getTime();
			long end = sdf.parse(endTime).getTime();
			return start - end;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static long getDateTimeLong(String time, String pattern){
		
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 取后一天
	 * @param date
	 * @return
	 */
	public static Date getDateAfterOneDay(Date date){
		
	    Calendar calendar = new GregorianCalendar(); 
	    calendar.setTime(date); 
	    calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动 
	    return calendar.getTime();   //这个时间就是日期往后推一天的结果 
	}
	
	/**
	 * 获取当天日期的Long
	 * @param time
	 * @return
	 */
	public static long getLongformatLong(long time) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date(time);
			return sdf.parse(sdf.format(date)).getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String getStringformatLong(long time){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date = new Date(time);
			return sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
		
	}

	public static String getStringformatLong(long time, String pattern){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			Date date = new Date(time);
			return sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}

	/**
	 * 年-月-日
	 * @param date
	 * @return
     */
	public static String getStringfromatDate(Date date){
		return getStringfromatDate(date, "yyyy-MM-dd");
	}
	
	public static String getStringfromatDate(Date date, String pattern){
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			return sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
}
