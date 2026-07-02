/**
 *
 */
package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 三方赛事历史赔率信息DTO
 * @author tell
 * @since  2021年4月16日10:40:51
 */
@Data
public class ThirdMatchHistoryOddsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "数据源赛事ID不能为null!")
    private String thirdMatchSourceId;
    @NotNull(message = "数据源赛事运动类型不能为null!")
    private Long sportId;
    @NotNull(message = "数据来源不能为null!")
    private String dataSourceCode;

    /** 供应商ID*/
    private Integer bookId;
    /** 供应商中文名*/
    private String bookCnName;
    /** 供应商英文名*/
    private String bookEnName;

    /** 玩法ID*/
    private Integer typeId;
    /** 玩法名称*/
    private String typeName;

    /** 盘口类型(1: 赛前盘; 0: 滚球盘)*/
    private Integer marketType;

    /** 初始盘口值*/
    private String value0;
    /** 即时盘口值*/
    private String value;

    /** 投注项字段说明
     * type：选项名（1：主队，2：客队， x：平局，over：大于，under：小于）
     * value：赔率值
     * value0：赔率值初盘，即第一次开出来的数据
     * active：是否锁盘 0 锁盘 1 正常
     */
    /** 投注项*/
    private String oddsJson;
}
