package com.jyoryo.entityjdbc.builder;

import com.jyoryo.entityjdbc.support.Condition;

/**
 * 基于模板和条件数据，动态构建sql
 * @author jyoryo
 *
 */
public interface SqlBuilder {

	/**
	 * 通过id获取模板内容，将模板与数据解析返回解析后的sql内容
	 * @param sqlOrId   SQL语句或对应SQL模板中的id。
	 * @param condition   条件
	 * @return
	 */
	String sql(String sqlOrId, Condition condition);
	
	/**
	 * 设置标识sql ID的前缀符号
	 * @param idPrefix
	 */
	void setIdPrefix(char idPrefix);
	
	/**
	 * 设置文件模板路径，支持classpath
	 * @param sqlFilePath
	 */
	void setSqlFilePath(String sqlFilePath);
	
	/**
	 * 设置模板文件扩展名
	 * <li>不设置，则默认为<code>sqlt</code></li>
	 * @param extension
	 */
	void setSqlFileExtension(String extension);
	
	/**
	 * 模板文件路径下文件变动是否自动重新加载
	 * @param autoReload
	 */
	void setAutoReload(boolean autoReload);
}
