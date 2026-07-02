package com.panda.merge.rocketmq.processor;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.ThreadPoolConfig;
import com.panda.merge.dto.MatchCopyDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ResultCode;
import com.panda.merge.mapper.MatchEventInfoMapper;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchEventInfoExample;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.producer.MatchEventInfoProducer;
import com.panda.merge.rocketmq.producer.MatchSaleOverProducer;
import com.panda.merge.rocketmq.producer.PaDataServiceLogProducer;
import com.panda.merge.service.MatchEventInfoService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 拷贝赛事相关信息（拷贝赛事事件）
 * @author   Aison
 * @since    2020年10月22日11:01:53
 */
@Slf4j
@Component
@Validated
public class MatchCopyProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private MatchEventInfoService matchEventInfoService;
    @Autowired
    private MatchEventInfoMapper matchEventInfoMapper;
    @Autowired
    private MatchSaleOverProducer matchSaleOverProducer;
    @Autowired
    private PaDataServiceLogProducer paDataServiceLogProducer;
    @Autowired
    private MatchEventInfoProducer matchEventInfoProducer;
    @Autowired
    private ThreadPoolConfig threadPoolConfig;


    public void putMatchCopyProcessor(Request<MatchCopyDTO> request){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = Response.success();
        try{
            MatchCopyDTO matchCopyDTO = request.getData();
            List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(matchCopyDTO.getNewStandardMatchId());
            if(!CollectionUtils.isEmpty(thirdMatchInfos)){
                for (ThirdMatchInfo thirdMatchInfo: thirdMatchInfos) {
                    log.info("::{}::putMatchCopyProcessor, 拷贝标准赛事下三方赛事信息：{}", request.getLinkId(), JSON.toJSONString(thirdMatchInfo));
                    try{
                        if(DataSourceCodeEnum.getEventCodeList().contains(thirdMatchInfo.getDataSourceCode())){
                            //拷贝赛事事件信息
                            MatchEventInfoExample matchEventInfoExample = new MatchEventInfoExample();
                            matchEventInfoExample.createCriteria().andStandardMatchIdEqualTo(matchCopyDTO.getOldStandardMatchId())
                                    .andDataSourceCodeEqualTo(thirdMatchInfo.getDataSourceCode());
                            matchEventInfoExample.setOrderByClause("event_time");
                            List<MatchEventInfo> matchEventInfos = matchEventInfoMapper.selectByExample(matchEventInfoExample);
//                            List<MatchEventInfo> matchEventInfos = matchEventInfoService.getItemByStandardMatchIdAndDataSoureCode(matchCopyDTO.getOldStandardMatchId(),thirdMatchInfo.getDataSourceCode());
                            if(!CollectionUtils.isEmpty(matchEventInfos)){
                                for (MatchEventInfo matchEventInfo: matchEventInfos) {
                                    matchEventInfo.setId(UUIdUtils.getId());
                                    matchEventInfo.setSendData(YesNoEnum.N.name());
                                    matchEventInfo.setStandardMatchId(thirdMatchInfo.getReferenceId());
                                    matchEventInfo.setThirdMatchId(thirdMatchInfo.getId());
                                    matchEventInfo.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
                                }
                                matchEventInfoService.saveBatch(matchEventInfos,request.getLinkId());

                                //1.推送三方赛事事件到队列 THIRD_MATCH_EVENT_INFO（比分中心）
                                thirdMatchEvent2Mq(request,matchEventInfos,thirdMatchInfo);
                                //2.推送生成标准赛事事件到队列 MATCH_EVENT_INFO_TO_RISK
                                if(null != thirdMatchInfo.getReferenceId()){
                                    matchEventInfoProducer.pushMatchEventDataToRisk(request.getLinkId(),matchEventInfos,thirdMatchInfo,false);
                                    log.info("::{}::putMatchCopyProcessor，推送事件到队列 MATCH_EVENT_INFO_TO_RISK，三方赛事原始id:{}", request.getLinkId(),thirdMatchInfo.getThirdMatchSourceId());
                                }
                            }else{
                                log.info("::{}::putMatchCopyProcessor，标准赛事下无关联数据源：{}事件信息！", request.getLinkId(),thirdMatchInfo.getDataSourceCode());
                            }
                        }
                    }catch (Exception e){
                        log.error("::"+request.getLinkId()+"::putMatchCopyProcessor, 拷贝标准赛事下事件和统计信息异常，Exception:",e);
                    }
                }
            }else{
                log.info("::{}::putMatchCopyProcessor, 拷贝标准赛事下无关联三方赛事！", request.getLinkId());
            }
        }catch (Exception e){
            response.setCode(ResultCode.FAILED.getCode());
            response.setMsg(e.getMessage());
            throw e;
        }finally {
            stopWatch.stop();
            response.setDataSourceTime(stopWatch.getTotalTimeMillis());
            //统计处理耗时
            paDataServiceLogProducer.sendPaDataServiceLog(
                    getPaDataServiceLogDTO(request.getLinkId(),realtime,COPY_MATCH,"拷贝赛事相关信息",
                            stopWatch.getTotalTimeMillis(),Integer.parseInt(String.valueOf(response.getCode())),response.getMsg())
            );
        }
        log.info("::{}::putMatchCopyProcessor, 拷贝赛事相关信息处理结束，共耗时:{}", request.getLinkId(), response.getDataSourceTime());
    }

    /**
     * 异步下发事件到比分中心，赛程赛果结算
     * */
    public void thirdMatchEvent2Mq(Request<MatchCopyDTO> request,List<MatchEventInfo> matchEventInfos,ThirdMatchInfo thirdMatchInfo){
        TaskExecutor taskExecutor = threadPoolConfig.getMatchThreadPool();
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                matchEventInfoProducer.pushMatchEventDataToRisk(request.getLinkId(),matchEventInfos,thirdMatchInfo,false);
                log.info("::{}::putMatchCopyProcessor，推送事件到队列 THIRD_MATCH_EVENT_INFO，三方赛事原始id:{}", request.getLinkId(),thirdMatchInfo.getThirdMatchSourceId());
            }
        });
    }
}
