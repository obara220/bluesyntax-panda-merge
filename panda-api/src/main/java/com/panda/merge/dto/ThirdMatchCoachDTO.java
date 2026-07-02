package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 赛事教练信息
 * @author     tell
 * @since      2021年4月23日12:24:40
 */
@Data
public class ThirdMatchCoachDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    @NotNull(message = "数据源教练ID不能为null!")
    private String coachId;

    @NotNull(message = "教练中文名不能为null!")
    private String cnName;
    /** 教练英文名*/
    private String enName;
    /** 生日*/
    private String birthdate;
    /** 执教比赛数*/
    private Integer gameCount;

    /** 胜场数*/
    private Integer winCount;
    /** 平场数*/
    private Integer drawCount;
    /** 负场数*/
    private Integer loseCount;


    /** 场均进球数*/
    private String score;
    /** 阵型风格，如：4-3-2-1*/
    private String style;
    /** 头像地址*/
    private String picUrl;
    /** 所属协会*/
    private String association;
    /** 所属协会的会徽图片地址*/
    private String associationLogo;

}
