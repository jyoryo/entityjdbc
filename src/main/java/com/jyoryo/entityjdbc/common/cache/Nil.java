package com.jyoryo.entityjdbc.common.cache;

/**
 * 缓存中的空对象，在整个运行期间是唯一的，区别于null。
 * @author jyoryo
 */
public enum Nil {
	/**
	 * 不同于null，设置缓存中的空对象
	 */
	INSTANCE;
}
