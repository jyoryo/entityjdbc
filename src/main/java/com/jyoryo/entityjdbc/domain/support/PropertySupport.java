package com.jyoryo.entityjdbc.domain.support;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.BooleanUtils;

import com.jyoryo.entityjdbc.common.Arrays;
import com.jyoryo.entityjdbc.common.Chars;
import com.jyoryo.entityjdbc.common.Numbers;
import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.domain.EntityBean;

/**
 * 配置项目表基类。
 * <li>包含：name、value、type</li>
 * @author jyoryo
 */
@MappedSuperclass
public class PropertySupport extends EntityBean {
    private static final long serialVersionUID = 5367600999641201646L;

    /**
     * 名称
     */
    @Id
    @Column(unique = true, nullable = false, updatable = false, length = 50)
    protected String name;
    
    /**
     * 值，默认设置长度为200的varchar。
     * <p>
     * 如果长度不够用，可以在具体实现类上添加注解：
     * <pre>
     * &#064;AttributeOverrides({
     *     &#064;AttributeOverride(name = "value", column = &#064;Column(nullable = false, columnDefinition="text"))
     * })
     * </pre>
     * </p>
     */
    @Column(nullable = false, length = 200)
    protected String value;
    
    /**
     * 值类型
     */
    @Column(nullable = false)
    protected Types type;

    public PropertySupport() {
        super();
    }

    public PropertySupport(String name, String value) {
        super();
        this.name = name;
        this.value = value;
        this.type = Types.STRING;
    }

    public PropertySupport(String name, Types type, String value) {
        super();
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Types getType() {
        return type;
    }

    public void setType(Types type) {
        this.type = type;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getValueByType() {
        if(null == value) {
            return null;
        }
        T retVal = null;
        switch(type) {
            case BOOLEAN:
                retVal = (T) BooleanUtils.toBooleanObject(value);
                break;
            case INT:
                retVal = (T) new Integer(Numbers.toInt(value));
                break;
            case LONG:
                retVal = (T) new Long(Numbers.toLong(value));
                break;
            case DOUBLE:
                retVal = (T) new Double(Numbers.toDouble(value));
                break;
            case ARRAY_STRING:
                retVal = (T) Strings.split(value, Chars.COMMA);
                break;
            case ARRAY_INT:
                retVal = (T) Arrays.toIntArray(value, Strings.COMMA);
                break;
            case ARRAY_LONG:
                retVal = (T) Arrays.toLongArray(value, Strings.COMMA);
                break;
            case ARRAY_DOUBLE:
                retVal = (T) Arrays.toDoubleArray(value, Strings.COMMA);
                break;
            default:
                retVal = (T) value;
                break;
        }
        return retVal;
    }
}