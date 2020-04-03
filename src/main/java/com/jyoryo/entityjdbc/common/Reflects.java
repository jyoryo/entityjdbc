package com.jyoryo.entityjdbc.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.jyoryo.entityjdbc.common.cache.LFUCache;
import com.jyoryo.entityjdbc.common.log.Logs;

/**
 * 反射工具类
 * 
 * @author jyoryo
 */
public class Reflects {
    /**
     * class对应所有Field的缓存
     */
    private static final LFUCache<Class<?>, Field[]> CACHE_DECLARED_FIELDS = new LFUCache<>(100000);
    
    
	/**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型.
     * 如public BookManager extends GenricManager<Book>
     * 
     * @param clazz The class to introspect
     * @return the first generic declaration, or <code>Object.class</code> if cannot be determined
     */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getSuperClassGenricType(final Class<?> clazz) {
		return (Class<T>) getSuperClassGenricType(clazz, 0);
	}
	
	/**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends GenricManager<Book>
     * 
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     */
    public static Class<?> getSuperClassGenricType(final Class<?> clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class<?>) params[index];
    }
    
    /**
     * 强制获取对象指定field的值
     * @param field
     * @param target
     * @return
     */
    public static Object readField(final Field field, final Object target) {
    	Object result = null;
    	try {
			result = FieldUtils.readField(field, target, true);
		} catch (IllegalAccessException e) {
			Logs.error("", e);
		}
    	return result;
    }
	
	/**
	 * 强制获取对象指定field name的值
	 * @param fieldName
	 * @param target
	 * @return
	 */
	public static Object readField(String fieldName, Object target) {
		if (null == target || Strings.isBlank(fieldName)) {
			return null;
		}
		return readField(FieldUtils.getField(target.getClass(), fieldName), target);
	}
    
    /**
     * 强制设置对象指定field值
     * @param target
     * @param field
     * @param value
     */
    public static void writeField(final Object target, final Field field, final Object value) {
    	try {
			FieldUtils.writeField(field, target, value, true);
		} catch (IllegalAccessException e) {
			Logs.error("", e);
		}
    }
    
    /**
     * 根据class信息获取所有Field，包含父类Field信息
     * @param clazz
     * @return
     */
    public static Field[] getAllFields(final Class<?> clazz) {
        Assert.notNull(clazz);
        // 已缓存，直接返回
        Field[] fields = CACHE_DECLARED_FIELDS.get(clazz);
        if(null != fields) {
            return fields;
        }
        
        fields = FieldUtils.getAllFields(clazz);
        CACHE_DECLARED_FIELDS.put(clazz, fields);
        return fields;
    }
    
    /**
     * 根据class信息和属性名称获取Field
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getField(final Class<?> clazz, String fieldName) {
        return FieldUtils.getField(clazz, fieldName, true);
    }
    
    /**
     * 获取指定class中包含的指定注解的方法列表
     * @param clazz
     * @param annotationClazz
     * @return
     */
    public static Method[] getMethodsWithAnnotation(final Class<?> clazz, final Class<? extends Annotation> annotationClazz) {
        return MethodUtils.getMethodsWithAnnotation(clazz, annotationClazz);
    }
    
    public static Object invokeMethod(final Object object, final String methodName) {
        try {
            return MethodUtils.invokeMethod(object, methodName);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("调用方法失败.", e);
        }
    }
}
