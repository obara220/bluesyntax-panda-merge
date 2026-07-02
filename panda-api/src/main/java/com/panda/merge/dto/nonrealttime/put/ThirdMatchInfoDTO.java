package com.panda.merge.dto.nonrealttime.put;

import com.panda.merge.dto.I18nItemDTO;
import com.panda.merge.dto.ThirdMatchTeamDTO;
import com.panda.merge.validator.EnumValue;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 赛事相关信息参数类
 * @author     tell
 * @since      2020年9月2日19:42:31
 */
@Data
public class ThirdMatchInfoDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    /** 数据源赛事ID*/
    @NotNull(message = "三方数据源赛事ID不能为null!")
    private String thirdMatchSourceId;

    /** 数据源联赛ID 注：如果是子联赛则赋值子联赛ID*/
    @NotNull(message = "三方数据源赛事联赛ID不能为null!")
    private String sourceTournamentId;

    /** 数据源运动种类ID*/
    @NotNull(message = "三方数据源赛事运动类型不能为null!")
    private Long sportId;

    /** 数据来源编码code（取值： SR BC分别代表：SportRadar、FeedConstruc。详情见data_source）*/
    @NotNull(message = "三方数据源赛事数据来源不能为null!")
    private String dataSourceCode;

    /** 数据源区域ID,无区域填0*/
    @NotNull(message = "三方数据源赛事区域ID不能为null!")
    private String thirdRegionId;

    /** 赛事开始时间（UTC时间）*/
    @NotNull(message = "三方数据源赛事开始时间不能为null!")
    private Long beginTime;

    /**
     * 赛事状态. 0:not_started;1:live;2:suspended;3:ended;4:closed;5:cancelled;6:abandoned;7:delayed;8:unknown;9:post
     * 字典数据，对应 parent_type_id = 5
     */
    private String matchStatus;

    /**
     * 赛事类型（默认1）{
     *     1：普通赛事
     *     2：电竞赛事
     *     3：篮球3x3(如果运动类型为篮球）
     *     4：MMA(如果运动类型为拳击）
     * }
     */
    private Integer matchType;

    /**
     * 比赛阶段，字典数据
     *   足球 对应 parent_type_id = 8 and addtion1 = 1（标准运动类型ID）
     *   篮球 对应 parent_type_id = 8 and addtion1 = 2（标准运动类型ID）
     *   ...
     */
    private String matchPeriod;

    /**
     * 赛事包含球队列表
     */
    @Valid
    @NotNull(message = "三方数据源赛事球队列表不能为null!")
    @Size(message = "三方数据源赛事球队列表长度应为2!",min = 2,max = 2)
    private List<ThirdMatchTeamDTO> matchTeamList;

    /** 赛事双方的对阵信息。格式：主场队名称 .vs 客场队名称*/
    private String homeAwayInfo;

    /** 是否为中立场(0:否,1:是)*/
    @NotNull(message = "三方数据源赛事是否中立场不能为null!")
    @EnumValue(message = "三方数据源赛事是否中立场值非法，值应为{0,1}其中之一,请检查",intValues ={0,1})
    private Integer neutralGround;

    /** 是否支持滚球(0:否,1:是)*/
    @NotNull(message = "三方数据源赛事是否支持滚球不能为null!")
    @EnumValue(message = "三方数据源赛事是否支持滚球值非法，值应为{0,1}其中之一,请检查",intValues ={0,1})
    private Integer liveOddSupport;

    /** 赛事是否激活(0:否,1:是)*/
    @NotNull(message = "三方数据源赛事是否激活不能为null!")
    @EnumValue(message = "三方数据源赛事是否激活值非法，值应为{0,1}其中之一,请检查",intValues ={0,1})
    private Integer active;

    /** 赛事是否预定(0:否,1:是)*/
    @NotNull(message = "三方数据源赛事是否预定不能为null!")
    @EnumValue(message = "三方数据源赛事是否预定值非法，值应为{0,1}其中之一,请检查",intValues ={0,1})
    private Integer booked;

    /**
     * 局数(赛制，未固定时长的赛种，固定局数的赛种)
     * 例如:5或者7,代表最多打5局 或者 7局
     * 支持球种:网球,乒乓球、羽毛球，斯洛克,棒球,排球,沙滩排球
     * 参考： http://lan-confluence.sportxxxr1pub.com/pages/viewpage.action?pageId=15675987
     */
    private Integer roundType;

    /**
     * 比赛时长（赛制，固定时长的赛种）
     * 支持球种:足，蓝，冰，美，水，曲，橄，MMA(拳击)
     * 参考： http://lan-confluence.sportxxxr1pub.com/pages/viewpage.action?pageId=15675987
     */
    private Integer matchLength;

    /** 场地类型(字典数据，对应 parent_type_id = 10)*/
    private Integer siteType;

    /** 比赛场地名称,中文优先*/
    private String matchPositionName;

    /** 比赛场地名称国际化*/
    private List<I18nItemDTO> matchPositionNameList;

    /** 彩票号.(竞猜编号)*/
    private String lotteryNumber;

    /** 主队阵型*/
    private String homeFormation;

    /** 客队阵型*/
    private String awayFormation;

    /** 备注.*/
    private String remark;
    /** 创建时间。*/
    private Long createTime;
    //====================联赛阶段优化需要新增字段开始=======================
    /** 数据源赛季id*/
//    @NotNull(message = "数据源赛季ID不能为null!")
    private String seasonId;

//    @NotNull(message = "联赛轮次类型不能为null!")
//    @EnumValue(message = "联赛轮次类型预定值非法，值应为{Group,Cup,Qualification}其中之一,请检查",strValues ={"Group","Cup","Qualification"})
    private String tournamentRoundType;

    /** 联赛轮次数 （当联赛轮次类型是 Group 时存在值）*/
    private Integer tournamentRoundNumber;

    /** 联赛轮次组 （当联赛轮次类型是 Group 时存在值）*/
    private String tournamentRoundGroup;

    /** 联赛轮名称（当联赛轮次类型是 Cup 时存在值）*/
    private String tournamentRoundName;

    /** 联赛轮阶段（当联赛轮次类型是 Cup 时存在值）*/
    private String tournamentRoundPhase;
    /** 重播赛事（0:否  1:是）*/
    private Integer replayMatch = 0;

    //====================联赛阶段优化需要新增字段结束=======================

    //====================优化单43014新增赛事级别赛事对阵类型&事件加速系数=======================
    /** 赛事对阵类型(0:人类，1:机器人) **/
    private Integer competitorType;

    /** 赛事事件加速系数 **/
    private String accelerationFactor;
    //====================优化单43014新增赛事级别赛事对阵类型&事件加速系数=======================
    //====================优化单66013 【产品】【生产】操盘后台新增比赛相关信息=======================
    /** 事件来源类型(0:其他，1:现场（VENUE）,2电视（TV）) **/
    private Integer liveEventSource;
    //====================优化单66013 【产品】【生产】操盘后台新增比赛相关信息=======================

}
