package com.panda.merge.dto.scores;

import lombok.Data;

import java.io.Serializable;

/**
 * @description 比分中心设置
 * @author fymen
 * @date 2023-11-24
 */
@Data
public class SportScoreShowStatusDTO implements Serializable {

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
     * 赛种赛果展示状态 true展示，false不展示
     */
    private Boolean showStatus;

    /**
     * 赛种赛果展示状态 true展示，false不展示
     */
    private Boolean defaultShowStatus;

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


    public SportScoreShowStatusDTO() {
    }


}
