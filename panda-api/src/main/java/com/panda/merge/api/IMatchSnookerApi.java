package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.v2.*;

/**
 * 比分报球板公共接口
 */
public interface IMatchSnookerApi {

    Response scoreList(EventListV2Dto eventListV2Dto);

    Response rerack(RerackV2Dto rerackV2Dto) throws Exception;

    Response batchEditScores(EditScoreV2Dto editScoreV2Dto);

}
