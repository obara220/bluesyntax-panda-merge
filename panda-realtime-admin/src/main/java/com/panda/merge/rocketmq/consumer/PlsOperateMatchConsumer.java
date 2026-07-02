package com.panda.merge.rocketmq.consumer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.panda.merge.bo.StandardMatchPeriodBO;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportTournament;
import com.panda.merge.rocketmq.producer.RealtimeBaseProduecr;
import com.panda.merge.rocketmq.producer.StandardMatchStatusProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportTournamentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import static com.panda.merge.constant.ConstantSystem.*;


/**
 * 3803 PLS比分网切换赛程标准赛事通知，收到通知需要补发赛事赛事状态和赛事阶段
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = NOTIFY_SCORE_CENTER_SEND_SCORE_PLS,
        consumerGroup = CONSUME_REALTIME_GROUP + NOTIFY_SCORE_CENTER_SEND_SCORE_PLS,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("realtimeAdminApplication")
public class PlsOperateMatchConsumer implements RocketMQListener<String> {

    @Autowired
    public RealtimeBaseProduecr realtimeBaseProduecr;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardSportTournamentService standardSportTournamentService;
    @Autowired
    private StandardMatchStatusProducer standardMatchStatusProducer;

    @Override
    public void onMessage(String jsonStr) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String linkId = NOTIFY_SCORE_CENTER_SEND_SCORE_PLS;
        try {
            if (StrUtil.isEmpty(jsonStr)) {
                return;
            }
            JSONObject jsonObj = new JSONObject(jsonStr);
            if (null == jsonObj.get("data")) {
                return;
            }
            JSONObject data = jsonObj.getJSONObject("data");
            linkId = jsonObj.getStr("linkId");
            log.info("linkId=【{}】NOTIFY_SCORE_CENTER_SEND_SCORE_PLS,PLS比分网切换赛程标准赛事通知开始，data={}", linkId, data.toString());
            Long standatdMatchId = data.getLong("standardMatchId");
            if (standatdMatchId == null) {
                return;
            }
            //当前标准赛事信息
            StandardMatchInfo itemDB = standardMatchInfoService.getItem(standatdMatchId);
            //阶段数据通知比分网
            StandardMatchPeriodBO standardMatchPeriodBO = new StandardMatchPeriodBO();
            BeanUtils.copyProperties(itemDB, standardMatchPeriodBO);
            standardMatchPeriodBO.setStandardMatchId(itemDB.getId());
            StandardSportTournament standardSportTournament = standardSportTournamentService.getItem(itemDB.getStandardTournamentId());
            if (standardSportTournament != null) {
                standardMatchPeriodBO.setPlsStandardTournamentId(standardSportTournament.getPlsStandardTournamentId());
            }
            realtimeBaseProduecr.send(standardMatchPeriodBO, linkId, STANDARD_MATCH_INFO_PERIODID_PLS, itemDB.getId() + "", itemDB.getDataSourceCode());

            //赛事状态通知比分网
            standardMatchStatusProducer.sendStandardMatchStatusPls(linkId, itemDB, System.currentTimeMillis(), standardSportTournament);
            stopWatch.stop();
            log.info("linkId=【{}】NOTIFY_SCORE_CENTER_SEND_SCORE_PLS,PLS比分网切换赛程标准赛事通知结束，共耗时={}", linkId);
        } catch (Exception e) {
            log.error("linkId=【" + linkId + "】NOTIFY_SCORE_CENTER_SEND_SCORE_PLS,PLS比分网切换赛程标准赛事通知处理异常,jsonStr="+jsonStr+",Exception:", e);
        }
    }


}
