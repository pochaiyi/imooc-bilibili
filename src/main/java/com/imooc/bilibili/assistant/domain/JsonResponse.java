package com.imooc.bilibili.assistant.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 标准返回类型，包含数据和状态码，规范JSON响应数据的格式
 */
@Getter
@Setter
public class JsonResponse<T> {

    /**
     * 状态码
     */
    private String code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    public JsonResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 成功响应数据
     */
    public JsonResponse(T data) {
        this.data = data;
        code = "0";
        msg = "成功";
    }

    /**
     * 成功响应，没有数据
     */
    public static JsonResponse<String> success() {
        return new JsonResponse<>(null);
    }

    /**
     * 失败响应，没有数据
     */
    public static JsonResponse<String> fail() {
        return new JsonResponse<>("1", "失败");
    }

    /**
     * 失败响应，包含状态码和提示信息
     */
    public static JsonResponse<String> fail(String code, String msg) {
        return new JsonResponse<>(code, msg);
    }

}
