package com.jyoryo.entityjdbc.exception;

import com.jyoryo.entityjdbc.common.exception.FormatRuntimeException;

/**
 * 模板解析 异常
 * @author jyoryo
 *
 */
public class TemplateParserException extends FormatRuntimeException {
    private static final long serialVersionUID = -5078440793229706454L;

    public TemplateParserException(String messageTemplate, Object... params) {
        super(messageTemplate, params);
    }

    public TemplateParserException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public TemplateParserException(String message) {
        super(message);
    }

    public TemplateParserException(Throwable throwable, String messageTemplate, Object... params) {
        super(throwable, messageTemplate, params);
    }

    public TemplateParserException(Throwable e) {
        super(e);
    }
}
