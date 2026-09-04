package com.panda.merge.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class DataSourceConstant {

    /**
     * 现有对接商业数据源-事件
     */
    public static HashSet<String> DATA_SOURCES=new HashSet<String>(Arrays.asList("SR","BG","RB","BC","PD","1X","KO","BE","LS","OD","TS","N01","N02","N03","A01","S01","FTS","F01"));

    /**
     * 现有对接商业数据源  并且有效数据源-统计
     */
    public static HashSet<String> VALID_DATA_SOURCES=new HashSet<String>(Arrays.asList("SR","BG","BC","BT","1X","KO","BE","LS","OD","TS","N01","N02","N03","A01","S01","FTS","F01"));
    /**
     * 现有有效的赛种
     */
    public static List<Long> SPORT_TYPES = new ArrayList<>(Arrays.asList(1L,2L,3L,4L,5L,6L,7L,8L,9L,10L,11L,12L,13L,14L,15L,16L,37L));

    /**
     * 足篮外的综合球种-B02通道校验
     */
    public static List<Long> B02_SPORT_TYPES = new ArrayList<>(Arrays.asList(3L,4L,5L,6L,7L,8L,9L,10L,11L,12L,13L,14L,15L,16L,37L));
    /**
     * 现有有效的赛种
     *  5网，8乒，9排，10羽
     */
    public static List<Long> STANDARC_SCORE_SPORTIDS = new ArrayList<>(Arrays.asList(1L,2L,5L,8L,9L,10L));
}
