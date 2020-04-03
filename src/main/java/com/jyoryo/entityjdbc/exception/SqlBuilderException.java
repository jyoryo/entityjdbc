package com.jyoryo.entityjdbc.exception;

/**
 * SqlBuilder Exception
 * @author jyoryo
 *
 */
public class SqlBuilderException extends DaoException {
    private static final long serialVersionUID = -4325858737763563361L;

    public SqlBuilderException(String msg) {
        super(msg);
    }

    public SqlBuilderException(String messageTemplate, Object... params) {
        super(messageTemplate, params);
    }

    public SqlBuilderException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public SqlBuilderException(Throwable throwable, String messageTemplate, Object... params) {
        super(throwable, messageTemplate, params);
    }

    public SqlBuilderException(Throwable e) {
        super(e);
    }
}
