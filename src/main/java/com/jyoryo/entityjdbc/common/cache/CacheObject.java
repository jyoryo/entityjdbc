package com.jyoryo.entityjdbc.common.cache;

/**
 * 缓存对象
 * @author jyoryo
 *
 * @param <K>
 * @param <V>
 */
public class CacheObject<K, V> {
	final K key;
	final V cachedObject;
	/** time-to-live 缓存保持时间，0表示不限 */
	long ttl;
	/** 最后一次访问时间 */
	long lastAccess;
	/** 访问次数 */
	long accessCount;
	
	CacheObject(final K key, final V toCacheObject, final long ttl) {
		this.key = key;
		this.cachedObject = toCacheObject;
		this.ttl = ttl;
		this.lastAccess = System.currentTimeMillis();
	}
	
	/**
	 * 判断是否过期
	 * 
	 * @return 是否过期
	 */
	public boolean isExpired() {
		if(0 >= ttl) {
			return false;
		}
		return lastAccess + ttl < System.currentTimeMillis();
	}
	
	/**
	 * 获取缓存值
	 * @param updateLastAccess   是否更新最后访问时间
	 * @return
	 */
	public V get(boolean updateLastAccess) {
		if(updateLastAccess) {
			lastAccess = System.currentTimeMillis();
		}
		accessCount ++;
		return cachedObject;
	}
	
	/**
	 * 直接返回缓存值，不涉及更新访问时间操作
	 * @return
	 */
	public V getValue() {
		return cachedObject;
	}
	
	/**
	 * 获取键
	 * @return
	 */
	public K getKey() {
		return key;
	}
}