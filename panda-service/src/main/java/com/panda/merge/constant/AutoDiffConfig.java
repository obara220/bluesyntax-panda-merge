package com.panda.merge.constant;

import java.util.Map;

/**
 * @author :  Jimmy
 * @Project Name :  panda_data_realtime_marketodds
 * @Package Name :  com.panda.sport.data.realtime.service.autodiff.config
 * @Description :  TODO
 * @Date: 2020-01-22 17:03
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class AutoDiffConfig {
    private volatile static Map<Long,String> autoDiffMap;
    public static String getAutoDiffCountClass(Long standardMarketCategoryId) {
        if (null == autoDiffMap || autoDiffMap.isEmpty()) {

        }
        return autoDiffMap.get(standardMarketCategoryId);
    }

    private static void initAutoDiffConfig() {
        autoDiffMap.put(1L,"");
        autoDiffMap.put(2L,"");
        autoDiffMap.put(4L,"");
        autoDiffMap.put(15L,"");
    }

    /**
     * 仅有一个盘口的独赢，如：上半场独赢，全场独赢
     */
    public static Long[] EUROPE_DIFF_CATEGORY = {1L,111L,17L};

    /**
     * 两项盘
     */
    public static Long[] MALAY_DIFF_CATEGORY = {2L,4L,15L,18L,19L,42L,113L,114L,118L,121L,122L,10L};
}
