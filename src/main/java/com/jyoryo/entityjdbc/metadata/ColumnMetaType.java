package com.jyoryo.entityjdbc.metadata;

/**
 * 数据库列类型
 * @author jyoryo
 *
 */
public enum ColumnMetaType {
	/**
	 * 标注为@Column注解
	 */
	COLUMN,
	/**
	 * 标注为@JoinColumn注解
	 */
	JOIN_COLUMN,
	/**
	 * 标注为@Id注解
	 */
	ID;
}
