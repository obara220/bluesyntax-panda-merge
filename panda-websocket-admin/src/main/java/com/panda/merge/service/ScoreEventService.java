package com.panda.merge.service;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.request.RequestVo;
import com.panda.merge.model.MatchEventCommon;
import com.panda.merge.model.MatchEventInfo;

import java.util.List;

public interface ScoreEventService {

    /**
     * 处理并推送比分消息
     *
     * @param scoreRequest
     * @Author: Top
     * @Date: 2021/1/10 15:03
     */
    Object queryScore(RequestVo scoreRequest);

    Object queryEvent(RequestVo requestVo);

    List<MatchEventCommon> queryEventName( List<MatchEventCommon> list);
}
