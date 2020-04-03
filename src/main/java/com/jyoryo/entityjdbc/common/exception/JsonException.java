package com.jyoryo.entityjdbc.common.exception;

/**
 * 处理Json异常类
 * @author jyoryo
 *
 */
public class JsonException extends FormatRuntimeException {
    private static final long serialVersionUID = 1157546809016674999L;

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String messageTemplate, Object... params) {
        super(messageTemplate, params);
    }

    public JsonException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public JsonException(Throwable throwable, String messageTemplate, Object... params) {
        super(throwable, messageTemplate, params);
    }

    public JsonException(Throwable e) {
        super(e);
    }
}
