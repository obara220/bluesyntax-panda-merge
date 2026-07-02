package com.panda.merge.api;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Response;

import java.util.Map;

public interface SystemMarketPreApi {
    /**
     * 系统级AO，SR关盘
     * @param params 是否关盘的参数
     * @return
     */
    void saveSystemPreResultAndPush(String params);
    /**
     * 系统级AO，SR关盘
     * @param params 查询的参数
     * @return
     */
    Map<String,Object> searchSystemPreResultAndPush(String params);

}
