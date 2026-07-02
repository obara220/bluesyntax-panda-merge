package com.panda.merge.mq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMarketResultMessage;
import com.panda.merge.mapper.StandardSportMarketMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.IThirdMarketResultTransService;
import com.panda.merge.service.IWsPushService;
import com.panda.merge.service.ThirdSportMarketService;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消费数据源赛果信息
 * @author    Aison
 * @since     2020年11月18日16:55:03
 */
@Slf4j
@Component
//@RocketMQMessageListener(
//        topic = "STANDARD_MATCH_RESULT",
//        consumerGroup = "settle-group-"+"STANDARD_MATCH_RESULT",
//        consumeThreadMax = 256,
//        consumeTimeout = 10000L
//)
//@DependsOn("settleScoresAdminApplication")
public class StandardMarketResultConsumer implements RocketMQListener<Request<StandardMarketResultMessage>> {

    @Autowired
    ThirdSportMarketService thirdSportMarketService;
    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    StandardSportMarketMapper standardSportMarketMapper;
    @Autowired
    IThirdMarketResultTransService thirdMarketResultTransService;
    @Autowired
    IWsPushService wsPushService;
    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;
    //是否使用数据中心接口
    @NacosValue(value = "${datacenter.settle.switch}", autoRefreshed = true)
    private Boolean datacenterSettleSwitch;

    @Override
    public void onMessage(Request<StandardMarketResultMessage> request) {
        if(datacenterSettleSwitch){
            return;
        }
        String linkId = request.getLinkId();
        log.info("::{}::数据源赛果处理开始", linkId);
        StandardMarketResultMessage thirdMatchResultDTO = request.getData();
        //只接收足球赛果
        if(!thirdMatchResultDTO.getSportId().equals(1l)){
            return;
        }
        try {
            ThirdMatchInfoExample example = new ThirdMatchInfoExample();
            example.createCriteria().andReferenceIdEqualTo(Long.parseLong(request.getData().getStandardMatchId())).andDataSourceCodeEqualTo(thirdMatchResultDTO.getDataSourceCode());
            //查找三方赛事
            List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExample(example);
            if (thirdMatchInfos.size() == 0) {
                log.info("::{}::查询三方赛事为空,三方赛事id={}", linkId, thirdMatchResultDTO.getThirdMatchId());
                return;
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfos.get(0);
            //切换2.0 校验 非切入过2.0 赛事不处理
            MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getModelMatchSettleInfo(thirdMatchInfo.getReferenceId());
            if (matchSettleInfo ==  null) {
                log.info("::{}::没有切换到2.0,三方赛事id={}", linkId, thirdMatchResultDTO.getThirdMatchId());
                return;
            }
            //获取三方盘口数据
            StandardSportMarketExample standardSportMarketExample = new StandardSportMarketExample();
            standardSportMarketExample.createCriteria().andRelationMarketIdEqualTo(Long.parseLong(request.getData().getStandardMarketId())).andDataSourceCodeEqualTo(request.getData().getDataSourceCode());

            List<StandardSportMarket> standardSportMarketList = standardSportMarketMapper.selectByExample(standardSportMarketExample);
            if (standardSportMarketList.size() == 0) {
                log.info("::{}::没有标准盘口,三方赛事id={}", linkId, thirdMatchResultDTO.getThirdMatchId());
                return;
            }
            StandardSportMarket standardSportMarket = standardSportMarketList.get(0);
            //进球第X 球员 148玩法
            boolean push = false;
            if (standardSportMarket.getMarketCategoryId().equals(148l)) {
                //进球第X 进球方式 222玩法
                log.info("::{}::148玩法 球员赛果获取,三方赛事id={}", linkId, thirdMatchResultDTO.getThirdMatchId());
                thirdMarketResultTransService.transFootballPlayerMarketResult(linkId, request.getData(), standardSportMarket);
                push = true;
            } else if (standardSportMarket.getMarketCategoryId().equals(222L)) {
                log.info("::{}::222玩法 进球方式赛果获取,三方赛事id={}", linkId, thirdMatchResultDTO.getThirdMatchId());
                thirdMarketResultTransService.transFootballGoalTypeMarketResult(linkId, request.getData(), standardSportMarket);
                push = true;
            }
            if (push) {
                log.info("::{}:: 推送标准进球事件WS,三方赛事id={}", linkId, thirdMatchResultDTO.getThirdMatchId());
                wsPushService.pushStandardSettleEvent(standardSportMarket.getStandardMatchInfoId(), "goal");
            }
        }catch (Exception e){
            log.error("赛果处理异常linkId ::"+linkId+"::error:",e);
        }
    }


}
