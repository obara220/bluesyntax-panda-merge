package com.panda.merge.mq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.mapper.MatchSettleResultMapper;
import com.panda.merge.model.MatchSettleResult;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.mq.producer.ScoreSettleResultProducer;
import com.panda.merge.utils.SettleNumToScoreCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * 结算2.0事件下发比分中心
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "MATCH_SETTLE_SCORES",
        consumerGroup = "scores-group-MATCH_SETTLE_SCORES-NEW",
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("scoresAdminApplication")
public class MatchSettleNewScoresConsumer implements RocketMQListener<Request<MatchSettleScore>> {

    @Autowired
    ScoreSettleResultProducer scoreSettleResultProducer;

    @Autowired
    RedisService redisService;

    @Autowired
    MatchSettleResultMapper matchSettleResultMapper;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
    /**
     *  结算同步比分中心开关key
     */
    private static final String SETTLE_SYNC_SCORES_KEY = "settle_sync_scores";
    /**
     *
     * 结算比分 计算  进球 角球 罚牌(不包含红黄牌)
     * */
    @Override
    public void onMessage(Request<MatchSettleScore> request) {
        log.info("MatchSettleNewScoresConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
        if (datacenterMergeSwitch && commonProducer.getDatacenterMatchIds(request.getData().getStandardMatchId().toString())) {
            //MQ消息转发给数据中心
            commonProducer.asyncSend(request, "datacenter-MATCH_SETTLE_SCORES",request.getLinkId());
            return;
        }
        String linkId = request.getLinkId();
        Long start = System.currentTimeMillis();
        log.info("{} MatchSettleScoresConsumer事件比分中心处理开始：{}", linkId, start);
        //1、过滤不符合的消息
        if (!check(request.getData())) {
            return;
        }

        handleData(request);

        log.info("{} MatchSettleScoresConsumer事件比分中心处理结束耗时：{}", linkId, System.currentTimeMillis()-start);
    }

    /**
     * 检查mq消息是否合法
     * @param data
     * @return
     */
    private boolean check(MatchSettleScore data){
        //非足球不对接
        if (!data.getSportId().equals(1L) && !data.getSportId().equals(2L)) {
            return false;
        }
        //非已结算不对接
        if (data.getStatus()==null || !data.getStatus().equals(3)) {
            log.info("2--------{}",data);
            return false;
        }
        return true;
    }

    /**
     * 处理同步比分中心的逻辑
     * @param request
     */
    private void handleData(Request<MatchSettleScore> request) {
        MatchSettleScore matchSettleScore = request.getData();
        String scoreCode = SettleNumToScoreCodeUtils.getScoreCodeBySettleScore(matchSettleScore);
        if(scoreCode==null){
            log.info("{} 无法匹配比分编码",matchSettleScore);
            return;
        }
        MatchSettleResult matchSettleResult = matchSettleResultMapper.selectByPrimaryKey(matchSettleScore.getId());
        if(matchSettleResult==null){
            matchSettleResult=this.createMatchSettleResult(matchSettleScore,scoreCode);
        }else {
            matchSettleResult.setT1(matchSettleScore.getT1());
            matchSettleResult.setT2(matchSettleScore.getT2());
            matchSettleResult.setSettleTime(matchSettleScore.getModifyTime());
            matchSettleResultMapper.updateByPrimaryKey(matchSettleResult);
        }
        log.info("更新结算比分到表:{} ",matchSettleResult);
        //发送MQ到下游
        scoreSettleResultProducer.sendResult(matchSettleResult);
    }

    private MatchSettleResult createMatchSettleResult(MatchSettleScore matchSettleScore, String scoreCode) {
        MatchSettleResult matchSettleResult =new MatchSettleResult();
        matchSettleResult.setId(matchSettleScore.getId());
        matchSettleResult.setScoreCode(scoreCode);
        matchSettleResult.setT1(matchSettleScore.getT1());
        matchSettleResult.setT2(matchSettleScore.getT2());
        matchSettleResult.setModifyTime(System.currentTimeMillis());
        matchSettleResult.setCreateTime(System.currentTimeMillis());
        matchSettleResult.setSportId(matchSettleScore.getSportId());
        matchSettleResult.setStandardMatchId(matchSettleScore.getStandardMatchId());
        matchSettleResult.setSettleTime(matchSettleScore.getModifyTime());
        //1 比分 2 事件
        matchSettleResult.setScoresType(1);
        matchSettleResult.setSettleScoreEventId(matchSettleScore.getId());
        matchSettleResult.setLinkId(matchSettleScore.getId().toString());
        matchSettleResultMapper.insert(matchSettleResult);
        return matchSettleResult;
    }


}
