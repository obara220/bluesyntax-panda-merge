package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description  :  操盘配置-联赛配置margin集合DTO
 * @author       :  Vito
 * @Date:  2019年11月6日 下午2:22:06
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class ConfigTemplateCategoryDTO implements Serializable {
    private static final long serialVersionUID = -8354357114094981498L;

    /**
     * 标准玩法 ID
     */
    private Long standardCategoryId;

    /**
     * 盘口投注类型.   my: 马来盘; eu: 欧盘
     */
    private String marketOddsType;

    /**
     * 比赛阶段
     */
    private long  matchPeriodId;

    /**
     * 玩法操作类型 ： 0 取消， 1 新增
     */
    private long  isSell;

    /**
     * 比赛进程时间 ，单位为秒
     */
    private long  matchProgressTime;

    /**
     * 补时时间，单位为秒
     */
    private long  injuryTime;

    /**
     * 分时margin集合
     */
    private List<CategoryMarginDtlDTO> categoryMarginDtlList;
}
