package com.panda.merge.constant;

import com.google.common.collect.Lists;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;

import java.util.*;

/**
 * @author :  Jimmy
 * @Project Name :  panda_data_realtime_marketodds
 * @Package Name :  com.panda.sport.data.realtime.service.autodiff.config
 * @Description :  TODO
 * @Date: 2020-01-22 17:03
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class MarginCategoryConfig {

    //篮球修改addtion2盘口值玩法集合
    public static List<Long> BASKETBALL_ADDTION2_CATEGORY = Arrays.asList(4L, 19L, 143L);

    //足球EU计算
    public static List<Long> FootBall_EU_CATEGORY = Arrays.asList(1L, 3L, 5L, 6L, 16L, 17L, 25L, 27L, 28L, 29L, 30L, 32L, 43L, 69L, 70L, 71L, 72L, 85L, 95L, 111L, 112L, 117L, 119L, 120L, 125L, 126L, 129L, 142L, 149L, 224L, 225L, 228L, 230L, 231L, 235L, 237L, 310L, 311L, 326L, 329L, 333L, 352L, 354L, 355L, 356L, 357L, 370L, 385L, 387L, 389L, 391L, 392L, 394L, 395L, 396L, 397L, 398L,
            1100400L, 1100401L, 1100405L, 1100408L, 1100412L, 1100413L, 1100415L, 1100419L, 1100429L, 1100434L, 1100435L, 1100437L, 1100444L,
            1100478L, 1100479L, 1100482L, 1100446L, 1100447L, 1100448L, 1100449L, 1100450L, 1100454L, 1100455L, 1100459L);
    //足球MY计算
    public static List<Long> FootBall_MY_CATEGORY = Arrays.asList(2L, 4L, 10L, 11L, 12L, 15L, 18L, 19L, 24L, 26L, 33L, 34L, 42L, 75L, 76L, 77L, 78L, 79L, 80L, 81L, 82L, 83L, 84L, 86L, 87L, 88L, 89L, 90L, 91L, 92L, 93L, 94L, 96L, 97L, 98L, 99L, 100L, 109L, 110L, 113L, 114L, 115L, 116L, 118L, 121L, 122L, 123L, 124L, 127L, 128L, 130L, 131L, 132L, 133L, 134L, 135L, 136L, 138L, 139L, 140L, 143L, 144L, 229L, 232L, 233L, 234L, 240L, 269L, 270L, 306L, 307L, 308L, 309L, 312L, 313L, 314L, 315L, 316L, 317L, 324L, 325L, 327L, 328L, 330L, 331L, 332L, 334L, 335L, 336L, 371L, 372L, 373L, 374L, 375L, 376L, 377L, 378L, 381L, 382L, 393L,
            1100402L, 1100403L, 1100404L, 1100406L, 1100407L, 1100409L, 1100410L, 1100411L, 1100414L, 1100416L, 1100417L, 1100418L, 1100436L, 1100438L, 1100439L, 1100440L, 1100441L, 1100442L, 1100443L, 1100445L,
            1100471L, 1100472L, 1100473L, 1100474L, 1100475L, 1100476L, 1100477L, 1100480L,1100451L,1100456L,1100457L,1100458L,1100460L,1100461L);

    //篮球EU计算
    public static List<Long> BASKETBALL_EU_CATEGORY = Arrays.asList(3L,5L, 17L,25L,37L, 43L,44L, 48L,50L, 54L,56L, 60L, 62L,66L, 142L, 147L, 201L, 214L, 215L,217L,218L, 385L, 387L, 389L, 391L, 392L, 394L, 395L, 396L, 397L, 398L, 49L, 55L, 61L, 67L, 200L, 41L, 401L, 402L, 403L, 404L, 405L, 406L, 407L, 3100409L);
    //篮球spread计算
    public static List<Long> BASKETBALL_MY_CATEGORY = Arrays.asList(2L,4L,10L,11L, 15L, 18L, 19L, 26L, 38L, 39L, 40L, 42L, 45L, 46L, 47L, 51L, 52L, 53L, 57L, 58L, 59L, 63L, 64L, 65L, 75L, 87L, 88L, 97L, 98L, 143L, 145L, 146L, 198L, 199L, 220L, 221L, 271L, 272L, 393L,
            3100414L, 3100415L, 3100430L, 3100431L, 3100432L, 3100433L, 3100434L, 3100435L, 3100436L, 3100437L, 3100438L, 3100439L, 3100440L, 3100441L, 3100442L, 3100443L, 3100444L, 3100445L, 3100446L, 3100447L, 3100448L, 3100449L, 3100450L, 3100451L, 3100452L, 3100453L, 3100474L, 3100475L, 3100476L, 3100477L, 3100478L, 3100479L, 3100480L, 3100481L, 3100467L, 3100468L);

    //棒球EU计算
    public static List<Long> BASEBALL_EU_CATEGORY = Arrays.asList(242L, 247L, 248L, 273L, 275L, 276L, 277L, 279L, 280L, 281L, 282L, 283L, 287L, 288L, 289L,4200293L,4200294L);
    //棒球MY计算
    public static List<Long> BASEBALL_MY_CATEGORY = Arrays.asList(243L, 244L, 245L, 246L, 249L, 250L, 251L, 252L, 274L, 278L, 284L, 285L, 286L, 290L, 291L, 292L,4200295L,4200296L,4200297L,4200298L,4200299L,4200300L);

    //网球独赢EU计算
    public static List<Long> TENNIS_EU_CATEGORY = Arrays.asList(153L, 162L, 168L);
    //网球MY两项盘
    public static List<Long> TENNIS_MY_CATEGORY = Arrays.asList(154L, 155L, 202L, 163L, 164L, 165L);

    //排球EU计算
    public static List<Long> VOLLEYBALL_EU_CATEGORY = Arrays.asList(153L, 159L, 162L);
    //排球MY计算
    public static List<Long> VOLLEYBALL_MY_CATEGORY = Arrays.asList(172L, 173L, 253L, 254L, 255L, 256L);

    //斯诺克EU计算
    public static List<Long> SNOOKER_EU_CATEGORY = Arrays.asList(1L, 153L, 184L);
    //斯诺克MY计算
    public static List<Long> SNOOKER_MY_CATEGORY = Arrays.asList(180L, 181L, 182L, 183L, 185L, 186L, 187L, 188L, 189L, 191L, 192L, 193L, 194L, 195L, 196L);

    //乒乓球/羽毛球EU计算
    public static List<Long> TABLETENNIS_AND_BADMINTON_EU_CATEGORY = Arrays.asList(153L, 174L, 175L);
    //乒乓球/羽毛球MY计算
    public static List<Long> TABLETENNIS_AND_BADMINTON_MY_CATEGORY = Arrays.asList(172L, 173L, 176L, 177L, 178L, 179L, 203L);

    //美式足球EU计算
    public static List<Long> AMERICAN_FOOTBALL_EU_CATEGORY = Arrays.asList(17L, 37L,41L, 44L, 50L, 56L, 62L);
    //美式足球MY计算
    public static List<Long> AMERICAN_FOOTBALL_MY_CATEGORY = Arrays.asList(18L, 19L, 38L, 39L, 40L, 42L, 45L, 46L, 51L, 52L, 57L, 58L, 63L, 64L, 87L, 97L, 198L, 199L, 305L);
    //美式足球多投注项
    public static List<Long> AMERICAN_FOOTBALL_MORE_CATEGORY = Arrays.asList(213L);

    // ------------------- 综合球种 start ----------------------
    //冰球EU计算
    public static List<Long> ICEBALL_EU_CATEGORY = Arrays.asList(1L, 3L, 5L, 6L, 12L, 28L, 41L, 149L, 259L, 261L, 266L);
    //冰球MY计算
    public static List<Long> ICEBALL_MY_CATEGORY = Arrays.asList(2L, 4L, 15L, 257L, 258L, 262L, 263L, 264L, 268L, 294L, 295L);
    //冰球多投注项
    public static List<Long> ICEBALL_MORE_CATEGORY = Arrays.asList(8L, 9L, 14L, 204L, 260L, 265L, 267L, 296L, 297L, 298L);


    //手球
    public static List<Long> HANDBALL_EU_CATEGORY = Arrays.asList(1L, 6L, 17L, 70L, 259L);
    public static List<Long> HANDBALL_MY_CATEGORY = Arrays.asList(2L, 4L, 5L, 15L, 18L, 19L, 42L, 43L, 127L, 128L);
    //沙滩排球
    public static List<Long> BEACH_VOLLEYBALL_EU_CATEGORY = Arrays.asList(153L, 159L, 162L);
    public static List<Long> BEACH_VOLLEYBALL_MY_CATEGORY = Arrays.asList(172L, 173L, 253L, 254L, 255L, 256L);

    //拳击
    public static List<Long> BOXING_EU_CATEGORY = Arrays.asList(153L, 338L);
    public static List<Long> BOXING_MY_CATEGORY = Arrays.asList(2L);
    public static List<Long> BOXING_MORE_CATEGORY = Arrays.asList(337L, 339L);

    //橄榄球
    public static List<Long> UK_FOOTBALL_EU_CATEGORY = Arrays.asList(1L, 3L, 6L, 16L, 17L, 69L, 70L, 126L);
    public static List<Long> UK_FOOTBALL_MY_CATEGORY = Arrays.asList(2L, 4L, 5L, 10L, 11L, 15L, 18L, 19L, 42L, 43L, 87L, 97L, 135L, 136L);
    public static List<Long> UK_FOOTBALL_MORE_CATEGORY = Arrays.asList(141L, 218L);

    //曲棍球
    public static List<Long> HOCKEY_EU_CATEGORY = Arrays.asList(1L, 3L, 6L, 17L, 28L, 48L, 44L, 50L, 54L, 56L, 60L, 62L, 66L, 69L, 70L);
    public static List<Long> HOCKEY_MY_CATEGORY = Arrays.asList(2L, 4L, 5L, 10L, 11L, 12L, 15L, 18L, 19L, 24L, 42L, 43L, 45L, 46L, 47L, 51L, 52L, 53L, 57L, 58L, 59L, 63L, 64L, 65L, 79L, 81L, 87L, 97L, 145L, 146L);
    public static List<Long> HOCKEY_MORE_CATEGORY = Arrays.asList(104L, 213L, 223L);

    //水球
    public static List<Long> WATER_BALL_EU_CATEGORY = Arrays.asList(1L, 17L, 44L, 50L, 56L, 62L, 259L);
    public static List<Long> WATER_BALL_MY_CATEGORY = Arrays.asList(2L, 4L, 19L, 45L, 46L, 47L, 51L, 52L, 53L, 57L, 58L, 59L, 63L, 64L, 65L);

    //板球
    public static List<Long> CRICKET_EU_CATEGORY = Arrays.asList(3700001L, 3700002L);
    public static List<Long> CRICKET_MY_CATEGORY = Arrays.asList(3700003L, 3700004L, 3700005L, 3700006L, 3700007L, 3700008L, 3700009L, 3700010L, 3700011L, 3700012L, 3700013L, 3700014L, 3700015L, 3700016L, 3700017L, 3700018L);


    //综合球种EU玩法 赔率校验
    public static Set<Long> COMPLEX_EU_CATEGORY_ODDS_VERIFY = new HashSet<Long>() {{
        //综合球种多项盘赔率校验
        addAll(Arrays.asList(190L, 197L, 204L));
        addAll(ICEBALL_EU_CATEGORY);
        addAll(AMERICAN_FOOTBALL_EU_CATEGORY);
        addAll(ICEBALL_MORE_CATEGORY);
        addAll(AMERICAN_FOOTBALL_MORE_CATEGORY);
        addAll(HANDBALL_EU_CATEGORY);
        addAll(BEACH_VOLLEYBALL_EU_CATEGORY);

        addAll(BOXING_EU_CATEGORY);
        addAll(BOXING_MORE_CATEGORY);
        addAll(UK_FOOTBALL_EU_CATEGORY);
        addAll(UK_FOOTBALL_MORE_CATEGORY);
        addAll(HOCKEY_EU_CATEGORY);
        addAll(HOCKEY_MORE_CATEGORY);
        addAll(WATER_BALL_EU_CATEGORY);
        addAll(CRICKET_EU_CATEGORY);
    }};

    /**
     * 赛种对应my计算玩法
     */
    public static Map<Long, List<Long>> SPORT_MY_CATEGORY = new HashMap() {{
        put(1L, MarginCategoryConfig.FootBall_MY_CATEGORY);//足球
        put(2L, MarginCategoryConfig.BASKETBALL_MY_CATEGORY);//篮球
        put(3L, MarginCategoryConfig.BASEBALL_MY_CATEGORY);//棒球
        put(4L, MarginCategoryConfig.ICEBALL_MY_CATEGORY);//冰球
        put(5L, MarginCategoryConfig.TENNIS_MY_CATEGORY);//网球
        put(6L, MarginCategoryConfig.AMERICAN_FOOTBALL_MY_CATEGORY);//美式足球
        put(7L, MarginCategoryConfig.SNOOKER_MY_CATEGORY);//斯诺克
        put(9L, MarginCategoryConfig.VOLLEYBALL_MY_CATEGORY);//排球
        put(8L, MarginCategoryConfig.TABLETENNIS_AND_BADMINTON_MY_CATEGORY);//乒乓球
        put(10L, MarginCategoryConfig.TABLETENNIS_AND_BADMINTON_MY_CATEGORY);//羽毛球
        put(11L, MarginCategoryConfig.HANDBALL_MY_CATEGORY);//手球
        put(12L, MarginCategoryConfig.BOXING_MY_CATEGORY);//拳击
        put(13L, MarginCategoryConfig.BEACH_VOLLEYBALL_MY_CATEGORY);//沙滩排球
        put(14L, MarginCategoryConfig.UK_FOOTBALL_MY_CATEGORY);//橄榄球
        put(15L, MarginCategoryConfig.HOCKEY_MY_CATEGORY);//曲棍球
        put(16L, MarginCategoryConfig.WATER_BALL_MY_CATEGORY);//水球
        put(37L, MarginCategoryConfig.CRICKET_MY_CATEGORY);//板球
    }};


    //综合球种MY玩法 赔率校验
    public static Set<Long> COMPLEX_MY_CATEGORY_ODDS_VERIFY = new HashSet<Long>() {{
        addAll(ICEBALL_MY_CATEGORY);
        addAll(HANDBALL_MY_CATEGORY);
        addAll(BEACH_VOLLEYBALL_MY_CATEGORY);

        addAll(BOXING_MY_CATEGORY);
        addAll(UK_FOOTBALL_MY_CATEGORY);
        addAll(HOCKEY_MY_CATEGORY);
        addAll(WATER_BALL_MY_CATEGORY);
        addAll(CRICKET_MY_CATEGORY);

    }};
    // ------------------- 综合球种 end ----------------------

    //操盘球种MY玩法，赔率优化
    public static Set<Long> MY_ODDS_GRACEFUL_CATEGORY = new HashSet<Long>() {{
        addAll(FootBall_MY_CATEGORY);
        addAll(BASKETBALL_MY_CATEGORY);
        addAll(BASEBALL_MY_CATEGORY);
        addAll(TENNIS_MY_CATEGORY);
        addAll(TABLETENNIS_AND_BADMINTON_MY_CATEGORY);
        addAll(VOLLEYBALL_MY_CATEGORY);
        addAll(SNOOKER_MY_CATEGORY);
        addAll(AMERICAN_FOOTBALL_MY_CATEGORY);
        addAll(CRICKET_MY_CATEGORY);
    }};

    //赔率合法性校验的玩法
    public static Set<Long> EUROPE_MY_MARGIN_CATEGORY = new HashSet<Long>() {{
        addAll(FootBall_EU_CATEGORY);
        addAll(FootBall_MY_CATEGORY);
        addAll(BASKETBALL_EU_CATEGORY);
        addAll(BASKETBALL_MY_CATEGORY);
        addAll(BASEBALL_EU_CATEGORY);
        addAll(BASEBALL_MY_CATEGORY);
        addAll(TENNIS_EU_CATEGORY);
        addAll(TENNIS_MY_CATEGORY);
        addAll(TABLETENNIS_AND_BADMINTON_EU_CATEGORY);
        addAll(TABLETENNIS_AND_BADMINTON_MY_CATEGORY);
        addAll(VOLLEYBALL_EU_CATEGORY);
        addAll(VOLLEYBALL_MY_CATEGORY);
        addAll(SNOOKER_EU_CATEGORY);
        addAll(SNOOKER_MY_CATEGORY);
        addAll(AMERICAN_FOOTBALL_EU_CATEGORY);
        addAll(AMERICAN_FOOTBALL_MY_CATEGORY);
        addAll(ICEBALL_EU_CATEGORY);
        addAll(ICEBALL_MY_CATEGORY);
        addAll(CRICKET_EU_CATEGORY);
        addAll(CRICKET_MY_CATEGORY);
    }};

    //三方冠军玩法
    public static String[] THIRD_OUTRIGHT_CATEGORY = {"BC:12027", "BC:12029", "BC:12030", "BC:12032", "BC:12033",
            "BC:12057", "BC:9508", "BC:9782", "BC:9817", "BC:9857", "SR:534_1", "PA:534_1", "SR:534_2", "SR:534_3",
            "SR:534_4", "SR:534_5", "SR:534_6", "SR:535_1", "SR:535_2", "SR:535_3", "SR:536_1", "SR:536_2", "SR:536_3",
            "SR:536_4", "SR:536_5", "SR:536_6", "SR:536_7", "SR:536_8", "SR:535_4", "SR:559_1", "F01:534_1",
            "F01:534_2", "F01:534_10", "F01:534_11", "F01:534_12", "F01:534_13", "F01:534_18", "F01:534_19",
            "F01:534_20", "F01:534_21", "F01:534_22", "F01:535_1", "F01:535_13", "F01:535_15", "F01:535_16",
            "F01:535_17", "F01:536_3", "F01:536_4", "F01:536_5", "F01:536_6", "F01:536_7", "F01:536_8", "F01:536_9",
            "F01:536_14", "F01:559_1"};

    //标准冠军玩法
    public static List<Long> STANDARD_OUTRIGHT_CATEGORY = Arrays.asList(10001L, 10002L, 10003L, 10004L, 10005L,
            10006L, 10007L, 10008L, 10009L, 10010L, 10011L, 10012L, 10013L, 10014L, 10015L, 10016L, 10017L,10018L,
            10019L,10020L,10021L,10022L);

    //需要特殊排序的玩法 玩法+addtion2
    public static List<Long> SPEAICL_ORDER_CATEGORY_ADDTION2 = Arrays.asList(145L,146L,162L,163L,164L,165L,166L,170L,175L,176L,177L,178L,
            184L,185L,186L,187L,189L,190L,191L,192L,193L,194L,196L,197L,253L,254L,262L,263L,264L,268L);
    //需要特殊排序的玩法 玩法+addtion1
    public static List<Long> SPEAICL_ORDER_CATEGORY_ADDTION1 = Arrays.asList(148L,201L,208L,214L,222L,224L,225L,230L,235L,237L,255L,261L,265L,266L,267L);
    //需要按照两个附加字段同时分组排序的玩法
    public static List<Long> SPEAICL_ORDER_CATEGORY_ADDTION12 = Arrays.asList(147L,167L,168L,179L,188L,195L,203L,215L,256L);

    /**
     * 新的排序规则：特殊玩法存在按照附加字段排序
     * 1.按照附加字段2，附加字段1排序
     * 2.按照附加字段1排序
     * 3.按照附加字段2排序
     * 4.按照附加字段1，附加字段2排序
     */
    public static List<Long> ORDER_BY_ADDTION1 = Arrays.asList(148L,201L,208L,214L,222L,224L,225L,230L,237L,255L,261L,265L,266L,267L);
    public static List<Long> ORDER_BY_ADDTION2 = Arrays.asList(145L,146L,162L,165L,166L,170L,175L,178L,184L,187L,189L,190L,191L,192L,193L,194L,196L,197L);
    public static List<Long> ORDER_BY_ADDTION12 = Arrays.asList(203L,215L,256L);
    public static List<Long> ORDER_BY_ADDTION21 = Arrays.asList(147L,163L,164L,167L,168L,176L,177L,179L,185L,186L,188L,195L,253L,254L,262L,263L,264L,268L);

    //全场大小，半场大小，全场让球，半场让球这四个玩法需要支持操盘手盘口级开启或弃用
    public static List<Long> OPEN_CLOSE_CATEGORY = Arrays.asList(2L,4L,18L,19L);

    //没有附加盘，不考虑最大盘口数
    public static List<Long> GREEN_MATEGORY = Arrays.asList(28L,30L,31L,32L,109L,110L,120L,133L,147L,148L,162L,165L,166L,167L,168L,170L,175L,178L,179L,184L,
            187L,188L,189L,190L,191L,192L,193L,194L,195L,196L,197L,201L,203L,208L,214L,215L,222L,224L,225L,231L,235L,237L,255L,256L,261L,265L,266L,267L);
    //玩法名称里面带盘或者局占位符的玩法
    public static List<Long> YELLOW_MATEGORY_ADDTION21 = Arrays.asList(145L,146L,163L,164L,176L,177L,185L,186L,253L,254L,262L,263L,264L,268L);

    //需要传坑位的玩法
    public static List<Long> NEED_PLACENUM_MATEGORY = Arrays.asList(2L,3L,4L,10L,11L,13L,18L,19L,26L,33L,34L,38L,39L,45L,46L,51L,52L,57L,58L,63L,
            64L,69L,71L,87L,88L,97L,98L,109L,110L,113L,114L,115L,116L,121L,122L,123L,124L,127L,128L,130L,
            134L,143L,145L,146L,154L,155L,156L,157L,163L,164L,169L,171L,172L,173L,176L,177L,181L,182L,
            185L,186L,198L,199L,202L,216L,218L,232L,233L,243L,244L,245L,246L,249L,250L,251L,252L,253L,
            254L,257L,258L,262L,263L,264L,268L,269L,270L);

    public static List<Long> TX_NEED_HANDLER = Arrays.asList(4L,19L,128L,143L);


    /**
     * 篮球 全场让球玩法
     */
    public static Long HANDICAP_CATEGORY = 39L;

    //-------------------- A/A+ 模式原始球头 start ------------------------
    /**
     * 篮球让分玩法 附加字段 5 赋值 附加字段1
     */
    public static List<Long> HANDICAP_CATEGORY_SUBSECTION = Arrays.asList(39L, 19L, 46L, 52L, 58L, 64L, 143L);
    /**
     * 篮球大小玩法/球员玩法/主客队总分玩法/第{X}节{主队/客队}总分/上半场{主队/客队} 附加字段 5 赋值 附加字段1
     */
    public static List<Long> BASKETBALL_CATEGORY_ORIGINAL_BALL_HEAD = Arrays.asList(38L, 18L, 45L, 51L, 57L, 63L, 26L, 198L, 199L, 145L, 146L, 87L, 97L, 220L, 221L, 271L, 272L);
    /**
     * 棒球 附加字段 5 赋值 附加字段1原始球头
     */
    public static List<Long> BASEBALL_CATEGORY_ORIGINAL_BALL_HEAD = Arrays.asList(243L,244L,245L,246L,249L,250L,251L,252L,274L,276L,278L,280L,281L,282L,284L,285L,286L,287L,288L,289L,290L,291L,292L);
    /**
     * 网球 附加字段 5 赋值 附加字段1原始球头
     */
    public static List<Long> TENNIS_CATEGORY_ORIGINAL_BALL_HEAD = Arrays.asList(154L, 155L, 202L, 163L, 164L, 156L, 157L, 169L);
    /**
     * 乒乓球 附加字段 5 赋值 附加字段1原始球头
     */
    public static List<Long> TABLETENNIS_CATEGORY_ORIGINAL_BALL_HEAD = Arrays.asList(172L, 173L, 176L, 177L);
    /**
     * 排球 附加字段 5 赋值 附加字段1原始球头
     */
    public static List<Long> VOLLEYBALL_CATEGORY_ORIGINAL_BALL_HEAD = Arrays.asList(253L, 254L);
    /**
     * 斯诺克 附加字段 5 赋值 附加字段1原始球头
     */
    public static List<Long> SNOOKER_CATEGORY_ORIGINAL_BALL_HEAD = Arrays.asList(181L, 182L, 185L, 186L);
    /**
     * 下发原始球头玩法 附加字段1 集合
     */
    public static Set<Long> CATEGORY_ORIGINAL_BALL = new HashSet<Long>() {{
        addAll(HANDICAP_CATEGORY_SUBSECTION);
        addAll(BASKETBALL_CATEGORY_ORIGINAL_BALL_HEAD);
        addAll(BASEBALL_CATEGORY_ORIGINAL_BALL_HEAD);
        addAll(TENNIS_CATEGORY_ORIGINAL_BALL_HEAD);
        addAll(TABLETENNIS_CATEGORY_ORIGINAL_BALL_HEAD);
        addAll(VOLLEYBALL_CATEGORY_ORIGINAL_BALL_HEAD);
        addAll(SNOOKER_CATEGORY_ORIGINAL_BALL_HEAD);
    }};

    //-------------------- end ------------------------
    /**
     * 篮球/棒球/网球 全场让球玩法 球头特殊值(0,+-0.5)处理
     */
    public static List<String> HANDICAP_MARKET_DISPOSE = Arrays.asList("0", "0.5", "-0.5");
    /**
     * 平手盘
     */
    public static List<String> FLAT_HANDICAP_DISPOSE = Arrays.asList("0");
    /**
     * Map<让球玩法盘口存在特殊值, 封盘玩法>
     */
    public static Map<Long, Long> HANDICAP_WINNER_MAP = new HashMap() {{
        put(39L, 37L);
        put(19L, 43L);
        put(46L, 48L);
        put(52L, 54L);
        put(58L, 60L);
        put(64L, 66L);
        put(143L, 142L);
        put(4L, 1L);
    }};

    /**
     * 篮球独赢赔率校验玩法集合
     */
    public static Map<Long, Long> HANDICAP_WINNER_MAP_BASKET = new HashMap() {{
        put(39L, 37L);
        put(19L, 43L);
        put(46L, 48L);
        put(52L, 54L);
        put(58L, 60L);
        put(64L, 66L);
        put(143L, 142L);
    }};
    public static Map<Long, Long> HANDICAP_WINNER_MAP_BASKET_2 = new HashMap() {{
        put(37L,39L);
        put(43L,19L);
        put(48L,46L);
        put(54L,52L);
        put(60L,58L);
        put(66L,64L);
        put(142L,143L);
    }};
    //让分
    public static List<Long> HANDICAP_WINNER_LIST_BASKET_1 =
            Arrays.asList(39L, 19L,
                    46L,  52L,
                    58L, 64L, 143L);
    //独赢
    public static List<Long> HANDICAP_WINNER_LIST_BASKET_2 =
            Arrays.asList(37L, 43L,
                    48L, 54L,
                    60L, 66L,142L);
    public static List<Long> HANDICAP_WINNER_LIST_BASKET =
            Arrays.asList(39L, 37L, 19L, 43L,
                    46L, 48L, 52L, 54L,
                    58L, 60L, 64L, 66L,143L, 142L);


    /**
     * 篮球球员玩法
     */
    public static List<Long> BASKETBALL_PLAYER_CATEGORY = Arrays.asList(220L, 221L,271L,272L);

    /**
     * 球员玩法集合,球员id为投注项附件字段
     */
    public static List<Long> PLAYER_CATEGORY_ODDS = Arrays.asList(35L, 36L, 148L, 150L, 151L, 152L, 363L, 364L, 365L, 366L,3700019L,3700020L,3700021L,3700022L);

    /**
     * 数据源变动挡板，篮球
     */
    public static List<Long> CHANGE_FLAP = Arrays.asList(5L,37L,48L,54L,60L,66L,142L,214L,201L,215L);

    /**
     * 滚球拿掉数据商挡板限制
     */
    public static List<Long> CHANGE_FLAP_BAK = Arrays.asList(215L,214L,201L);

    /**
     * AO需要排序的玩法
     */
    public static List<Long> CHANGE_AO_FLAP_BAK1 = Arrays.asList(3100430L, 3100431L, 3100432L, 3100433L, 3100434L, 3100435L, 3100436L, 3100437L, 3100438L, 3100439L, 3100440L, 3100441L, 3100442L, 3100443L, 3100444L, 3100445L, 3100446L, 3100447L, 3100448L, 3100449L, 3100450L, 3100451L, 3100452L, 3100453L);
    /**
     * 数据源变动挡板，大小让分玩法
     */
    public static List<Long> CHANGE_FLAP1 = Arrays.asList(18L, 19L, 26L, 38L, 39L, 45L, 46L, 51L, 52L, 57L, 58L, 63L,
            64L, 143L, 154L, 155L, 172L, 173L, 202L, 181L, 182L, 243L, 244L, 245L, 246L);
    /**
     * 数据源变动挡板，总分类副盘1大
     */
    public static List<Long> CHANGE_FLAP2 = Arrays.asList(18L, 26L, 38L, 45L, 51L, 57L, 63L, 173L, 202L, 182L, 244L, 245L, 246L);
    /**
     * 数据源变动挡板，让分类副盘1小
     */
    public static List<Long> CHANGE_FLAP3 = Arrays.asList(19L, 39L, 46L, 52L, 58L, 64L, 143L, 154L, 155L, 172L, 181L, 243L);
    /**
     *  数据源变动挡板，篮球，球头变化值
     */
    public static Double BASKETBALL_FLAP_ADDTION1_DOUBLE = 5.0D;
    /**
     *  数据源变动挡板，篮球，单次赔率变化值
     */
    public static Double BASKETBALL_FLAP_ODDSVALUE_DOUBLE = 0.3*100000;

    /**
     * TX足球 常规让球玩法
     * 比分取比分中心数据 主队比分：附加字段3 、 客队比分：附加字段4
     */
    public static List<Long> FOOTBALL_SCORE_CATEGORY = Arrays.asList(4L, 19L, 143L);//33L,
    /**
     * TX足球 加时赛比分
     * 比分取比分中心数据 主队比分：附加字段3 、 客队比分：附加字段4
     */
    public static List<Long> FOOTBALL_OVERTIME_SCORE_CATEGORY = Arrays.asList(128L, 130L);
    /**
     * TX足球 点球大战比分
     * 比分取比分中心数据 主队比分：附加字段3 、 客队比分：附加字段4
     */
    public static List<Long> FOOTBALL_PENALTY_SCORE_CATEGORY = Arrays.asList(334L);

    /**
     * 角球玩法 比分处理
     */
    public static List<Long> FOOTBALL_CORNER_SCORE_CATEGORY = Arrays.asList(113L, 121L);//232L
    /**
     * 罚牌 比分处理
     */
    public static List<Long> FOOTBALL_RAD_SCORE_CATEGORY = Arrays.asList(306L, 308L);
    /**
     * 黄牌 比分处理
     */
    public static List<Long> FOOTBALL_YELLOW_SCORE_CATEGORY = Arrays.asList(324L, 327L);
    /**
     * 15分钟 常规让球/角球让球
     */
    public static Map<Long, String> FIFTEEN_MINUTES_SCORE = new HashMap<Long, String>() {{
        put(33L, "goal");
        put(232L, "corner");
    }};

    /**
     * 足球玩法报警 一分钟
     * 【常规赛】【加时赛】【角球】【点球大作战】【前四】
     */
    public static List<Long> MATCH_CATEGORY_ODDS_WARNING = Arrays.asList(1L, 4L, 2L, 17L, 19L, 18L, 111L, 113L, 114L, 119L, 121L, 122L, 126L, 128L, 127L, 129L, 130L, 132L, 134L, 133L, 240L, 10004L);
    /**
     * 足球玩法报警 2分钟
     * 【常规赛】【加时赛】【角球】 独赢玩法 、上半场独赢
     */
    public static List<Long> FIVE_MATCH_CATEGORY_ODDS_WARNING = Arrays.asList(1L, 17L, 126L, 129L, 119L, 111L);
    /**
     * 全场独赢  ，上半场独赢 ，全场大小  全场让球，上半场大小，上半场让球 2分钟没更新数据商关
     */
    public static List<Long> TWO_NO_UPDATE = Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L);

    /**
     * 足球球员玩法集,投注项类型集合
     */
    public static List<String> PLAYER_CATEGORY_ODDS_TYPE = Arrays.asList("None","OwnGoal","Other","没有进球","乌龙球","其他","no goal","own goal","other");

    /**
     * 852需求 大于三项（走的数据源抽水赔） 清除概率差
     */
    public static List<Long> THREE_CATEGORY = Arrays.asList(7L, 8L, 9L, 13L, 14L, 20L, 21L, 22L, 23L, 31L, 35L, 36L, 68L, 73L, 74L, 101L, 102L, 103L, 104L, 105L, 106L, 107L, 108L, 117L, 137L, 141L, 148L, 150L, 151L, 152L, 159L, 161L, 166L, 170L, 171L, 174L, 190L, 197L, 204L,
            209L, 210L, 211L, 212L, 213L, 216L, 218L, 219L, 220L, 221L, 222L, 223L, 226L, 227L, 236L, 238L, 239L, 241L, 260L, 265L, 267L, 271L, 272L, 296L, 297L, 298L, 318L, 319L, 320L, 321L, 322L, 323L, 337L, 339L, 361L, 362L, 363L, 364L, 365L, 366L, 367L, 368L, 369L, 379L, 380L, 209L, 210L, 211L, 212L, 216L, 219L, 213L, 384L, 386L, 400L, 3100408L, 1100452L, 1100453L);


    /**
     * margin优化 ：1,X,2 投注项玩法 ，X默认锚点，另外一个描点是1和2之间赔率较小者，如果1,2赔率相同，则是1
     */
    public static List<Long> PRESET_ODDS_TYPE_ANCHOR_CATEGORY = Arrays.asList(1L, 3L, 17L, 25L, 27L, 29L, 32L, 69L, 71L, 111L, 119L, 126L, 129L, 231L);
    /**
     * 投注项需要排序的玩法
     */
    public static List<Long> ODDS_ORDER = Arrays.asList(318L,319L,320L,321L,322L,323L,70L,72L,76L,337L);
    /**
     * 主客队获胜退款玩法计算获取上下盘处理
     */
    public static List<Long> SPECIAL_CATEGORY = Arrays.asList(77L, 91L);
    /**
     * margin优化计算，有玩法支持附加盘 ，需要做最大盘口数校验
     */
    public static List<Long> MARGIN_SPECIAL_CATEGORY = Arrays.asList(3L, 69L, 71L, 125L, 230L);

    /**
     * 信用等级玩法分类
     */
    public static List<Long> NORMAL_CATEGORY = Arrays.asList(2L,4L,10L,11L,12L,18L,19L,24L,26L,33L,34L,38L,39L,41L,45L,46L,51L,52L,57L,58L,63L,64L,
            76L,77L,79L,80L,81L,82L,83L,84L,86L,87L,88L,89L,90L,91L,93L,94L,96L,97L,98L,99L,100L,109L,110L,113L,114L,115L,116L,121L,122L,123L,124L,
            127L,128L,130L,131L,132L,135L,136L,143L,144L,145L,146L,154L,155L,156L,157L,158L,163L,164L,169L,172L,173L,176L,177L,180L,181L,182L,185L,
            186L,192L,193L,194L,198L,199L,202L,208L,215L,220L,221L,232L,233L,243L,244L,245L,246L,249L,250L,251L,252L,253L,254L,257L,258L,262L,263L,
            264L,266L,268L,271L,272L,274L,276L,278L,280L,281L,282L,284L,285L,286L,287L,288L,289L,290L,291L,292L,294L,295L,305L,306L,307L,308L,309L,
            314L,315L,316L,317L,324L,325L,327L,328L,331L,332L,335L,336L);
    public static List<Long> CATEGORY_50 = Arrays.asList(15L,40L,41L,42L,47L,53L,59L,65L,75L,78L,92L,118L,133L,134L,138L,139L,140L,
            160L,165L,178L,183L,187L,197L,229L,234L,240L,247L,255L,279L,312L,313L,330L,334L);
    public static List<Long> THREE_ODDS_CATEGORY = Arrays.asList(1L,3L,5L,6L,7L,8L,9L,13L,14L,16L,17L,20L,21L,22L,23L,25L,27L,28L,29L,30L,
            31L,32L,35L,36L,37L,43L,44L,48L,49L,50L,54L,55L,56L,60L,61L,62L,66L,67L,68L,69L,70L,71L,72L,73L,74L,85L,95L,101L,
            102L,103L,104L,105L,106L,107L,108L,111L,112L,117L,119L,120L,125L,126L,129L,137L,141L,142L,147L,148L,149L,150L,151L,152L,153L,
            159L,161L,162L,166L,167L,168L,170L,171L,174L,175L,179L,184L,188L,189L,190L,191L,195L,196L,200L,201L,203L,204L,205L,206L,207L,209L,
            210L,211L,212L,213L,214L,216L,217L,218L,219L,222L,223L,224L,225L,226L,227L,228L,230L,231L,235L,236L,237L,238L,239L,241L,242L,248L,
            256L,259L,260L,261L,265L,267L,269L,270L,273L,275L,277L,283L,296L,297L,298L,310L,311L,318L,319L,320L,321L,322L,323L,326L,329L,333L,
            10001L,10002L,10003L,10004L,10005L,10006L,10007L,10008L,10009L,10010L,10011L,10012L,10013L,10014L);
    /**
     * 球类
     */
    public static List<Long> SOPRT_TYPE = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 12L, 11L, 13L, 14L, 15L, 16L, 37L);

    /**
     * 支持操盘球种：足球、篮球、棒球、网球、乒乓球、排球、斯诺克
     * 1.MY计算，查询坑位水差跟玩法水差
     * 2.最大盘口数
     */
    public static List<Long> TRADER_SUPPORT_SPORT = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 37L);
    /**
     * 综合球种：棒球(去掉棒球)/冰球/美式足球/手球/沙滩排球
     * 1.联赛赔率校验
     * 2.多项盘赔率计算
     */
    public static List<Long> COMPLEX_SPORTIDS = Arrays.asList(11L, 12L, 13L, 14L, 15L, 16L);
    /**
     * 动态最大最小球头校验
     * 斯诺克、乒乓球、排球,棒球
     */
    public static Map<Long, List<Long>> DYNAMIC_SPORT = new HashMap<Long, List<Long>>() {{
        put(4L, Arrays.asList(2L, 4L, 262L, 268L));
        put(5L, Arrays.asList(154L, 155L, 202L, 163L, 164L));
        put(6L, Arrays.asList(39L, 38L, 198L, 199L, 87L, 97L, 19L, 18L));
        put(7L, Arrays.asList(181L, 182L, 185L, 186L));
        put(8L, Arrays.asList(172L, 173L, 176L, 177L));
        put(9L, Arrays.asList(172L, 173L, 253L, 254L));
        put(3L,Arrays.asList(243L,244L,245L,246L,249L,250L,251L,252L,274L,276L,278L,280L,281L,282L,284L,285L,286L,287L,288L,289L,290L,291L,292L));
        put(10L, Arrays.asList(172L, 173L, 176L, 177L));
    }};
    /**
     * 球头校验不需要取绝对值的玩法,总分类
     */
    public static List<Long> DYNAMIC_NO_ABS = Arrays.asList(2L,18L,38L,87L,97L,182L,186L,173L,177L,199L,254L,244L,245L,246L,250L,251L,252L,274L,276L,
            281L,282L,284L,285L,286L,287L,288L,289L,290L,291L,292L);
    /**
     * 需要校验的赔率优化球种
     * 1.操盘球种和综合球种，中的MY计算玩法不走赔率优化计算
     */
    public static Set<Long> ODDS_GRACEFUL_SPORT = new HashSet<Long>() {{
        addAll(TRADER_SUPPORT_SPORT);
        addAll(COMPLEX_SPORTIDS);
    }};

    /**
     * 支持操盘球种 +-盘口差
     * 球种：篮球、棒球、网球、乒乓球、排球、斯诺克
     * 查询坑位水差、玩法水差
     */
    public static List<Long> SPORT_HEAD = Arrays.asList(2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 6L, 37L);
    /**
     * +-盘口差 玩法
     * 支持操盘球种 盘口差玩法：篮球、棒球、网球、乒乓球、排球、斯诺克、美式足球
     */
    public static Set<Long> MARKET_CATEGORY_HEAD = new HashSet<Long>() {{
        addAll(BASKETBALL_MY_CATEGORY);
        addAll(BASEBALL_MY_CATEGORY);//新增棒球MY玩法
        addAll(TENNIS_MY_CATEGORY);
        addAll(TABLETENNIS_AND_BADMINTON_MY_CATEGORY);
        addAll(VOLLEYBALL_MY_CATEGORY);
        addAll(SNOOKER_MY_CATEGORY);
        addAll(ICEBALL_MY_CATEGORY);
        addAll(AMERICAN_FOOTBALL_MY_CATEGORY);
        addAll(CRICKET_MY_CATEGORY);
    }};

    /**
     * 篮球支持新需求盘口差玩法
     */
    public static List<Long> BASKETBALL_HEAD_CATEGORY = Arrays.asList(39L, 19L, 46L, 52L, 58L, 64L, 143L);

    /**
     * 赔率合法性校验 多项盘下发最大值赔率
     * 篮球，棒球，网球，乒乓球，排球、斯诺克
     */
    public static List<Long> VERIFY_SPORT = Arrays.asList(2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 6L, 37L);

    /**
     * 篮球 、网球、乒乓球、排球
     * 坑位水差球种 ，TX坑位排序球种
     * TX 排序
     * 坑位水差设置在坑位：1
     * 坑位：1关 坑位：2开  坑位：3开
     * 坑位：2 - > 1 用坑位1水差  坑位：3 - > 2
     */
    public static List<Long> TX_SORT_SPORT = Arrays.asList(2L, 4L, 5L, 8L, 9L, 10L, 6L);

    /**
     * 综合球种玩法 下盘 NO 玩法
     */
    public static List<Long> COMPLEX_SPORT_CATEGORY_ODDS_TYPE_NO = Arrays.asList(41L, 338L);

    //-------------------- 带{X}玩法，总分时margin修改子玩法margin start ------------------------
    /**
     * 700需求带{X}玩法，总分时margin修改子玩法margin
     */
    public static List<Long> CATEGORY_700_UPDATE_MARGIN = Arrays.asList(145L, 146L, 147L, 201L, 214L, 215L,
            336L, 28L, 30L, 109L, 110L, 34L, 32L, 33L, 31L, 222L, 148L, 233L, 225L, 120L, 125L, 230L, 231L, 232L, 224L, 235L, 133L, 237L, 363L, 364L, 365L, 366L, 1100478L, 1100482L);


    /**
     * 棒球带{x}玩法 ，总分时margin修改子玩法margin
     */
    public static List<Long> BASEBALL_CATEGORY_UPDATE_MARGIN = Arrays.asList(275L, 276L, 280L, 281L, 282L, 823L, 287L, 288L, 289L);

    /**
     * 网球带{x}玩法 ，总分时margin修改子玩法margin
     */
    public static List<Long> TENNIS_CATEGORY_UPDATE_MARGIN = Arrays.asList(162L, 163L, 164L, 165L, 166L, 170L, 208L, 167L, 168L);
    /**
     * 乒乓球带{x}玩法 ，总分时margin修改子玩法margin
     */
    public static List<Long> TABLETENNIS_CATEGORY_UPDATE_MARGIN = Arrays.asList(162L, 163L, 164L, 165L, 166L, 170L, 208L, 167L, 168L);
    /**
     * 排球带{x}玩法 ，总分时margin修改子玩法margin
     */
    public static List<Long> VOLLEYBALL_CATEGORY_UPDATE_MARGIN = Arrays.asList(162L, 253L, 254L, 255L, 256L);
    /**
     * 斯诺克带{x}玩法 ，总分时margin修改子玩法margin
     */
    public static List<Long> SNOOKER_CATEGORY_UPDATE_MARGIN = Arrays.asList(184L, 185L, 186L, 187L, 188L, 189L, 190L, 191L, 192L, 193L, 194L, 195L, 196L, 197L);
    /**
     * 羽毛球带{x}玩法 ，总分时margin修改子玩法margin
     */
    public static List<Long> BADMINTON_CATEGORY_UPDATE_MARGIN = Arrays.asList(175L, 176L, 177L, 178L, 179L, 203L);
    /**
     * 带{X}玩法，总分时margin修改子玩法margin
     */
    public static Set<Long> CATEGORY_X_UPDATE_MARGIN = new HashSet<Long>() {{
        addAll(CATEGORY_700_UPDATE_MARGIN);
        addAll(BASEBALL_CATEGORY_UPDATE_MARGIN);
        addAll(TENNIS_CATEGORY_UPDATE_MARGIN);
        addAll(TABLETENNIS_CATEGORY_UPDATE_MARGIN);
        addAll(VOLLEYBALL_CATEGORY_UPDATE_MARGIN);
        addAll(SNOOKER_CATEGORY_UPDATE_MARGIN);
        addAll(BADMINTON_CATEGORY_UPDATE_MARGIN);
    }};

    //-------------------- end ------------------------

    /**
     * 足球 赛前进入滚球无缝切换
     */
    public static List<Long> FootBall_PRE_LIVE_CATEGORY = Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L);

    /**
     * 篮球 赛前进入滚球无缝切换
     */
    public static List<Long> Basketball_PRE_LIVE_CATEGORY = Arrays.asList(37L,39L, 38L, 43L,19L, 18L,
            48L,46L, 45L,
            54L,52L,51L,60L,58L,57L,66L,64L,63L,142L,143L,26L);

    /**
     * 足球
     * 提前结算支持玩法
     */
    public static List<Long> PRE_STANDARD_CATEGORY = Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L, 6L, 7L, 10L, 11L, 12L, 14L, 15L, 341L, 34L, 68L,
            32L,33L,3L,5L,16L,43L,104L,340L,77L,91L,344L,8L,9L,24L,23L,87L,97L,70L,102L,101L,13L);
    /**
     * 足球 主列表玩法
     */
    public static List<Long> FootBall_MAIN_CATEGORY = Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L);

    /**
     * 3446/3447百家赔玩法
     */
    public static List<Long> FootBall_3446_3447_CATEGORY = Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L,
            111L,113L,114L,121L,122L,119L,
            306L,307L,310L,308L,309L,311L,
            126L,127L,128L,130L,332L,129L,
            1100414L,331L,1100413L,1100416L,1100417L,1100415L,
            1100406L,1100407L,1100405L,1100409L,1100410L,1100408L);
    public static List<Long> BasketBall_3446_3447_CATEGORY = Arrays.asList(37L,38L,39L,18L,19L,43L,46L,45L,48L,52L,57L,60L);

    /**
     * A0 足球 球头玩法
     * 常规比分：全场大小/全场让球/上半场大小/上半场让球
     * 常规角球：大小/让球/上半场大小/上半场让球
     * 常规罚牌：大小/让球/上半场大小/上半场让球
     * 常规加时赛：大小/让球/上半场大小/上半场让球
     * 加时赛角球大小
     *
     * 篮球
     * 全场让分
     * 全场总分
     */
    public static List<Long> BALL_HEAD_AO_CATEGORY = Arrays.asList(2L, 4L, 18L, 19L, 113L, 114L, 121L, 122L, 306L, 307L, 308L, 309L, 128L, 127L, 130L
            , 332L, 331L, 1100406L, 1100407L, 1100409L, 1100410L, 1100414L, 1100416L, 1100417L);
    public static List<Long> BASKETBALL_HEAD_AO_CATEGORY = Arrays.asList(37L, 38L, 39L, 48L, 46L, 45L, 60L, 58L, 57L, 43L, 19L, 18L);
    public static List<Long> TABLE_TENNIS_HEAD_AO_CATEGORY = Arrays.asList(153L);

    public static Map<Long,List<Long>> SPORT_HEAD_AO = new HashMap<Long, List<Long>>() {{
        put(StandardSportTypeEnum.FootBall.code,BALL_HEAD_AO_CATEGORY);
        put(StandardSportTypeEnum.Basketball.code,BASKETBALL_HEAD_AO_CATEGORY);
        put(StandardSportTypeEnum.TableTennis.code,TABLE_TENNIS_HEAD_AO_CATEGORY);
    }};
    /**
     * 数据源三方球头 初盘
     */
    public static List<Long> THIRD_FIRST_MARKET_BALL_HEAD_CATEGORY = Arrays.asList(2L, 4L, 127L, 128L, 37L, 38L, 39L);

    /**
     *  TX 逻辑球种
     */
    public static List<String> SPORT_TX_LOGIC = Arrays.asList(DataSourceCodeEnum.TX.code,DataSourceCodeEnum.AO.code);

    /**
     * 1852 足球增加开盘时间-封、关盘/接拒-2.0 支持的数据源
     */
    public static List<String> NO_CLOS_DATA_SOURCE_CODE = Arrays.asList(DataSourceCodeEnum.TX.code,DataSourceCodeEnum.SR.code,DataSourceCodeEnum.BG.code);
    /**
     * 1852 足球增加开盘时间-封、关盘/接拒-2.0 支持的赛种
     */
    public static List<Long> NO_CLOS_SPORT = Arrays.asList(1L);

    /**
     * 33011优化单
     */
    public static List<String> ADD1_VERIFY = Arrays.asList(DataSourceCodeEnum.BC.code,DataSourceCodeEnum.SR.code,DataSourceCodeEnum.BG.code,DataSourceCodeEnum.TX.code,
            DataSourceCodeEnum.LS.code);
    /**
     * 33011优化单
     */
    public static List<Long> ADD1_CATEGORY = Arrays.asList(2L,10L,11L,18L,26L,34L,87L,88L,97L,98L,127L,332L,134L,335L,114L,115L,116L,122L,123L,124L,233L,331L,
            307L,309L,314L,315L,316L,317L,325L,328L);
    /**
     * 1852上半场玩法
     */
    public static List<Long> NO_CLOS_CATEGORY_HT = Arrays.asList(17L, 18L, 19L, 119L, 121L, 122L, 308L, 309L, 311L);
    /**
     * 1852下半场玩法
     */
    public static List<Long> NO_CLOS_CATEGORY_FT = Arrays.asList(1L, 2L, 4L, 111L, 113L, 114L, 310L, 306L, 307L);
    /**
     * 1852 足球增加开盘时间-封、关盘/接拒-2.0 支持的玩法
     */
    public static List<Long> NO_CLOS_CATEGORY = new ArrayList<Long>() {{
        addAll(NO_CLOS_CATEGORY_HT);
        addAll(NO_CLOS_CATEGORY_FT);
    }};
    /**
     * 1852盘口值校验
     */
    public static List<Long> SCORE_CHECK_GOAL = Arrays.asList(2L, 18L);
    public static List<Long> SCORE_CHECK_CORNER = Arrays.asList(114L, 122L);
    public static List<Long> SCORE_CHECK_FACARD = Arrays.asList(307L, 309L);

    /**
     * 1852比分校验
     */
    public static List<Long> HANDICAP_VALUE_CHECK_GOAL = Arrays.asList(4L, 19L);
    public static List<Long> HANDICAP_VALUE_CHECK_CORNER = Arrays.asList(113L, 121L);
    public static List<Long> HANDICAP_VALUE_CHECK_FACARD = Arrays.asList(306L, 308L);
    /**
     * 35705 阶段强开逻辑优化
     */
    public static Map<Long, Long> MATCH_PERIOD_CATEGORY_OPEN = new HashMap<Long, Long>() {{
        //1	 全场独赢    17	半场独赢
        put(1L, 17L);
        //2	 全场大小    18	半场大小
        put(2L, 18L);
        //4	 全场让球    19	半场让球
        put(4L, 19L);
        //111角球独赢    119  上半场角球独赢
        put(111L, 119L);
        //113角球让球盘  121 上半场角球让球盘
        put(113L, 121L);
        //114角球大小盘  122 上半场角球大小盘
        put(114L, 122L);
        //306罚牌让分    308  上半场罚牌让分
        put(306L, 308L);
        //307罚牌大小    309  上半场罚牌大小
        put(307L, 309L);
        //310罚牌独赢    311  上半场罚牌独赢
        put(310L, 311L);
    }};

    /**
     * 1852（兜底）指定阶段需要关闭的玩法
     */
    public static Map<Long, List<Long>> MATCH_PERIOD_CLOS_CATEGORY = new HashMap<Long, List<Long>>() {{
        put(31L, Arrays.asList(17L, 18L, 19L, 308L, 309L, 311L, 119L, 121L, 122L));
        put(7L, Arrays.asList(17L, 18L, 19L, 308L, 309L, 311L, 119L, 121L, 122L));
        put(100L, Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L, 308L, 309L, 311L, 119L, 121L, 122L, 306L, 307L, 310L, 111L, 113L, 114L));
        put(32L, Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L, 308L, 309L, 311L, 119L, 121L, 122L, 306L, 307L, 310L, 111L, 113L, 114L));
        put(41L, Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L, 308L, 309L, 311L, 119L, 121L, 122L, 306L, 307L, 310L, 111L, 113L, 114L));
        put(110L, Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L, 308L, 309L, 311L, 119L, 121L, 122L, 306L, 307L, 310L, 111L, 113L, 114L));
        put(34L, Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L, 308L, 309L, 311L, 119L, 121L, 122L, 306L, 307L, 310L, 111L, 113L, 114L));
        put(50L, Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L, 308L, 309L, 311L, 119L, 121L, 122L, 306L, 307L, 310L, 111L, 113L, 114L));
        put(120L, Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L, 308L, 309L, 311L, 119L, 121L, 122L, 306L, 307L, 310L, 111L, 113L, 114L));
        //put(80L, Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L, 308L, 309L, 311L, 119L, 121L, 122L, 306L, 307L, 310L, 111L, 113L, 114L));
        put(90L, Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L, 308L, 309L, 311L, 119L, 121L, 122L, 306L, 307L, 310L, 111L, 113L, 114L));
        put(999L, Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L, 308L, 309L, 311L, 119L, 121L, 122L, 306L, 307L, 310L, 111L, 113L, 114L));
    }};


    /**
     * 需求 39924 ,需要校验的玩法
     */
    public  static List<Long> CHECK_MAIN_CATEGORY = Arrays.asList(2L, 4L, 18L, 19L);

    /**
     * 需求：2505 支持玩法
     */
    public static List<Long> BASKETBALL_AUTO_OPEN_CATEGORY = Arrays.asList(51L, 52L, 53L, 54L, 55L, 57L, 58L, 59L, 60L, 61L, 63L, 64L, 65L, 66L, 67L);
    /**
     * 需要验证盘口时间玩法
     */
    public static Map<Long, List<Long>> VERIFY_MODIFY_TIME_CATEGORY = new HashMap<Long, List<Long>>() {{
        put(1L, Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L));
        put(2L, Arrays.asList(37L, 39L, 38L, 40L, 43L, 19L, 18L, 42L, 48L, 54L, 60L, 66L, 46L, 52L, 58L, 64L, 45L, 51L, 57L, 63L));
    }};

    /**
     * 子玩法操盘 支持切换模式玩法
     */
    public static Map<Long, List<Long>> SWITCH_MODE_CHILD_CATEGORY = new HashMap<Long, List<Long>>() {{
        put(3L, Arrays.asList(249L, 250L, 251L, 252L, 274L, 277L, 278L, 279L, 290L));//棒球
        put(4L, Arrays.asList(261L, 262L, 263L, 264L, 268L));//冰球
        put(5L, Arrays.asList(162L, 163L, 164L, 165L, 168L));//网球
        put(7L, Arrays.asList(184L, 185L, 186L, 187L));//斯洛克
        put(8L, Arrays.asList(153L, 172L, 173L, 174L, 175L, 176L, 177L, 178L, 179L, 203L));//乒乓球
        put(9L, Arrays.asList(153L, 172L, 173L, 162L, 253L, 254L, 255L, 256L));//排球
        put(10L, Arrays.asList(175L, 176L, 177L, 178L, 179L, 203L));//羽毛球

    }};

    /**
     * 不计算基准分数据源
     */
    public static List<String> IGNORE_SCORE_DATASOURCE_CODE = Arrays.asList(DataSourceCodeEnum.AO.code, DataSourceCodeEnum.N01.code,
            DataSourceCodeEnum.N02.code, DataSourceCodeEnum.N03.code, DataSourceCodeEnum.LS.code,DataSourceCodeEnum.L02.code, DataSourceCodeEnum.F01.code);

    /**
     *  角球大小 标准玩法id集合
     */
    public static List<Long> TOTAL_CORNERS_CATEGORY = Arrays.asList(114L, 115L, 116L, 122L, 123L, 124L, 233L, 331L, 1100417L);

    /**
     * 角球大小玩法需要过滤的球头,1/4球头全部过滤
     */
    public static List<String> TOTAL_CORNERS_CATEGORY_NOT_SUPPORT_ADDITION1 = Arrays.asList("25", "75");

    /**
     * 6分钟玩法类玩法 坑位2/3固定操盘盘口位置关
     */
    public static List<Long> SIX_PLACE_NUM_CATEGORY_CLOSE = Lists.newArrayList(3100430L, 3100431L, 3100432L, 3100433L, 3100434L, 3100435L, 3100436L, 3100437L, 3100438L, 3100439L, 3100440L, 3100441L, 3100442L, 3100443L, 3100444L, 3100445L, 3100446L, 3100447L, 3100448L, 3100449L, 3100450L, 3100451L, 3100452L, 3100453L);
    public static List<Integer> SIX_PLACE_NUM_CLOSE = Lists.newArrayList(2, 3);

    //篮球主玩法不参与 关转封
    public static List<Long> BASKETBALL_MAIN_CATEGORY = Lists.newArrayList(
            37L, 43L, 48L, 54L, 60L, 66L, 142L,
            38L, 19L, 46L, 52L, 58L, 64L, 143L,
            39L, 18L, 45L, 51L, 57L, 63L, 26L,
            40L, 42L, 47L, 53L, 59L, 65L, 75L);
    public static List<Long> FootBall_MAIN3484_CATEGORY = Lists.newArrayList(
            1L,111L,310L,126L,1100413L,1100405L,333L,
            4L,113L,306L,128L,1100414L,1100406L,334L,
            2L,114L,307L,127L,331L,1100407L,335L,
            17L,119L,311L,129L,1100415L,1100408L,
            19L,121L,308L,130L,1100416L,1100409L,
            18L,122L,309L,332L,1100417L,1100410L
    );

    /**
     *忽略玩法赔率校验 1/(1/o1 + 1/o2 + ... + 1/on)大于0.99
     */
    public static List<Long> ignoreCheckCategoryMarketOddsOnValid = Lists.newArrayList(151L, 152L, 1100484L, 1100485L, 1100486L);

    /**
     * 关盘转封盘需要校验球头的玩法
     */
    public static List<Long> CHECK_BASKETBALL_MARKET_VALUE = Lists.newArrayList(38L,18L,45L,51L,57L,63L,26L);
    public static List<Long> CHECK_FOOTBALL_MARKET_VALUE = Lists.newArrayList(2L,114L,307L,127L,331L,1100407L,335L,
            18L,122L,309L,332L,1100417L,1100410L);

    public static List<Long> SPECIAL_CATEGORY_CLOSING = Arrays.asList(101L, 102L,105L,107L);


    /**
     *
     * ao1延长开售玩法
     *
     */
    public static List<Long> A01_EXTENDED_TIME_CATEGORY = Arrays.asList(2L, 4L, 18L, 19L);

    /**
     * a01  margin不抽水
     */
    public static List<Long> A01_MARGIN_CATEGORY_NOT = Arrays.asList(1100487L, 1100488L, 1100489L);
    /**
     * value的数据源必须是a01
     */
    public static Map<Long, Long> A01_MARGIN_CATEGORY_CHEACK = new LinkedHashMap<Long, Long>() {{
        put(1100487L, 7L);
        put(1100488L, 341L);
        put(1100489L, 342L);
    }};

    /**
     * 支持A+的玩法id集合
     */
    public static List<Long> A_MARGIN_CATEGORY = Arrays.asList(39L, 19L, 46L,52L,58L,64L,143L,
                                                               38L, 18L, 45L,51L,57L,63L,26L);

    /**
     * 100290
     * public static List<Long> FootBall_3446_3447_CATEGORY = Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L,
     *             111L,113L,114L,121L,122L,119L,
     *             306L,307L,310L,308L,309L,311L,
     *             126L,127L,128L,130L,332L,129L,
     *             1100414L,331L,1100413L,1100416L,1100417L,1100415L,
     *             1100406L,1100407L,1100405L,1100409L,1100410L,1100408L);
     */
    public static Map<Integer,Set<Long>> STANDARD_MATCH_SCORE_CHANGE = new HashMap<Integer,Set<Long>>(
    ){{
        put(1,new HashSet<Long>(){{addAll(Arrays.asList(1L, 2L, 4L, 17L, 18L, 19L,126L,127L,128L,130L,332L,129L));}});
        put(2,new HashSet<Long>(){{addAll(Arrays.asList(306L,307L,310L,308L,309L,311L,1100406L,1100407L,1100405L,1100409L,1100410L,1100408L));}});
        put(3,new HashSet<Long>(){{addAll(Arrays.asList(111L,113L,114L,121L,122L,119L,1100414L,331L,1100413L,1100416L,1100417L,1100415L));}});
    }};


    public static Map<String,List<Long>> A99_category = new HashMap(){{
        put("10001",Arrays.asList(4L,2L,19L,18L));
        put("10002",Arrays.asList(113L,114L,121L,122L));
        put("10003",Arrays.asList(306L,307L,308L,309L));
        put("10005",Arrays.asList(128L,127L,130L,332L));
        put("10006",Arrays.asList(1100414L,331L,1100416L,1100417L));
        put("10007",Arrays.asList(1100406L,1100407L,1100409L,1100410L));
    }};
}
