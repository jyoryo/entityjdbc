package com.jyoryo.entityjdbc.common.cache;

import java.util.WeakHashMap;

/**
 * 简单缓存，无大小限制，无超时
 * <li>底层通过：WeakHashMap实现</li>
 * @author jyoryo
 *
 * @param <K>
 * @param <V>
 */
public class SimpleCache<K, V> extends AbstractCache<K, V> {

	/**
	 * 无限制大小的缓存
	 */
	public SimpleCache() {
		this.cacheSize = 0;
		cacheMap = new WeakHashMap<>();
	}
	
	@Override
	protected int pruneCache() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void put(K key, V object, long timeout) {
		super.put(key, object, 0);
	}

}
