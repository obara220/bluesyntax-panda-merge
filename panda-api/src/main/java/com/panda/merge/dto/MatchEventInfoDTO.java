package com.panda.merge.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 三方事件信息入参DTO
 * @author   tell
 * @since    2021年1月10日15:00:44
 */
@Data
public class MatchEventInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 三方数据源赛事id*/
    @NotEmpty(message = "thirdMatchSourceId不能为空")
    private String thirdMatchSourceId;

    /** 事件编码（必填）*/
    private String eventCode;

    /**三方数据源事件id.*/
    private String thirdEventId;

    /** 运动种类id。 对应sport.id（如果玩法不区分体育类型，传0，否则传对应体育类型标识）*/
    private Long sportId;

    /** 数据来源编码*/
    @NotEmpty(message="dataSourceCode字段不能为空")
    private String dataSourceCode;

    /**
     * 三方数据源球队原始id
     * 如果homeAway字段为 home 或者 away,thirdTeamId字段是必传的
     * */
    private String thirdTeamId;

    /** 是否被取消.1 被取消; 0:没有被取消*/
    @NotNull(message = "canceled字段不能为null")
    private Integer canceled;

    /**
     * 数据来源类型（0 : UOF，1:  Scoring Feed）
     * 如果是V02事件源，0:集锦事件，1:实时文字直播事件
     * */
    @NotEmpty(message="sourceType不能为空")
    @Pattern(regexp = "^[0-1]*$",message = "sourceType状态不正确")
    private String sourceType;

    /** 事件发生时间*/
    @NotNull(message = "eventTime字段不能为null")
    @Min(value = 0, message = "必须为正数")
    private Long eventTime;

    /** 主客场. 主队: home 客队: away，公共事件：none*/
    private String homeAway;

    /** 比赛阶段id.  system_item_dict.value*/
    private Long matchPeriodId;

    /** 球员1的id*/
    private Long player1Id;
    /** 球员2的id*/
    private Long player2Id;

    /** 球员1的名称*/
    private String player1Name;
    /** 球员2的名称*/
    private String player2Name;

    /**
     * 比赛时长
     * */
    private Integer matchLength;

    /** 比赛已进行时长
     *  如果当前赛种应该显示倒计时正计时为空，则 secondsFromStart字段和 periodRemainingSeconds值可以一致，如：篮球赛种
     * */
    @NotNull(message = "secondsFromStart字段不能为null")
    @Min(value = 0, message = "必须为正数")
    private Long secondsFromStart;

    /**
     * 可能事件发时的比赛进行的时长。用来记录可能进球、黄牌、红牌事件，
     * 具体是属于可能事件的哪个5、15分钟进球区间
     */
    private Long possibleEventStarTime;

    private Long possibleEventTime;

    private Long possibleEventId;

    /** 阶段剩余时间（倒计时）
     *  篮球
     *  冰球根据正计时转换公式：
     *  小节=20*60-数据商下发的正计时秒数
     *  加时=5*60-数据商当前下发的正计时秒数
     * */
    private Long periodRemainingSeconds;

    /** 主队数量*/
    private Integer t1;

    /** 客队数量*/
    private Integer t2;

    /**
     * 当前盘数
     * 每个赛种含义可能不一样，该字段按照网球定义命名
     * 足球：上半场表示1，下半场表示2
     * 篮球：小节数
     * 网球：网球是先有盘，才有局，所以这里网球就是代表 盘数
     * 乒乓球：乒乓球只有局,所以这里代表 局数
     * 板球：板球先有局，后面是轮，所以这里表示 局数
     * */
    private Integer firstNum;

    /** 主队盘比分（t1 直属下级的比分）*/
    private Integer firstT1;

    /** 客队盘比分（t2 直属下级的比分）*/
    private Integer firstT2;

    /**
     * 当前局数
     * 每个赛种含义可能不一样，该字段按照网球定义命名
     * 足球：无
     * 篮球：无
     * 网球：网球是先有盘，才有局，所以这里网球就是代表 局数
     * 乒乓球：乒乓球只有局,所以这里不需要赋值
     * 板球：板球先有局，后面是轮，所以这里表示 轮数
     * */
    private Integer secondNum;

    /** 主队局比分（firstT1 直属下级的比分）*/
    private Integer secondT1;

    /** 客队局比分（firstT2 直属下级的比分）*/
    private Integer secondT2;

    /** 扩展信息 后面统一用 extraInfo
     *  eventCode如果为 time_start  （0:stop,1:start）
     *  eventCode如果为 delete_event 值应该为被删除的事件ID
     *  参考文档：http://lan-confluence.dbsports.online/pages/viewpage.action?pageId=76261
     */
    @Deprecated
    private String extrainfo;

    /**
     * 扩展信息
     * eventCode如果为 time_start  （0:stop,1:start）
     * eventCode如果为 delete_event 值应该为被删除的事件ID
     * 参考文档：http://lan-confluence.dbsports.online/pages/viewpage.action?pageId=76261
     */
    private String extraInfo;

    /** 扩展字段1
     * */
    private String addition1;
    /** 扩展字段2
     *  斯诺克：ball_pot事件表示当前局当前队伍在当前轮出杆的比分
     * */
    private String addition2;
    /** 扩展字段3
     *
     * */
    private String addition3;
    /** 扩展字段4
     * V02数据源：集锦排序依据
     * 其它数据源：暂无
     * */
    private String addition4;
    /** 扩展字段5
     *  异常事件特殊标识
     *  1:异常事件,其他:正常事件
     * */
    private String addition5;
    /** 扩展字段*/
    private String addition6;
    /** 扩展字段*/
    private String addition7;
    /** 扩展字段*/
    private String addition8;
    /**
     * 扩展字段9
     * V02数据源：赛事轮次
     * 其它数据源：暂无
     * */
    private String addition9;
    /**
     * 扩展字段10
     * V02数据源：赛事轮次类型
     * 其它数据源：暂无
     * */
    private String addition10;

    /** 链路ID(冗余字段)*/
    private String copyLinkId;
    /** 备注 注：如果是TS事件则该字段值为json字符串
     * TS：
     * {
     *  "fragmentId":"集锦id，若没有本子段，表示该事件没有集锦视频"
     * ,"fragmentCode":"集锦32位的编码，获取视频文件和截图文件有该code拼接"
     * ,"fragmentVideo":"集锦视频的url地址。路径中“_fragment_cdn_path”应替换为cdn的实际地址"
     * ,"fragmentPic":"集锦视频截图的url地址。路径中“_fragment_cdn_path”应替换为cdn的实际地址"
     * }
     *
     * */
    private String remark;
    /**
     * 是否错误完赛事件（普通足球阶段为999才会使用该字段，0:否，1:是 这里表示操盘确认完赛后的回调）
     */
    private Integer isErrorEndEvent = 0;

    /**
     * 赛事控制类型:1 开始   2 暂停  3 继续  4 结束"
     */
    private Integer controlType;

    public String getExtrainfo() {
        return extrainfo;
    }

    public void setExtrainfo(String extrainfo) {
        this.extrainfo = extrainfo;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
}
