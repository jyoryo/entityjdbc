package com.jyoryo.entityjdbc.common.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * 定时缓存<br>
 * 此缓存没有容量限制，对象只有在过期后才会被移除
 * @author jyoryo
 *
 * @param <K>
 * @param <V>
 */
public class TimedCache<K, V> extends AbstractCache<K, V> {

	public TimedCache(final long timeout) {
		this.cacheSize = 0;
		this.timeout = timeout;
		cacheMap = new HashMap<>();
	}

	@Override
	protected int pruneCache() {
		int count = 0;
		Iterator<CacheObject<K,V>> values = cacheMap.values().iterator();
		while (values.hasNext()) {
			CacheObject<K, V> co = values.next();
			if (co.isExpired()) {
				values.remove();
				count++;
			}
		}
		return count;
	}


	protected ScheduledExecutorService pruneExecutorService;
	
	/**
	 * 定时清理
	 * @param delay
	 */
	public void schedulePrune(final long delay) {
		if(null != pruneExecutorService) {
			pruneExecutorService.shutdownNow();
			pruneExecutorService = null;
		}
		pruneExecutorService = new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder().namingPattern("timed-cache-schedule-pool%d").daemon(true).build());
		pruneExecutorService.scheduleAtFixedRate(new Runnable() {
	        @Override
	        public void run() {
	            prune();
	        }
	    },0, delay, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 取消清理定时任务
	 */
	public void cancelPruneSchedule() {
		if(null != pruneExecutorService) {
			pruneExecutorService.shutdownNow();
			pruneExecutorService = null;
		}
	}
}
