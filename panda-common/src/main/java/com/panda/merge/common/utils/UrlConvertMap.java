package com.panda.merge.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : nonhung
 * @project Name : panda-merge
 * @package Name : com.panda.merge.common.utils
 * @description : TODO
 * @date: 2020-09-24 19:49
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public class UrlConvertMap {
    /**
     * 将url参数转换成map
     *
     * @param param aa=11&bb=22&cc=33
     * @return
     */
    public static Map<String, Object> getUrlParams(String param) {
        Map<String, Object> map = new HashMap<String, Object>(0);
        if (StringUtils.isBlank(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String param2 = params[i];
            if (param2.startsWith("p=")) {
                String substring = param2.substring(2);
                map.put("p", substring);
            } else {
                String[] p = params[i].split("=");
                if (p.length == 2) {
                    map.put(p[0], p[1]);
                }
            }
        }
        return map;
    }

    /**
     * 替换Url中某个具体参数
     * 正则替换
     *
     * @param url
     * @param name
     * @param accessToken
     * @return
     */
    public static String replaceAccessTokenReg(String url, String name, String accessToken) {
        if (StringUtils.isNotBlank(url) && StringUtils.isNotBlank(accessToken)) {
            url = url.replaceAll("(" + name + "=[^&]*)", name + "=" + accessToken);
        }
        return url;
    }

    public static String replaceAccessToken(String url, String name, String accessToken) {
        if (StringUtils.isNotBlank(url) && StringUtils.isNotBlank(accessToken)) {
            int index = url.indexOf(name + "=");
            if (index != -1) {
                StringBuilder sb = new StringBuilder();
                String substring = url.substring(0, index);
                sb.append(url.substring(0, index)).append(name + "=").append(accessToken);
                int idx = url.indexOf("&", index);
                if (idx != -1) {
                    sb.append(url.substring(idx));
                }
                url = sb.toString();
            }

        }
        return url;

    }

    public static void main(String[] args) {
        String str = "https://ani.budrp.cn/sc/index.jsp?matchId=2615984&reverse=0&code=0349735f9467d7f0414bdb952c66f1f0&t=2020092515&auth_token=00830fb8c414cda4fdc09d3051c650b3&p=aG9tZU5hbWU95YWL5LuA5ouJJmF3YXlOYW1lPeWFueaLiSZsZWFndWVOYW1lPUF6ZXJiYWlqYW4gUHJlbWllciBMZWFndWU==&s=a8c1760ccbdbd1f9325b1d0213d057d5";
        replaceAccessToken(str, "p", "1234");
    }
}
