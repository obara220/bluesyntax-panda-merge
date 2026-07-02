package com.panda.merge.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @description 比分中心设置
 * @author fymen
 * @date 2023-11-24
 */
@Data
public class SportScoreShowStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 体种
     */
    private Integer sportId;

    /**
     * 赛种赛果展示状态 1展示，0不展示
     */
    private Integer showStatus;

    /**
     * 赛种赛果展示状态 1展示，0不展示
     */
    private Integer defaultShowStatus;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;


    private String userId;
    private String userName;
    private String ipAddress;


    public SportScoreShowStatus() {
    }


}
