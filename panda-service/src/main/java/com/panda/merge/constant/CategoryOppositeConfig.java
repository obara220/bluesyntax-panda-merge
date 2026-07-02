package com.panda.merge.constant;

import java.util.*;

/**
 * 主客队相反配置
 * 按照球类分开配置
 * 需求链接：http://lan-zentao.sportxxxr1pub.com/story-view-1008.html
 * 第一期，足球
 * 玩法集合：
 * 1,3,4,5,6,7,8,9,10,11,17,19,20,21,22,25,27,28,29,30,32,33
 * 43,69,70,71,72,74,78,79,80,81,82,103,104
 * 特殊-92
 */
public interface CategoryOppositeConfig {
    class FootBall{
        //1.玩法名称互换 CATEGORY_TYPE_1
        //2.add1取反 CATEGORY_TYPE_2
        //3.add2取反 CATEGORY_TYPE_3
        //4.add3，add4互换 CATEGORY_TYPE_4
        //5.投注项类型互换 CATEGORY_TYPE_5
        //6.add1，add2互换 CATEGORY_TYPE_6
        //7.投注项add1，add2互换 CATEGORY_TYPE_7
        //8.投注项add3，add4互换 CATEGORY_TYPE_8
        public static Map<Long,Long> CATEGORY_TYPE_1 = new HashMap(){{
            put(8L,9L);
            put(9L,8L);
            put(10L,11L);
            put(11L,10L);
            put(21L,22L);
            put(22L,21L);
            put(79L,81L);
            put(81L,79L);
            put(80L,82L);
            put(82L,80L);
            put(78L,92L);
            put(92L,78L);
        }};
        public static List<Long> CATEGORY_TYPE_2 = Arrays.asList(3L,
                4L,113L,306L,128L,1100414L,1100406L,
                19L,121L,308L,130L,1100416L,1100409L,
                33L,69L,71L);
        public static List<Long> CATEGORY_TYPE_3 = Arrays.asList(4L,113L,306L,128L,1100414L,1100406L,
                                                                 19L,121L,308L,130L,1100416L,1100409L);
        public static List<Long> CATEGORY_TYPE_4 = Arrays.asList(4L,113L,306L,128L,1100414L,1100406L,
                                                                 19L,121L,308L,130L,1100416L,1100409L);
        public static List<Long> CATEGORY_TYPE_5 = Arrays.asList(1L,3L,4L,5L,6L,7L,17L,19L,20L,25L,27L,28L,29L,30L,32L,33L,
                43L,69L,70L,71L,72L,74L,103L,104L,
                111L,310L,126L,1100413L,1100405L,
                113L,306L,128L,1100414L,1100406L,
                119L,311L,129L,1100415L,1100408L,
                121L,308L,130L,1100416L,1100409L);
        public static List<Long> CATEGORY_TYPE_6 = Arrays.asList(27L,29L);
        public static List<Long> CATEGORY_TYPE_7 = Arrays.asList(7L,20L,74L,103L);
        public static List<Long> CATEGORY_TYPE_8 = Arrays.asList(103L);
        public static List<Long> CATEGORY_TYPE_9 = Arrays.asList(2L,12L,14L,15L,16L,18L,23L,24L,26L,31L,34L,42L,68L,73L,75L,76L,102L,108L,109L,110L,114L,117L,118L,122L,127L,131L,133L,134L
                ,137L,138L,222L,228L,229L,233L,234L,239L,240L,307L,309L,312L,313L,318L,319L,325L,328L,330L,331L,332L,335L,1100407L);

        public static List<Long> CATEGORY_TYPE_10 = Arrays.asList(
                1L, 111L,310L,126L,1100413L,1100405L,
                2L, 114L,307L,127L,331L,1100407L,
                17L, 119L,311L,129L,1100415L,1100408L,
                18L,122L,309L,332L,1100417L,1100410L);


        //投注项处理
        public static Map<String,String> CATEGORY_ODDS_TYPE_CHANGE = new HashMap(){{
            put("1","2");
            put("2","1");
            put("1X","X2");
            put("1x","x2");
            put("X2","1X");
            put("x2","1x");
        }};

        //104玩法投注项特殊处理
        public static Map<String,String> CATEGORY_ODDS_TYPE_CHANGE_104 = new HashMap(){{
            put("1X","2X");
            put("1x","2x");
            put("12","21");
            put("11","22");
            put("X2","X1");
            put("x2","x1");
            put("X1","X2");
            put("x1","x2");
            put("2X","1X");
            put("2x","1x");
            put("22","11");
            put("21","12");
        }};

        public static boolean containsCategory(Long categoryId)
        {
            Set set = new HashSet();
            set.addAll(CATEGORY_TYPE_1.keySet());
            set.addAll(CATEGORY_TYPE_2);
            set.addAll(CATEGORY_TYPE_3);
            set.addAll(CATEGORY_TYPE_4);
            set.addAll(CATEGORY_TYPE_5);
            set.addAll(CATEGORY_TYPE_6);
            set.addAll(CATEGORY_TYPE_7);
            set.addAll(CATEGORY_TYPE_8);
            set.addAll(CATEGORY_TYPE_9);
            if (set.contains(categoryId))
            {
                return true;
            }
            return false;
        }

        public static void main(String[] args) {
            System.out.println(containsCategory(103L));
            String s = "0:1 0:2";

            if (s.contains(":"))
            {
                String[] strArr = s.split(":");
                if (strArr.length == 2)
                {
                    s = (strArr[1]+":"+strArr[0]);
                }
                else
                {
                    String[] strArr1 = s.split(" ");
                    if (strArr1.length == 2)
                    {
                        String[] strArrOdds0 = strArr1[0].split(":");
                        String[] strArrOdds1 = strArr1[1].split(":");
                        if (strArrOdds0.length == 2 && strArrOdds1.length == 2)
                        {
                            s = (strArrOdds0[1]+":"+strArrOdds0[0] + " " + strArrOdds1[1]+":"+strArrOdds1[0]);
                        }
                    }
                }
            }

            System.out.println("------" + s);
        }
    }
}
