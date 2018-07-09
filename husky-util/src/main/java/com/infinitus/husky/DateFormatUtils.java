package com.infinitus.husky;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatUtils {

	/**
	 * 将时间转换为字符串
	 * @param pattern 转换后的格式
	 * @return 返回转换后的时间
	 */
	public static String formatDate2StringByPattern(String pattern,Date date){
		DateFormat format=new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
	/**
	 * 
	 * @param date
	 *  @return 返回时间格式yyyy-MM-dd HH:mm:ss
	 */
	public static String formatDate2String(Date date){
		return formatDate2StringByPattern("yyyy-MM-dd HH:mm:ss",date);
	}

	public static String formatDate2yyyyMMdd(Date date){
		return formatDate2StringByPattern("yyyyMMdd",date);
	}

	public static String formatDate2yyyy(Date date){
		return formatDate2StringByPattern("yyyy",date);
	}

	public static String formatDate2yyyyMM(Date date){
		return formatDate2StringByPattern("yyyyMM",date);
	}

	public static String formatDate2yyyyMMddHHmmssSSS(Date date){
		return formatDate2StringByPattern("yyyyMMddHHmmss.SSS",date);
	}
	
	public static Date formatString2DateByPattern(String pattern,String date){
		try {
			DateFormat format=new SimpleDateFormat(pattern);
			return format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}
	
	/**
	 * 
	 * @param date 参考时间
	 * @param count 偏移多少天，count为负，参考时间向前count天，count为正，参考时间向后count天
	 * @return 返回时间格式yyyy-MM-dd HH:mm:ss
	 */
	public static String currentDateBeforeOrAfter(Date date,int count){
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(date);
		rightNow.add(Calendar.DAY_OF_YEAR, count);
		Date beforedate=rightNow.getTime();
		return formatDate2String(beforedate);
	}
	
	
	
	
	
}
