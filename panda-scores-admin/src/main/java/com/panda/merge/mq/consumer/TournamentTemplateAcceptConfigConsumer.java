package com.panda.merge.mq.consumer;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.mq.message.TournamentTemplateAcceptConfigScore;
import com.panda.merge.mq.message.TournamentTemplateAcceptConfigScoreDTO;
import com.panda.merge.mq.producer.CommonProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;



/**
 * 玩法集tMax开关配置
 * 风控下发
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "Tournament_Template_Accept_Config_Score",
        consumerGroup = "scores-group-Tournament_Template_Accept_Config_Score",
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("scoresAdminApplication")
public class TournamentTemplateAcceptConfigConsumer implements RocketMQListener<String> {


    @Autowired
    RedisService redisService;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
    @Override
    public void onMessage(String s) {
        log.info("TournamentTemplateAcceptConfigConsumer MQ消费数据开始...{}",datacenterMergeSwitch);

        if(StrUtil.isEmpty(s)){
            return;
        }
        JSONObject jsonObj = new JSONObject(s);
        JSONObject data = jsonObj.getJSONObject("data");
        String linkId = data.getStr("linkId");
        if (datacenterMergeSwitch) {
            //MQ消息转发给数据中心
            commonProducer.asyncSend(s, "datacenter-Tournament_Template_Accept_Config_Score",linkId);
            return;
        }
        log.info("TournamentTemplateAcceptConfigScore接收开关配置 监控标准比分处理开始：{}", s);
        try{
            TournamentTemplateAcceptConfigScore cinfig = data.toBean(new TypeReference<TournamentTemplateAcceptConfigScore>() {
            });
            if(cinfig==null){
                return;
            }
            if(!cinfig.getList().isEmpty()) {
                log.info("{}，TournamentTemplateAcceptConfigScore接收开关配置 ,cinfig.getList()={}",linkId,cinfig.getList());
                List<TournamentTemplateAcceptConfigScoreDTO> list = cinfig.getList();
                for(TournamentTemplateAcceptConfigScoreDTO obj:list){
                    String redisKey = "MATCH:CATEGORYSET:SWITCH:"+cinfig.getMatchId()+"_"+obj.getCategoryType();
                    redisService.set(redisKey,
                            obj.getIsOpen(), RedisConfig.REDIS_WEEK_TIME);
                    log.info("{}，TournamentTemplateAcceptConfigScore接收开关配置 监控标准比分处理：redisKey:{}",linkId,redisKey);

                }
            }
            log.info("{}，TournamentTemplateAcceptConfigScore接收开关配置 监控标准比分处理完成：{}",linkId,s);
        }catch(Exception e){
            log.error("{}TournamentTemplateAcceptConfigScore 接收开关配置:",linkId,e);
        }
    }


//    public static void main(String[] args) {
//                String s = "{\"data\":{\"linkId\":\"d983582f575f444bb8fbb97b7fe36b6a_trade\",\"list\":[{\"categorySetId\":586,\"categoryType\":\"faCard\",\"id\":874,\"isOpen\":1,\"templateId\":7497586},{\"categorySetId\":1000000179,\"categoryType\":\"otCorner\",\"id\":875,\"isOpen\":1,\"templateId\":7497586},{\"categorySetId\":1000000180,\"categoryType\":\"otFaCard\",\"id\":876,\"isOpen\":1,\"templateId\":7497586},{\"categorySetId\":1000000208,\"categoryType\":\"corner\",\"id\":877,\"isOpen\":1,\"templateId\":7497586}],\"matchId\":3811784},\"dataSourceTime\":1736498900436,\"linkId\":\"d983582f575f444bb8fbb97b7fe36b6a_trade\",\"operaterId\":-1}";
//        JSONObject jsonObj = new JSONObject(s);
//        JSONObject data = jsonObj.getJSONObject("data");
//        TournamentTemplateAcceptConfigScore cinfig = data.toBean(new TypeReference<TournamentTemplateAcceptConfigScore>() {
//        });
//        System.out.println(cinfig.getLinkId());
//        System.out.println(data.get("linkId"));
//    }
}
