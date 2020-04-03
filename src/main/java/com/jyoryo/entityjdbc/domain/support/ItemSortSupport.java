package com.jyoryo.entityjdbc.domain.support;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 包含id、name、notes内容，而且支持排序号
 * @auther: jyoryo
 */
@MappedSuperclass
public abstract class ItemSortSupport extends ItemSupport {
    private static final long serialVersionUID = 4575491032278042377L;

    //排序号
    @Column
    protected int sortBy;

    public ItemSortSupport() {
        super();
    }

    public ItemSortSupport(int id) {
        super(id);
    }

    public int getSortBy() {
        return sortBy;
    }

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
    }
}
