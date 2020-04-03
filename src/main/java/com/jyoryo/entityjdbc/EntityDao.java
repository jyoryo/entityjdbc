package com.jyoryo.entityjdbc;

import java.io.Serializable;
import java.util.List;

/**
 * 实体 DTO
 * @author jyoryo
 *
 * @param <T>
 */
public interface EntityDao<T> {
	/**
	 * 通过主键获取对象
	 * 
	 * @param id 主键
	 * @return 根据主键获取的对象
	 */
	T get(Serializable id);
	
	/**
	 * 获取全部的实体对象
	 * 
	 * @return 返回全部实体对象
	 */
	List<T> getAll();
	
	/**
	 * 保存实体对象并返回主键值
	 * 
	 * @param t 实体对象
	 * @return 返回新插入数据的主键值
	 */
	Serializable save(T target);
	
	/**
	 * 保存实体对象但不返回主键值，该方法用于不含有主键的情况
	 * 
	 * @param t 实体对象
	 * @return 操作成功返回影响的数量，否则返回0
	 * @since 2.7.5
	 */
	int saveNotReturnKey(T target);
	
	/**
	 * 更新实体对象
	 * 
	 * @param t 实体对象
	 * @param columns 只需要更新的字段，如果没有设置的话就更新所有的字段。
	 */
	int update(T target, String... columns);
	
	/**
	 * 保存实体对象，如果该对象不存在则创建一条新的数据，否则就更新该数据。
	 * 
	 * @param t 实体对象
	 */
	void saveOrUpdate(T target);
	
	/**
	 * 删除实体对象
	 * 
	 * @param t 实体对象
	 */
	int delete(T target);
	
	/**
	 * 通过主键删除对象
	 * 
	 * @param id 主键
	 */
	int deleteById(Serializable id);
}
