package com.panda.merge.bo;

import com.panda.merge.dto.ThirdMatchCoachDTO;
import com.panda.merge.dto.ThirdMatchInforMatinsDTO;
import com.panda.merge.dto.ThirdMatchWinningOddsDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 三方赛事球员伤停信息
 * @author  tell
 * @since   2021年4月17日14:09:54
 */
@Data
public class ThirdMatchExInfomationBO implements Serializable{

    private static final long serialVersionUID = 1L;

    /** 数据来源ID+赛事源ID+球队源ID+球员源ID*/
    private String id;

    /** 三方赛事id */
    private Long thirdMatchId;
    /**标准赛事id */
    private Long standardMatchId;

    /** 数据源赛事id */
    private String thirdMatchSourceId;

    /** 运动类型*/
    private Long sportId;

    /** 数据来源*/
    private String dataSourceCode;

    /** 主队教练信息*/
    private ThirdMatchCoachDTO homeCoach;

    /** 客队队教练信息*/
    private ThirdMatchCoachDTO awayCoach;

    /** 新闻情报信息*/
    private List<ThirdMatchInforMatinsDTO> inforMatinsList;

    /** 指数情报（赔率情况分析）*/
    private ThirdMatchWinningOddsDTO winningOdds;

    private Long createTime;

    private Long modifyTime;

}
