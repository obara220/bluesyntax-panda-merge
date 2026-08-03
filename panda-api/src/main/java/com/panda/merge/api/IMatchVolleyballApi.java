package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.v2.EditScoreV2Dto;
import com.panda.merge.dto.advertise.v2.EventListV2Dto;

/**
 * 排球报球板特有接口（与 IMatchScoreCommonApi 互补）。
 *
 * 公共动作（kickOff / changeScore / sendEvent / changeMatchPeriod / 暂停（controlType=2/3）等）
 * 走 {@link IMatchScoreCommonApi}，按 sportId=9 分发到排球处理器。
 */
public interface IMatchVolleyballApi {

    /**
     * 查询所有局比分。setNums 含 -1 表示全部。
     */
    Response scoreList(EventListV2Dto eventListV2Dto);

    /**
     * 批量编辑比分（{'1':{home:1,away:1},'2':{home:1,away:1}} 形式）。
     */
    Response batchEditScores(EditScoreV2Dto editScoreV2Dto);
}
