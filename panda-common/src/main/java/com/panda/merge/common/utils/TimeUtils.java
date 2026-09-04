package com.panda.merge.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author :  dorich
 * @Project Name :  panda_data_service
 * @Package Name :  com.panda.sports.manager.utils
 * @Description :  用于时间转换
 * @Date: 2019-08-02 11:40
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class TimeUtils {
    /** 一小时多少毫秒 **/
    private static long millsSecondPerHour = 3600 * 1000;

    /** 一小时多少秒 **/
    private static long secondPerHour = 3600;

    /** 一小时多少微秒 **/
    private static long microSecondPerHour = 3600 * 1000 * 1000;

    /**
     * @Description   将GMT时间转为指定时区的时间
     * @Param gmtTime:    格林威治时间，单位毫秒
     * timeZone:   时区数。 如果是 格林威治时间线 往东（美洲方向），则该数字小于0；往西（亚洲方向），数字大于0，跨时区个数是该数字的具体值。
     * @Author  dorich
     * @Date   2019/8/2
     * @return long
     **/
    public static long timeMillsSecondsTimeZone(Long gmtTime, int timeZone) {
        return gmtTime + timeZone * millsSecondPerHour;
    }

    /**
     * @Description   将GMT时间转为指定时区的时间
     * @Param gmtTime:    格林威治时间，单位毫秒
     * timeZone:   时区数。 如果是 格林威治时间线 往东（美洲方向），则该数字小于0；往西（亚洲方向），数字大于0，跨时区个数是该数字的具体值。
     * @param  gmtTime  需要转换的UTC时间
     * @param  timeZone 时区个数
     * @author  dorich
     * @date   2019/8/9
     * @return long
     **/
    public static long timeSecondsTimeZone(Long gmtTime, int timeZone) {
        return gmtTime + timeZone * secondPerHour;
    }

    /**
     * @Description   本地的东八区时间转换为GMT时间
     * @Param
     * @Author  dorich
     * @Date   2019/8/2
     * @return long
     **/
    public static long millsSecondsEast8ZoneGmt() {
        return System.currentTimeMillis();
    }

    /**
     * @Description   将GMT时间转为指定时区的时间
     * @Param
     * gmtTime:    格林威治时间，单位微秒
     * timeZone:   时区数。 如果是 格林威治时间线 往东（美洲方向），则该数字小于0；往西（亚洲方向），数字大于0，跨时区个数是该数字的具体值。
     * @Author  dorich
     * @Date   2019/8/2
     * @return long
     **/
    public static long timeMicroSecondsTimeZone(Long gmtTime, int timeZone) {
        return gmtTime + timeZone * microSecondPerHour;
    }

    /**
     * 小于7天后的时间
     *
     * @param addTime
     * @return
     */
    public static boolean timeCalendar(Long addTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为7天后
        Date after7days = calendar.getTime();   //得到7天后的时间
        if (addTime < after7days.getTime()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取UTC时间戳，比GMT多8小时，适用于转换数据商下发时间比本地时间少8小时的情况
     * @param text 时间字符串，格式支持两种
     *             1、不包含毫秒值，如"2019-01-03T08:26:15Z"；
     *             2、支持任意位数的毫秒值：2019-01-03T08:26:15.503162206Z；
     *             转换出来的Date类型精度知道毫秒位
     * @return
     * @throws ParseException
     */
    public static Date parseUTCText(String text) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (text.indexOf(".") > -1) {
            String prefix = text.substring(0, text.indexOf("."));
            String suffix = text.substring(text.indexOf("."));
            if (suffix.length() >= 5) {
                suffix = suffix.substring(0, 4) + "Z";
            } else {
                int len = 5 - suffix.length();
                String temp = "";
                temp += suffix.substring(0, suffix.length() - 1);
                for (int i = 0; i < len; i++) {
                    temp += "0";
                }
                suffix = temp + "Z";
            }
            text = prefix + suffix;
        } else {
            text = text.substring(0, text.length() - 1) + ".000Z";
        }
        Date date = sdf.parse(text);
        return date;
    }


    /**
     * 将13位时间戳根据日期格式转换为字符串
     * */
    public static String timestamp2Str(Long timestamp,String pattern){
        // 创建一个SimpleDateFormat对象，定义你想要的日期格式 "yyyy-MM-dd HH:mm:ss"
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        // 使用Date对象将时间戳转换为日期
        Date date = new Date(timestamp);
        // 将Date对象格式化为字符串
        return sdf.format(date);
    }

    /**
     * 将秒数转化为分秒字符串（例如：20‘12‘‘）
     * */
    public static String convertSecondsToMMSS(Long seconds) {
        if (seconds < 0) {
            return "00‘00’‘";
        }
        Long minutes = seconds / 60;
        Long secs = seconds % 60;
        return String.format("%02d‘%02d’‘", minutes, secs);
    }


    public static void main(String[] args) {
        try{
            System.out.println(parseUTCText("2023-04-17T09:41:43.9346138Z").getTime());
            System.out.println(parseUTCText(" 2019-01-03T08:26:15Z").getTime());;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
