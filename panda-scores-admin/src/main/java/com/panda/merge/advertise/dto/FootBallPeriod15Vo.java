package com.panda.merge.advertise.dto;

import com.panda.merge.dto.CommonItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 足球报球版详情-按时间阶段分类
 *
 * @author warren
 * @since 2023/12/11 20:15:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FootBallPeriod15Vo implements Serializable {
    /**
     * 进球
     */
    private CommonItem goal;
    /**
     * 角球
     */
    private CommonItem corner;
    /**
     * 黄牌
     */
    private CommonItem yellowCard;
    /**
     * 红牌
     */
    private CommonItem redCard;
}
