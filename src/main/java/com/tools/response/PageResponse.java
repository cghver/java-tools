package com.tools.response;

import com.tools.json.JsonHelper;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

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


    public static <V> PageResponse<V> success(int pageNo, int pageSize, int total, int totalPages, List<V> data) {
        return new PageResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getDesc(), pageNo, pageSize, total, totalPages, data);
    }

    public static <V> PageResponse<V> failed() {
        return new PageResponse<>(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getDesc());
    }

    public static PageResponse failed(String msg) {
        return new PageResponse<>(ResponseCode.FAILED.getCode(), msg);
    }


    public static <V> PageResponse<V> failed(ResponseCode responseCode) {
        return new PageResponse<>(responseCode.getCode(), responseCode.getDesc());
    }

    public static <V> PageResponse<V> failed(String code, String msg) {
        return new PageResponse<>(code, msg);
    }


    /**
     * 将PageResponse的json字符串转化为PageResponse,如果转失败，返回null
     * @param json
     * @param bean
     * @return
     */
    public static <T> PageResponse<T> jsonToPageResponse(String json, Class<T> bean) {
        String code = JsonHelper.getNodeString("code", json);
        String msg = JsonHelper.getNodeString("msg", json);

        Integer pageNo = Optional.ofNullable(JsonHelper.getNodeInteger("pageNo", json)).orElse(0);
        Integer pageSize = Optional.ofNullable(JsonHelper.getNodeInteger("pageSize", json)).orElse(0);
        Integer total = Optional.ofNullable(JsonHelper.getNodeInteger("total", json)).orElse(0);
        Integer totalPages = Optional.ofNullable(JsonHelper.getNodeInteger("totalPages", json)).orElse(0);
        String data = JsonHelper.getNodeString("data", json);
        if (code == null || msg == null  || bean == null){
            return null;
        }

        if (data == null || "null".equals(data) ){
            return new PageResponse<>(code, msg, pageNo, pageSize, total, totalPages, null);
        }

        List<T> list = JsonHelper.transToList(data, bean);
        return new PageResponse<>(code, msg, pageNo, pageSize, total, totalPages, list);
    }
}
