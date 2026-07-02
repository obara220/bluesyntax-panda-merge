package com.panda.merge.dubbo;

import com.panda.merge.api.IThirdMatchInfoPutApi;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdSportTournamentDTO;
import com.panda.merge.dto.nonrealttime.put.ThirdMatchInfoDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchInfoProcessor;
import com.panda.merge.rocketmq.processor.ThirdSportTournamentProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 三方库赛事信息相关查询
 * @author :  tell
 * @Date:    2020年9月9日11:33:27
 */
@Deprecated
@Slf4j
@Component
@DubboService
public class ThirdMatchInfoPutApiImpl extends BaseProcessor implements IThirdMatchInfoPutApi {

    @Autowired
    private ThirdSportTournamentProcessor thirdSportTournamentProcessor;

    @Autowired
    private ThirdMatchInfoProcessor thirdMatchInfoProcessor;

    @Override
    public Response pushThirdMatchInfo(Request<List<ThirdMatchInfoDTO>> request){
        return thirdMatchInfoProcessor.processMatchData(request);
    }


    @Override
    public Response pushThirdSportTournament(Request<List<ThirdSportTournamentDTO>> request){
        return thirdSportTournamentProcessor.processTournamentData(request);
    }


}
