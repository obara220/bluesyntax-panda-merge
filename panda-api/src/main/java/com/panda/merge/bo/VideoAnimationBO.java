package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 泰森视频播控中心
 * @author   tell
 * @since    2020年9月15日19:16:02
 */
@Data
public class VideoAnimationBO implements Serializable {
    private static final long serialVersionUID = 418870384314172330L;

    /** 标准赛事id*/
    private Long standardMatchId;
    /** 三方赛事id*/
    private Long thirdMatchId;
    /** 三方数据源赛事ID*/
    private String thirdMatchSourceId;
    /** 数据来源*/
    private String dataSourceCode;
    /** 运动类型*/
    private Long sportId;

    /*** TS:动画ID , SR:流ID ***/
    private String aniId;
    /*** 3.0动画 ***/
    private String animation3Paths;
    /*** 2.0动画 ***/
    private String animationPath;
    /*** 主客队显示是否相反(0:否;1:是)***/
    private Integer reverse;

    /*** 直播视频状态 1预告,3直播中,10已结束,11取消 ***/
    private Long liveVideoPathStatus;
    /*** 直播视频在线状态：0线下,1线上 ***/
    private Long liveVideoOnline;
    /*** 直播视频清晰度：0标清（ ***/
    private String liveVideoHd;

    /*** 直播视频m3u8播放地址 ***/
    private String liveVideoPathM3u8;

    /**
     * 流媒体对内地址(无延迟)
     */
    private String streamInsideUrl;

    /*** 直播视频flv播放地址 ***/
    private String liveVideoPathFlv;
    /** 播放器url(type  pc:电脑,mobile:手机, 默认pc)*/
    private String playerUrl;

    /** 视频截图地址（最新图片）*/
    private String liveVideoImgUrl;

    /** 修改时间*/
    private Long modifyTime;

    /** 是否重要联赛（1:是,0:否）
     * 如，世界杯，欧洲杯， */
    private Integer leagueFlag;

    /**
     * 新旧机房域名标识
     */
    private String urlDomain;
}
