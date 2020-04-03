package com.jyoryo.entityjdbc.exception;

import org.springframework.dao.DataAccessException;

import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.common.exception.Exceptions;

/**
 * Dao异常基类
 * @author jyoryo
 *
 */
public class DaoException extends DataAccessException {
    private static final long serialVersionUID = 1646554486186829274L;

    public DaoException(Throwable e) {
        super(Exceptions.getMessage(e), e);
    }
    
    public DaoException(String msg) {
        super(msg);
    }

    public DaoException(String messageTemplate, Object... params) {
        super(Strings.format(messageTemplate, params));
    }
    
    public DaoException(String message, Throwable throwable) {
        super(message, throwable);
    }
    
    public DaoException(Throwable throwable, String messageTemplate, Object... params) {
        super(Strings.format(messageTemplate, params), throwable);
    }
}
