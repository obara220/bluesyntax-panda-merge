package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 赛事文字直播信息
 * @author     tell
 * @since      2021年3月6日15:14:38
 */
@Data
public class ThirdMatchPhraseDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    @NotNull(message = "数据源赛事文字直播ID不能为null!")
    private String phraseId;

    @NotNull(message = "数据源赛事ID不能为null!")
    private String thirdMatchSourceId;

    @NotNull(message = "运动类型不能为null!")
    private Long sportId;

    @NotNull(message = "数据来源不能为null!")
    private String dataSourceCode;

    /** 时间*/
    private String time;

    /** 中文文字内容*/
    @NotNull(message = "中文文字内容不能为null!")
    private String cnText;

    /** 中文英字内容*/
    private String enText;

    /** 当前比分*/
    private String scores;

    /** 所属球队（0 公共，1 主队，2 客队,当前仅篮球有该字段，并不保证全部比赛都有该字段）*/
    private Integer team;

    /** 融合赛事阶段*/
    private String matchPeriod;

}
