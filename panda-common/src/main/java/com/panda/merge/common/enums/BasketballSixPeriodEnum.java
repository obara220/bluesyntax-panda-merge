package com.panda.merge.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报球板篮球枚举类
 *
 * @author warren
 * @since 2024/05/11 19:00:46
 */
@Getter
public enum BasketballSixPeriodEnum {
    /**
     * 第一节前6分钟
     */
    BASKETBALL_1312(1312L, "6-min before first quarter", "第一节前6分钟", "12:00-6:00 (12*4倒计时)"),
    /**
     * 第一节后6分钟
     */
    BASKETBALL_1306(1306L, "6-min after first quarter", "第一节后6分钟", "6:00-0:00 (12*4倒计时)"),
    /**
     * 第二节前6分钟
     */
    BASKETBALL_1412(1412L, "6-min before second quarter", "第二节前6分钟", "12:00-6:00 (12*4倒计时)"),
    /**
     * 第二节后6分钟
     */
    BASKETBALL_1406(1406L, "6-min after second quarter", "第二节后6分钟", "6:00-0:00 (12*4倒计时)"),
    /**
     * 第三节前6分钟
     */
    BASKETBALL_1512(1512L, "6-min before third quarter", "第三节前6分钟", "12:00-6:00 (12*4倒计时)"),
    /**
     * 第三节后6分钟
     */
    BASKETBALL_1506(1506L, "6-min after third quarter", "第三节后6分钟", "6:00-0:00 (12*4倒计时)"),
    /**
     * 第四节前6分钟
     */
    BASKETBALL_1612(1612L, "6-min before fourth quarter", "第四节前6分钟", "12:00-6:00 (12*4倒计时)"),
    /**
     * 第四节后6分钟
     */
    BASKETBALL_1606(1606L, "6-min after fourth quarter", "第四节后6分钟", "6:00-0:00 (12*4倒计时)"),
    ;

    private final Long code;

    private final String value;

    private final String name;

    private final String desc;

    BasketballSixPeriodEnum(Long code, String value, String name, String desc) {
        this.code = code;
        this.value = value;
        this.name = name;
        this.desc = desc;
    }

    public static boolean getSixPeriodId(Long period) {
        for (BasketballSixPeriodEnum periodEnum : BasketballSixPeriodEnum.values()) {
            if (periodEnum.getCode().equals(period)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查出枚举所有code
     *
     * @return code集合
     */
    public static List<Long> getSixPeriodCode() {
        return Arrays.stream(BasketballSixPeriodEnum.values()).map(BasketballSixPeriodEnum::getCode).collect(Collectors.toList());
    }
}
