package com.jyoryo.entityjdbc;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;

import com.jyoryo.entityjdbc.common.Arrays;
import com.jyoryo.entityjdbc.common.BaseDo;
import com.jyoryo.entityjdbc.common.Enums;
import com.jyoryo.entityjdbc.common.Numbers;
import com.jyoryo.entityjdbc.common.Reflects;
import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.metadata.ColumnMetaData;
import com.jyoryo.entityjdbc.metadata.ColumnMetaType;
import com.jyoryo.entityjdbc.utils.JpaUtils;

/**
 * 通过JPA注解信息获取的类
 * @author jyoryo
 *
 */
public class JpaEntity extends BaseDo {
	private static final long serialVersionUID = 3751621273899475492L;
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
	@Override
	public ToStringStyle stringStyle() {
		return ToStringStyle.MULTI_LINE_STYLE;
	}
	
	private Class<?> entityClass;
	/**
	 * 数据库table名称
	 */
	private String tableName;
	/**
	 * 按Java字段名映射列信息
	 */
	private Map<String, ColumnMetaData> fieldColumnMap = new HashMap<>();
	/**
	 * 按数据库字段名映射列信息
	 */
	private Map<String, ColumnMetaData> columnFieldMap = new HashMap<>();
	/**
	 * 主键列信息
	 */
	private ColumnMetaData idColumn;
	
	/**
	 * 通过Entity类构造表信息
	 * @param entityClass
	 */
	public JpaEntity(Class<?> entityClass) {
		this.entityClass = entityClass;
		// 执行初始操作
		init();
	}
	
	/**
	 * 根据类信息，初始化TableMetaData
	 */
	private void init() {
		Assert.notNull(entityClass);
		if(!JpaUtils.isEntity(entityClass)) {
			throw new IllegalArgumentException(Strings.format("{}不是@Entity或@Table类型！", entityClass));
		}
		// 数据库表名
		this.tableName = JpaUtils.getTableName(entityClass);
		//获取字段内容
		Field[] fields = JpaUtils.getFields(entityClass);
		ColumnMetaType tmpColumnType = null;
		for(Field field : fields) {
			ColumnMetaData columnMetaData = getColumnMetaData(field, entityClass);
			if(null == columnMetaData) {
				continue ;
			}
			fieldColumnMap.put(columnMetaData.getFieldName(), columnMetaData);
			columnFieldMap.put(columnMetaData.getColumnName(), columnMetaData);
			tmpColumnType = columnMetaData.getColumnType();
			if(ColumnMetaType.ID == tmpColumnType) {
				this.idColumn = columnMetaData;
			}
		}
	}
	
	/**
	 * 根据JPA注解获取类指定字段的ColumnMetaData
	 * @param field
	 * @param clazz
	 * @return
	 */
	private ColumnMetaData getColumnMetaData(Field field, Class<?> clazz) {
		if(!JpaUtils.isField(field)) {
			return null;
		}
		// Logs.debug("============Field Original Info===============");
		ColumnMetaData columnMetaData = new ColumnMetaData();
		Class<?> fieldType = field.getType();	//字段类型
		String fieldName = field.getName();	//字段名称
		String columnName = fieldName;	//字段数据库列名
		// Logs.debug("Field:{}___Column:{}___Class:{}", fieldName, columnName, fieldType);
		ColumnMetaType columnType = ColumnMetaType.COLUMN;
		if(!Arrays.contains(FIELD_TYPES, fieldType)) {
			//字段为枚举类型
			if(Enums.isEnum(fieldType)) {
				fieldType = int.class;
			}
			//字段为包含@JoinColumn注解
			else if(field.isAnnotationPresent(JoinColumn.class)) {
				columnType = ColumnMetaType.JOIN_COLUMN;
				JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
				String joinColumnName = joinColumn.name();
				if(Strings.isNoneBlank(joinColumnName)) {
					columnName = joinColumnName;
					Field idField = JpaUtils.getIdField(clazz);
					if(null != idField) {
						fieldType = idField.getType();
					}
				}
			}
		}
		// @Id
		if(field.isAnnotationPresent(Id.class)) {
			// Logs.debug("--------ID--------");
			if(!Arrays.contains(ID_TYPES, fieldType)) {
				throw new IllegalArgumentException("Id注解应用类型非法！类型：" + fieldType);
			}
			columnType = ColumnMetaType.ID;
		}
		// @Column设置name，则取该值。如果设置同时设置@Id、@Column，以@Column为主
		if(fieldType.isAnnotationPresent(Column.class)) {
			Column column = fieldType.getAnnotation(Column.class);
			String defineColumnName = column.name();
			if(Strings.isNoneBlank(defineColumnName)) {
				columnName = defineColumnName;
			}
		}
		columnMetaData.setField(field);
		columnMetaData.setType(fieldType);
		columnMetaData.setFieldName(fieldName);
		columnMetaData.setColumnName(columnName);
		columnMetaData.setColumnType(columnType);
		return columnMetaData;
	}
	
