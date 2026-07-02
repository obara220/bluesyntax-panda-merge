package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Mirro
 * @Project Name :  panda_data_realtime
 * @Package Name :  com.panda.sport.data.realtime.dto
 * @Description:
 * @date 2019/10/4 19:50
 * @ModificationHistory Who    When    What
 */
@Data
public class MatchEventInfoMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 运动种类id。 对应sport.id
     * 如果玩法不区分体育类型，传0，否则传对应体育类型标识
     */
    private Long sportId;

    /**
     * 是否被取消.1 被取消; 0:没有被取消
     */
    private Integer canceled;

    /**
     * 对应data_source.code
     */
    private String dataSourceCode;

    /**
     * 事件来源类型.0:UOF;1:常规事件
     */
    private Integer sourceType;

    /**
     * 事件编码. 对应 match_event_type.event_code
     */
    private String eventCode;

    /**
     * 事件发生时间. UTC时间
     */
    private Long eventTime;

    /**
     * 扩展信息
     */
    private String extraInfo;

    /**
     * 主客场. 主场队:home; 客场队:away
     */
    private String homeAway;

    /**
     * 比赛阶段id.  system_item_dict.value
     */
    private Long matchPeriodId;

    /**
     * 赛事类型（默认1）{
     *     1：普通赛事
     *     2：电竞赛事
     *     3：篮球3x3(如果运动类型为篮球）
     * }
     */
    private Integer matchType;

    /** 球员id前缀*/
    private String playerIdPrefix;

    /**
     * 球员1的id
     */
    private Long player1Id;

    /**
     * 球员1的名称
     */
    private String player1Name;

    /**
     * 球员2的id
     */
    private Long player2Id;

    /**
     * 球员2的名称
     */
    private String player2Name;

    /**
     * 距离比赛开始多少秒
     */
    private Long secondsFromStart;

    /**
     * 标准赛事的id. 对应 standard_match_info.id
     */
    private Long standardMatchId;

    /**
     * 标准球队 ID. 对应 standard_sport_team.id
     */
    private Long standardTeamId;

    /**
     * 主队数量
     */
    private Integer t1;

    /**
     * 客队数量
     */
    private Integer t2;
    /**
     * 当前第几局
     */
    private Integer secondNum;

    /**
     * 盘主队比分
     */
    private Integer firstT1;

    /**
     * 盘客队比分
     */
    private Integer firstT2;

    /**
     * 局主队比分
     */
    private Integer secondT1;

    /**
     * 局客队比分
     */
    private Integer secondT2;

    /**
     * 当前盘数
     */
    private Integer firstNum;
    /**
     * 第三方数据源提供的该事件id.
     */
    private String thirdEventId;

    /**
     * 第三方赛事的id. 对应third_match_info.id
     */
    private String thirdMatchId;

    /**
     * 比赛在数据源中的ID
     */
    private String thirdMatchSourceId;

    /**
     * 关联的AO数据源赛事ID
     */
    private String aoThirdMatchSourceId;

    /**
     * 第三方球队id. 对应 third_sport_team.id
     */
    private String thirdTeamId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 当前节\阶段剩余时间
     */
    private Long periodRemainingSeconds;

    /**
     * 点球大战回合
     */
    private Integer penaltyShootoutRound;

    /**
     * 创建时间. UTC时间
     */
    private Long createTime;

    /**
     * 修改时间. UTC时间
     */
    private Long modifyTime;
    /**
     * 扩展字段
     */
    private String addition6;

    /**
     * 扩展字段
     */
    private String addition7;

    /**
     * 扩展字段
     */
    private String addition8;

    /**
     * 扩展字段
     */
    private String addition9;

    /**
     * 扩展字段
     */
    private String addition10;

    private String addition1;

    private String addition2;

    /**
     * 扩展字段
     */
    private String addition3;

    /**
     * 扩展字段
     */
    private String addition4;

    /**
     * 扩展字段
     */
    private String addition5;

    /**
     * 是否错误完赛事件（普通足球阶段为999才会使用该字段，0:否，1:是）
     */
    private Integer isErrorEndEvent = 0;


    //TS赛事事件信息
    /** 集锦id，若没有本子段，表示该事件没有集锦视频*/
    private String fragmentId;

    /** 集锦32位的编码，获取视频文件和截图文件有该code拼接*/
    private String fragmentCode;

    /** 集锦视频的url地址。路径中“_fragment_cdn_path”应替换为cdn的实际地址*/
    private String fragmentVideo;

    /** 集锦视频截图的url地址。路径中“_fragment_cdn_path”应替换为cdn的实际地址*/
    private String fragmentPic;

    /**集锦视频的长度（秒）*/
    private String fragmentLength;

    //====================优化单66013 【产品】【生产】操盘后台新增比赛相关信息=======================
    /** 事件来源类型(0:其他，1:现场（VENUE）,2电视（TV）) **/
    private Integer liveEventSource;

    /**
     * 比赛时长（赛制，固定时长的赛种）
     * 支持球种:足，蓝，冰，美，水，曲，橄，MMA(拳击)
     * 参考： http://lan-confluence.sportxxxr1pub.com/pages/viewpage.action?pageId=15675987
     */
    private Integer matchLength;
    //====================优化单66013 【产品】【生产】操盘后台新增比赛相关信息=======================

    /**
     * 需求 3531，切换事件源标识历史事件，避免前端赛事进行时间在切换过程中乱
     * 是否延迟的事件(true:切换事件源，或者延迟消费的事件，false:开售事件，或者正常通道下发事件）
     */
    private Boolean isReissue;
}
