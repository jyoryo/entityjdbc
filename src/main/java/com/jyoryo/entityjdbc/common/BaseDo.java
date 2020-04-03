package com.jyoryo.entityjdbc.common;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class BaseDo implements StringStyle {
	private static final long serialVersionUID = 1709284751601310330L;

	/**
	 * 定义一个默认的构造方法，在其中调用{@link #initialize()}方法，这样
	 * 使得每个子类都自动继承了这样的操作，方便对实体类中的某些属性进行初始化。
	 */
	public BaseDo() {
		initialize();
	}

	/**
	 * 提供了一个没有任何操作的对实体类进行初始化的方法，之所以没有将此方法设置 为 abstract
	 * 类型是考虑到没有必要强制子类实现此方法，只有在需要初始化实体 类中的某些属性时才需要继承该方法。
	 */
	protected void initialize() {
	}

	/**
	 * 子类可以覆盖该方法，改变toString样式
	 */
	@Override
	public ToStringStyle stringStyle() {
		return null;
	}

	@Override
	public String toString() {
		ToStringStyle style = stringStyle();
		if (null == style) {
			return ToStringBuilder.reflectionToString(this);
		}
		return ToStringBuilder.reflectionToString(this, style);
	}
}
