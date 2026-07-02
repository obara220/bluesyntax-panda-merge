package com.panda.merge.common.enums;

import com.alibaba.fastjson.JSON;

/**
 * 赛事开售状态
 * @author   riben
 * @since    2020年9月21日13:12:13
 */
public enum SellStatusEnum {
    UNSOLD("Unsold","未开售"),
    SOLD("Sold","开售");
    public String value;
    public String desc;

    SellStatusEnum(String value, String desc) {
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
