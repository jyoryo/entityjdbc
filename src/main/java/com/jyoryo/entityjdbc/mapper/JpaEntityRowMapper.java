package com.jyoryo.entityjdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;

import com.jyoryo.entityjdbc.JpaEntity;
import com.jyoryo.entityjdbc.metadata.ColumnMetaData;
import com.jyoryo.entityjdbc.utils.JpaUtils;

/**
 * RowMapper的实现，将数据库行记录转换为基于注解Entity对象
 * 
 * @author jyoryo
 *
 * @param <T>
 */
public class JpaEntityRowMapper<T> implements RowMapper<T> {
	private Class<T> entityClass;
	private JpaEntity jpaEntity;
	
	public JpaEntityRowMapper(Class<T> entityClass) {
		this.entityClass = entityClass;
		this.jpaEntity = new JpaEntity(entityClass);
	}

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		T target = BeanUtils.instantiateClass(this.entityClass);
		Map<String, ColumnMetaData> columnFieldMap = jpaEntity.getColumnFieldMap();
		ColumnMetaData columnMetaData;
		for(String columnName : columnFieldMap.keySet()) {
			columnMetaData = columnFieldMap.get(columnName);
			JpaUtils.writeField(target, columnMetaData.getField(), rs, columnName);
		}
		return target;
	}

}
