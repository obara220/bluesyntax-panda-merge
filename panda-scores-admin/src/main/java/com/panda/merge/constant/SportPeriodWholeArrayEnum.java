package com.panda.merge.constant;

/**
 * @author :  dorich
 * @project Name :  panda_data_association
 * @package Name :  com.panda.sport.data.association.common.enums
 * @description :   体育种类比赛进行中的全场枚举
 * @date: 2019-09-04 15:05
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum SportPeriodWholeArrayEnum {
    /**
     * 足球赛事全场包含阶段标识,涉及统计,因此仅包含上下半场
     */
    FOOTBALL(new Long[]{6L, 7L, 41L, 42L, 50L}, 1L, "足球半场阶段标识,这些半场构成全场"),

    /**
     * 篮球赛事全场包含阶段标识,涉及统计,因此仅包含四节
     */
    BASKETBALL(new Long[]{1L, 2L, 13L, 14L, 15L, 16L, 40L}, 2L, "篮球半场阶段标识,这些半场构成全场"),
    /**
     * 羽毛球球赛事全场包含阶段标识
     */
    BADMINTON(new Long[]{8L, 9L, 10L, 11L, 12L}, 10L, "羽毛球羽毛球关注的比赛阶段标识"),
    /**
     * 网球球球赛事全场包含阶段标识
     */
    TENNIS(new Long[]{8L, 9L, 10L, 11L, 12L}, 5L, "网球网球关注的比赛阶段标识"),
    /**
     * 乒乓球赛事全场包含阶段标识
     */
    TABLE_TENNIS(new Long[]{8L, 9L, 10L, 11L, 12L, 441L, 442L}, 8L, "乒乓球关注的比赛阶段标识"),
    /**
     * 斯诺克赛事全场包含阶段标识
     */
    SNOOKER(new Long[]{21L}, 7L, "斯诺克关注的比赛阶段标识"),
    /**
     * 棒球赛事全场包含阶段标识
     */
    BASEBALL(new Long[]{401L,  402L, 403L,  404L,  405L, 406L,  407L,  408L, 409L, 410L,  411L, 412L,  413L,  414L,  415L, 416L, 417L,  418L,  41910L, 42010L, 41911L,42011L,  41912L,42012L,  41913L,42013L,  41914L,42014L,  41915L,42015L,  41916L,42016L,  41917L, 42017L, 41918L,42018L,  41919L,42019L,  41920L, 42020L}, 3L, "棒球关注的比赛阶段"),
    /**
     * 冰球赛事全场包含阶段标识
     */
    ICEHOCKEY(new Long[]{1L,  2L,  3L,40L,50L}, 4L, "冰球关注的比赛阶段标识"),
    /**
     * 美式足球赛事全场包含阶段标识
     */
    AMERICANFOOTBALL(new Long[]{13L, 14L, 15L, 16L,40L}, 6L, "美式足球关注的比赛阶段标识"),
    /**
     * 排球赛事全场包含阶段标识
     */
    VOLLEYBALL(new Long[]{8L, 9L, 10L, 11L, 12L, 441L, 442L}, 9L, "排球关注的比赛阶段标识");


    private Long[] code;

    private Long sportId;

    private String description;

    SportPeriodWholeArrayEnum(Long[] code, Long sportId, String description) {
        this.code = code;
        this.sportId = sportId;
        this.description = description;
    }


    public Long[] getCode() {
        return code;
    }

    public Long getSportId() {
        return sportId;
    }

    public String getDescription() {
        return description;
    }

    public static Long[] getPeriodsBySportId(Long sportId) {

        if (null == sportId || sportId == 0) {
            return null;
        }
        SportPeriodWholeArrayEnum[] values = SportPeriodWholeArrayEnum.values();
        for (SportPeriodWholeArrayEnum e : values) {
            if (e.sportId.equals(sportId)) {
                return e.getCode();
            }
        }
        return null;
    }
}
