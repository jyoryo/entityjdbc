package com.jyoryo.entityjdbc.common;

import org.apache.commons.lang3.EnumUtils;

/**
 * Enum工具类
 * <li>基于Apache EnumUtils</li>
 * @author jyoryo
 *
 */
public class Enums extends EnumUtils {

	/**
	 * 从枚举类中获取所有的常量，如果不是枚举类型则返回null。</p>
	 * 
	 * 注意：无任何枚举值的枚举类型返回长度为0的空数组。
	 * 
	 * @param <T>
	 * @param enumType 必须是枚举类型
	 * @return 如果enumType不是枚举类型则返回null
	 */
	public static <E> E[] values(final Class<E> enumClass) {
		return enumClass.getEnumConstants();
	}
	
	/**
	 * check the class is enum or not
	 * @param <T>
	 * @param clazz
	 * @return true if enum type, false or not
	 */
	public static <T> boolean isEnum(Class<T> clazz) {
		return clazz != null && clazz.isEnum();
	}
	
	/**
	 * 将指定的数字转换为对应的枚举类型
	 * 
	 * @param <T>
	 * @param enumType 枚举类型的class对象
	 * @param ordinal 索引号
	 * @return 如果索引号范围溢出则返回null
	 */
	public static <T> T valueOf(Class<T> enumType, int ordinal) {
		return valueOf(enumType, ordinal, null);
	}
	
	/**
	 * 将指定的数字转换为对应的枚举类型，如果转换出错则返回设定的默认值
	 * 
	 * @param <T>
	 * @param enumType 枚举类型的class对象
	 * @param ordinal 索引号
	 * @param defaultValue 转换失败之后返回的默认值，如果该值不是枚举类型则返回null
	 * @return 返回指定索引号的枚举类型。如果索引号范围溢出则返回枚举类型的默认值，但如果默认值为非枚举类型则返回null
	 */
	public static <T> T valueOf(Class<T> enumType, int ordinal, T defaultValue) {
		T[] enums = values(enumType);
		if(enums == null || ordinal < 0 || ordinal >= enums.length) {
			return (defaultValue instanceof Enum) ? defaultValue : null;
		} // end of IF
		
		return enums[ordinal];
	}
	
	/**
	 * 将指定的枚举名称转换为对应的枚举类型，如果转换失败则返回null而不是抛出异常
	 * 
	 * @param <T>
	 * @param enumType 枚举类型的class对象
	 * @param name 枚举类型的名称
	 * @return 如果转换失败则返回null
	 */
	public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
		return valueOf(enumType, name, null);
	}
	
	public static <T> T toValue(Class<T> clazz, String name) {
		if(!isEnum(clazz)) {
			return null;
		}
		for(T c : clazz.getEnumConstants()) {
			if(Strings.equals(name, c.toString())) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * 将指定的枚举名称转换为对应的枚举类型，如果转换失败则返回默认值而不是抛出异常
	 * 
	 * @param <T>
	 * @param enumType 枚举类型的class对象
	 * @param name 枚举类型的名称
	 * @param defaultValue 转换失败之后返回的默认值
	 * @return 如果转换失败则返回null
	 */
	public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name, 
			T defaultValue) {
		if(enumType == null || name == null) {
			return defaultValue;
		}
		
		try {
			return Enum.valueOf(enumType, name);
		} catch (IllegalArgumentException e) {
			return defaultValue;
		} // end of try...catch
	}
}
