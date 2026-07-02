package com.panda.merge.common.enums;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 标准运动类型枚举
 * @author   tell
 * @since    2020年9月11日19:23:49
 */
@Getter
public enum StandardSportTypeEnum {
    FootBall(1L, "足球"),
    Basketball(2L, "篮球"),
    BaseBall(3L, "棒球"),
    IceBall(4L, "冰球"),
    Tennis(5L, "网球"),
    Soccer(6L, "美式足球"),
    Snooker(7L, "斯诺克"),
    TableTennis(8L, "乒乓球"),
    Vollyball(9L, "排球"),
    Badminton(10L, "羽毛球"),
    Handball(11L, "手球"),
    Boxing(12L, "拳击"),
    BeachVolley(13L, "沙滩排球"),
    RugbyUnion(14L, "联合式橄榄球"),
    Hockey(15L, "曲棍球"),
    WaterPolo(16L, "水球"),
    Athletics(17L, "田径"),
    SpecialEvents(18L, "政治娱乐"),
    Swimming(19L, "游泳"),
    Gymnastics(20L, "体操"),
    Diving(21L, "跳水"),
    Shooting(22L, "射击"),
    Weightlifting(23L, "举重"),
    Archery(24L, "射箭"),
    Fencing(25L, "击剑"),
    Curling(26L, "冰壶"),
    Taekwondo(27L, "跆拳道"),
    Golf(28L, "高尔夫"),
    Cycling(29L, "自行车"),
    HorseRacing(30L, "赛马"),
    Sailing(31L, "帆船"),
    Rowing(32L, "划船"),
    Motorsport(33L, "赛车运动"),
    Judo(34L, "柔道"),
    karate(35L, "空手道"),
    Wrestling(36L, "摔跤"),
    Cricket(37L, "板球"),
    Darts(38L, "飞镖"),
    BeachFootball(39L, "沙滩足球"),
    Others(40L, "其他"),
    RugbyLeague(41L, "联盟式橄榄球"),
    Fun(50L, "趣味"),
    LOL(100L, "英雄联盟"),
    Dota2(101L, "Dota2"),
    CS(102L, "CS"),
    HonorOfKings(103L, "王者荣耀"),
    PUBG(104L, "绝地求生"),
    VirtualFootBall(1001L, "虚拟足球"),
    Valorant(105L, "无畏契约"),
    ;
    public Long code;
    public String msg;

    StandardSportTypeEnum(Long code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    /**
     * 是否赛事类型枚举
     * @param code 赛事类型编码
     * @return 具体的枚举对象
     */
    public static StandardSportTypeEnum getEnum(Long code) {
        for (StandardSportTypeEnum standardSportTypeEnum : StandardSportTypeEnum.values()) {
            if (standardSportTypeEnum.code.equals(code)) {
                return standardSportTypeEnum;
            }
        }
        return null;
    }
    /**
     * 获取全部标准运动类型列表
     * */
    public static Set<Long> getSportIds() {
        return Lists.newArrayList(StandardSportTypeEnum.values()).stream().map(obj -> obj.getCode()).collect(Collectors.toSet());
    }

}
