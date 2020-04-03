package com.jyoryo.entityjdbc.common.io.watch;

import com.jyoryo.entityjdbc.common.exception.FormatRuntimeException;

/**
 * 监听异常
 * @author jyoryo
 *
 */
public class WatchException extends FormatRuntimeException {
    private static final long serialVersionUID = 4890805766926721391L;

    public WatchException(String messageTemplate, Object... params) {
        super(messageTemplate, params);
    }

    public WatchException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public WatchException(String message) {
        super(message);
    }

    public WatchException(Throwable throwable, String messageTemplate, Object... params) {
        super(throwable, messageTemplate, params);
    }

    public WatchException(Throwable e) {
        super(e);
    }

}
