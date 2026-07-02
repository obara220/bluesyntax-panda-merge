package com.panda.merge.common.enums;

import com.alibaba.fastjson.JSON;

/**
 * 冠军赛事是否自动开售新玩法  Yes:是 No:否
 * @Author: Kepa
 * @Date: 2020/10/24 17:10
 */
public enum OutrightAutoSellStatusEnum {

    AutoSell("Yes","自动开售"),
    NotAutoSell("No","非自动开售");

    public String value;
    public String desc;

    OutrightAutoSellStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue(){
        return this.value;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }


}
