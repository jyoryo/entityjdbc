package com.jyoryo.entityjdbc.common.io.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.jyoryo.entityjdbc.common.exception.IORuntimeException;
import com.jyoryo.entityjdbc.common.io.IOs;

/**
 * 基于{@link InputStream}的资源获取器<br>
 * 注意：此对象中getUrl方法始终返回null
 * 
 * @author jyoryo
 */
public class InputStreamResource implements Resource {

	private InputStream in;
	private String name;

	/**
	 * 构造
	 * 
	 * @param in {@link InputStream}
	 */
	public InputStreamResource(InputStream in) {
		this(in, null);
	}
	
	/**
	 * 构造
	 * 
	 * @param in {@link InputStream}
	 * @param name 资源名称
	 */
	public InputStreamResource(InputStream in, String name) {
		this.in = in;
		this.name = name;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public URL getUrl() {
		return null;
	}

	@Override
	public InputStream getStream() {
		return this.in;
	}

	@Override
	public BufferedReader getReader(Charset charset) {
		return IOs.getReader(this.in, charset);
	}

	@Override
	public String readString(Charset charset) throws IORuntimeException {
		BufferedReader reader = null;
		try {
			reader = getReader(charset);
			return IOs.toString(reader);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			IOs.close(reader);
		}
	}

	@Override
	public String readUtf8String() throws IORuntimeException {
		return readString(StandardCharsets.UTF_8);
	}

	@Override
	public byte[] readBytes() throws IORuntimeException {
		InputStream in = null;
		try {
			in = getStream();
			return IOs.toByteArray(in);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			IOs.close(in);
		}
	}

}
