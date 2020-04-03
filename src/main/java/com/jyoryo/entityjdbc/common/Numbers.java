package com.jyoryo.entityjdbc.common;

import java.math.BigDecimal;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Number 工具类
 * <li>基于Apache NumberUtils</li>
 * @author jyoryo
 *
 */
public class Numbers extends NumberUtils {

	/**
	 * 检测对象是否为数字
	 * @param object
	 * @return
	 */
	public static boolean isNumber(Object object) {
		return (null != object) && (object instanceof Number);
	}
	
	/**
	 * 检测是否为空数字
	 * @param object
	 * @return
	 */
	public static boolean isEmptyNumber(Object object) {
		if(null == object) {
			return true;
		}
		if(isNumber(object)) {
			Number number = (Number)object;
			return 0 == number.intValue();
		}
		return false;
	}
	
	/**
	 * 获取数字类型的值
	 * @param value
	 * @param defaultNumber
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T toNumber(Object value, T defaultNumber) {
		// 如果为null则返回默认值
		if(value == null) {
			return defaultNumber;
		}
		String strval;
		// 如果为BigDecimal
		if(value instanceof BigDecimal) {
			strval = ((BigDecimal)value).toPlainString();
		}
		// 如果为字符串则尝试进行转换
		else if(value instanceof String) {
			strval = String.valueOf(value);
		} else {
		    strval = value.toString();
		}
		// byte
        if(defaultNumber instanceof Byte) {
            return (T)(Object)NumberUtils.toByte(strval, (Byte)defaultNumber);
        }
        // short
        if(defaultNumber instanceof Short) {
            return (T)(Object)NumberUtils.toShort(strval, (Short)defaultNumber);
        }
        // int
        if(defaultNumber instanceof Integer) {
            return (T)(Object)NumberUtils.toInt(strval, (Integer)defaultNumber);
        }
        // long
        if(defaultNumber instanceof Long) {
            return (T)(Object)NumberUtils.toLong(strval, (Long)defaultNumber);
        }
        // float
        if(defaultNumber instanceof Float) {
            return (T)(Object)NumberUtils.toFloat(strval, (Float)defaultNumber);
        }
        // double
        if(defaultNumber instanceof Double) {
            return (T)(Object)NumberUtils.toDouble(strval, (Double)defaultNumber);
        }
        // BigDecimal
        if(defaultNumber instanceof BigDecimal) {
            try {
                return (T)(Object)new BigDecimal(strval);
            } catch (NumberFormatException e) {
                return defaultNumber;
            }
        }
		
		// 最后返回默认值
		return defaultNumber;
	}
}
