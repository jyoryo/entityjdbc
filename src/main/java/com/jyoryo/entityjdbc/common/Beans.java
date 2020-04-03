package com.jyoryo.entityjdbc.common;

import java.lang.reflect.Method;

/**
 * JavaBean工具类
 * @author jyoryo
 */
public class Beans {
    /**
     * getter方法的前缀：get
     */
    public static final String PREFIX_GET = "get";
    /**
     * getter方法的前缀：is
     */
    public static final String PREFIX_IS = "is";

    /**
     * 通过JavaBean中getter方法获取字段名称
     * @param getterMethod
     * @param defaultValue
     * @return
     */
    public static String getFieldName(Method getterMethod, String defaultValue) {
        return null == getterMethod ? defaultValue : getFieldName(getterMethod.getName(), defaultValue);
    }
    
    /**
     * 通过JavaBean中getter方法名称获取字段名称
     * @param getterMethodName
     * @param defaultValue
     * @return
     */
    public static String getFieldName(String getterMethodName, String defaultValue) {
        if(Strings.isEmpty(getterMethodName)) {
            return defaultValue;
        }
        
        String retVal = defaultValue;
        if(getterMethodName.startsWith(PREFIX_GET)) {
            retVal = Strings.removeStart(getterMethodName, PREFIX_GET);
        } else if(getterMethodName.startsWith(PREFIX_IS)) {
            retVal = Strings.removeStart(getterMethodName, PREFIX_IS);
        } else {
            // 不以get和is开头的方法排除
            return defaultValue;
        }
        // 骆驼命名法处理
        retVal = Strings.camelCase(retVal);
        return retVal == null ? defaultValue : retVal;
    }
}
