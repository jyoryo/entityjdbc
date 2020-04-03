package com.jyoryo.entityjdbc.common.exception;

import com.jyoryo.entityjdbc.common.Strings;

/**
 * 异常工具类
 * 
 * @author jyoryo
 *
 */
public class Exceptions {
	private static final String NULL = "null";

	/**
	 * 获得完整消息，包括异常名
	 * 
	 * @param e 异常
	 * @return 完整消息
	 */
	public static String getMessage(Throwable e) {
		if(null == e) {
			return NULL;
		}
		return Strings.format("{}: {}", e.getClass().getSimpleName(), e.getMessage());
	}
	
	/**
	 * 获得完整消息，包括异常名
	 * 
	 * @param e 异常
	 * @return 完整消息
	 */
	public static String getSimpleMessage(Throwable e) {
		return (null == e) ? NULL : e.getMessage();
	}
	
	/**
	 * 使用运行时异常包装编译异常
	 * @param throwable 异常
	 * @return 运行时异常
	 */
	public static RuntimeException wrapRuntime(Throwable throwable){
		if(throwable instanceof RuntimeException){
			return (RuntimeException) throwable;
		}else{
			return new RuntimeException(throwable);
		}
	}
}
