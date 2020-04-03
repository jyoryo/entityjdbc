package com.jyoryo.entityjdbc.common.cache;

import java.util.Map;

/**
 * 缓存接口
 * 
 * @author jyoryo
 *
 * @param <K>
 * @param <V>
 */
public interface Cache<K, V> {
	/**
	 * 返回缓存容量，<code>0</code>表示无大小限制
	 * 
	 * @return 返回缓存容量，<code>0</code>表示无大小限制
	 */
	int limit();
	
	/**
	 * 缓存失效时长， <code>0</code> 表示没有设置，单位毫秒
	 * 
	 * @return 缓存失效时长， <code>0</code> 表示没有设置，单位毫秒
	 */
	long timeout();
	
	/**
	 * 将对象加入到缓存，使用默认失效时长
	 * 
	 * @param key 键
	 * @param object 缓存的对象
	 * @see Cache#put(Object, Object, long)
	 */
	void put(K key, V object);
	
	/**
	 * 将对象加入到缓存，使用指定失效时长<br>
	 * 如果缓存空间满了，{@link #prune()} 将被调用以获得空间来存放新对象
	 * 
	 * @param key 键
	 * @param object 缓存的对象
	 * @param timeout 失效时长，单位毫秒
	 * @see Cache#put(Object, Object, long)
	 */
	void put(K key, V object, long timeout);
	
	/**
	 * 根据key从缓存中获取对象。如果该key不在缓存中或者已过期，则返回<code>null</code>
	 * 
	 * @param key
	 * @return
	 */
	V get(K key);
	
	/**
	 * 根据key从缓存中获得对象，当对象不在缓存中或已经过期返回<code>null</code>
	 * <p>
	 * 调用此方法时，会检查上次调用时间，如果与当前时间差值大于超时时间返回<code>null</code>，否则返回值。
	 * 
	 * @param key 键
	 * @param updateLastAccess 是否更新最后访问时间，即重新计算超时时间。
	 * @return 键对应的对象
	 */
	V get(K key, boolean updateLastAccess);
	
	/**
	 * 从缓存中清理过期对象，清理策略取决于具体实现
	 * 
	 * @return 清理的缓存对象个数
	 */
	int prune();
	
	/**
	 * 缓存是否已满，仅用于有空间限制的缓存对象
	 * 
	 * @return 缓存是否已满，仅用于有空间限制的缓存对象
	 */
	boolean isFull();

	/**
	 * 从缓存中移除对象
	 * 
	 * @param key 键
	 */
	V remove(K key);

	/**
	 * 清空缓存
	 */
	void clear();
	
	/**
	 * 缓存的对象数量
	 * 
	 * @return 缓存的对象数量
	 */
	int size();

	/**
	 * 缓存是否为空
	 * 
	 * @return 缓存是否为空
	 */
	boolean isEmpty();
	
	/**
	 * 从当前缓存值创建快照。
	 * 返回的值可能不再有效，或者可能已经过期!快照创建期间缓存被锁定。
	 * @return   缓存的快照
	 */
	Map<K, V> snapshot();
}
