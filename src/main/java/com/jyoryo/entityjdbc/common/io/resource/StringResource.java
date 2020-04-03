package com.jyoryo.entityjdbc.common.io.resource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.jyoryo.entityjdbc.common.exception.IORuntimeException;
import com.jyoryo.entityjdbc.common.io.IOs;

/**
 * 字符串资源，字符串做为资源
 * 
 * @author jyoryo
 */
public class StringResource implements Resource {

	private String data;
	private String name;
	private Charset charset;

	/**
	 * 构造，使用UTF8编码
	 *
	 * @param data 资源数据
	 */
	public StringResource(String data) {
		this(data, null);
	}

	/**
	 * 构造，使用UTF8编码
	 *
	 * @param data 资源数据
	 * @param name 资源名称
	 */
	public StringResource(String data, String name) {
		this(data, name, StandardCharsets.UTF_8);
	}

	/**
	 * 构造
	 *
	 * @param data 资源数据
	 * @param name 资源名称
	 * @param charset 编码
	 */
	public StringResource(String data, String name, Charset charset) {
		this.data = data;
		this.name = name;
		this.charset = charset;
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
		return new ByteArrayInputStream(readBytes());
	}

	@Override
	public BufferedReader getReader(Charset charset) {
		return IOs.toBufferedReader(new StringReader(this.data));
	}

	@Override
	public String readString(Charset charset) throws IORuntimeException {
		return this.data;
	}

	@Override
	public String readUtf8String() throws IORuntimeException {
		return this.data;
	}

	@Override
	public byte[] readBytes() throws IORuntimeException {
		return this.data.getBytes(this.charset);
	}

}
