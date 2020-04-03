package com.jyoryo.entityjdbc.common;

import java.lang.annotation.Annotation;

import org.apache.commons.lang3.AnnotationUtils;

/**
 * 注解工具类
 * <li>基于Apache AnnotationUtils</li>
 * @author jyoryo
 *
 */
public class Annotations extends AnnotationUtils {

	/**
	 * 检测某个类或其父类中是否设置某个注解。
	 * 
	 * @param clazz 类
	 * @param annotationClass 注解类型
	 * @return
	 */
	public static boolean isPresent(Class<?> clazz, Class<? extends Annotation> annotationClass) {
		if(clazz == null || annotationClass == null) {
			return false;
		}
				
		// 开始进行遍历查找
		for(Class<?> superClass = clazz; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
			if(superClass.isAnnotationPresent(annotationClass)) {
				return true;
			}
		}
		
		return false;
	}
}
