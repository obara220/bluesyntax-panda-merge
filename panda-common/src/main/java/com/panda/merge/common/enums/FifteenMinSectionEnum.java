package com.panda.merge.common.enums;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 15分钟区间
 * @author :  fymen
 * @since     2024年1月12
 */
public enum FifteenMinSectionEnum implements Serializable {

    ONE_FIFTEENMIN("60899", "0:00-14:59"),
    TWO_FIFTEENMIN("61799", "15:00-29:59"),
    THREE_FIFTEENMIN("62699", "30:00-1HT"),
    FOUR_FIFTEENMIN("73599", "45:00-59:59"),
    FIVE_FIFTEENMIN("74499", "60:00-74:59"),
    SIX_FIFTEENMIN("75399", "75:00-FT");

    /**
     * 描述说明
     */
    public String code;
    public String desc;

    FifteenMinSectionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean isExist(String code) {
         HashSet<String> lists=new HashSet<String>(Arrays.asList("60899","61799","62699","73599","74499","75399"));
         if(lists.contains(code)){
            return true;
        }else{
            return false;
        }

    }
//    public static String getDescByCode(String code) {
//        for (FifteenMinSectionEnum item : FifteenMinSectionEnum.values()) {
//            if (item.getCode().equals(code)) {
//                return item.getDesc();
//            }
//        }
//        return null;
//    }
}
