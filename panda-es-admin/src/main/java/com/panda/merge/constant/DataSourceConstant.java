package com.panda.merge.constant;

import java.util.HashMap;
import java.util.Map;

public class DataSourceConstant {
    public static Map<String,Integer> DATA_SOURCE_CODE_MAP;
    static {
        DATA_SOURCE_CODE_MAP=new HashMap<>();
        DATA_SOURCE_CODE_MAP.put("SR",1);
        DATA_SOURCE_CODE_MAP.put("BC",2);
        DATA_SOURCE_CODE_MAP.put("BG",3);
        DATA_SOURCE_CODE_MAP.put("PA",4);
        DATA_SOURCE_CODE_MAP.put("GR",5);
        DATA_SOURCE_CODE_MAP.put("TX",6);
        DATA_SOURCE_CODE_MAP.put("RB",7);
        DATA_SOURCE_CODE_MAP.put("PA",8);
    }


}
