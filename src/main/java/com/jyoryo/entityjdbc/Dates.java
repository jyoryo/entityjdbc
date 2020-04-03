package com.jyoryo.entityjdbc;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * 基于Apache DateUtils加强日期工具类
 * 
 * @author jyoryo
 */
public class Dates extends DateUtils {
    /**
     * 常用的日期格式
     * <li>yyyy-MM-dd</li>
     */
    public static final String PATTERN_DATE = "yyyy-MM-dd";

    /**
     * 常见的时间格式
     * <li>HH:mm:ss</li>
     */
    public static final String PATTERN_TIME = "HH:mm:ss";

    /**
     * 常见的日期时间格式
     * <li>yyyy-MM-dd HH:mm:ss</li>
     */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * IOS日期格式
     * <li>例如：2018-06-01</li>
     */
    public static final String PATTERN_ISO_DATE = "yyyy-MM-dd";

    /**
     * ISO时间格式
     * <li>例如：10:30:00.000-05:00</li>
     */
    public static final String PATTERN_ISO_TIME = "hh:mm:ss.SSSZ";

    /**
     * ISO日期时间格式
     * <li></li>
     */
    public static final String PATTERN_ISO_DATETIME = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";
    
//    private static final DateTimeFormatter FORMATTER_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//    private static final DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    /**
     * 获取当前时间戳
     * @param isNano   是否为高精度时间
     * @return
     */
    public static long current(boolean isNano) {
        return isNano ? System.nanoTime() : System.currentTimeMillis();
    }
    
    /**
     * 当前时间的时间戳（秒）
     * @return
     */
    public static long currentSeconds() {
        return System.currentTimeMillis() / 1000;
    }
    
    /**
     * 获取当前时间的long值
     * @return
     */
    public static long dateToLong() {
        return dateToLong(new Date());
    }
    
    /**
     * 获取给定的日期的long值
     * @param date
     * @return   如果date为null，返回0L
     */
    public static long dateToLong(Date date) {
        return (null == date) ? 0L : date.getTime();
    }
    
    /**
     * 将Long类型的数字转换为{@link Date}对象
     * @param millis 如果num为0则返回null，否则返回对应的日期对象
     * @return
     */
    public static Date longToDate(long millis) {
        return (0L == millis) ? null : new Date(millis);
    }
    
    /**
     * 格式化日期 yyyy-MM-dd
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return (null == date) ? null : DateFormatUtils.format(date, PATTERN_DATE);
    }
    
    /**
     * 格式化日期 yyyy-MM-dd
     * @param millis
     * @return
     */
    public static String formatDate(long millis) {
        return (0L == millis) ? null : formatDate(new Date(millis));
    }
    
    /**
     * 格式化日期的时间 HH:mm:ss
     * @param date
     * @return
     */
    public static String formatTime(Date date) {
        return (null == date) ? null : DateFormatUtils.format(date, PATTERN_TIME);
    }
    
    /**
     * 格式化日期的时间 HH:mm:ss
     * @param millis
     * @return
     */
    public static String format(long millis) {
        return (0L == millis) ? null : formatTime(new Date(millis));
    }
    
    /**
     * 格式化日期时间 yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String formatDatetime(Date date) {
        return (null == date) ? null : DateFormatUtils.format(date, PATTERN_DATETIME);
    }
    
    /**
     * 格式化日期时间 yyyy-MM-dd HH:mm:ss
     * @param millis
     * @return
     */
    public static String formatDatetime(long millis) {
        return (0L == millis) ? null : formatDatetime(new Date(millis));
    }
}
