package com.jyoryo.entityjdbc.exception;

/**
 * Page Exception
 * @author jyoryo
 *
 */
public class PageException extends DaoException {
    private static final long serialVersionUID = 6232619362282336623L;

    public PageException(String msg) {
        super(msg);
    }

    public PageException(String messageTemplate, Object... params) {
        super(messageTemplate, params);
    }

    public PageException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public PageException(Throwable throwable, String messageTemplate, Object... params) {
        super(throwable, messageTemplate, params);
    }

    public PageException(Throwable e) {
        super(e);
    }
}
