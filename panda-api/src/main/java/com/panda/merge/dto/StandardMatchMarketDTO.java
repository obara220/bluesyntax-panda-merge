package com.panda.merge.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 
 * @Description  : 标准盘口与投注项消息
 * @author       :  Vito
 * @Date:  2019年10月7日 下午5:01:27
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardMatchMarketDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
     *  标准联赛ID
     */
    private Long standardTournamentId;

    /**
     * 标准比赛ID   standard_match_info.id
     */
    @NotNull(message="标准赛事id不能为空")
    private Long standardMatchInfoId;

    private Integer matchType;

    /**
     * 取值:  PA 代表熊猫操盘
     */
    private String dataSourceCode;

    /**
     * 修改时间	
     */
    private Long modifyTime;

    /**
     * 盘口列表
     */
    @Valid
    @NotNull(message="盘口列表不能为空")
	private List<StandardMarketDTO> marketList;
}
