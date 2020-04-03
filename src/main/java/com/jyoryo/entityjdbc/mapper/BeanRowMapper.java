package com.jyoryo.entityjdbc.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import com.jyoryo.entityjdbc.common.Assert;
import com.jyoryo.entityjdbc.common.Reflects;
import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.utils.Jdbcs;

/**
 * RowMapper的实现类，用于将返回的行记录转换为给定的Java类型
 * <li>自带缓存，用于缓存操作够的Java类型的所有Field</li>
 * @author jyoryo
 *
 * @param <T>
 */
public class BeanRowMapper<T> implements RowMapper<T> {
    /**
     * 带映射RowMapper的class
     */
    private Class<T> mappedClass;
    private Map<String, Field> fieldMap = new HashMap<>(); 

    public BeanRowMapper() {
        super();
    }

    /**
     * 通过带映射的class构建BeanRowMapper
     * @param mappedClass
     */
    public BeanRowMapper(Class<T> mappedClass) {
        Assert.notNull(mappedClass);
        initialize(mappedClass);
    }
    
    /**
     * 根据给定的class，初始化class 对应的JavaBean Java字段名映射信息
     * @param mappedClass
     */
    protected void initialize(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
        Field[] fields = Reflects.getAllFields(mappedClass);
        for(Field field : fields) {
            // 忽略final字段
            if(Modifier.isFinal(field.getModifiers())) {
                continue ;
            }
            fieldMap.put(Strings.lowerCase(field.getName()), field);
        }
    }
    
    /**
     * 追加新的字段
     * @param name
     * @param field
     */
    protected void addField(String name, Field field) {
        if(Strings.isBlank(name) || null == field) {
            return ;
        }
        fieldMap.put(Strings.lowerCase(name), field);
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T target = BeanUtils.instantiateClass(this.mappedClass);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        for(int i = 1; i <= columnCount; i++) {
            String column = JdbcUtils.lookupColumnName(rsmd, i);
            Field field = fieldMap.get(Strings.lowerCase(column));
            if(null == column || null == field) {
                continue ;
            }
            Object value = Jdbcs.getResultSetValue(rs, column, field.getType());
            if(null == value) {
                continue ;
            }
            Reflects.writeField(target, field, value);
        }
        return target;
    }

}
