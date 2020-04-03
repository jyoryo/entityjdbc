package com.jyoryo.entityjdbc.common.cache;

import java.io.File;
import java.io.IOException;

import com.jyoryo.entityjdbc.common.io.Files;

/**
 * 文件缓存，以解决频繁读取文件引起的性能问题
 * 
 * @author jyoryo
 *
 */
public abstract class AbstractFileCache {
	protected final Cache<File, byte[]> cache;
	/** 容量 */
	protected final int maxSize;
	/** 缓存的最大文件大小，文件大于此大小时将不被缓存 */
	protected final int maxFileSize;
	/** 默认超时时间，0表示无默认超时 */
	protected final long timeout;
	/** 缓存实现 */
	protected int usedSize;
	
	/**
	 * 构造
	 * @param maxSize 缓存容量
	 * @param maxFileSize 文件最大大小
	 * @param timeout 默认超时时间，0表示无默认超时
	 */
	public AbstractFileCache(int maxSize, int maxFileSize, long timeout) {
		this.maxSize = maxSize;
		this.maxFileSize = maxFileSize;
		this.timeout = timeout;
		this.cache = createCache();
	}
	
	/**
	 * 初始化实现文件缓存的缓存对象
	 * @return
	 */
	protected abstract Cache<File, byte[]> createCache();
	
	/**
	 * Returns max cache size in bytes.
	 */
	public int maxSize() {
		return maxSize;
	}

	/**
	 * Returns actually used size in bytes.
	 */
	public int usedSize() {
		return usedSize;
	}

	/**
	 * Returns maximum allowed file size that can be added to the cache.
	 * Files larger than this value will be not added, even if there is
	 * enough room.
	 */
	public int maxFileSize() {
		return maxFileSize;
	}

	/**
	 * Returns number of cached files.
	 */
	public int cachedFilesCount() {
		return cache.size();
	}

	/**
	 * Returns timeout.
	 */
	public long cacheTimeout() {
		return cache.timeout();
	}

	/**
	 * Clears the cache.
	 */
	public void clear() {
		cache.clear();
		usedSize = 0;
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns cached file bytes. If file is not cached it will be
	 * read and put in the cache (if all the rules are satisfied).
	 */
	public byte[] getFileBytes(final File file) throws IOException {
		byte[] bytes = cache.get(file);
		if (bytes != null) {
			return bytes;
		}

		// add file
		bytes = Files.readFileToByteArray(file);

		if ((maxFileSize != 0) && (file.length() > maxFileSize)) {
			// don't cache files that size exceed max allowed file size
			return bytes;
		}

		usedSize += bytes.length;

		// put file into cache
		// if used size > total, purge() will be invoked
		cache.put(file, bytes);

		return bytes;
	}
}
