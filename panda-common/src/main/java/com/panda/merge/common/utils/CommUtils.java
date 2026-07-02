package com.panda.merge.common.utils;

import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 公共帮助类
 * @author  tell
 * @since   2020年9月5日18:08:30
 * */
public class CommUtils {

    public static String HOME_PARAM = "home";

    public static BigDecimal SETTLE_FACTOR = new BigDecimal("0.5");


    /**
     *将ID转换为键值对JSON字符串，用于业务逻辑处理完毕后推送MQ
     * @param id  需要转换为JSON的ID
     * @return  JSON.toJSONString(map)
     */
    public static String getJsonById(Long id) {
        Map<String, Long> map = new HashMap<>(2);
        map.put("id", id);
        return JSON.toJSONString(map);
    }

    /**
     * 按照自定义长度分隔集合
     * @param list        原集合
     * @param listLength 子集合的长度
     * @return
     */
    public static<T>  List<List<T>> groupList(List<T> list, int listLength) {
        List<List<T>> listGroup = new ArrayList<>();
        int listSize = list.size();
        //子集合的长度
        int toIndex = listLength;
        for (int i = 0; i < list.size(); i += listLength) {
            if (i + listLength > listSize) {
                toIndex = listSize - i;
            }
            List<T> newList = list.subList(i, i + toIndex);
            listGroup.add(newList);
        }
        return listGroup;
    }

}
