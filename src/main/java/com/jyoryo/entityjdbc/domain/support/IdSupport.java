package com.jyoryo.entityjdbc.domain.support;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.jyoryo.entityjdbc.domain.EntityBean;

/**
 * 默认以int数字自增为主键的基类
 * @auther: jyoryo
 */
@MappedSuperclass
public abstract class IdSupport extends EntityBean {
    private static final long serialVersionUID = -2385351597757233227L;

    // 主键id(int型)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    protected int id;

    public IdSupport() {
        super();
    }

    public IdSupport(int id) {
        super();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
