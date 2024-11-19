package com.imbos.chat.util;


import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	/**
	 * 静态常量
	 */
	public static final String C_TIME_PATTON_DEFAULT = "yyyy-MM-dd HH:mm:ss";
	public static final String C_DATE_PATTON_DEFAULT = "yyyy-MM-dd";
	
	public static final int C_ONE_SECOND = 1000;
	public static final int C_ONE_MINUTE = 60 * C_ONE_SECOND;
	public static final long C_ONE_HOUR = 60 * C_ONE_MINUTE;
	public static final long C_ONE_DAY = 24 * C_ONE_HOUR;
	
	
	/**
	 * 计算当前月份的最大天数
	 * @return
	 */
	public static int findMaxDayInMonth() {
		return findMaxDayInMonth(0, 0);
	}
	
	/**
	 * 计算指定日期月份的最大天数
	 * @param date
	 * @return
	 */
	public static int findMaxDayInMonth(Date date) {
		if (date == null) {
			return 0;
		}
		return findMaxDayInMonth(date2Calendar(date));
	}
	
	/**
	 * 计算指定日历月份的最大天数
	 * @param calendar
	 * @return
	 */
	public static int findMaxDayInMonth(Calendar calendar) {
		if (calendar == null) {
			return 0;
		}
		
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * 计算当前年某月份的最大天数
	 * @param month
	 * @return
	 */
	public static int findMaxDayInMonth(int month) {
		return findMaxDayInMonth(0, month);
	}
	
	/**
	 * 计算某年某月份的最大天数
	 * @param year
	 * @param month
	 * @return
	 */
	public static int findMaxDayInMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		if (year > 0) {
			calendar.set(Calendar.YEAR, year);
		}
		
		if (month > 0) {
			calendar.set(Calendar.MONTH, month - 1);
		}
		
		return findMaxDayInMonth(calendar);
	}
	
	/**
	 * Calendar 转换为 Date
	 * @param calendar
	 * @return
	 */
	public static Date calendar2Date(Calendar calendar) {
		if (calendar == null) {
			return null;
		}
		return calendar.getTime();
	}
	
	/**
	 * Date 转换为 Calendar
	 * @param date
	 * @return
	 */
	public static Calendar date2Calendar(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	
	/**
	 * 拿到默认格式的SimpleDateFormat
	 * @return
	 */
	public static SimpleDateFormat getSimpleDateFormat() {
		return getSimpleDateFormat(null);
	}
	
	/**
	 * 拿到指定输出格式的SimpleDateFormat
	 * @param format
	 * @return
	 */
	public static SimpleDateFormat getSimpleDateFormat(String format) {
		SimpleDateFormat sdf;
		if (format == null) {
			sdf = new SimpleDateFormat(C_TIME_PATTON_DEFAULT);
		} else {
			sdf = new SimpleDateFormat(format);
		}
		
		return sdf;
	}
	
	/**
	 * 转换当前时间为默认格式
	 * @return
	 */
	public static String formatDate2Str() {
		return formatDate2Str(new Date());
	}
	
	/**
	 * 转换指定时间为默认格式
	 * @param date
	 * @return
	 */
	public  static String formatDate2Str(Date date) {
		return formatDate2Str(date, C_TIME_PATTON_DEFAULT);
	}
	
	/**
	 * 转换指定时间为指定格式
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDate2Str(Date date, String format) {
		if (date == null) {
			return null;
		}
		
		if (format == null || format.equals("")) {
			format = C_TIME_PATTON_DEFAULT;
		}
		SimpleDateFormat sdf = getSimpleDateFormat(format);
		return sdf.format(date);
	}
	
	/**
	 * 转换默认格式的时间为Date
	 * @param dateStr
	 * @return
	 */
	public static Date formatStr2Date(String dateStr) {
		return formatStr2Date(dateStr, null);
	}
	
	/**
	 * 转换指定格式的时间为Date
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Date formatStr2Date(String dateStr, String format) {
		if (dateStr == null) {
			return null;
		}
		
		if (format == null || format.equals("")) {
			format = C_TIME_PATTON_DEFAULT;
		}
		SimpleDateFormat sdf = getSimpleDateFormat(format);
		return sdf.parse(dateStr, new ParsePosition(0));
	}
	
	/**
	 * 转换默认格式的时间为指定格式时间
	 * @param dateStr
	 * @param defineFormat
	 * @return
	 */
	public static String formatDefault2Define(String dateStr, String defineFormat) {
		return formatSource2Target(dateStr, C_TIME_PATTON_DEFAULT, defineFormat);
	}
	
	/**
	 * 转换源格式的时间为目标格式时间
	 * @param dateStr
	 * @param sourceFormat
	 * @param targetFormat
	 * @return
	 */
	public static String formatSource2Target(String dateStr, String sourceFormat, String targetFormat) {
		Date date = formatStr2Date(dateStr, sourceFormat);
		return formatDate2Str(date, targetFormat);
	}
	
	/**
	 * 计算当天是该年中的第几周
	 * @return
	 */
	public static int findWeeksNoInYear() {
		return findWeeksNoInYear(new Date());
	}
	
	/**
	 * 计算指定日期是该年中的第几周
	 * @param date
	 * @return
	 */
	public static int findWeeksNoInYear(Date date) {
		if (date == null) {
			return 0;
		}
		return findWeeksNoInYear(date2Calendar(date));
	}
	
	/**
	 * 计算指定日历是该年中的第几周
	 * @param calendar
	 * @return
	 */
	public static int findWeeksNoInYear(Calendar calendar) {
		if (calendar == null) {
			return 0;
		}
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}
	
	/**
	 * 计算一年中的第几星期是几号 
	 * @param year
	 * @param weekInYear
	 * @param dayInWeek
	 * @return
	 */
	public static Date findDateInWeekOnYear(int year, int weekInYear, int dayInWeek) {
		Calendar calendar = Calendar.getInstance();
		if (year > 0) {
			calendar.set(Calendar.YEAR, year);
		}
		
		calendar.set(Calendar.WEEK_OF_YEAR, weekInYear);
		calendar.set(Calendar.DAY_OF_WEEK, dayInWeek);
		
		return calendar.getTime();
	}
	
	/**
	 * 相对于当前日期的前几天(days < 0０００００)或者后几天(days > 0)
	 * @param days
	 * @return
	 */
	public static Date dayBefore2Date(int days) { 
		Date date = new Date();
		return dayBefore2Object(days, null, date);
	}
	
	/**
	 * 相对于当前日期的前几天(days < 0０００００)或者后几天(days > 0)
	 * @param days
	 * @return
	 */
	public static String dayBefore2Str(int days) {
		String string = formatDate2Str();
		return dayBefore2Object(days, null, string);
	}
	
	/**
	 * 相对于当前日期的前几天(days < 0０００００)或者后几天(days > 0)
	 * @param days
	 * @param format
	 * @param instance
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> T dayBefore2Object(int days, String format, T instance) {
		Calendar calendar = Calendar.getInstance();
		if (days == 0) {
			return null;
		}
		
		if (format == null || format.equals("")) {
			format = C_TIME_PATTON_DEFAULT;
		}
		
		calendar.add(Calendar.DATE, days);
		if (instance instanceof Date) {
			return (T)calendar.getTime();
		} else if (instance instanceof String) {
			return (T)formatDate2Str(calendar2Date(calendar), format);
		}
		return null;
	}
	
	/**
	 * 相对于指定日期的前几天(days < 0０００００)或者后几天(days > 0)
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date defineDayBefore2Date(Date date, int days) {
		Date dateInstance = new Date();
		return defineDayBefore2Object(date, days, null, dateInstance);
	}
	
	/**
	 * 相对于指定日期的前几天(days < 0０００００)或者后几天(days > 0)
	 * @param date
	 * @param days
	 * @return
	 */
	public static String defineDayBefore2Str(Date date, int days) {
		String stringInstance = formatDate2Str();
		return defineDayBefore2Object(date, days, null, stringInstance);
	}
	
	/**
	 * 相对于指定日期的前几天(days < 0０００００)或者后几天(days > 0)
	 * @param <T>
	 * @param date
	 * @param days
	 * @param format
	 * @param instance
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> T defineDayBefore2Object(Date date, 
			int days, String format, T instance) {
		if (date == null || days == 0) {
			return null;
		}
		
		if (format == null || format.equals("")) {
			format = C_TIME_PATTON_DEFAULT;
		}
		
		Calendar calendar = date2Calendar(date);
		calendar.add(Calendar.DATE, days);
		if (instance instanceof Date) {
			return (T)calendar.getTime();
		} else if (instance instanceof String) {
			return (T)formatDate2Str(calendar2Date(calendar), format);
		}
		return null;
	}
	
	/**
	 * 相对于当前日期的前几月(months < 0０００００)或者后几月(months > 0)时间
	 * @param months
	 * @return
	 */
	public static Date monthBefore2Date(int months) {
		Date date = new Date();
		return monthBefore2Object(months, null, date);
	}
	
	/**
	 * 相对于当前日期的前几月(months < 0０００００)或者后几月(months > 0)时间
	 * @param months
	 * @return
	 */
	public static String monthBefore2Str(int months) {
		String string = formatDate2Str();
		return monthBefore2Object(months, null, string);
	}
	
	/**
	 * 相对于当前日期的前几月(months < 0０００００)或者后几月(months > 0)时间
	 * @param <T>
	 * @param months
	 * @param format
	 * @param instance
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> T monthBefore2Object(int months, String format, T instance) {
		if (months == 0) {
			return null;
		}
		
		if (format == null || format.equals("")) {
			format = C_TIME_PATTON_DEFAULT;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, months);
		
		if (instance instanceof Date) {
			return (T)calendar.getTime();
		} else if (instance instanceof String) {
			return (T)formatDate2Str(calendar2Date(calendar), format);
		}
		
		return null;
	}
	
	/**
	 * 相对于指定日期的前几月(months < 0０００００)或者后几月(months > 0)时间
	 * @param date
	 * @param months
	 * @return
	 */
	public static Date defineMonthBefore2Date(Date date, int months) {
		Date dateInstance = new Date();
		return defineMonthBefore2Object(date, months, null, dateInstance);
	}
	
	/**
	 * 相对于指定日期的前几月(months < 0０００００)或者后几月(months > 0)时间
	 * @param date
	 * @param months
	 * @return
	 */
	public static String defineMonthBefore2Str(Date date, int months) {
		String stringInstance = formatDate2Str();
		return defineMonthBefore2Object(date, months, null, stringInstance);
	}
	
	/**
	 * 相对于指定日期的前几月(months < 0０００００)或者后几月(months > 0)时间
	 * @param <T>
	 * @param date
	 * @param months
	 * @param format
	 * @param instance
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> T defineMonthBefore2Object(Date date,
			int months, String format, T instance) {
		if (months == 0) {
			return null;
		}
		
		if (format == null || format.equals("")) {
			format = C_TIME_PATTON_DEFAULT;
		}
		
		Calendar calendar = date2Calendar(date);
		calendar.add(Calendar.MONTH, months);
		
		if (instance instanceof Date) {
			return (T)calendar.getTime();
		} else if (instance instanceof String) {
			return (T)formatDate2Str(calendar2Date(calendar), format);
		}
		
		return null;
	}
	
	/**
	 * 计算两个日期直接差的天数
	 * @param firstDate
	 * @param secondDate
	 * @return
	 */
	public static int caculate2Days(Date firstDate, Date secondDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(firstDate);
		int dayNum1 = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.setTime(secondDate);
		int dayNum2 = calendar.get(Calendar.DAY_OF_YEAR);
		return Math.abs(dayNum1 - dayNum2);
	}
	
	/**
	 * 计算两个日期直接差的天数
	 * @param firstCalendar
	 * @param secondCalendar
	 * @return
	 */
	public static int caculate2Days(Calendar firstCalendar, Calendar secondCalendar) {
		if (firstCalendar.after(secondCalendar)) {
			Calendar calendar = firstCalendar;
			firstCalendar = secondCalendar;
			secondCalendar = calendar;
		}
		
		long calendarNum1 = firstCalendar.getTimeInMillis();
		long calendarNum2 = secondCalendar.getTimeInMillis();
		return Math.abs((int)((calendarNum1 - calendarNum2)/C_ONE_DAY));
	}
	
	public static void main(String[] args) {
//		System.out.println("当前月份的最大天数:" + findMaxDayInMonth(new Date()));
//		System.out.println("6月份的最大天数:" + findMaxDayInMonth(6));
//		System.out.println("1999-02月份的最大天数:" + findMaxDayInMonth(1999, 2));
//		System.out.println("2000-02月份的最大天数:" + findMaxDayInMonth(2000, 2));
		
//		System.out.println(formatSource2Target("2009-07-24 11:02:35", null, "yyyy/MM/dd"));
//		System.out.println(findWeeksNoInYear());
		
//		System.out.println("2003年的第30个星期一是那天:" + findDateInWeekOnYear(2003, 30, Calendar.MONDAY));
//		System.out.println("2009年的第30个星期一是那天:" + findDateInWeekOnYear(2009, 30, Calendar.FRIDAY));
		
//		System.out.println("【日期格式】当前日期的前7天是:" + dayBefore2Date(-7));
//		System.out.println("【字符格式】当前日期的前7天是:" + dayBefore2Str(-7));
//		System.out.println("【日期格式】当前日期的后7天是:" + dayBefore2Date(7));
//		System.out.println("【字符格式】当前日期的后7天是:" + dayBefore2Str(7));
		
//		System.out.println(formatStr2Date("2009-07-22", "yyyy-MM-dd"));
		
//		System.out.println("【日期格式】2009-07-22的前7天是:" + 
//				defineDayBefore2Date(formatStr2Date("2009-07-22", "yyyy-MM-dd"), -7));
//		System.out.println("【字符格式】2009-07-22的前7天是:" + 
//				defineDayBefore2Str(formatStr2Date("2009-07-22", "yyyy-MM-dd"), -7));
//		System.out.println("【日期格式】2009-07-22的后7天是:" + 
//				defineDayBefore2Date(formatStr2Date("2009-07-22", "yyyy-MM-dd"), 7));
//		System.out.println("【字符格式】2009-07-22的后7天是:" + 
//				defineDayBefore2Str(formatStr2Date("2009-07-22", "yyyy-MM-dd"), 7));
		
//		System.out.println("【日期格式】相对于当前时间的前2月日期是:" + monthBefore2Date(-2));
//		System.out.println("【字符格式】相对于当前时间的前2月日期是:" + monthBefore2Date(-2));
//		System.out.println("【日期格式】相对于当前时间的后2月日期是:" + monthBefore2Date(2));
//		System.out.println("【字符格式】相对于当前时间的后2月日期是:" + monthBefore2Date(2));
		
//		System.out.println("【日期格式】2009-07-22的前2月日期是:" + 
//				defineMonthBefore2Date(formatStr2Date("2009-07-22", "yyyy-MM-dd"), -2));
//		System.out.println("【字符格式】2009-07-22的前2月日期是:" +
//				defineMonthBefore2Date(formatStr2Date("2009-07-22", "yyyy-MM-dd"), -2));
//		System.out.println("【日期格式】2009-07-22的后2月日期是:" + 
//				defineMonthBefore2Date(formatStr2Date("2009-07-22", "yyyy-MM-dd"), 2));
//		System.out.println("【字符格式】2009-07-22的后2月日期是:" + 
//				defineMonthBefore2Date(formatStr2Date("2009-07-22", "yyyy-MM-dd"), 2));
		
//		Date firstDate = formatStr2Date("2009-07-22", "yyyy-MM-dd");
//		Date secondDate = formatStr2Date("2009-07-18", "yyyy-MM-dd");
//		System.out.println(caculate2Days(firstDate, secondDate));
		
//		Calendar firstC = date2Calendar(formatStr2Date("2009-07-22", "yyyy-MM-dd"));
//		Calendar secondC = date2Calendar(formatStr2Date("2009-07-18", "yyyy-MM-dd"));
//		System.out.println(caculate2Days(firstC, secondC));
		
		System.out.println(getWeekDay(123));
		
	}
	
	/**
	 * 计算两个日期相差多少天
	 * 函数功能说明 
	 * 修改者名字 
	 * 修改日期 
	 * 修改内容
	 * @author wanxianze@gmail.com 2013-5-29
	 * @param startDate
	 * @param endDate
	 * @return
	 * long
	 */
	public static long diffDays(Date startDate,Date endDate){
		long diff = startDate.getTime() - endDate.getTime();
		return diff/(1000L * 60L * 60L * 24L);
	}
	/**
	 * 计算两个日期相差多少天
	 * 函数功能说明 
	 * 修改者名字 
	 * 修改日期 
	 * 修改内容
	 * @author wanxianze@gmail.com 2013-5-29
	 * @param startDate
	 * @param endDate
	 * @return
	 * long
	 */
	public static long diffYears(Date startDate,Date endDate){
		long diff = startDate.getTime() - endDate.getTime();
		return diff/(1000L * 60L * 60L * 24L * 360L);
	}
	
	/**
	* 取得当前日期是多少周
	* 
	* @param date
	* @return
	*/
	public static int getWeekOfYear(Calendar calendar) {
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(7);
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}
	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String toDateString(Date date){
		
		String result = "";
		Calendar curCalendar = Calendar.getInstance();
		Calendar anotherCalendar = Calendar.getInstance();
		anotherCalendar.setTime(date);
		//同一年
		if(curCalendar.get(Calendar.YEAR)==anotherCalendar.get(Calendar.YEAR)){
			int days=curCalendar.get(Calendar.DAY_OF_YEAR)-anotherCalendar.get(Calendar.DAY_OF_YEAR);
			//同一周
			if(getWeekOfYear(curCalendar)==getWeekOfYear(anotherCalendar)){
				if(days<3){//三天内
					if(days==0)
					{
						return result = toTimeString(anotherCalendar,true);
					}else if(days==1){
						return result ="昨天"+toTimeString(anotherCalendar,false);
					}else if(days==2){
						return result ="前天"+toTimeString(anotherCalendar,false);
					}
				}else{
					return toWeekDay(anotherCalendar)+toTimeString(anotherCalendar,false);
				}
			}else {
				return result = formatDate2Str(anotherCalendar.getTime(),"M月d日");
			}
	
		}else
			return result = formatDate2Str();
		return result;
		
	}
	public static String toTimeString(Calendar c,boolean addTime){
		int hour = c.get(Calendar.HOUR_OF_DAY);
		String time = addTime?formatDate2Str(c.getTime(), "HH:mm"):"";
		if(hour<4){
			return "凌晨"+time;
		}else if(hour<=9)
			return "早上"+time;
		if(hour<=11)
			return "上午"+time;
		else if(hour==12)
			return "中午"+time;
		else if(hour<=18)
			return "下午"+time;
		else 
			return "晚上"+time;
			
	}
	
	/**
	 * 获取星期几
	 * @return
	 */
	public static String toWeekDay(Calendar c){
		int dayFlag = c.get(Calendar.DAY_OF_WEEK);
		switch (dayFlag) {
		case 1:
			return "星期天";
		case 2:
			return "星期一";
		case 3:
			return "星期二";
		case 4:
			return "星期三";
		case 5:
			return "星期四";
		case 6:
			return "星期五";
		case 7:
			return "星期六";
		default:
			return null;
		}
	}
	
	/**
	 * 获取星期几
	 * @param afterOrBefore 如果是今天为0，前一天-1，后一天1
	 * @return
	 */
	public static String getWeekDay(int afterOrBefore){
		Date requestDate = new Date();
		if(afterOrBefore != 0){
			requestDate = DateUtil.dayBefore2Date(afterOrBefore);
		}
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH,1);
		// DateUtils.date2Calendar(requestDate);
//		System.out.println(requestDate);
		int dayFlag = c.get(Calendar.DAY_OF_WEEK);
		switch (dayFlag) {
		case 1:
			return "星期天";
		case 2:
			return "星期一";
		case 3:
			return "星期二";
		case 4:
			return "星期三";
		case 5:
			return "星期四";
		case 6:
			return "星期五";
		case 7:
			return "星期六";
		default:
			return null;
		}
	}

}
