package com.jyoryo.entityjdbc.common.cache;

import java.util.HashMap;
import java.util.Iterator;

/**
 * LFU(least frequently used) 最少使用率缓存<br>
 * 根据使用次数来判定对象是否被持续缓存<br>
 * 使用率是通过访问次数计算的。<br>
 * 当缓存满时清理过期对象。<br>
 * 清理后依旧满的情况下清除最少访问（访问计数最小）的对象并将其他对象的访问数减去这个最小访问数，以便新对象进入后可以公平计数。
 * 
 * @author jyoryo
 *
 * @param <K>
 * @param <V>
 */
public class LFUCache<K, V> extends AbstractCache<K, V> {

	public LFUCache(final int cacheSize) {
		this(cacheSize, 0);
	}
	public LFUCache(final int cacheSize, final long timeout) {
		this.cacheSize = cacheSize;
		this.timeout = timeout;
		cacheMap = new HashMap<>(cacheSize + 1);
	}
	

	/**
	 * 清理过期对象。<br>
	 * 清理后依旧满的情况下清除最少访问（访问计数最小）的对象并将其他对象的访问数减去这个最小访问数，以便新对象进入后可以公平计数。
	 * 
	 * @return 清理个数
	 */
	@Override
	protected int pruneCache() {
        int count = 0;
		CacheObject<K,V> comin = null;

		// remove expired items and find cached object with minimal access count
		Iterator<CacheObject<K,V>> values = cacheMap.values().iterator();
		while (values.hasNext()) {
			CacheObject<K,V> co = values.next();
			if (co.isExpired()) {
				values.remove();
				onRemove(co.key, co.cachedObject);
				count++;
				continue;
			}
			
			if (comin == null) {
				comin = co;
			} else {
				if (co.accessCount < comin.accessCount) {
					comin = co;
				}
			}
		}

		if (!isFull()) {
			return count;
		}

		// decrease access count to all cached objects
		if (comin != null) {
			long minAccessCount = comin.accessCount;

			values = cacheMap.values().iterator();
			while (values.hasNext()) {
				CacheObject<K, V> co = values.next();
				co.accessCount -= minAccessCount;
				if (co.accessCount <= 0) {
					values.remove();
					onRemove(co.key, co.cachedObject);
					count++;					
				}
			}
		}
		return count;
	}

}
