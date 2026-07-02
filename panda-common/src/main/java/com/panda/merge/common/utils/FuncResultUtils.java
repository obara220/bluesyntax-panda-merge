package com.panda.merge.common.utils;

import lombok.Data;

/**
 * 方法返回结果封装方法
 * */
@Data
public class FuncResultUtils {
    /**
     * 成功且带返回参数
     * */
    public static Result  success(Object data){
        Result response=new Result();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }
    /**
     * 失败且带返回参数
     * */
    public static Result  failure(Object data){
        Result response=new Result();
        response.setSuccess(false);
        response.setData(data);
        return response;
    }

    /**
     * 内部返回体类
     * */
    @Data
    public static class Result<T>{
        private boolean  isSuccess;
        private T  data;
    }

}

