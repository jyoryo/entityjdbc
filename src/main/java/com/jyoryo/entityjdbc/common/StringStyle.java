package com.jyoryo.entityjdbc.common;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 基于ToStringBuilder，用于设置具体序列化类单独设置toString的样式
 * @author jyoryo
 */
public interface StringStyle extends Serializable {

	/**
	 * 设置toString的样式
	 * @return
	 */
	ToStringStyle stringStyle();
}
