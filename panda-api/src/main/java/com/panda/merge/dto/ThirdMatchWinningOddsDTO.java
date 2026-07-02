package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 赛事指数情报（赔率情况分析）
 * @author     tell
 * @since      2021年4月23日12:24:40
 */
@Data
public class ThirdMatchWinningOddsDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    /** 主队胜出赔率(十进制)*/
    private String homeDecimalValue;
    /** 主队实际胜出率*/
    private String homeActual;
    /** 主队按赔率的预期胜出率*/
    private String homeExpected;
    private String homeDesc;

    /** 客队胜出赔率(十进制)*/
    private String awayDecimalValue;
    /** 客队实际胜出率*/
    private String awayActual;
    /** 客队按赔率的预期胜出率*/
    private String awayExpected;
    private String awayDesc;

}
