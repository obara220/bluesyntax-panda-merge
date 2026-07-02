package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class A99SystemConfigParam implements Serializable {

    /**
     * 球种id
     */
    private Long sportId;

    /**
     * 0:早盘, 1:滚球
     */
    private Integer matchType;

    /**
     * 执行间隔
     */
    private Integer interval;

    /**
     * 0:关, 1:开
     */
    private Integer enable;

}
