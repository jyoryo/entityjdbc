package com.jyoryo.entityjdbc.common.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.jyoryo.entityjdbc.common.Arrays;
import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.common.cache.Cache;
import com.jyoryo.entityjdbc.common.cache.LFUCache;

/**
 * 封装日志工具类
 * @author jyoryo
 *
 */
public final class Logs {
	/**
	 * 缓存调用log的目标对象log
	 */
	private static final Cache<String, Logger> CACHE_LOG_CLASS = new LFUCache<String, Logger>(100000);

	public Logs() {
	}
	
	/**
	 * 通过堆栈获取调用类的className
	 * https://www.cnblogs.com/lcchuguo/p/5335689.html
	 * @return
	 */
	private static String getClassName() {
		StackTraceElement[] array = new Throwable().getStackTrace();
		if(null == array || 2 > array.length) {
			return null;
		}
		final String selfClassName = Logs.class.getName();
		String className = null;
		for(StackTraceElement ste : array) {
			className = ste.getClassName();
			if(Strings.equalsIgnoreCase(selfClassName, className)) {
				continue;
			}
			return className;
		}
		return null;
	}
	
	/**
	 * 通过堆栈获取调用Logs的logger
	 * @return
	 */
	private static Logger getLogger() {
		return getLogger(getClassName());
	}
	
	/**
	 * 通过className获取logger
	 * @param className
	 * @return
	 */
	private static Logger getLogger(String className) {
		if(null == className) {
			return null;
		}
		Logger logger = CACHE_LOG_CLASS.get(className);
		if(null == logger) {
			logger = Logger.getLogger(className);
			CACHE_LOG_CLASS.put(className, logger);
		}
		return logger;
	}
	
	/**
	 * 判断当前日志级别是否启用
	 * @param logger
	 * @param level
	 * @return
	 */
	private final static boolean isEnable(Level level) {
		return isEnable(getLogger(), level);
	}
	
	/**
	 * 判断当前日志级别是否启用
	 * @param logger
	 * @param level
	 * @return
	 */
	private final static boolean isEnable(Logger logger, Level level) {
		return (null != logger && logger.isEnabledFor(level));
	}
	
	/**
	 * DEBUG级别是否可用
	 * @return
	 */
	public static boolean isDebugEnabled() {
		return isEnable(Level.DEBUG);
	}

	/**
	 * ERROR级别是否可用
	 * @return
	 */
	public static boolean isErrorEnabled() {
		return isEnable(Level.ERROR);
	}
	
	/**
	 * FATAL级别是否可用
	 * @return
	 */
	public static boolean isFatalEnabled() {
		return isEnable(Level.FATAL);
	}
	
	/**
	 * INFO级别是否可用
	 * @return
	 */
	public static boolean isInfoEnabled() {
		return isEnable(Level.INFO);
	}
	
	/**
	 * TRACE级别是否可用
	 * @return
	 */
	public static boolean isTraceEnabled() {
		return isEnable(Level.TRACE);
	}
	
	/**
	 * WARN级别是否可用
	 * @return
	 */
	public static boolean isWarnEnabled() {
		return isEnable(Level.WARN);
	}
	
	/**
	 * 忽略日志级别，直接输入日志
	 * @param logger
	 * @param level
	 * @param message
	 */
	private final static void forceLog(Logger logger, Level level, Object message, Throwable throwable) {
		if(null == logger) {
			return ;
		}
		logger.log(Logs.class.getName(), level, message, throwable);
	}
	
	/**
	 * 自动判定日志级别，满足级别进行输入日志
	 * @param level   日志级别
	 * @param message   日志内容
	 */
	private final static void logTargetObject(Level level, Object message) {
		final Logger logger = getLogger();
		if(!isEnable(logger, level)) {
			return ;
		}
		if(Arrays.isArray(message)) {
			System.out.println("message is array");
			message = java.util.Arrays.toString(Arrays.objectToArray(message));
		}
		forceLog(logger, level, message, null);
	}
	
	/**
	 * 格式化日志内容
	 * @param level
	 * @param format
	 * @param throwable
	 * @param args
	 */
	private final static void logFormat(Level level, String format, Throwable throwable, Object... args) {
		final Logger logger = getLogger();
		if(!isEnable(logger, level)) {
			return ;
		}
		String message = Arrays.isEmpty(args) ? format : Strings.format(format, args);
		forceLog(logger, level, message, throwable);
	}
	
	/**
	 * 简化log调用
	 * <pre>
	 * private static final Log log = LogFactory.getLog(XXXClass.class);
	 * if(log.isDebugEnabled()) {
	 * log.debug("Your Log Message!");
	 * }
	 * </pre>
	 * @param message
	 */
	public static void debug(Object message) {
		logTargetObject(Level.DEBUG, message);
	}
	
	/**
	 * 简化log调用，支持格式化
	 * @param format
	 * @param args
	 */
	public static void debug(String format, Object... args) {
		debug(format, null, args);
	}
	
	/**
	 * 简化log调用，支持格式化
	 * @param format
	 * @param throwable
	 * @param args
	 */
	public static void debug(String format, Throwable throwable, Object... args) {
		logFormat(Level.DEBUG, format, throwable, args);
	}
	
	/**
	 * 简化log调用
	 * <pre>
	 * private static final Log log = LogFactory.getLog(XXXClass.class);
	 * if(log.isDebugEnabled()) {
	 * log.debug("Your Log Message!");
	 * }
	 * </pre>
	 * @param message
	 */
	public static void info(Object message) {
		logTargetObject(Level.INFO, message);
	}
	
	/**
	 * 简化log调用，支持格式化
	 * @param format
	 * @param args
	 */
	public static void info(String format, Object... args) {
		info(format, null, args);
	}
	
	/**
	 * 简化log调用，支持格式化
	 * @param format
	 * @param throwable
	 * @param args
	 */
	public static void info(String format, Throwable throwable, Object... args) {
		logFormat(Level.INFO, format, throwable, args);
	}
	
	/**
	 * 简化log调用
	 * <pre>
	 * private static final Log log = LogFactory.getLog(XXXClass.class);
	 * if(log.isDebugEnabled()) {
	 * log.debug("Your Log Message!");
	 * }
	 * </pre>
	 * @param message
	 */
	public static void warn(Object message) {
		logTargetObject(Level.WARN, message);
	}
	
	/**
	 * 简化log调用，支持格式化
	 * @param format
	 * @param args
	 */
	public static void warn(String format, Object... args) {
		warn(format, null, args);
	}
	
	/**
	 * 简化log调用，支持格式化
	 * @param format
	 * @param throwable
	 * @param args
	 */
	public static void warn(String format, Throwable throwable, Object... args) {
		logFormat(Level.WARN, format, throwable, args);
	}
	
	/**
	 * 简化log调用
	 * <pre>
	 * private static final Log log = LogFactory.getLog(XXXClass.class);
	 * if(log.isDebugEnabled()) {
	 * log.debug("Your Log Message!");
	 * }
	 * </pre>
	 * @param message
	 */
	public static void error(Object message) {
		logTargetObject(Level.ERROR, message);
	}
	
	/**
	 * 简化log调用，支持格式化
	 * @param format
	 * @param args
	 */
	public static void error(String format, Object... args) {
		error(format, null, args);
	}
	
	/**
	 * 简化log调用，支持格式化
	 * @param format
	 * @param throwable
	 * @param args
	 */
	public static void error(String format, Throwable throwable, Object... args) {
		logFormat(Level.ERROR, format, throwable, args);
	}
}
