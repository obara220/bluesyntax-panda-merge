package com.panda.merge.service;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.ChangeBusinessEventScoresDto;
import com.panda.merge.dto.advertise.EventOperationDto;
import com.panda.merge.dto.advertise.NewlyMatchEventQuery;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.merge.dto.scores.MatchScoresRequestDTO;

import java.util.List;

/**
 *  比分数据中心的对外api
 *  kb
 *  2020-11-26
 * */
public interface IScoresCenterService {

    /**
     *  三方比分查询接口
     * 传参：
     * 返回:
     * */
    List<JSONObject> searchListMatchScores(List<MatchScoresRequestDTO> thirdMatchIds);
    /**
     *  三方时间查询
     * 传参：
     * 返回:
     * */
    List<JSONObject> searchListMatchTime(List<MatchScoresRequestDTO> thirdMatchIds);

}
