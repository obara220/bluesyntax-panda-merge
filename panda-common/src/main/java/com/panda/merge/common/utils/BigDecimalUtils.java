package com.panda.merge.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 使用double运算，容易丢失精度。
 * 为了防止丢失精度，使用BigDecimal运算，就可以解决java程序运算丢失精度的问题。
 * 并且让分母(除数)为0时抛异常
 */
public class BigDecimalUtils {

    // 除法运算默认精度
    private static final int DEF_DIV_SCALE = 10;

    private BigDecimalUtils() {

    }

    /**
     * 精确加法
     */
    public static double add(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.add(b2).doubleValue();
    }

    /**
     * 精确加法
     */
    public static double add(double value1, double value2, double value3) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        BigDecimal b3 = BigDecimal.valueOf(value3);
        return b1.add(b2).add(b3).doubleValue();
    }

    /**
     * 精确加法
     */
    public static double add(String value1, String value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.add(b2).doubleValue();
    }

    /**
     * 精确减法
     */
    public static double subtract(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 精确减法
     */
    public static double subtract(double value1, double value2, double value3) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        BigDecimal b3 = BigDecimal.valueOf(value3);
        return b1.subtract(b2).subtract(b3).doubleValue();
    }

    /**
     * 精确减法
     */
    public static double subtract(String value1, String value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 精确乘法
     */
    public static Double multiply(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 精确乘法
     */
    public static Double multiply(double value1, double value2, int scale) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return scale(b1.multiply(b2).doubleValue(), scale);
    }

    /**
     * 精确乘法
     */
    public static double multiply(String value1, String value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 精确除法 使用默认精度
     */
    public static Double divide(double value1, double value2) {
        return divide(value1, value2, DEF_DIV_SCALE);
    }

    /**
     * 精确除法 使用默认精度
     */
    public static double divide(String value1, String value2) {
        return divide(value1, value2, DEF_DIV_SCALE);
    }

    /**
     * 精确除法
     *
     * @param scale 精度
     */
    public static double divide(double value1, double value2, int scale) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        // return b1.divide(b2, scale).doubleValue();
        return b1.divide(b2, scale, BigDecimal.ROUND_DOWN).doubleValue();
    }

    /**
     * 精确除法
     *
     * @param scale 精度
     */
    public static double divideUP(double value1, double value2, int scale) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 精确除法
     *
     * @param scale 精度
     */
    public static double divide(String value1, String value2, int scale) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        // return b1.divide(b2, scale).doubleValue();
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 四舍五入
     *
     * @param scale 小数点后保留几位
     */
    public static double scale(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 小数点截取
     *
     * @param scale 小数点后截取几位
     */
    public static double scaleCrop(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, BigDecimal.ROUND_DOWN).doubleValue();
    }

    /**
     * 小数点截取，去除 .0
     *
     * @param scale 小数点后截取几位
     */
    public static double scaleCropTrailingZeros(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, BigDecimal.ROUND_DOWN).stripTrailingZeros().doubleValue();
    }

    /**
     * 四舍五入
     *
     * @param scale 小数点后保留几位
     */
    public static double scale(String value, int scale) {
        return new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 比较相等
     */
    public static boolean equalTo(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return 0 == b1.compareTo(b2);
    }


    public static Double changeZero(Double value1) {
        return value1 == null ? 0 : value1;
    }

    public static Double changeZero(BigDecimal value1) {
        return value1 == null ? 0 : value1.doubleValue();
    }

    public static Integer changeZero(Integer value1) {
        return value1 == null ? 0 : value1;
    }

    /**
     * 小数点位数
     */
    public static int scaleNum(double value1) {
        BigDecimal num = BigDecimal.valueOf(value1);
        return num.scale();
    }

    /**
     * 马来赔规整为2位小数（Double 入参，消除二进制浮点噪声）。
     */
    public static BigDecimal normalizeMalayOddsDecimal(Double malayOddsValue) {
        if (malayOddsValue == null) {
            return null;
        }
        if (malayOddsValue == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(Double.toString(malayOddsValue)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 马来赔规整为2位小数（BigDecimal 入参）。
     */
    public static BigDecimal normalizeMalayOddsDecimal(BigDecimal malayOddsValue) {
        if (malayOddsValue == null) {
            return null;
        }
        return malayOddsValue.setScale(2, RoundingMode.HALF_UP);
    }
}