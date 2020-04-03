package com.jyoryo.entityjdbc.common.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU (least recently used)最近最久未使用缓存<br>
 * 根据使用时间来判定对象是否被持续缓存<br>
 * 当对象被访问时放入缓存，当缓存满了，最久未被使用的对象将被移除。<br>
 * 此缓存基于LinkedHashMap，因此当被缓存的对象每被访问一次，这个对象的key就到链表头部。<br>
 * 这个算法简单并且非常快，他比FIFO有一个显著优势是经常使用的对象不太可能被移除缓存。<br>
 * 缺点是当缓存满时，不能被很快的访问。
 * 
 * @author jyoryo
 *
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> extends AbstractCache<K, V> {

	public LRUCache(final int cacheSize) {
		this(cacheSize, 0);
	}
	
	public LRUCache(final int cacheSize, final long timeout) {
		this.cacheSize = cacheSize;
		this.timeout = timeout;
		cacheMap = new LinkedHashMap<K, CacheObject<K,V>>(cacheSize + 1, 1.0f, true) {
			private static final long serialVersionUID = -6450131717583951407L;
			@Override
			protected boolean removeEldestEntry(final Map.Entry<K, CacheObject<K, V>> eldest) {
				return LRUCache.this.removeEldestEntry(size());
			}
		};
	}
	
	/**
	 * 如果当前缓存大小超过缓存大小，则删除最老（最久未被使用）的条目。
	 */
	protected boolean removeEldestEntry(final int currentSize) {
		if (cacheSize == 0) {
			return false;
		}
		return currentSize > cacheSize;
	}

	@Override
	protected int pruneCache() {
		if (!isPruneExpiredActive()) {
			return 0;
		}
        int count = 0;
		Iterator<CacheObject<K,V>> values = cacheMap.values().iterator();
		while (values.hasNext()) {
			CacheObject<K,V> co = values.next();
			if (co.isExpired()) {
				values.remove();
				onRemove(co.key, co.cachedObject);
				count++;
			}
		}
		return count;
	}
}
