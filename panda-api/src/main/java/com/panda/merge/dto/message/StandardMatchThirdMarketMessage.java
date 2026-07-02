package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StandardMatchThirdMarketMessage implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
     * 标准比赛ID   standard_match_info.id
     */
    private Long standardMatchInfoId;

    /**
     * 赛事类型：0：普通赛事，1:冠军赛事
     */
    private Integer matchType;

    /**
     * 运动种类
     */
    private Long sportId;
	/**
	 * 盘口投注项
	 */
	private List<ThirdSportMarketMessage> marketList;
}
