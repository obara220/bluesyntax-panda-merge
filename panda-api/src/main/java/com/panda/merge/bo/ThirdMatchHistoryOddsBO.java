package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 三方赛事历史统计信息
 * @author  tell
 * @since   2021年2月9日15:52:11
 */
@Data
public class ThirdMatchHistoryOddsBO implements Serializable{

    private static final long serialVersionUID = 1L;

    /** 数据来源ID+赛事源ID+供应商ID+玩法ID+盘口类型*/
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

    /** 供应商ID*/
    private Integer bookId;

    /** 供应商中文名称*/
    private String bookCnName;

    /** 供应商英文名称*/
    private String bookEnName;

    /** 玩法ID*/
    private Integer typeId;

    /** 玩法名称*/
    private String typeName;

    /** 盘口类型(1:赛前盘;0:滚球盘)*/
    private Integer marketType;

    /** 让球初始盘口值*/
    private String handicapVal0;

    /** 大小初始盘口值*/
    private String overUnderVal0;

    /** 让球即时盘口值*/
    private String handicapVal;

    /** 大小即时盘口值*/
    private String overUnderVal;

    /** 投注项字段说明
     * type：  选项名（1：主队，2：客队， x：平局，over：大于，under：小于）
     * value： 即时赔率值
     * value0：赔率值初盘，即第一次开出来的数据
     * active：是否锁盘 0 锁盘 1 正常
     */
    /** 胜平负投注项值*/
    private String winnerOdds;

    /** 让球投注项值*/
    private String handicapOdds;

    /** 大小投注项值*/
    private String overUnderOdds;

    private Long createTime;

    private Long modifyTime;

}