	/**
	 * 获取表名
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * 获取类中@Id的字段信息
	 * @return
	 */
	public ColumnMetaData getIdColumn() {
		return idColumn;
	}
	
	/**
	 * 是否存在主键
	 * @return
	 */
	public boolean existId() {
		return null != idColumn && Strings.isNoneBlank(idColumn.getColumnName());
	}
	
	/**
	 * 获取表主键列名称
	 * @return
	 */
	public String getIdColumnName() {
		if(!existId()) {
			return null;
		}
		return idColumn.getColumnName();
	}

	/**
	 * 按Java字段名映射列信息
	 */
	public Map<String, ColumnMetaData> getFieldColumnMap() {
		return fieldColumnMap;
	}
	
	/**
	 * 按数据库字段名映射列信息
	 */
	public Map<String, ColumnMetaData> getColumnFieldMap() {
		return columnFieldMap;
	}
	
	/**
	 * 获取对象中@Id的属性的值
	 * @param target
	 * @return
	 */
	public Object getIdValue(Object target) {
		if(null == idColumn || null == idColumn.getField()) {
			return null;
		}
		return Reflects.readField(idColumn.getField(), target);
	}
	
	/**
	 * 获取具体对象注解列及对应的值记录映射
	 * @param target   具体对象
	 * @param excludeId   是否排除Id列
	 * @param columns   指定操作列名称，如果不设置默认为实体所有列
	 * @return
	 */
	public <T> Map<String, Object> columnDataMapper(T target, boolean excludeId, String... columns) {
		Map<String, Object> parameters = new HashMap<>();
		Set<String> operateColumns = new HashSet<>();
		Set<String> entityColumns = columnFieldMap.keySet();
		if(null == columns || 0 >= columns.length) {
			operateColumns = entityColumns;
		} else {
			Collections.addAll(operateColumns, columns);
			operateColumns.retainAll(entityColumns);
		}
		if(null == operateColumns || operateColumns.isEmpty()) {
			throw new IllegalArgumentException("指定列名无效！");
		}
		final String idColumnName = this.getIdColumnName();
		for(String columnName : operateColumns) {
			if(excludeId && Strings.equalsIgnoreCase(idColumnName, columnName)) {
				continue ;
			}
			ColumnMetaData columnMetaData = columnFieldMap.get(columnName);
			Field field = columnMetaData.getField();
			Object value = Reflects.readField(field, target);
			if(null == value) {
				continue ;
			}
			//字段为枚举类型
			if(value instanceof Enum<?>) {
				value = ((Enum<?>)value).ordinal();
			}
			// 字段为外键关联其他实体
			else if(JpaUtils.isEntity(value.getClass())) {
				value = JpaUtils.getIdValue(value);
				//处理空数字和0
				if(Numbers.isEmptyNumber(value)) {
					value = null;
				}
			}
			parameters.put(columnName, value);
		}
		return parameters;
	}
	
//	/**
//	 * 获取具体对象注解列及对应的值记录映射
//	 * @param target
//	 * @return
//	 */
//	public <T> Map<String, Object> columnDataMapper(T target) {
//		Map<String, Object> parameters = new HashMap<>();
//		for(String columnName : columnFieldMap.keySet()) {
//			ColumnMetaData columnMetaData = columnFieldMap.get(columnName);
//			Field field = columnMetaData.getField();
//			Object value = Reflects.readField(field, target);
//			if(null == value) {
//				continue ;
//			}
//			//字段为枚举类型
//			if(value instanceof Enum<?>) {
//				value = ((Enum<?>)value).ordinal();
//			}
//			// 字段为外键关联其他实体
//			else if(JpaUtils.isEntity(value.getClass())) {
//				value = JpaUtils.getIdValue(value);
//				//处理空数字和0
//				if(Numbers.isEmptyNumber(value)) {
//					value = null;
//				}
//			}
//			parameters.put(columnName, value);
//		}
//		return parameters;
//	}	
}
