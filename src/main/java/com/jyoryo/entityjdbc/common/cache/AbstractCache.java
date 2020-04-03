package com.jyoryo.entityjdbc.common.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;

/**
 * 支持超时和限制大小的缓存默认实现
 * 继承此抽象缓存需要：<br>
 * <ul>
 * 		<li>创建一个新的Map</li>
 * 		<li>实现 <code>prune</code> 策略</li>
 * </ul>
 * Uses <code>ReentrantReadWriteLock</code> to synchronize access.
 * Since upgrading from a read lock to the write lock is not possible,
 * be careful withing {@link #get(Object)} method.
 * @author jyoryo
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {
	protected Map<K, CacheObject<K, V>> cacheMap;
	private final StampedLock lock = new StampedLock();
	/** 返回缓存容量，<code>0</code>表示无大小限制 */
	protected int cacheSize;
	
	/** 缓存失效时长， <code>0</code> 表示没有设置，单位毫秒 */
	protected long timeout;
	
	/** 每个对象是否有单独的失效时长，用于决定清理过期对象是否有必要。 */
	protected boolean existCustomTimeout;
	
	/** 命中数 */
	protected int hitCount;
	
	/** 丢失数 */
	protected int missCount;
	
	@Override
	public void put(K key, V object) {
		put(key, object, timeout);		
	}

	@Override
	public void put(K key, V object, long timeout) {
		Objects.requireNonNull(object);
		
		final long stamp = lock.writeLock();
		try {
			CacheObject<K,V> co = new CacheObject<K, V>(key, object, timeout);
			if (timeout != 0) {
				existCustomTimeout = true;
			}
			if (isReallyFull(key)) {
				pruneCache();
			}
			cacheMap.put(key, co);
		}
		finally {
			lock.unlockWrite(stamp);
		}
	}

	@Override
	public int limit() {
		return cacheSize;
	}

	@Override
	public long timeout() {
		return timeout;
	}

	@Override
	public V get(K key) {
		return get(key, true);
	}

	@Override
	public V get(K key, boolean updateLastAccess) {
		long stamp = lock.readLock();
		try {
			CacheObject<K,V> co = cacheMap.get(key);
			if (co == null) {
				missCount++;
				return null;
			}
			if (co.isExpired()) {
				final long newStamp = lock.tryConvertToWriteLock(stamp);

				if (newStamp != 0L) {
					stamp = newStamp;
					// lock is upgraded to write lock
				}
				else {
					// manually upgrade lock to write lock
					lock.unlockRead(stamp);
					stamp = lock.writeLock();
				}

				CacheObject<K,V> removedCo = cacheMap.remove(key);
				if (removedCo != null) {
					onRemove(removedCo.key, removedCo.cachedObject);
				}

				missCount++;
				return null;
			}

			hitCount++;
			return co.get(updateLastAccess);
		}
		finally {
			lock.unlock(stamp);
		}
	}
	
	/**
	 * 在删除项目时调用的回调。
	 * <li>缓存仍然是锁定的。</li>
	 * @param key
	 * @param cachedObject
	 */
	protected void onRemove(final K key, final V cachedObject) {
		
	}
	
	/**
	 * 清理实现
	 * @return 清理数
	 */
	protected abstract int pruneCache();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int prune() {
		final long stamp = lock.writeLock();
		try {
			return pruneCache();
		}
		finally {
			lock.unlockWrite(stamp);
		}
	}
	
	/**
	 * 只有设置公共缓存失效时长或每个对象单独的失效时长时清理可用
	 * @return 过期对象清理是否可用，内部使用
	 */
	protected boolean isPruneExpiredActive() {
		return (timeout != 0) || existCustomTimeout;
	}

	@Override
	public boolean isFull() {
		if(0 == cacheSize) {
			return false;
		}
		return cacheMap.size() > cacheSize;
	}

	/**
	 * 检测缓存控件是否到达设置阈值
	 * @param key
	 * @return
	 */
	protected boolean isReallyFull(final K key) {
		if (cacheSize == 0) {
			return false;
		}
		if (cacheMap.size() >= cacheSize) {
			return !cacheMap.containsKey(key);
		}
		else {
			return false;
		}
	}
	
	@Override
	public V remove(K key) {
		V removedValue = null;
		final long stamp = lock.writeLock();
		try {
			CacheObject<K,V> co = cacheMap.remove(key);

			if (co != null) {
				onRemove(co.key, co.cachedObject);
				removedValue = co.cachedObject;
			}
		}
		finally {
			lock.unlockWrite(stamp);
		}
		return removedValue;		
	}

	@Override
	public void clear() {
		final long stamp = lock.writeLock();
		try {
			cacheMap.clear();
		} finally {
			lock.unlockWrite(stamp);
		}
		
	}

	@Override
	public int size() {
		return cacheMap.size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Map<K, V> snapshot() {
		final long stamp = lock.writeLock();
		try {
			Map<K, V> map = new HashMap<>(cacheMap.size());
			cacheMap.forEach((key, cacheValue) -> map.put(key, cacheValue.getValue()));
			return map;
		}
		finally {
			lock.unlockWrite(stamp);
		}
	}
}
