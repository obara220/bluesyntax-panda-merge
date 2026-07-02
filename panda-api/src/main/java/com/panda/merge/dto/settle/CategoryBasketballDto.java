package com.panda.merge.dto.settle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryBasketballDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 第一节
     */
    private Integer bkQ104;

    /**
     * 第二节
     */
    private Integer bkQ204;

    /**
     * 第三节
     */
    private Integer bkQ304;

    /**
     * 第四节
     */
    private Integer bkQ404;

    /**
     * 比赛结束
     */
    private Integer end;

    /**
     * 上半场
     */
    private Integer bk1ht;

    /**
     * 下半场(包含加时)
     */
    private Integer bk2ht;

    /**
     * 加时赛
     */
    private Integer bkEt;

    /**
     * 全场 (含加时)
     */
    private Integer bkFtEt;

    /**
     * 全场 (常规时间)
     */
    private Integer bkFtRg;

    /**
     * 首先获得N分
     */
    private Integer firstNPoint;

    /**
     * 球员得分
     */
    private Integer point;

    /**
     * 球员三分球
     */
    private Integer bk3pt;

    /**
     * 球员助攻
     */
    private Integer bkAst;

    /**
     * 球员篮板
     */
    private Integer bkRbd;


    public CategoryBasketballDto unFreeze(){
        return new CategoryBasketballDto(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
    }
    public CategoryBasketballDto builderFreeze(){
        return new CategoryBasketballDto(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1);
    }

}
