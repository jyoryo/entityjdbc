package com.jyoryo.entityjdbc.common.io.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FilenameUtils;

import com.jyoryo.entityjdbc.common.Objects;
import com.jyoryo.entityjdbc.common.exception.IORuntimeException;
import com.jyoryo.entityjdbc.common.io.Files;
import com.jyoryo.entityjdbc.common.io.IOs;
import com.jyoryo.entityjdbc.common.io.URLs;

/**
 * URL资源访问类
 * @author jyoryo
 *
 */
public abstract class UrlResource implements Resource{
	
	protected URL url;
	protected String name;
	
	//-------------------------------------------------------------------------------------- Constructor start
	/**
	 * 构造
	 * @param url URL
	 */
	public UrlResource(URL url) {
		this(url, null);
	}
	
	/**
	 * 构造
	 * @param url URL，允许为空
	 * @param name 资源名称
	 */
	public UrlResource(URL url, String name) {
		this.url = url;
		this.name = Objects.defaultIfNull(name, (null != url) ? FilenameUtils.getName(url.getPath()) : null);
	}
	
	//-------------------------------------------------------------------------------------- Constructor end
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public URL getUrl(){
		return this.url;
	}
	
	@Override
	public InputStream getStream() throws NoResourceException{
		if(null == this.url){
			throw new NoResourceException("Resource [{}] not exist!", this.url);
		}
		return URLs.getStream(url);
	}
	
	/**
	 * 获得Reader
	 * @param charset 编码
	 * @return {@link BufferedReader}
	 * @since 3.0.1
	 */
	public BufferedReader getReader(Charset charset){
		return URLs.getReader(this.url, charset);
	}
	
	//------------------------------------------------------------------------------- read
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
	public String readUtf8String() throws IORuntimeException{
		return readString(StandardCharsets.UTF_8);
	}
	
	@Override
	public byte[] readBytes() throws IORuntimeException{
		try {
			return IOs.toByteArray(this.url);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}
	
	/**
	 * 获得File
	 * @return {@link File}
	 */
	public File getFile(){
		return Files.toFile(this.url);
	}
	
	/**
	 * 返回路径
	 * @return 返回URL路径
	 */
	@Override
	public String toString() {
		return (null == this.url) ? "null" : this.url.toString();
	}
}
