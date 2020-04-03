package com.jyoryo.entityjdbc.common.exception;

/**
 * 封装的ClassNotFoundException
 * @auther: jyoryo
 * @Date: 2019.4.2 01:16
 */
public class ClassNotFoundRuntimeException extends FormatRuntimeException {
    private static final long serialVersionUID = 4299886429542456099L;

    public ClassNotFoundRuntimeException(Throwable e) {
		super(e);
	}
	
	public ClassNotFoundRuntimeException(String message) {
		super(message);
	}
	
	public ClassNotFoundRuntimeException(String messageTemplate, Object... params) {
		super(messageTemplate, params);
	}
	
	public ClassNotFoundRuntimeException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public ClassNotFoundRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
		super(throwable, messageTemplate, params);
	}
}
