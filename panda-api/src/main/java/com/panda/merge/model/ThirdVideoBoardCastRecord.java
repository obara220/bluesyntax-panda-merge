package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

public class ThirdVideoBoardCastRecord implements Serializable {
    @ApiModelProperty(value = "主键ID（数据源ID+赛事ID）")
    private String id;

    @ApiModelProperty(value = "赛事ID")
    private String matchId;

    @ApiModelProperty(value = "数据来源编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "运动种类")
    private Long sportId;

    @ApiModelProperty(value = "类别名")
    private String cate;

    @ApiModelProperty(value = "联赛名")
    private String league;

    @ApiModelProperty(value = "开始时间")
    private Date startDate;

    @ApiModelProperty(value = "主队中文名")
    private String homeZn;

    @ApiModelProperty(value = "客队中文名")
    private String awayZn;

    @ApiModelProperty(value = "主队英文名")
    private String homeEn;

    @ApiModelProperty(value = "客队英文名")
    private String awayEn;

    @ApiModelProperty(value = "直播视频状态1预告,3直播中,10已结束,11取消")
    private Long liveVideoPathStatus;

    @ApiModelProperty(value = "直播视频在线状态：0线下,1线上")
    private Long liveVideoOnline;

    @ApiModelProperty(value = "直播视频清晰度：0标清（<=640）1高清（>640）只有播放中或已结束的直播具有该参数，预告阶段没有该阶段参数")
    private String liveVideoHd;

    @ApiModelProperty(value = "TS:动画ID,SR:流ID")
    private String aniId;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "播放器url(typepc:电脑,mobile:手机,默认pc)")
    private String playerUrl;

    @ApiModelProperty(value = "直播视频m3u8播放地址")
    private String liveVideoPathM3u8;

    @ApiModelProperty(value = "直播视频flv播放地址")
    private String liveVideoPathFlv;

    @ApiModelProperty(value = "2.0动画信息播放地址，当没有动画的时候,字段为空")
    private String animationPath;

    @ApiModelProperty(value = "3.0动画对象信息JSON字符串,格式：{“style_name”:'','path':“”}")
    private String animation3Paths;

    @ApiModelProperty(value = "主队logo地址")
    private String homeTeamLogoUrl;

    @ApiModelProperty(value = "客队logo地址")
    private String awayTeamLogoUrl;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getHomeZn() {
        return homeZn;
    }

    public void setHomeZn(String homeZn) {
        this.homeZn = homeZn;
    }

    public String getAwayZn() {
        return awayZn;
    }

    public void setAwayZn(String awayZn) {
        this.awayZn = awayZn;
    }

    public String getHomeEn() {
        return homeEn;
    }

    public void setHomeEn(String homeEn) {
        this.homeEn = homeEn;
    }

    public String getAwayEn() {
        return awayEn;
    }

    public void setAwayEn(String awayEn) {
        this.awayEn = awayEn;
    }

    public Long getLiveVideoPathStatus() {
        return liveVideoPathStatus;
    }

    public void setLiveVideoPathStatus(Long liveVideoPathStatus) {
        this.liveVideoPathStatus = liveVideoPathStatus;
    }

    public Long getLiveVideoOnline() {
        return liveVideoOnline;
    }

    public void setLiveVideoOnline(Long liveVideoOnline) {
        this.liveVideoOnline = liveVideoOnline;
    }

    public String getLiveVideoHd() {
        return liveVideoHd;
    }

    public void setLiveVideoHd(String liveVideoHd) {
        this.liveVideoHd = liveVideoHd;
    }

    public String getAniId() {
        return aniId;
    }

    public void setAniId(String aniId) {
        this.aniId = aniId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getPlayerUrl() {
        return playerUrl;
    }

    public void setPlayerUrl(String playerUrl) {
        this.playerUrl = playerUrl;
    }

    public String getLiveVideoPathM3u8() {
        return liveVideoPathM3u8;
    }

    public void setLiveVideoPathM3u8(String liveVideoPathM3u8) {
        this.liveVideoPathM3u8 = liveVideoPathM3u8;
    }

    public String getLiveVideoPathFlv() {
        return liveVideoPathFlv;
    }

    public void setLiveVideoPathFlv(String liveVideoPathFlv) {
        this.liveVideoPathFlv = liveVideoPathFlv;
    }

    public String getAnimationPath() {
        return animationPath;
    }

    public void setAnimationPath(String animationPath) {
        this.animationPath = animationPath;
    }

    public String getAnimation3Paths() {
        return animation3Paths;
    }

    public void setAnimation3Paths(String animation3Paths) {
        this.animation3Paths = animation3Paths;
    }

    public String getHomeTeamLogoUrl() {
        return homeTeamLogoUrl;
    }

    public void setHomeTeamLogoUrl(String homeTeamLogoUrl) {
        this.homeTeamLogoUrl = homeTeamLogoUrl;
    }

    public String getAwayTeamLogoUrl() {
        return awayTeamLogoUrl;
    }

    public void setAwayTeamLogoUrl(String awayTeamLogoUrl) {
        this.awayTeamLogoUrl = awayTeamLogoUrl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", matchId=").append(matchId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", sportId=").append(sportId);
        sb.append(", cate=").append(cate);
        sb.append(", league=").append(league);
        sb.append(", startDate=").append(startDate);
        sb.append(", homeZn=").append(homeZn);
        sb.append(", awayZn=").append(awayZn);
        sb.append(", homeEn=").append(homeEn);
        sb.append(", awayEn=").append(awayEn);
        sb.append(", liveVideoPathStatus=").append(liveVideoPathStatus);
        sb.append(", liveVideoOnline=").append(liveVideoOnline);
        sb.append(", liveVideoHd=").append(liveVideoHd);
        sb.append(", aniId=").append(aniId);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", playerUrl=").append(playerUrl);
        sb.append(", liveVideoPathM3u8=").append(liveVideoPathM3u8);
        sb.append(", liveVideoPathFlv=").append(liveVideoPathFlv);
        sb.append(", animationPath=").append(animationPath);
        sb.append(", animation3Paths=").append(animation3Paths);
        sb.append(", homeTeamLogoUrl=").append(homeTeamLogoUrl);
        sb.append(", awayTeamLogoUrl=").append(awayTeamLogoUrl);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}