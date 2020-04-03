package com.jyoryo.entityjdbc.common.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jyoryo.entityjdbc.annotation.ComputedMethod;
import com.jyoryo.entityjdbc.common.Beans;
import com.jyoryo.entityjdbc.common.Numbers;
import com.jyoryo.entityjdbc.common.Reflects;
import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.common.log.Logs;

/**
 * 基于HashMap，用于构建JavaBean Map的数据绑定
 * @author jyoryo
 *
 */
public class MapBean extends HashMap<String, Object> {
    private static final long serialVersionUID = 5451406978229242425L;

    /**
     * 是否将枚举类型转换为数字
     */
    protected boolean convertEnum = true;
    /**
     * 手工指定解析JavaBean对应的属性
     */
    protected Set<String> includeFields;
    /**
     * 手工指定忽略解析JavaBean对应的属性
     */
    protected Set<String> excludeFields;
    
    /**
     * 默认空构造
     */
    public MapBean() {
        super();
    }

    /**
     * 是否将枚举类型转换为对应数字，构造一个带添加数据的对象
     * @param convertEnum
     */
    public MapBean(boolean convertEnum) {
        this(null, convertEnum);
    }
    
    /**
     * 将JavaBean中所有属性和对应的值放在MapBean中。
     * <li>枚举类型会自动转换为对应的数字</li>
     * @param bean
     */
    public MapBean(Object bean) {
        this(bean, true);
    }
    
    /**
     * 将JavaBean中所有属性和对应的值放在MapBean中。
     * @param bean
     * @param convertEnum   是否将枚举类型转换为对应数字
     */
    public MapBean(Object bean, boolean convertEnum) {
        this(bean, null, null, convertEnum);
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
    public MapBean(Object bean, Set<String> includeFields, Set<String> excludeFields, boolean convertEnum) {
        this.convertEnum = convertEnum;
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
    
    /**
     * 设置将枚举类型转换为数字
     * @param convertEnum
     */
    protected void setConvertEnum(boolean convertEnum) {
        this.convertEnum = convertEnum;
    }
    
    /**
     * 获取排除的属性名称
     * @return
     */
    protected Set<String> getExcludeFields() {
        Set<String> results = new HashSet<>();
        results.add("serialVersionUID");
        if(null == excludeFields || excludeFields.isEmpty()) {
            return results;
        }
        results.addAll(excludeFields);
        return results;
    }
    
    /**
     * 添加数据
     * @param name
     * @param value
     * @return
     */
    public MapBean addValue(String name, Object value) {
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
    
    /**
     * 通过Map批量添加数据
     * @param values
     * @return
     */
    public MapBean addValues(Map<String, Object> values) {
        if(null == values || values.isEmpty()) {
            Logs.warn("添加的数据为空，本次添加不做任何处理！");
            return this;
        }
        for(String name : values.keySet()) {
            addValue(name, values.get(name));
        }
        return this;
    }

    @Override
    public Object get(Object key) {
        return super.get(String.valueOf(key));
    }
    
    /**
     * 获取String类型的值
     * @param key   键名
     * @return
     */
    public String getString(Object key) {
        Object value = get(key);
        return (null == value) ? null : String.valueOf(value);
    }
    
    /**
     * 获取Number类型的值
     * @param key   键名
     * @param defaultNumber   如果未获取到值，则返回该值
     * @return
     */
    public <T extends Number> T getNumber(Object key, T defaultNumber) {
        Object value = get(key);
        return Numbers.toNumber(value, defaultNumber);
    }
    
    /**
     * 添加Bean的属性数据
     * @param bean
     * @return
     */
    protected void addBeanFiledValues(Object bean) {
        final Class<?> clazz = bean.getClass();
        Set<String> excludeNames = this.getExcludeFields();
        String mappedName = null;
        // 如果包含的属性为空，则获取所有属性
        if(null == includeFields || includeFields.isEmpty()) {
            Field[] fields = Reflects.getAllFields(clazz);
            for(Field field : fields) {
                mappedName = field.getName();
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
    
    /**
     * 添加Bean的带ComputedMethod注解的方法
     * @param bean
     */
    protected void addBeanComputedMethod(Object bean) {
        final Class<?> clazz = bean.getClass();
        Method[] methods = Reflects.getMethodsWithAnnotation(clazz, ComputedMethod.class);
        if(null == methods || 0 >= methods.length) {
            return ;
        }
        for(Method method : methods) {
            String fieldName = Beans.getFieldName(method, null);
            if(Strings.isBlank(fieldName)) {
                continue ;
            }
            this.addValue(fieldName, Reflects.invokeMethod(bean, method.getName()));
        }
    }
}
