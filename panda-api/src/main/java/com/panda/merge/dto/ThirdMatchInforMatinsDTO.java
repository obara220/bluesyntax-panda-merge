package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 赛事情报信息列表
 * @author     tell
 * @since      2021年4月23日12:24:36
 */
@Data
public class ThirdMatchInforMatinsDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 情报类型id：1主队伤情,2 客队伤情,3主队新闻,4客队新闻,5比赛数据,6比赛预测
     * */
    @NotNull(message = "情报类型ID不能为null!")
    private String typeId;

    @NotNull(message = "情报类型名称不能为null!")
    private String typeName;
    /** 情报子类型名:伤停、禁赛、伤病名单、上阵成疑、恢复上阵*/
    private String subType;
    /** 情报对哪方有利：0主队中立,1客队中立，2 主队有利，3客队有利,  4主队不利,   5客队不利,  6无用*/
    private Integer benefit;
    /** 情报文字内容*/
    private String content;



}
