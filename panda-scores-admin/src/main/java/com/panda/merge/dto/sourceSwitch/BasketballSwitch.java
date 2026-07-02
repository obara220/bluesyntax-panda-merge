package com.panda.merge.dto.sourceSwitch;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * 篮球标准比分中心与数据源联动开关
 * fymen
 */
@Slf4j
@Data
public class BasketballSwitch {

    /**
     * 第一节比分联动开关
     */
    private int firstSwitch;
    private int secondSwitch;
    private int thirdSwitch;
    private int fourSwitch;

    /**
     * 上半场开关
     */
    private int hfSwitch;
    /**
     * 下半场开关
     */
    private int ftSwitch;
    /**
     * 加时赛比分联动开关
     */
    private int otSwitch;
    /**
     * 3*3 开关
     */
    private int allSwitch;
    public BasketballSwitch(){
        this.firstSwitch = 1;
        this.secondSwitch = 1;
        this.thirdSwitch = 1;
        this.fourSwitch = 1;
        this.hfSwitch = 1;
        this.ftSwitch = 1;
        this.otSwitch = 1;
        this.allSwitch = 1;
    }
}

