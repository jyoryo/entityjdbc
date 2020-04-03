package com.jyoryo.entityjdbc.common.exception;

import com.jyoryo.entityjdbc.common.Strings;

/**
 * 格式化RuntimeException
 * @author jyoryo
 *
 */
public class FormatRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -334993282914471599L;

	public FormatRuntimeException(Throwable e) {
		super(Exceptions.getMessage(e), e);
	}
	
	public FormatRuntimeException(String message) {
		super(message);
	}
	
	public FormatRuntimeException(String messageTemplate, Object... params) {
		super(Strings.format(messageTemplate, params));
	}
	
	public FormatRuntimeException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public FormatRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
		super(Strings.format(messageTemplate, params), throwable);
	}
}
