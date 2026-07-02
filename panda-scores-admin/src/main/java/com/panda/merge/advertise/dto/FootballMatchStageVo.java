package com.panda.merge.advertise.dto;

import com.panda.merge.dto.CommonItem;
import lombok.Data;

import java.io.Serializable;

/**
 * PA报球板-技术统计-按场次分类
 *
 * @author warren
 * @since 2023/12/11 21:01:39
 */
@Data
public class FootballMatchStageVo implements Serializable {
    /**
     * 上半场
     */
    private CommonItem firstHalf;
    /**
     * 下半场
     */
    private CommonItem secondHalf;
    /**
     * 全场
     */
    private CommonItem fullHalf;
    /**
     * 加时上半场
     */
    private CommonItem firstHalfOvertime;
    /**
     * 加时下半场
     */
    private CommonItem secondHalfOvertime;
    /**
     * 技术统计
     */
    private String techName;
}
