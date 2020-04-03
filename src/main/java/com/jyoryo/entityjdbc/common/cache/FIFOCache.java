package com.jyoryo.entityjdbc.common.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * FIFO(first in first out) 先进先出缓存.
 * <li>底层通过LinkedHashMap实现</li>
 * <p>
 * 元素不停的加入缓存直到缓存满为止，当缓存满时，清理过期缓存对象，清理后依旧满则删除先入的缓存（链表首部对象）<br>
 * 优点：简单快速 <br>
 * 缺点：不灵活，不能保证最常用的对象总是被保留
 * </p>
 * 
 * @author jyoryo
 *
 * @param <K>
 * @param <V>
 */
public class FIFOCache<K, V> extends AbstractCache<K, V> {
	/**
	 * 构造，默认对象不过期
	 * 
	 * @param cacheSize
	 */
	public FIFOCache(final int cacheSize) {
		this(cacheSize, 0);
	}

	/**
	 * 构造：指定缓存大小，过期时间
	 */
	public FIFOCache(final int cacheSize, final long timeout) {
		this.cacheSize = cacheSize;
		this.timeout = timeout;
		cacheMap = new LinkedHashMap<>(cacheSize + 1, 1.0f, false);
	}

	@Override
	protected int pruneCache() {
        int count = 0;
		CacheObject<K,V> first = null;
		Iterator<CacheObject<K,V>> values = cacheMap.values().iterator();
		while (values.hasNext()) {
			CacheObject<K,V> co = values.next();
			if (co.isExpired()) {
				values.remove();
				onRemove(co.getKey(), co.getValue());
				count++;
			}
			if (null == first) {
				first = co;
			}
		}
		
		// 清理结束后依旧是满的，则删除第一个被缓存的对象
		if (isFull() && null != first) {
			cacheMap.remove(first.key);
			onRemove(first.key, first.cachedObject);
			count++;
		}
		return count;
	}

}
