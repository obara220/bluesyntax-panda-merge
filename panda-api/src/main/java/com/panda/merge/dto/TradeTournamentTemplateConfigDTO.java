package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description  :  操盘配置-联赛模板配置DTO
 * @author       :  Vito
 * @Date:  2019年11月6日 下午2:22:06
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class TradeTournamentTemplateConfigDTO implements Serializable {
    private static final long serialVersionUID = -8358357118098986868L;

    /**
     * 模板id
     */
    @NotNull(message = "模板id不能为null!")
    private Long templateId;

    /**
     * 标准联赛ID
     */
    @NotNull(message = "联赛等级不能为null!")
    private Long standardTournamentId;

    /**
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
     */
    @NotNull(message = "盘口类型不能为null!")
    private Integer marketType;



}
