package com.panda.merge.api;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdSportTournamentDTO;
import com.panda.merge.dto.nonrealttime.put.ThirdMatchInfoDTO;

import java.util.List;


/**
 * 三方赛事相关信息接入API
 * @author  tell
 * @since   2020年11月6日14:59:55
 * */
public interface IThirdMatchInfoPutApi {

    /**
     * 三方赛事数据接入
     * @param  request  入参
     *  @return  Response
     * */
    Response pushThirdMatchInfo(Request<List<ThirdMatchInfoDTO>> request);

    /**
     * 三方联赛数据接入
     * @param  request  入参
     * @return  Response
     * */
    Response pushThirdSportTournament(Request<List<ThirdSportTournamentDTO>> request);

}
