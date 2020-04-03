package com.jyoryo.entityjdbc.common.io.resource;

import com.jyoryo.entityjdbc.common.exception.IORuntimeException;

/**
 * 资源文件或资源不存在异常
 * @auther: jyoryo
 * @Date: 2019.4.3 01:59
 */
public class NoResourceException extends IORuntimeException {
    private static final long serialVersionUID = 2352158318633237810L;

    public NoResourceException(Throwable e) {
		super(e);
	}
	
	public NoResourceException(String message) {
		super(message);
	}
	
	public NoResourceException(String messageTemplate, Object... params) {
		super(messageTemplate, params);
	}
	
	public NoResourceException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public NoResourceException(Throwable throwable, String messageTemplate, Object... params) {
		super(throwable, messageTemplate, params);
	}
}
