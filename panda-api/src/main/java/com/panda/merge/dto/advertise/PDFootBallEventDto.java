package com.panda.merge.dto.advertise;

import lombok.Data;
import java.io.Serializable;

@Data
public class PDFootBallEventDto implements Serializable {
    //事件ID
    private String id;

    /** 事件编码*/
    private String eventCode;

    /**三方数据源事件id.*/
    private String thirdEventId;

    /** 运动种类id。 对应sport.id（如果玩法不区分体育类型，传0，否则传对应体育类型标识）*/
    private Long sportId;

    /** 是否被取消.1 被取消; 0:没有被取消*/
    private Integer canceled;


    /** 事件发生时间*/
    private Long eventTime;

    /** 主客场. 主队: home 客队: away*/
    private String homeAway;

    /** 比赛阶段id.  system_item_dict.value*/
    private Long matchPeriodId;

    /** 比赛已进行时长*/
    private Long secondsFromStart;

    /** 当前节\阶段剩余时间*/
    private Long periodRemainingSeconds;

    /** 主队数量*/
    private Integer t1;

    /** 客队数量*/
    private Integer t2;
    /**危险还是安全*/
    private boolean isDanger;

    /** 链路ID(冗余字段)*/
    private String copyLinkId;

    private String addition10;

    private String addition9;

    private String addition8;

    private String addition7;

    private String addition6;

    private String addition2;

    private String addition1;

    /**
     * 赛事控制类型: 1 开始   2 暂停  3 继续  4 结束
     */
    private Integer controlType;

    /**
     * 扩展信息
     */
    private String extraInfo;
}
