package com.jyoryo.entityjdbc.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;

import com.jyoryo.entityjdbc.common.Annotations;
import com.jyoryo.entityjdbc.common.Assert;
import com.jyoryo.entityjdbc.common.Reflects;
import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.common.cache.SimpleCache;
import com.jyoryo.entityjdbc.common.log.Logs;

/**
 * 用于JPA处理工具类
 * @author jyoryo
 */
public abstract class JpaUtils {
	/** 实体类缓存字段 */
	private static final SimpleCache<Class<?>, Field[]> CACHE_FIELDS = new SimpleCache<>();
	/**
	 * 支持对应数据库列的类型
	 */
	public static final Class<?> [] FIELD_TYPES = {
			boolean.class, char.class, byte.class, short.class, int.class, long.class, float.class, double.class,
			Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
			String.class, java.util.Date.class, java.sql.Date.class, BigDecimal.class, BigInteger.class, Enum.class 
	};
	/**
	 * Id注解应用的类型
	 */
	public static final Class<?> [] ID_TYPES = {
			boolean.class, char.class, byte.class, short.class, int.class, long.class, float.class, double.class,
			Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
			String.class, java.util.Date.class, java.sql.Date.class, BigDecimal.class, BigInteger.class
	};
	
	/**
	 * 获得某个类的所有声明的属性，即包括public、private和proteced，该方法包括父类声明的属性。</p>
	 * 支持<code>@Entity</code>、<code>@MappedSuperclass</code>、<code>@Column</code>和
	 * <code>@JoinColumn</code>注解。</p>
	 * 
	 * @param entityClass
	 * @return 如果没有@Id注解则返回null
	 */
	public static Field[] getFields(Class<?> entityClass) {
		Assert.notNull(entityClass);
		//已缓存，直接返回
		Field[] fields = CACHE_FIELDS.get(entityClass);
		if(null != fields) {
			return fields;
		}
		
		if(!isEntity(entityClass)) {
			Logs.error("{}不是@Entity或@Table类型！", entityClass);
			return null;
		}
		
		List<Field> results = new ArrayList<>();
		for(Class<?> superClass = entityClass; superClass != Object.class; superClass = superClass.getSuperclass()) {
			// 父类没有@MappedSuperclass或@Entity注解的话则忽略
			if(superClass != entityClass && !superClass.isAnnotationPresent(MappedSuperclass.class) && !superClass.isAnnotationPresent(Entity.class)) {
				continue;
			}
			// 属性如果没有@Column、@Id、 @JoinColumn则忽略
			for(Field f : superClass.getDeclaredFields()) {
				if(isField(f)) {
					results.add(f);
				}
			} // end of for(Field f : fs)
		} // end of FOR
		fields = results.toArray(new Field[results.size()]);
		CACHE_FIELDS.put(entityClass, fields);
		return fields;
	}
	
	/**
	 * 获取实体类中<code>@Id</code>注解的属性对象。</p>
	 * @param entityClass
	 * @return 如果没有@Id注解则返回null
	 */
	public static Field getIdField(Class<?> entityClass) {
		Assert.notNull(entityClass);
		Field[] fields = getFields(entityClass);
		for(Field field : fields) {
			if(field.isAnnotationPresent(Id.class)) {
				return field;
			}
		}
		return null;
	}
	
	/**
	 * 获取对象中@Id的属性的值
	 * @param target
	 * @return
	 */
	public static Object getIdValue(Object target) {
	    if(null == target) {
	        return null;
	    }
		Field idFiled = getIdField(target.getClass());
		if(null == idFiled) {
			return null;
		}
		return Reflects.readField(idFiled, target);
	}
	
	/**
	 * 检测类型是否可用被解析为JpaEntity
	 * <li>至少包含@Entiry或@Table其中一个</li>
	 * @param clazz
	 */
	public static boolean isEntity(Class<?> clazz) {
		return Annotations.isPresent(clazz, Entity.class) || Annotations.isPresent(clazz, Table.class);
	}
	
	/**
	 * 检测Field是否为JPA列
	 * <li>field包含：@Column、@JoinColumn或@Id注解
	 * @param field
	 * @return
	 */
	public static boolean isField(Field field) {
		return null != field && (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(JoinColumn.class));
	}
	
	/**
	 * 获取类标识的table名称，如果未设置默认取类名
	 * <li>优先从@Table中获取表名；如果没有@Tabel再通过@Entity获取表名</li>
	 * @param entityClass
	 * @return
	 */
	public static String getTableName(Class<?> entityClass) {
		if(null == entityClass) {
			return null;
		}
		//Table注解
		if(entityClass.isAnnotationPresent(Table.class)) {
			Table table = entityClass.getAnnotation(Table.class);
			String tableName = table.name();
			if(Strings.isNoneBlank(tableName)) {
				return tableName;
			}
		}
		//Entity注解
		if(entityClass.isAnnotationPresent(Entity.class)) {
			Entity entity = entityClass.getAnnotation(Entity.class);
			String entityName = entity.name();
			if(Strings.isNoneBlank(entityName)) {
				return entityName;
			}
		}
		return Strings.camelToSymbolCase(entityClass.getSimpleName());
	}

	/**
	 * 设置实体字段值
	 * @param target   待设置值的模板对象
	 * @param field   带设置的对象属性
	 * @param rs   ResultSet
	 * @param columnLable   数据库列名称
	 * @throws SQLException 
	 */
	public static <T> void writeField(T target, Field field, ResultSet rs, String columnName) throws SQLException {
		if(null == target || null == field || null == rs || Strings.isBlank(columnName)) {
			return ;
		}
		Class<?> fieldClass = field.getType();
		Object value;
		//字段为外键关联其他实体
		if(JpaUtils.isEntity(fieldClass)) {
			// 通过反射初始化外键关联类对象
			value = BeanUtils.instantiateClass(fieldClass);
			//获取对应id
			Field valueIdField = JpaUtils.getIdField(fieldClass);
			Object valueIdValue = Jdbcs.getResultSetValue(rs, columnName, valueIdField.getType());
			if(null == valueIdValue) {
				value = null;
			} else {
				Reflects.writeField(value, valueIdField, valueIdValue);
			}
		} else {
			value = Jdbcs.getResultSetValue(rs, columnName, fieldClass);
			if(null == value) {
				return ;
			}
		}
		Reflects.writeField(target, field, value);
	}
}
