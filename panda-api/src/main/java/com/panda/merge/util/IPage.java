package com.panda.merge.util;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页
 */
@Data
public class IPage implements Serializable {
    private List<?> records;
    /**
     * 当前页
     */
    private int current = 1;
    /**
     * 每页显示条数
     */
    private int size = 10;
    /**
     * 总页数
     */
    private int pages;
    /**
     * 数据总条数
     */
    private int total;
}
