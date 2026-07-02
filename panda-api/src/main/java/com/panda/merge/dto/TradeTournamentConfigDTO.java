package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Description  :  操盘配置-联赛模板配置DTO
 * @author       :  Vito
 * @Date:  2019年11月6日 下午2:22:06
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class TradeTournamentConfigDTO implements Serializable {
    private static final long serialVersionUID = -8354357114094981498L;
    /**
     * 赛事种类id
     */
    @NotNull(message = "运动种类不能为null!")
    private Long sportId;

    /**
     * 模板id
     */
    @NotNull(message = "模板Id不能为null!")
    private Long templateId;

    /**
     * 模板内容
     */
    private String templateName;

    /**
     * 标准联赛ID
     */
    @NotNull(message = "联赛id不能为null!")
    private Long standardTournamentId;

    /**
     * 联赛等级
     */
    @NotNull(message = "联赛等级不能为null!")
    private Integer tournamentLevel;

    /**
     * 模板类型,1:联赛等级模板,2:专用模板，
     */
    @NotNull(message = "模板类型不能为null!")
    private String templateType;


    /**
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
     */
    @NotNull(message = "盘口类型不能为null!")
    private Integer marketType;

    /**
     * SR权重
     */
    private Integer srWeight;

    /**
     * BC权重
     */
    private Integer bcWeight;

    /**
     * BG权重
     */
    private Integer bgWeight;

    /**
     * 最大盘口数
     */
    private Integer displayMarketCount;

    /**
     * 联赛设置事件审核
     */
    private List<ConfigTemplateEventDTO> templateEventDTOList;

    /**
     * 标准玩法分时
     */
    private List<ConfigTemplateCategoryDTO> templateCategoryDTOList;


}
