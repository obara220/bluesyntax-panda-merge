package com.panda.merge.config;

import java.util.Arrays;
import java.util.List;

public class A99MarketClassifyConfig {

    //大小类玩法(需要校验球头值和add3/add4)
    public static List<Long> OVER_UNDER_CATEGORY = Arrays.asList(2L, 18L, 127L, 332L, 114L, 122L, 331L, 1100417L, 307L, 309L, 1100407L, 1100410L);

    //让球类玩法(只需要校验add3/add4)
    public static List<Long> HANDICAP_CATEGORY = Arrays.asList(4L, 19L, 128L, 130L, 113L, 121L, 1100414L, 1100416L, 306L, 308L, 1100406L, 1100409L);

}
