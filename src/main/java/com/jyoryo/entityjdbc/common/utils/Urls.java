package com.jyoryo.entityjdbc.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @auther: jyoryo
 * @Date: 2019.4.3 02:46
 */
public class Urls {
	/**
	 * 对网址进行utf-8编码处理
	 *
	 * @param url 待处理的网址
	 * @return 编码后的网址
	 */
	public static String encode(String url) {
		return encode(url, StandardCharsets.UTF_8.name());
	}
	
	/**
	 * 对网址进行utf-8编码处理
	 *
	 * @param url 待处理的网址
	 * @param charset 编码
	 * @return 编码后的网址
	 */
	public static String encode(String url, Charset charset) {
		return encode(url, charset.name());
	}
	
	/**
	 * 对原来的网址进行URL编码处理，与JDK中的URLEncoder不同的是：前者将
	 * 空格替换为加号(+)，后者是将空格替换为 %20。除此之外其他都相同。
	 *
	 * @param url 待编码的网址
	 * @param enc 编码
	 * @return 返回编码过的网址
	 */
	public static String encode(String url, String enc) {
		if (url == null || url.trim().equals("")) {
			return null;
		}
		
		try {
			// 首先使用JDK中的编码方法
			url = URLEncoder.encode(url, enc);
			// 替换空格
			url = url.replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			// 出现异常返回原值
		}
		
		return url;
	}
	
	/**
	 * 将经过utf-8编码加码后的地址进行解码
	 *
	 * @param url 加码后的网址
	 * @return 返回解码后的网址
	 */
	public static String decode(String url) {
		return decode(url, StandardCharsets.UTF_8);
	}
	
	/**
	 * 将经过utf-8编码加码后的地址进行解码
	 *
	 * @param url 加码后的网址
	 * @param charset 编码
	 * @return 返回解码后的网址
	 */
	public static String decode(String url, Charset charset) {
		return decode(url, charset.name());
	}
	
	/**
	 * 将经过加码后的网址进行解码
	 *
	 * @param url 加码后的网址
	 * @param enc 编码
	 * @return 返回解码后的网址
	 */
	public static String decode(String url, String enc) {
		if (url == null || url.trim().equals("")) {
			return null;
		}
		
		try {
			url = URLDecoder.decode(url, enc);
		} catch (UnsupportedEncodingException e) {
			// 出现异常返回原值
		}
		
		return url;
	}
}
