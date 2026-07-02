package com.panda.merge.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 赛事时间处理类
 */
public class MatchTimeUtils {
    //将时间进行补充，如果判断时长是5分钟灰色区间的，则把时长调整为5分钟的灰色区间
   private static   Map<Integer, Set<Integer>> GRAY_PERIOD_SECOND_MAP=new HashMap<>();
    static {
        //分钟数
        Set<Integer> HT_PERIOD_GRAY=new HashSet<>();
        HT_PERIOD_GRAY.add(4); HT_PERIOD_GRAY.add(9); HT_PERIOD_GRAY.add(14); HT_PERIOD_GRAY.add(19); HT_PERIOD_GRAY.add(24);
        HT_PERIOD_GRAY.add(29); HT_PERIOD_GRAY.add(34); HT_PERIOD_GRAY.add(39); HT_PERIOD_GRAY.add(44);
        HT_PERIOD_GRAY.add(5); HT_PERIOD_GRAY.add(10); HT_PERIOD_GRAY.add(15); HT_PERIOD_GRAY.add(20); HT_PERIOD_GRAY.add(25);
        HT_PERIOD_GRAY.add(30); HT_PERIOD_GRAY.add(35); HT_PERIOD_GRAY.add(40); HT_PERIOD_GRAY.add(45);
        Set<Integer> HT2_PERIOD_GRAY=new HashSet<>();
        HT2_PERIOD_GRAY.add(49); HT2_PERIOD_GRAY.add(54); HT2_PERIOD_GRAY.add(59); HT2_PERIOD_GRAY.add(64);
        HT2_PERIOD_GRAY.add(69); HT2_PERIOD_GRAY.add(74); HT2_PERIOD_GRAY.add(79); HT2_PERIOD_GRAY.add(84); HT2_PERIOD_GRAY.add(89);
        HT2_PERIOD_GRAY.add(50); HT2_PERIOD_GRAY.add(55); HT2_PERIOD_GRAY.add(60); HT2_PERIOD_GRAY.add(65);
        HT2_PERIOD_GRAY.add(70); HT2_PERIOD_GRAY.add(75); HT2_PERIOD_GRAY.add(80); HT2_PERIOD_GRAY.add(85); HT2_PERIOD_GRAY.add(90);

        GRAY_PERIOD_SECOND_MAP.put(6,HT_PERIOD_GRAY);
        GRAY_PERIOD_SECOND_MAP.put(7,HT2_PERIOD_GRAY);
    }
    public static Integer checkAndFixedGreySecond(Integer btSeconds, Integer period) {
        if(period==31){
            period=6;
        }else if(period==100){
            period=7;
        }
        Integer fixedSeconds=btSeconds;
        Set<Integer> set =GRAY_PERIOD_SECOND_MAP.get(period);
        if(set==null){
            return fixedSeconds;
        }
        Integer minut= btSeconds/60;
        Integer seconds = btSeconds-minut*60;
        if(set.contains(minut)){
            //如果是5的倍数则 灰色区间范围是 秒数<=40 补充为秒数<=5
            if(minut%5==0){
                if(seconds<=40){
                    seconds=5;
                }
            }else {
                //如果不是5的倍数则 灰色区间范围是 秒数>=20 补充为 秒数 >=55
                if(seconds>=20){
                    seconds=55;
                }
            }
        }
        fixedSeconds=minut*60+seconds;
        return fixedSeconds;
    }
}
