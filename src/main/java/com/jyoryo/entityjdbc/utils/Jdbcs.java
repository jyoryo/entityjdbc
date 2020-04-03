package com.jyoryo.entityjdbc.utils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import com.jyoryo.entityjdbc.common.Arrays;
import com.jyoryo.entityjdbc.common.Enums;
import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.common.log.Logs;
import com.jyoryo.entityjdbc.mapper.BeanRowMapper;
import com.jyoryo.entityjdbc.mapper.JpaEntityRowMapper;

/**
 * Jdbc工具
 * <li>基于Spring JdbcUtils</li>
 * @author jyoryo
 */
public abstract class Jdbcs extends JdbcUtils {
    /**
     * SingleColumnRowMapper支持的列类型
     */
    public static final Class<?> [] SINGLE_COLUMN_TYPES = {
            boolean.class, short.class, int.class, long.class, float.class, double.class,
            Boolean.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
            String.class, Date.class, BigDecimal.class, Number.class
    };

	/**
	 * 通过ResultSet获取值并设置目标对象
	 * @param rs
	 * @param columnLabel
	 * @param requiredType
	 * @return
	 * @throws SQLException
	 */
	public static Object getResultSetValue(ResultSet rs, String columnLabel, Class<?> requiredType) throws SQLException {
		if(null == requiredType) {
			return null;
		}
		Object value = null;
		// Explicitly extract typed value, as far as possible.
		if (String.class == requiredType) {
			return rs.getString(columnLabel);
		}
		else if (boolean.class == requiredType || Boolean.class == requiredType) {
			value = rs.getBoolean(columnLabel);
		}
		else if (byte.class == requiredType || Byte.class == requiredType) {
			value = rs.getByte(columnLabel);
		}
		else if (short.class == requiredType || Short.class == requiredType) {
			value = rs.getShort(columnLabel);
		}
		else if (int.class == requiredType || Integer.class == requiredType) {
			value = rs.getInt(columnLabel);
		}
		else if (long.class == requiredType || Long.class == requiredType) {
			value = rs.getLong(columnLabel);
		}
		else if (float.class == requiredType || Float.class == requiredType) {
			value = rs.getFloat(columnLabel);
		}
		else if (double.class == requiredType || Double.class == requiredType ||
				Number.class == requiredType) {
			value = rs.getDouble(columnLabel);
		}
		else if (BigDecimal.class == requiredType) {
			return rs.getBigDecimal(columnLabel);
		}
		else if (java.sql.Date.class == requiredType) {
			return rs.getDate(columnLabel);
		}
		else if (java.sql.Time.class == requiredType) {
			return rs.getTime(columnLabel);
		}
		else if (java.sql.Timestamp.class == requiredType || java.util.Date.class == requiredType) {
			return rs.getTimestamp(columnLabel);
		}
		// 枚举
		else if(requiredType.isEnum()) {
			return Enums.valueOf(requiredType, rs.getInt(columnLabel));
		} else {
			Logs.error("列名：{}，不支持的类型：{}", columnLabel, requiredType);
		}
		return value;
	}
	
	/**
     * 根据请求的类，获取对应的RowMapper
     * @param requiredType
     * @return
     */
    public static <T> RowMapper<T> getRowMapperByClass(Class<T> requiredType) {
        if(JpaUtils.isEntity(requiredType)) {
            return new JpaEntityRowMapper<T>(requiredType);
        }
        if(Arrays.contains(SINGLE_COLUMN_TYPES, requiredType)) {
            return new SingleColumnRowMapper<T>(requiredType);
        }
        return  new BeanRowMapper<T>(requiredType);
    }
	
	/**
	 * 删除第一个select，返回剩余的sql
	 * @param sql
	 * @return
	 */
	public static String removeFirstSelect(String sql) {
	    int index = Strings.indexOfIgnoreCase(sql, "select");
	    return (-1 == index) ? sql : Strings.trim(Strings.substring(sql, index + 6));
	}
}
