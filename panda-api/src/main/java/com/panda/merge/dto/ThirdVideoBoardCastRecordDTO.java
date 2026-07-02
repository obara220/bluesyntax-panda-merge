package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * 泰森播控平台
 * @author   tell
 * */
@Data
public class ThirdVideoBoardCastRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "数据源赛事Id不能为空!")
    private String matchId;

    /** 数据来源*/
    @NotNull(message = "数据源编码不能为空!")
    private String dataSourceCode;

    /** 运动类型*/
    private Long sportId;

    /** 类别名（运动类型名称）*/
    @Deprecated
    private String cate;

    /** 联赛名*/
    @Deprecated
    private String league;

    /** 开始时间*/
    private Date startDate;

    /** 主队中文名*/
    @Size(max = 50, message = "主队中文名长度不能超过50字符")
    private String homeZn;

    /** 客队中文名*/
    @Size(max = 50, message = "客队中文名长度不能超过50字符")
    private String awayZn;

    /** 主队英文名*/
    @Size(max = 50, message = "主队英文名长度不能超过50字符")
    private String homeEn;

    /** 客队英文名*/
    @Size(max = 50, message = "客队英文名长度不能超过50字符")
    private String awayEn;

    /** 主队logo地址*/
    private String homeTeamLogoUrl;

    /** 客队logo地址*/
    private String awayTeamLogoUrl;

    /** 直播视频状态
     *   1预告,2准备中,3直播中,4视频中断,10已结束,11取消 (默认取消)
     *   直播视频状态为3 并且 直播视频在线状态为1 才能播放
     * */
    private Long liveVideoPathStatus;

    /** 直播视频在线状态：0线下,1线上
     *  直播视频状态为3 并且 直播视频在线状态为1 才能播放
     * */
    private Long liveVideoOnline;

    /** 直播视频清晰度：0标清（<=640）1高清（>640）只有播放中或已结束的直播具有该参数，预告阶段没有该阶段参数*/
    private String liveVideoHd;

    /** 视频截图地址（最新图片）*/
    private String liveVideoImgUrl;

    /** 直播视频m3u8播放地址
     *  必须是m3u8格式流地址,多个地址逗号分隔
     *格式：m3u8_path1,m3u8_path2,...
     * */
    private String liveVideoPathM3u8;

    /** 直播视频flv播放地址
     *  必须是flv格式流地址,多个地址逗号分隔
     *  格式：flv_path1,flv_path2,...
     * */
    private String liveVideoPathFlv;

    /** 播放器url(type  pc:电脑,mobile:手机, 默认pc)*/
    private String playerUrl;

    /** 动画id，等于本中心比赛id，当没有动画的时候，字段为空*/
    private String aniId;

    /** 2.0动画信息播放地址，当没有动画的时候,字段为空*/
    private String animationPath;

    /** 3.0动画对象信息JSON字符串
     * 格式：[{“style_name”:'','path':“”},{“style_name”:'','path':“”}]
     * */
    private String animation3Paths;

    /** 是否重要联赛（1:是,0:否）
     * 如，世界杯，欧洲杯， */
    private Integer leagueFlag;

    /** 创建时间。*/
    private Long createTime;
}