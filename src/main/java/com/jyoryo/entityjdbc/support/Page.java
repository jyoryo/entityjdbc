package com.jyoryo.entityjdbc.support;

import java.util.List;

import com.jyoryo.entityjdbc.common.BaseDo;
import com.jyoryo.entityjdbc.exception.PageException;

/**
 * 分页器
 * @author jyoryo
 *
 * @param <T>
 */
public class Page<T> extends BaseDo {
    private static final long serialVersionUID = 3416660350658060189L;
    /**
     * 默认每页记录数
     */
    public final static int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 是否流式获取分页数据
     * <p>以流式获取分页数据时，是判断是否存在下一页，不会获取总的记录数和总的页数。</p>
     */
    private final boolean flowMode;
    
    /**
     * 当前页码数，从1开始
     */
    private int pageIndex;
    /**
     * 每页的记录数
     */
    private int pageSize;
    
    /**
     * 当前页的数据记录
     */
    private List<T> items;
    
    /**
     * 是否存在下一页
     */
    private boolean hasNext;
    
    /**
     * 总的记录数
     */
    private int totalCount = -1;
    /**
     * 总的页数
     */
    private int totalPage = -1;
    
    
    public Page() {
        this(1, DEFAULT_PAGE_SIZE);
    }

    public Page(int pageIndex, int pageSize) {
        this(false, pageIndex, pageSize);
    }
    
    public Page(boolean flowMode, int pageIndex, int pageSize) {
        this.flowMode = flowMode;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    /**
     * 是否以流式获取分页数据
     * @return
     */
    public boolean isFlowMode() {
        return flowMode;
    }

    /**
     * 当前序列号
     * @return
     */
    public int getPageIndex() {
        return pageIndex;
    }
    
    /**
     * 获取上一页序列号
     * @return
     */
    public int getPrevPageIndex() {
        return (pageIndex > 1) ? (pageIndex - 1) : pageIndex;
    }
    
    /**
     * 获取下一页序列号
     * @return
     */
    public int getNextPageIndex() {
        return isHasNext() ? (pageIndex + 1) : pageIndex;
    }

    /**
     * 当前序列号
     * @param pageIndex
     */
    public void setPageIndex(int pageIndex) {
        if(1 >= pageIndex) {
            pageIndex = 1;
        }
        this.pageIndex = pageIndex;
    }

    /**
     * 每页记录数
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 每页记录数
     * <li>不设置，则取默认值</li>
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        if(0 >= pageSize) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        this.pageSize = pageSize;
    }
    
    /**
     * 当前页数据记录内容
     * @return
     */
    public List<T> getItems() {
        return items;
    }

    /**
     * 当前页数据记录内容
     * @param items
     */
    public void setItems(List<T> items) {
        this.items = items;
    }
    
    /**
     * 是否存在上一页
     * @return
     */
    public boolean isHasPrev() {
        return pageIndex > 1;
    }
    
    /**
     * 是否存在下一页
     * @return
     */
    public boolean isHasNext() {
        return hasNext;
    }

    /**
     * 是否存在下一页
     * @param hasNext
     */
    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    /**
     * 总记录数
     * @return
     */
    public int getTotalCount() {
        if(flowMode) {
            return -1;
        }
        return totalCount;
    }

    /**
     * 总记录数
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        if(flowMode) {
            throw new PageException("flowMode Page unsupport operation!");
        }
        this.totalCount = totalCount;
        final int pageSize = getPageSize();
        this.totalPage = totalCount / pageSize + ((0 == totalCount % pageSize) ? 0 : 1);
        // 设置当前页
        if(0 == totalCount) {
            if(1 != pageIndex) {
                pageIndex = 1;
            }
        } else {
            if(pageIndex > totalPage) {
                pageIndex = totalPage;
            }
        }
        this.hasNext = totalPage > pageIndex;
    }

    /**
     * 总页数
     * @return
     */
    public int getTotalPage() {
        if(flowMode) {
            return -1;
        }
        return totalPage;
    }

    /**
     * 总页数
     * @param totalPage
     */
    public void setTotalPage(int totalPage) {
        if(flowMode) {
            throw new PageException("flowMode Page unsupport operation!");
        }
        this.totalPage = totalPage;
    }
    
    /**
     * 获取当前页第一条数据在数据库中的起始index。
     * <li>类似sql分页中的limit {start}, {limit}中的start</li>
     * 比如每页pageSize大小是20，那么第二页的该值为：(2 - 1) * 20 = 20
     * @return
     */
    public int getStart() {
        return (pageIndex - 1) * pageSize;
    }
}
