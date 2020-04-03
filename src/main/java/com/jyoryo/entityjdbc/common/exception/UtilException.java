package com.jyoryo.entityjdbc.common.exception;

/**
 * 工具类异常
 * @auther: jyoryo
 * @Date: 2019.4.2 00:07
 */
public class UtilException extends FormatRuntimeException {
    private static final long serialVersionUID = 5247287101431501083L;

    public UtilException(Throwable e) {
		super(e);
	}
	
	public UtilException(String message) {
		super(message);
	}
	
	public UtilException(String messageTemplate, Object... params) {
		super(messageTemplate, params);
	}
	
	public UtilException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public UtilException(Throwable throwable, String messageTemplate, Object... params) {
		super(throwable, messageTemplate, params);
	}
}
