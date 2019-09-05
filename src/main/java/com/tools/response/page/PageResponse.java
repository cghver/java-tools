package com.tools.response.page;

import java.io.Serializable;
import java.util.List;

/**
 * @author 唐小白创建于2019/9/5 18:10
 */
public class PageResponse<T> implements Serializable {

    private String code;
    private String msg;

    /**
     * 当前页码
     */
    private int pageNo;
    /**
     * 页大小
     */
    private int pageSize;
    /**
     * 总数据条数
     */
    private int total;
    /**
     * 总页数
     */
    private int totalPages;
    /**
     * 数据
     */
    private List<T> data;


    public PageResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public PageResponse(String code, String msg, int pageNo, int pageSize, int total, int totalPages, List<T> data) {
        this.code = code;
        this.msg = msg;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = totalPages;
        this.data = data;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PageResponse{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", total=" + total +
                ", totalPages=" + totalPages +
                ", data=" + data +
                '}';
    }
}
