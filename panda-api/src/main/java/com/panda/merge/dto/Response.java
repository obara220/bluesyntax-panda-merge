package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回对象
 */
@Data
public class Response<T> implements Serializable {
    private long code;
    private String msg;
    private T data;
    private Long dataSourceTime;

    public Response() {
    }

    public Response(long code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功返回结果
     *
     */
    public static <T> Response<T> success() {
        return new Response<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> Response<T> success(T data) {
        return new Response<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param msg 提示信息
     */
    public static <T> Response<T> success(T data, String msg) {
        return new Response<T>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     */
    public static <T> Response<T> failed(ResultCode errorCode) {
        return new Response<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     * @param msg   错误信息
     */
    public static <T> Response<T> failed(ResultCode errorCode, String msg) {
        return new Response<T>(errorCode.getCode(), msg, null);
    }

    /**
     * 失败返回结果
     *
     * @param msg 提示信息
     */
    public static <T> Response<T> failed(String msg) {
        return new Response<T>(ResultCode.FAILED.getCode(), msg, null);
    }

    /**
     * 失败返回结果,带消息体
     * @param msg
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Response<T> changeTradeFailed(String msg,T data) {
        return new Response<T>(ResultCode.CHANGETRADEFAILED.getCode(), msg, data);
    }

    /**
     * 不需要关盘
     * @param msg
     * @param <T>
     * @return
     */
    public static <T>Response<T> noNeedClose(String msg) {
        return new Response<T>(ResultCode.NONEEDCLOSE.getCode(), msg, null);
    }

    /**
     * 需要关盘
     * @param msg
     * @param <T>
     * @return
     */
    public static <T>Response<T> needClose(String msg) {
        return new Response<T>(ResultCode.NEEDCLOSE.getCode(), msg, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> Response<T> failed() {
        return failed(ResultCode.FAILED);
    }

    /**
     * 唯一主键冲突返回结果
     * @param msg 提示信息
     */
    public static <T> Response<T> duplicateKey(String msg) {
        return new Response<T>(ResultCode.DUPLICATE_KEY.getCode(), msg, null);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> Response<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param msg 提示信息
     */
    public static <T> Response<T> validateFailed(String msg) {
        return new Response<T>(ResultCode.VALIDATE_FAILED.getCode(), msg, null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> Response<T> unauthorized(T data) {
        return new Response<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 未授权返回结果
     */
    public static <T> Response<T> forbidden(T data) {
        return new Response<T>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }

    /**
     * 判断是否成功,增加两个成功状态，便于风控根据状态码做相应处理
     * @return
     */
    public boolean isSuccess() {
        return getCode() == ResultCode.SUCCESS.getCode() || getCode() == ResultCode.NONEEDCLOSE.getCode() || getCode() == ResultCode.NEEDCLOSE.getCode();
    }

}
