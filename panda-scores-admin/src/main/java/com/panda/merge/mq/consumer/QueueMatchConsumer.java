package com.panda.merge.mq.consumer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.DataSourceConstant;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.sourceSwitch.BasketballSwitch;
import com.panda.merge.dto.sourceSwitch.FootballSwitch;
import com.panda.merge.dto.sourceSwitch.TennisSwitch;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.repository.ScoresRedisHelp;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;


/**
 * 比分中心生成标准比分（赛程生成标准赛事后下发）
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "modify_match",
        consumerGroup = "scores_group_queue_match",
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("scoresAdminApplication")
public class QueueMatchConsumer implements RocketMQListener<String> {


    @Autowired
    RedisService redisService;

    @Autowired
    ScoresRedisHelp scoresRedisHelp;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
    @Override
    public void onMessage(String s) {
        log.info("QueueMatchConsumer MQ消费数据开始...{}",datacenterMergeSwitch);

        StopWatch extWatch = new StopWatch();
        extWatch.start();
        if(StrUtil.isEmpty(s)){
            return;
        }
       try{
           JSONObject jsonObj = new JSONObject(s);
           if(null == jsonObj.get("data")) {
               return;
           }
            JSONArray jsonArray = jsonObj.getJSONArray("data");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            if(jsonObject==null){
                return;
            }
            Long standatdMatchId = 0L;
            if(null != jsonObject.get("id")){
                String matchId = jsonObject.getStr("id");
                standatdMatchId = Long.parseLong(matchId);
            }
           if (datacenterMergeSwitch) {
               //MQ消息转发给数据中心
               commonProducer.asyncSend(s, "datacenter-modify_match",standatdMatchId+"");
               return;
           }
           StandardMatchScores standardMatchScores = scoresRedisHelp.getCatchStandScoreByMatchId(standatdMatchId);
            if(null!=standardMatchScores){
                if(StrUtil.isEmpty(standardMatchScores.getMatchManageId())){
                    log.info("已存在标准赛事：,重新保存：{}",standatdMatchId);
                    //已存在
                    standardMatchScores.setMatchManageId(jsonObject.getStr("matchManageId"));
                    standardMatchScores.setUpdateTime(System.currentTimeMillis());
                    scoresRedisHelp.saveCatchStandScore(standardMatchScores);
                }
                return;
            }
            StandardMatchScores scores = new StandardMatchScores();
            Long sportId = 0L;
            if(null != jsonObject.get("sportId")){
               Integer sport = (Integer) jsonObject.get("sportId");
               sportId = Long.parseLong(sport.toString());
               scores.setSportId(sportId);
               //只保存网球乒乓球羽毛球排球 需求3838:新增足球、篮球
               if(!DataSourceConstant.STANDARC_SCORE_SPORTIDS.contains(sportId)){
                   return;
               }
            }
            if(null != jsonObject.get("matchManageId")){
               String mmid = (String) jsonObject.get("matchManageId");
               scores.setMatchManageId(mmid);
            }
            scores.setThirdMatchId(null);
            scores.setMatchId(standatdMatchId);
            scores.setShowStatus(0);
            if(null != jsonObject.get("businessEvent")){
                scores.setDataSourceCode(jsonObject.get("businessEvent").toString());
            }
            scores.setDataSourceAccoSwitch(getSwitchsAsSportId(sportId));
            scores.setCreateTime(System.currentTimeMillis());
            scores.setUpdateTime(System.currentTimeMillis());
            scores.setScoreJson("");
            scores.setSendSettleCount(0);
            scoresRedisHelp.saveCatchStandScore(scores);
            extWatch.stop();
            log.info("保存标准比分数据：{},用时:{}",standatdMatchId,extWatch.getTotalTimeMillis());
       }catch (Exception e){
           log.error("队列匹配赛事数据异常：{}",e.getMessage(),e);
       }
    }

    private static String getSwitchsAsSportId(Long sportId) {
        String dataSourceAccoSwitch = "";
        if(SportTypeEnum.FOOTBALL.getValue().equals(sportId)){
            FootballSwitch footballSwitch = new FootballSwitch();
            dataSourceAccoSwitch = JSONUtil.toJsonStr(footballSwitch);
        }else if(SportTypeEnum.BASKETBALL.getValue().equals(sportId)){
            BasketballSwitch basketballSwitch = new BasketballSwitch();
            dataSourceAccoSwitch = JSONUtil.toJsonStr(basketballSwitch);
        }else{
            TennisSwitch switchs = new TennisSwitch();
            dataSourceAccoSwitch = JSONUtil.toJsonStr(switchs);
        }
        return dataSourceAccoSwitch;
    }

//    public static void main(String[] args) {
//        System.out.println(DigestUtil.md5Hex("FOOTBALL_STANDARD_MATCH_SCORES:39795016"));
//    }
}
