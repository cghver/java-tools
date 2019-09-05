package com.tools.response.page;

import com.tools.response.ResponseCode;

import java.util.List;

/**
 * @author 唐小白创建于2019/9/5 18:18
 */
public final class PageResponseTemplate {
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
}
