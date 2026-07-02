package com.panda.merge.bo;

import com.panda.merge.dto.ThirdMatchCoachDTO;
import com.panda.merge.dto.ThirdMatchInforMatinsDTO;
import com.panda.merge.dto.ThirdMatchWinningOddsDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 三方赛事正面交手数据
 * @author  tell
 * @since   2021年4月17日14:09:54
 */
@Data
public class ThirdMatchFrontStatisticsBO implements Serializable{

    private static final long serialVersionUID = 1L;

    /** 三方赛事id */
    private Long thirdMatchId;

    /**标准赛事id */
    private Long standardMatchId;

    /** 数据源赛事id */
    private String thirdMatchSourceId;

    /** 运动类型*/
    private Long sportId;

    /** 标准主队ID */
    private Long standardHomeTeamId;

    /** 标准客队ID */
    private Long standardAwayTeamId;

    /** 主队多语言 */
    private List<I18nItemBO> homeTeamNameIl8nList;

    /** 客队多语言 */
    private List<I18nItemBO> awayTeamNameIl8nList;


   /** 数据源赛事id+数据来源+运动类型 */
    private String id;

   /** 数据来源 */
    private String dataSourceCode;

   /** 数据源主队ID */
    private String homeTeamId;

   /** 数据源客队ID */
    private String awayTeamId;

   /** 主队名称 */
    private String homeTeamName;

   /** 客队名称 */
    private String awayTeamName;

   /** 0:自动1:手动 */
    private Integer editStatus;

   /** 总场数 */
    private Integer countTotal;

   /** 主队赢场数 */
    private Integer homeWin;

   /** 客队赢场数 */
    private Integer awayWin;

   /** 和局场数 */
    private Integer dogfallTotal;

   /** 高于1.5场数 */
    private Integer moreThanOne;

   /** 高于2.5场数 */
    private Integer moreThanTwo;

   /** 高于3.5场数 */
    private Integer moreThanThree;

   /** 两队都得分场数 */
    private Integer allScores;

   /** 主队没有失球场数 */
    private Integer homeNotLost;

   /** 客队没有失球场数 */
    private Integer awayNotLost;

   /** 修改时间 */
    private Long modifyTime;

   /** 创建时间 */
    private Long createTime;

}
