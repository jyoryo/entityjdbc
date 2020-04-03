package com.jyoryo.entityjdbc.metadata;

import java.lang.reflect.Field;

import com.jyoryo.entityjdbc.common.BaseDo;

/**
 * 记录数据库列信息
 * @author jyoryo
 *
 */
public class ColumnMetaData extends BaseDo {
	private static final long serialVersionUID = -7438825179099559851L;

	private Field field;
	private Class<?> type;
	// java 属性名称
	private String fieldName;
	// 数据库中列名
	private String columnName;
	// 注解数据库列类型
	private ColumnMetaType columnType;
	
	
	public ColumnMetaData() {
		super();
	}
	/**
	 * 获取 Field
	 * @return
	 */
	public Field getField() {
		return field;
	}
	/**
	 * 设置 Field
	 * @param type
	 */
	public void setField(Field field) {
		this.field = field;
	}
	/**
	 * 获取类型
	 * @return
	 */
	public Class<?> getType() {
		return type;
	}
	/**
	 * 设置类型
	 * @param type
	 */
	public void setType(Class<?> type) {
		this.type = type;
	}
	/**
	 * 获取Java field名称
	 * @return
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * 设置Java field名称
	 * @param fieldName
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	/**
	 * 获取SQL column名称
	 * @return
	 */
	public String getColumnName() {
		return columnName;
	}
	/**
	 * 设置SQL column名称
	 * @param columnName
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	/**
	 * 获取列的类型
	 * @return
	 */
	public ColumnMetaType getColumnType() {
		return columnType;
	}
	/**
	 * 设置列的类型
	 * @param columType
	 */
	public void setColumnType(ColumnMetaType columnType) {
		this.columnType = columnType;
	}
}
