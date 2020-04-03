package com.jyoryo.entityjdbc.domain.support;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 包含id、name、notes内容
 * @auther: jyoryo
 */
@MappedSuperclass
public abstract class ItemSupport extends IdSupport {
    private static final long serialVersionUID = -1512255775413879542L;

    //名称
    @Column(nullable = false)
    protected String name;

    //备注说明
    @Column(nullable = false)
    protected String notes;

    public ItemSupport() {
        super();
    }

    public ItemSupport(int id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
