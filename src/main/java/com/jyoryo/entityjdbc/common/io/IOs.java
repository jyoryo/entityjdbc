package com.jyoryo.entityjdbc.common.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.jyoryo.entityjdbc.common.log.Logs;

/**
 * IO 工具类
 * <li>基于Apache IOUtils</li>
 * @author jyoryo
 *
 */
public class IOs extends IOUtils {

	/**
	 * 关闭<br>
	 * 关闭失败不会抛出异常
	 * @param closeable
	 */
	public static void close(Closeable closeable) {
		if(null != closeable) {
			try {
				closeable.close();
			} catch(Exception e) {
				Logs.error("", e);
			}
		}
	}
	
	/**
	 * 关闭<br>
	 * 关闭失败不会抛出异常
	 * 
	 * @param closeable 被关闭的对象
	 */
	public static void close(AutoCloseable closeable) {
		if (null != closeable) {
			try {
				closeable.close();
			} catch (Exception e) {
				// 静默关闭
			}
		}
	}
	
	/**
	 * 获取一个Reader
	 * @param in 输入流
	 * @param charset 字符集
	 * @return
	 */
	public static BufferedReader getReader(InputStream in, Charset charset) {
		if (null == in) {
			return null;
		}
		InputStreamReader reader = (null == charset) ? new InputStreamReader(in) : new InputStreamReader(in, charset);
		return new BufferedReader(reader);
	}
}
