package com.jyoryo.entityjdbc.support;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.JoinColumn;

import com.jyoryo.entityjdbc.common.Reflects;
import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.common.bean.MapBean;
import com.jyoryo.entityjdbc.common.log.Logs;
import com.jyoryo.entityjdbc.utils.JpaUtils;

/**
 * sql模板构建数据条件数据
 * @author jyoryo
 *
 */
public class Condition extends MapBean {
    private static final long serialVersionUID = -7017468711915683689L;

    /**
     * 是否支持解析JPA的注解:@Column、@JoinColumn
     */
    private boolean supportJpa = false;
    
    /**
     * 默认空构造
     */
    public Condition() {
        super();
    }

    /**
     * 是否将枚举类型转换为对应数字，构造一个带添加数据的对象
     * @param convertEnum
     */
    public Condition(boolean convertEnum) {
        super(convertEnum);
    }
    
    /**
     * 将JavaBean中所有属性和对应的值放在MapBean中。
     * <li>枚举类型会自动转换为对应的数字</li>
     * @param bean
     */
    public Condition(Object bean) {
        super(bean);
    }
    
    /**
     * 将JavaBean中所有属性和对应的值放在MapBean中。
     * @param bean
     * @param convertEnum   是否将枚举类型转换为对应数字
     */
    public Condition(Object bean, boolean convertEnum) {
        super(bean, convertEnum);
    }
    
    /**
     * 将JavaBean中所有属性和对应的值放在MapBean中。
     * @param bean
     * @param convertEnum   是否将枚举类型转换为对应数字
     * @param supportJpa   是否支持解析JPA的注解
     */
    public Condition(Object bean, boolean convertEnum, boolean supportJpa) {
        this(bean, null, null, convertEnum, supportJpa);
    }

    /**
     * 将JavaBean中所有属性和对应的值放在MapBean中。
     * <li>枚举类型会自动转换为对应的数字</li>
     * @param bean
     * @param includeFields   手工指定解析的属性
     * @param excludeFields   手工忽略解析的属性
     */
    public Condition(Object bean, Set<String> includeFields, Set<String> excludeFields) {
        this(bean, includeFields, excludeFields, true, false);
    }
    
    /**
     * 添加数据
     */
    public Condition addValue(String name, Object value) {
        if(null == name) {
            Logs.warn("由于name为null，本次添加不作任何处理！");
            return this;
        }
        if(convertEnum && (null != value) && (value instanceof Enum<?>)) {
            value = ((Enum<?>)value).ordinal();
        }
        super.put(name, value);
        return this;
    }
    
    public Condition addValues(Map<String, Object> values) {
        if(null == values || values.isEmpty()) {
            Logs.warn("添加的数据为空，本次添加不做任何处理！");
            return this;
        }
        for(String name : values.keySet()) {
            addValue(name, values.get(name));
        }
        return this;
    }
    
    /**
     * 将JavaBean中所有属性和对应的值存放在MapBean中
     * @param bean   存储数据的JavaBean
     * @param includeFields   手工指定解析的属性
     * @param excludeFields   手工忽略解析的属性
     * @param convertEnum   是否将枚举类型转换为对应数字
     * @param supportJpa   是否支持解析JPA的注解
     */
    @SuppressWarnings("unchecked")
    public Condition(Object bean, Set<String> includeFields, Set<String> excludeFields, boolean convertEnum, boolean supportJpa) {
        this.convertEnum = convertEnum;
        this.supportJpa = supportJpa;
        this.includeFields = includeFields;
        this.excludeFields = excludeFields;
        
        if(null == bean) {
            return ;
        }
        // 如果为Map类型，直接添加
        if(bean instanceof Map) {
            addValues(((Map<String, Object>)bean));
            return ;
        }
        
        // 添加Bean的属性数据
        this.addBeanFiledValues(bean);
        
        // 增加带ComputedMethod注解的方法
        this.addBeanComputedMethod(bean);
    }

    @Override
    protected void addBeanFiledValues(Object bean) {
        final Class<?> clazz = bean.getClass();
        Set<String> excludeNames = this.getExcludeFields();
        String mappedName = null;
        // 如果包含的属性为空，则获取所有属性
        if(null == includeFields || includeFields.isEmpty()) {
            Field[] fields = Reflects.getAllFields(clazz);
            JoinColumn joinColumn = null;
            Column column = null;
            String columnName = null;
            for(Field field : fields) {
                mappedName = field.getName();
                // 支持JPA注解
                if(supportJpa) {
                    // 属性含@JoinColumn注解
                    if (field.isAnnotationPresent(JoinColumn.class)) {
                        joinColumn = field.getAnnotation(JoinColumn.class);
                        columnName = joinColumn.name();
                        if(Strings.isNoneBlank(columnName)) {
                            mappedName = columnName;
                        }
                        Object joinColumnValue = JpaUtils.getIdValue(Reflects.readField(field, bean));
                        if(null != joinColumnValue) {
                            this.addValue(mappedName, JpaUtils.getIdValue(joinColumnValue));
                            continue ;
                        }
                    }
                    // 属性含@Column注解
                    else if(field.isAnnotationPresent(Column.class)) {
                        column = field.getAnnotation(Column.class);
                        columnName = column.name();
                        if(Strings.isNoneBlank(columnName)) {
                            mappedName = columnName;
                        }
                    }
                }
                
                // 排除的属性，直接忽略
                if(excludeNames.contains(mappedName)) {
                    continue ;
                }
                this.addValue(mappedName, Reflects.readField(field, bean));
            }   //end for fields
        } else {
            for(String name : includeFields) {
                if(excludeNames.contains(name) || null == Reflects.getField(clazz, name)) {
                    continue ;
                }
                this.addValue(name, Reflects.readField(name, bean));
            }
        }
    }
}
