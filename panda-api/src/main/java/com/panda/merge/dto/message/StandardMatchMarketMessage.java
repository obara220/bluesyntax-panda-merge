package com.panda.merge.dto.message;

import lombok.Data;

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
public class StandardMatchMarketMessage implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
     *  标准联赛ID
     */
    private Long standardTournamentId;
    
    /**
     * 标准比赛ID   standard_match_info.id
     */
    private Long standardMatchInfoId;
    /**
     * 比赛开始时间.比赛开始时间UTC时间
     */
    private Long beginTime;
    /**
     * 赛事类型：0：普通赛事，1:冠军赛事
     */
    private Integer matchType;

    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    private String dataSourceCode;

    private Long modifyTime;
    /**
     * 是否展示角球玩法
     * 默认展示 * Y：展示；N：不展示
     */
    private String displayCorner;

    /**
     * 是否显示罚球
     * 默认展示 * Y：展示；N：不展示
     */
    private String displayPenalty;
    /**
     * 多盘口玩法展示盘口个数
     */
    private Integer displayMarketCount;
    /**
     *  比赛开盘标识
     *  0:active 开盘, 1:suspended 封盘, 2:deactivated 关盘,11:锁盘状态
     */
    private Integer status;

    /**
     * 运动种类
     */
    private Long sportId;

    /**
     *  赔率来源
     */
    private int oddsSource = -1;
	/**
	 * 盘口投注项
	 */
	private List<StandardMarketMessage> marketList;
}
