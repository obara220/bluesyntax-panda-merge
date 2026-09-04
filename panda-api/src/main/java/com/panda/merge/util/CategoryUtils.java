package com.panda.merge.util;

import com.panda.merge.common.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class CategoryUtils {
    /**
     * 700需求玩法分类170L
     */
    public static List<Long> CATEGORY_700_ADD1 = Arrays.asList(28L, 30L, 31L, 109L, 110L, 120L, 125L, 133L, 148L, 201L, 214L, 222L, 224L, 225L, 230L, 235L, 237L, 336L, 357L, 363L, 364L, 365L, 366L, 387L, 404L, 407L, 3100409L, 1100478L, 1100482L);
    public static List<Long> CATEGORY_700_ADD1_SPECIAL = Arrays.asList();
    public static List<Long> CATEGORY_750_ADD1 = Arrays.asList(208L);
    public static List<Long> CATEGORY_700_ADD2 = Arrays.asList(145L, 146L);
    public static List<Long> CATEGORY_750_ADD2 = Arrays.asList(162L, 163L, 164L, 165L, 166L, 170L);
    public static List<Long> CATEGORY_700_ADD2_ADD1 = Arrays.asList(147L);
    public static List<Long> CATEGORY_750_ADD1_ADD2 = Arrays.asList(167L, 168L, 405L, 406L);
    public static List<Long> CATEGORY_700_ADD1_ADD2 = Arrays.asList(215L);
    public static List<Long> CATEGORY_700_ADD3 = Arrays.asList(32L, 34L, 231L, 233L, 370L, 372L);
    public static List<Long> CATEGORY_700_MARKETID = Arrays.asList();
    /**
     * 15分钟 特殊处理
     * 附加字段5 (a,b).spilt[1]/15
     */
    public static List<Long> FIFTEEN_MINUTES_CATEGORY_ADD5 = Arrays.asList(33L, 232L, 371L);
    /**
     * 乒乓球 / 羽毛球 玩法一致
     */
    public static List<Long> TABLETENNIS_AND_BADMINTON_CATEGORY_ADD2 = Arrays.asList(175L, 176L, 177L, 178L);
    public static List<Long> TABLETENNIS_AND_BADMINTON_CATEGORY_ADD1_ADD2 = Arrays.asList(203L);
    public static List<Long> TABLETENNIS_AND_BADMINTON_CATEGORY_ADD2_ADD1 = Arrays.asList(179L);
    /**
     * 排球/手球
     */
    public static List<Long> VOLLEYBALL_CATEGORY_ADD1 = Arrays.asList(255L);
    public static List<Long> VOLLEYBALL_CATEGORY_ADD2 = Arrays.asList(162L, 253L, 254L);
    public static List<Long> VOLLEYBALL_CATEGORY_ADD1_AND2 = Arrays.asList(256L);
    /**
     * 斯诺克
     */
    public static List<Long> SNOOKER_CATEGORY_ADD2 = Arrays.asList(184L, 185L, 186L, 187L, 189L, 190L, 191L, 192L, 193L, 194L, 196L, 197L);
    public static List<Long> SNOOKER_CATEGORY_ADD2_ADD1 = Arrays.asList(188L, 195L);
    /**
     * 棒球
     */
    public static List<Long> BASEBALL_CATEGORY_ADD1 = Arrays.asList(275L, 283L);
    public static List<Long> BASEBALL_CATEGORY_ADD2 = Arrays.asList(276L, 280L, 281L, 282L, 287L, 288L, 289L);
    /**
     * 冰球
     */
    public static List<Long> ICEBALL_CATEGORY_ADD1 = Arrays.asList(28L, 261L, 266L, 267L);
    public static List<Long> ICEBALL_CATEGORY_ADD2 = Arrays.asList(262L, 263L, 264L, 265L, 268L, 297L, 298L);

    public static List<Long> FOOTBALL_CATEGORY_ADD1 = Arrays.asList(362L,1100412L,1100419L,1100446L,1100447L);

    public static List<Long> CATEGORY_ADD2_ADD3 = Arrays.asList(274L, 277L, 278L, 279L);

    /**
     * 板球X玩法
     */
    public static List<Long> CATEGORY_3648_ADD1 = Arrays.asList(3700011L, 3700012L, 3700013L, 3700014L);

    public static List<Long> CATEGORY_3648_ADD2 = Arrays.asList(3700007L, 3700008L, 3700009L, 3700010L);


    public static String SPLIT_LINE = " - ";

    public static String SPLIT_AND = "&&";

    public static String HOME_PARAM = "H-";

    public static String AWAY_PARAM = "A-";

    public static String SOCCER = "Soccer";

    public static String BASKETBALL = "Basketball";

    public static String OFF = "Off";

    public static String ON = "On";

    public static String OFF_All = "Off All";

    public static String ON_All = "On All";

    public static Integer UN_LEVEL = -1;

    public static String min5Goal = "5mins Goal";

    public static String min15Goal = "15mins Goal";

    public static String min15Corner = "15mins Corner";

    public static String min15Bookings = "15mins Bookings";

    public static String bookingSwitch = "booking switch";

    public static String cornerSwitch = "corner switch";

    public static String goalSwitch = "goal switch";

    public static String topWeightSwitch = "topWeight switch";

    public static String graySwitch = "gray switch";

    public static String dataSourceCode = "dataSourceCode";


    public static Map<Long, String> time5MinMap = new LinkedHashMap<Long, String>() {{
        put(6005L, "00-4:59");
        put(6010L, "5:00 - 9:59");
        put(6015L, "10:00 - 14:59");
        put(6020L, "15:00 - 19:59");
        put(6025L, "20:00 - 24:59");
        put(6030L, "25:00 - 29:59");
        put(6035L, "30:00 - 34:59");
        put(6040L, "35:00 - 39:59");
        put(6045L, "40:00 - 45:00");
        put(6050L, "1H 45:00+");
        put(7050L, "45:00- 49:59");
        put(7055L, "50:00 - 54:59");
        put(7060L, "55:00 - 59:59");
        put(7065L, "60:00 - 64:59");
        put(7070L, "65:00 - 69:59");
        put(7075L, "70:00 - 74:59");
        put(7080L, "75:00 - 79:59");
        put(7085L, "80:00 - 84:59");
        put(7090L, "85:00 - 90:00");
        put(7095L, "2H 90:00+");
    }};

    public static Map<Long, String> time15MinMap = new LinkedHashMap<Long, String>() {{
        put(60899L, "0:00-14:59");
        put(61799L, "15:00-29:59");
        put(62699L, "30:00-1HT");
        put(73599L, "2HT-59:59");
        put(74499L, "60:00-74:59");
        put(75399L, "75:00-FT");
    }};


    public static Long get15MinPeriod(Long period, Long secondStart) {
        //开局15分钟
        if (period == 6 && secondStart < 60 * 15) {
            return 60899L;
        }
        //15分钟-30分钟
        if (period == 6 && secondStart >= 60 * 15 && secondStart < 60 * 30) {
            return 61799L;
        }
        //30分钟-上半场
        if (period == 6 && secondStart >= 60 * 30) {
            return 62699L;
        }
        //下半场开始-59:59分钟
        if (period == 7 && secondStart < 60 * 60) {
            return 73599L;
        }
        //下半场 60分钟-74:59
        if (period == 7 && secondStart >= 60 * 60 && secondStart < 60 * 75) {
            return 74499L;
        }
        //下半场 75分钟-全场
        if (period == 7 && secondStart >= 60 * 75) {
            return 75399L;
        }
        return null;
    }

    public static Long get5MinPeriod(Long period, Long secondStart) {
        //开场-4:59
        if (period == 6 && secondStart < 60 * 5) {
            return 6005L;
        }
        //5:00 - 9:59
        if (period == 6 && secondStart >= 60 * 5 && secondStart < 60 * 10) {
            return 6010L;
        }
        //10:00 - 14:59
        if (period == 6 && secondStart >= 60 * 10 && secondStart < 60 * 15) {
            return 6015L;
        }
        //15:00 - 19:59
        if (period == 6 && secondStart >= 60 * 15 && secondStart < 60 * 20) {
            return 6020L;
        }
        //20:00 - 24:59
        if (period == 6 && secondStart >= 60 * 20 && secondStart < 60 * 25) {
            return 6025L;
        }
        //25:00 - 29:59
        if (period == 6 && secondStart >= 60 * 25 && secondStart < 60 * 30) {
            return 6030L;
        }
        //30:00 - 34:59
        if (period == 6 && secondStart >= 60 * 30 && secondStart < 60 * 35) {
            return 6035L;
        }
        //35:00 - 39:59
        if (period == 6 && secondStart >= 60 * 35 && secondStart < 60 * 40) {
            return 6040L;
        }
        //40:00 - 45:00
        if (period == 6 && secondStart >= 60 * 40 && secondStart < 60 * 45) {
            return 6045L;
        }
        //1H Last-minute Goal
        if (period == 6 && secondStart > 60 * 45) {
            return 6050L;
        }

        //下半场- 49:59
        if (period == 7 && secondStart < 60 * 50) {
            return 7050L;
        }
        //50:00 - 54:59
        if (period == 7 && secondStart >= 60 * 50 && secondStart < 60 * 55) {
            return 7055L;
        }
        //55:00 - 59:59
        if (period == 7 && secondStart >= 60 * 55 && secondStart < 60 * 60) {
            return 7060L;
        }
        //60:00 - 64:59
        if (period == 7 && secondStart >= 60 * 60 && secondStart < 60 * 65) {
            return 7065L;
        }
        //65:00 - 69:59
        if (period == 7 && secondStart >= 60 * 65 && secondStart < 60 * 70) {
            return 7070L;
        }
        //70:00 - 74:59
        if (period == 7 && secondStart >= 60 * 70 && secondStart < 60 * 75) {
            return 7075L;
        }
        //75:00 - 79:59
        if (period == 7 && secondStart >= 60 * 75 && secondStart < 60 * 80) {
            return 7080L;
        }
        //80:00 - 84:59
        if (period == 7 && secondStart >= 60 * 80 && secondStart < 60 * 85) {
            return 7085L;
        }
        //85:00 - 90:00
        if (period == 7 && secondStart >= 60 * 85 && secondStart < 60 * 90) {
            return 7090L;
        }
        //2H Last-minute Goal
        if (period == 7 && secondStart > 60 * 90) {
            return 7095L;
        }
        return null;
    }

    /**
     * 附加字段1
     */
    public static List<Long> ADD1 = new ArrayList<Long>() {{
        addAll(CATEGORY_700_ADD1);
        addAll(CATEGORY_750_ADD1);
        addAll(VOLLEYBALL_CATEGORY_ADD1);
        addAll(BASEBALL_CATEGORY_ADD1);
        addAll(ICEBALL_CATEGORY_ADD1);
        addAll(FOOTBALL_CATEGORY_ADD1);
        addAll(CATEGORY_3648_ADD1);
    }};

    /**
     * 附加字段1 附加字段2
     * {X} - {Y}
     */
    public static List<Long> ADD1_ADD2 = new ArrayList<Long>() {{
        addAll(CATEGORY_750_ADD1_ADD2);
        addAll(CATEGORY_700_ADD1_ADD2);
        addAll(TABLETENNIS_AND_BADMINTON_CATEGORY_ADD1_ADD2);
        addAll(VOLLEYBALL_CATEGORY_ADD1_AND2);
    }};

    /**
     * 附加字段2
     */
    public static List<Long> ADD2 = new ArrayList<Long>() {{
        addAll(CATEGORY_700_ADD2);
        addAll(CATEGORY_750_ADD2);
        addAll(TABLETENNIS_AND_BADMINTON_CATEGORY_ADD2);
        addAll(VOLLEYBALL_CATEGORY_ADD2);
        addAll(SNOOKER_CATEGORY_ADD2);
        addAll(BASEBALL_CATEGORY_ADD2);
        addAll(ICEBALL_CATEGORY_ADD2);
        addAll(CATEGORY_3648_ADD2);
    }};


    /**
     * 附加字段2 附加字段1
     * {Y} - {X}
     */
    public static List<Long> ADD2_ADD1 = new ArrayList<Long>() {{
        addAll(CATEGORY_700_ADD2_ADD1);
        addAll(TABLETENNIS_AND_BADMINTON_CATEGORY_ADD2_ADD1);
        addAll(SNOOKER_CATEGORY_ADD2_ADD1);
    }};
    /**
     * 附加字段2 附加字段3
     * {a} - {b}
     */
    public static List<Long> ADD2_ADD3 = new ArrayList<Long>() {{
        addAll(CATEGORY_ADD2_ADD3);
    }};

    /**
     * 所有带{X}玩法
     */
    public static Set<Long> All_X_CATEGORY = new HashSet<Long>() {{
        addAll(ADD1);
        addAll(ADD1_ADD2);
        addAll(ADD2);
        addAll(ADD2_ADD1);
    }};

    /**
     * 根据标准玩法id生成子玩法id
     * 145  	145*100+add2
     * 146		146*100+add2
     * 147		147*10000+add2*100+add1
     * 201		201*100+add1
     * 214		214*100+add1
     * 215		215*10000+add1*100+add2
     * 336		336*100+add1
     * 28		28*100+add1
     * 30		30*100+add1
     * 109		109*100+add1
     * 110		110*100+add1
     * 34		34*100+(add3/15)
     * 32		32*100+(add3/15)
     * 33		33*100+(add5.spilt[1]/15)
     * 31		31*100+add1
     * 222		222*100+add1
     * 148		148*100+add1
     * 233		233*100+(add3/15)
     * 225		225*100+add1
     * 120		120*100+add1
     * 125		125*100+add1
     * 230		230*100+add1
     * 231		231*100+(add3/15)
     * 232		232*100+(add5.spilt[1]/15)
     * 224		224*100+add1
     * 235		235*100+add1
     * 133		133*100+add1
     * 237		237*100+add1
     * 220,221,271,272 marketId
     *
     * @param marketCategoryId
     * @return
     */
    public static Long getChildCategoryId(String linkId, Long marketCategoryId, String... params) {
        try {
            if (ADD1.contains(marketCategoryId)) {
                if (params.length >= 1 && null != params[0]) {
                    Double d = Double.parseDouble(params[0]);
                    return marketCategoryId * 100 + d.intValue();
                }
                return marketCategoryId;
            } else if (CATEGORY_700_ADD1_SPECIAL.contains(marketCategoryId)) {
                if (params.length >= 1 && null != params[0]) {
                    Double d = Double.parseDouble(params[0].replace("+", ""));
                    return marketCategoryId * 100 + d.intValue();
                }
                return marketCategoryId;
            } else if (ADD2.contains(marketCategoryId)) {
                if (params.length >= 2 && null != params[1]) {
                    return marketCategoryId * 100 + Long.parseLong(params[1]);
                }
                return marketCategoryId;
            } else if (ADD2_ADD1.contains(marketCategoryId)) {
                if (params.length >= 2 && null != params[0] && null != params[1]) {
                    return marketCategoryId * 10000 + (Long.parseLong(params[1]) * 100) + Long.parseLong(params[0]);
                }
                return marketCategoryId;
            }  else if (ADD2_ADD3.contains(marketCategoryId)) {
                if (params.length >= 2 && null != params[1] && null != params[2]) {
                    return marketCategoryId * 1000 + (Long.parseLong(params[1]) * 100) + Long.parseLong(params[2]);
                }
                return marketCategoryId;
            } else if (ADD1_ADD2.contains(marketCategoryId)) {
                if (params.length >= 2 && null != params[0] && null != params[1]) {
                    return marketCategoryId * 10000 + (Long.parseLong(params[0]) * 100) + Long.parseLong(params[1]);
                }
                return marketCategoryId;
            } else if (CATEGORY_700_ADD3.contains(marketCategoryId)) {
                if (params.length >= 3 && null != params[2]) {
                    return marketCategoryId * 100 + (Long.parseLong(params[2]) / 15);
                }
                return marketCategoryId;
            } else if (CATEGORY_700_MARKETID.contains(marketCategoryId)) {
                if (params.length >= 6 && null != params[0] && null != params[1] && null != params[5]) {
                    StringBuffer redisKey = new StringBuffer("Ronghe:StandardMarket:RelationMarketId:");
                    redisKey.append(params[5])
                            .append("_").append(marketCategoryId)
                            .append("_")
                            .append(params[1].replace(".0", ""));
                    return MD5Utils.getLongByMD5(redisKey.toString());
                }
                return marketCategoryId;
            } else if (FIFTEEN_MINUTES_CATEGORY_ADD5.contains(marketCategoryId)) {
                if (StringUtils.isNotBlank(params[4])) {
                    String[] b = params[4].split(",");
                    if (b.length == 2) {
                        return marketCategoryId * 100 + (Long.parseLong(b[1]) / 15);
                    }
                }
                return marketCategoryId;
            } else {
                return marketCategoryId;
            }
        } catch (Exception e) {
            log.info("::{}::生成子玩法异常,玩法ID:{},异常", linkId, marketCategoryId, e);
        }
        return marketCategoryId;
    }
}
