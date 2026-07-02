package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Mirro
 * @Project Name :  panda_data_nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.api.query.bo
 * @Description:
 * @date 2019/10/24 14:58
 * @ModificationHistory Who    When    What
 */
@Data
public class ThirdSportMarketBO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 数据库id
     */
    private Long id;

    /**
     * 所属联赛ID
     */
    private String tournamentId;

    /**
     * 运动种类id. 对应 sport.id
     */
    private Long sportId;

    /**
     * 比赛开始时间. UTC时间
     */
    private Long beginTime;

    /**
     * 比赛ID:third_match_info.id
     */
    private Long matchId;

    /**
     * 第三方玩法id   standard_sport_market_category.id
     */
    private Long marketCategoryId;

    /**
     * 第三提供的id。SR: 报文中有id字段。
     */
    private String thirdMarketSourceId;

    /**
     * 如果当前盘口与标准盘口中的B记录玩法相同且盘口显示内容相同, 则该记录的当前字段值为B.ID
     */
    private Long referenceId;

    /**
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
     */
    private Integer marketType;

    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    private String dataSourceCode;

    /**
     * 盘口状态0-5. 0:active, 1:suspended, 2:deactivated, 3:settled, 4:cancelled, 5:handedOver
     */
    private Integer status;

    /**
     * 盘口阶段id. 对应 对应 system_item_dict.value
     */
    private String scopeId;

    /**
     * 盘口名称编码. 用于多语言
     */
    private Long nameCode;

    /**
     * 玩法的中文名称. 仅用用于数据库操作人员使用.
     */
    private String oddsTypeName;

    /**
     * 接收到第三方数据后, 可以通过该字段快速定位到当前的盘口. 通过玩法和具体内容确认盘口的唯一性.  SR提供的盘口数据id 生成算法: Type_Typeid_Subtypeid_Specialoddsvalue
     */
    private String thirdOddsType;

    /**
     * 该盘口具体显示的值. 例如: 大小球中, 大小界限是:  3.5
     */
    private String oddsValue;

    /**
     * 排序类型
     */
    private String orderType;

    /**
     * 盘口名称.
     */
    private String oddsName;

    /**
     * 附加字段1
     */
    private String addition1;

    /**
     * 附加字段2
     */
    private String addition2;

    /**
     * 附加字段3
     */
    private String addition3;

    /**
     * 附加字段4
     */
    private String addition4;

    /**
     * 附加字段5
     */
    private String addition5;

    private String remark;

    private Long createTime;

    private Long modifyTime;


    /**
     * 标准赛事id
     */
    private Long standardMatchId;
    /**
     * 比赛开盘标识。0：未开盘；1：开盘；2：关盘；3：封盘；开盘后用户可下注
     */
    private Integer operateMatchStatus;
}
