package com.jyoryo.entityjdbc.common.exception;

/**
 * 封装的IOException
 * @auther: jyoryo
 * @Date: 2019.4.3 01:17
 */
public class IORuntimeException extends FormatRuntimeException {
    private static final long serialVersionUID = 6986579947317614898L;

    public IORuntimeException(Throwable e) {
		super(e);
	}
	
	public IORuntimeException(String message) {
		super(message);
	}
	
	public IORuntimeException(String messageTemplate, Object... params) {
		super(messageTemplate, params);
	}
	
	public IORuntimeException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public IORuntimeException(Throwable throwable, String messageTemplate, Object... params) {
		super(throwable, messageTemplate, params);
	}
}
