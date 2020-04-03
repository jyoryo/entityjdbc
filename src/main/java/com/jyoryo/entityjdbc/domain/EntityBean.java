package com.jyoryo.entityjdbc.domain;

import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.jyoryo.entityjdbc.common.BaseDo;

/**
 * 所有实体Entity基类
 * @auther: jyoryo
 */
@MappedSuperclass
public class EntityBean extends BaseDo {
    private static final long serialVersionUID = 5536205024042485643L;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
