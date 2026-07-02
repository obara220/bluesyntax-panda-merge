package com.panda.merge.api;


import com.panda.merge.dto.Response;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.TradeTournamentConfigDTO;
import com.panda.merge.dto.TradeTournamentTemplateConfigDTO;

/**
 * @author :  myname
 * @Project Name :  data-realtime
 * @Package Name :  com.panda.sport.data.realtime.api
 * @Description :  TODO
 * @Date: 2020-07-03 20:25
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ITradeTournamentConfigApi {

    /**
     * 操盘配置联赛数据处理
     * @param message
     * @return
     */
    Response putTradeTournamentConfig(Request<TradeTournamentConfigDTO> message);

    /**
     * 操盘配置联赛与模板配置
     * @param message
     * @return
     */
    Response putTournamentTemplateRelationConfig(Request<TradeTournamentTemplateConfigDTO> message);

}
