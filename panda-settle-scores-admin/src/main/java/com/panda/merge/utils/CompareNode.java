package com.panda.merge.utils;

import lombok.Data;

@Data
public class CompareNode {

    /**
     * 字段
     */
    private String fieldKey;

    /**
     * 字段值
     */
    private Object fieldValue;

    /**
     * 字段名称
     */
    private String fieldName;

}