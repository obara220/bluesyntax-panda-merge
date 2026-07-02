package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 赛事玩法 最新赔率更新时间下发消息对象
 * @author damian
 *
 */
@Data
public class MatchMonitorMessage  implements Serializable {
	private static final long serialVersionUID = 5188624964566856388L;
	//赛种Id
	private Long sportId;
	
    //标准赛事id
    private Long matchId;

    //标准玩法id
    private Long categoryId;
    
    //更新时间
    private Long dataSourceTime;
    
    //盘口类型
    private Integer marketType;
    
    //阶段Id
    private Long matchPeriodId;

}
