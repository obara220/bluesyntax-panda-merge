package com.panda.merge.dto.sourceSwitch;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * 网球标准比分中心与数据源联动开关
 * fymen
 */
@Slf4j
@Data
public class TennisSwitch {

    /**
     * 第一盘比分联动开关
     */
    private int firstSwitch;
    private int secondSwitch;
    private int thirdSwitch;
    private int fourSwitch;
    private int fifSwitch;
    private int sixSwitch;
    private int sevenSwitch;

    public TennisSwitch(){
        this.firstSwitch = 1;
        this.secondSwitch = 1;
        this.thirdSwitch = 1;
        this.fourSwitch = 1;
        this.fifSwitch = 1;
        this.sixSwitch = 1;
        this.sevenSwitch = 1;
    }
}

