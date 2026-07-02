package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchStatisticsInfoDetailMessage;
import com.panda.merge.dto.message.MatchStatisticsInfoMessage;
import com.panda.merge.model.ThirdMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 下发赛事统计信息
 *
 * @author : bevan
 * @since 2020年12月20日13:47:24
 * -------- --------- --------------------------
 */
@Slf4j
@Component
public class MatchStatisticsInfoProducer {

    @Autowired
    private RealtimeBaseProduecr realtimeBaseProduecr;


    public void sendMatchStatisticsInfo(String linkId, MatchStatisticsInfoMessage matchStatisticsInfoMessage, ThirdMatchInfo thirdMatchInfo) {
        //转换比分
        if (!CollectionUtils.isEmpty(matchStatisticsInfoMessage.getMatchStatisticsInfoDetailList())) {
            List<MatchStatisticsInfoDetailMessage> matchStatisticsInfoDetailMessages = matchHomeAwayExchange(matchStatisticsInfoMessage.getMatchStatisticsInfoDetailList(), thirdMatchInfo);
            if (!CollectionUtils.isEmpty(matchStatisticsInfoDetailMessages)) {
                matchStatisticsInfoMessage.setMatchStatisticsInfoDetailList(matchStatisticsInfoDetailMessages);
            }
        }
        Request<MatchStatisticsInfoMessage> request = new Request<>(matchStatisticsInfoMessage, linkId, null, thirdMatchInfo.getReferenceId() + "", thirdMatchInfo.getDataSourceCode());
        if (ONE.equals(thirdMatchInfo.getLiveOddSupport())) {
            request.setDataType("STANDARD_MATCH_STATISTICS");
            realtimeBaseProduecr.sendAdminOrSpare(request,thirdMatchInfo,ZERO);
            log.info("linkId=【{}】开始组装赛事统计信息并下发,request={}", linkId, JSON.toJSONString(request));
        } else {
            request.setDataType("STANDARD_MATCH_STATISTICS_NO_LIVE");
            //不支持滚球的赛事统计信息单独下发，报球板需要
            realtimeBaseProduecr.sendAdminOrSpare(request,thirdMatchInfo,ZERO);
            log.info("linkId=【{}】开始组装赛前盘赛事统计信息并下发,request={}", linkId, JSON.toJSONString(request));
        }
    }


    /**
     * 主客队比分对调
     *
     * @param matchStatisticsInfoDetailList 需要转换的统计比分列表
     * @param thirdMatchInfo                当前事件列表对应的三方赛事信息
     */
    public List<MatchStatisticsInfoDetailMessage> matchHomeAwayExchange(List<MatchStatisticsInfoDetailMessage> matchStatisticsInfoDetailList, ThirdMatchInfo thirdMatchInfo) {
        //目前只处理足球
        if (StandardSportTypeEnum.FootBall.getCode().equals(thirdMatchInfo.getSportId())) {
            //如果主客队是相反
            if (ONE.equals(thirdMatchInfo.getHomeAwayOpposite())) {
                for (MatchStatisticsInfoDetailMessage message : matchStatisticsInfoDetailList) {
                    //主客队比分互换
                    Integer t1 = message.getT1() != null ? message.getT1() : ZERO;
                    Integer t2 = message.getT2() != null ? message.getT2() : ZERO;
                    if (!t1.equals(t2)) {
                        message.setT1(t2);
                        message.setT2(t1);
                    }
                }
            }
        }
        return null;
    }
}
