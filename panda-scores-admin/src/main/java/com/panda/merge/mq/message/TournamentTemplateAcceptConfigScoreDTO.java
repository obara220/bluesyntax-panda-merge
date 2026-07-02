package com.panda.merge.mq.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 3574 玩法集tMax开关配置，风控下发
 */
@Data
public class TournamentTemplateAcceptConfigScoreDTO implements Serializable {
     /**
     * 玩法集ID
     */
     private Long categorySetId;
     /**
     * ID
     */
     private Long id;
     /**
     * 开关配置：0-关闭，1-开启
     * 1：Tmax  0安全
     */
     private int isOpen;
     private Long templateId;
    /**
     * 玩法集类型 角球=corner 罚牌=faCard , 加时角球=otCorner，加时罚牌=otFaCard
     */
    private String categoryType;

}
