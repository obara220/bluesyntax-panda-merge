package com.panda.merge.rocketmq.consumer;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.MatchStatusEnum;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchStatusDTO;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.rocketmq.processor.ThirdMatchStatusProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 需求2659 PD报球板新增可删除数据商事件(可以覆盖完赛状态和阶段)
 * @author : lans
 * @since   2023年10月21日16:39:25
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = MATCH_CANCEL_END,
        consumerGroup = CONSUME_REALTIME_GROUP + MATCH_CANCEL_END,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class StandardMatchStatusConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ThirdMatchStatusProcessor thirdMatchStatusProcessor;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private RedisService redisService;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer dataCenterProducer;

    @Override
    public void onMessage(MessageExt ext) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(ext,MATCH_CANCEL_END);
            return;
        }
        String linkId = UUIdUtils.getId()+"";
        try{
            String message = new String(ext.getBody(), "utf-8");
            if(StringUtils.isNotBlank(ext.getMsgId())){
                linkId = ext.getMsgId();
            }
            log.info("【"+PROJECT_ID_REALTIME+":"+ MATCH_CANCEL_END+"::"+linkId+"::】接收到PD报球板新增可删除数据商事件信息开始，message:{}",message);
            JSONObject data = JSONObject.parseObject(message).getJSONObject("data");
            Long standardMatchId = data.getLong("standardMatchId");
            //1.获取标准赛事
            StandardMatchInfo oldStandardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
            if (null == oldStandardMatchInfo) {
                log.info("【"+PROJECT_ID_REALTIME+":"+ MATCH_CANCEL_END+"::"+linkId+"::】标准赛事不存在，标准赛事id：{}", standardMatchId);
                return;
            }
            //2.更新标准赛事
            StandardMatchInfo upMatchInfo = new StandardMatchInfo();
            upMatchInfo.setId(standardMatchId);
            upMatchInfo.setMatchStatus(data.getLong("matchStatus").intValue());
            upMatchInfo.setMatchPeriodId(data.getLong("periodId"));
            //标准赛事已经完赛特殊处理逻辑
            if(YesNoEnum.Y.value.equals(oldStandardMatchInfo.getMatchOver())){
                //判断是否完赛
                List<Integer> matchStatusList = Arrays.asList(MatchStatusEnum.Ended.value, MatchStatusEnum.Cancelled.value,
                        MatchStatusEnum.Closed.value,  MatchStatusEnum.Ended999.value);
                if (!matchStatusList.contains(upMatchInfo.getMatchStatus())) {
                    upMatchInfo.setMatchOver(YesNoEnum.N.value);
                }
            }
            oldStandardMatchInfo = standardMatchInfoService.updateByPrimaryKeySelective(upMatchInfo);

            //直接查询数据库中的开售信息
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.refreshCache(standardMatchId);
            //PD可以强制回滚标准赛事状态
            if(StringUtils.isNotBlank(standardSportMarketSell.getMatchStatusSourceCode())){
                oldStandardMatchInfo.setDataSourceCode(standardSportMarketSell.getMatchStatusSourceCode());
            }
            //3. 下发赛事状态给业务
            thirdMatchStatusProcessor.pushMatchStatusInfo(linkId, oldStandardMatchInfo, standardSportMarketSell,
                    oldStandardMatchInfo.getDataSourceCode(), Calendar.getInstance().getTimeInMillis());
            //删除自动关盘
            String autoCloseRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + standardMatchId);
            redisService.del(autoCloseRedisKey);
        }catch (Exception e){
            log.error("【"+PROJECT_ID_REALTIME+":"+ MATCH_CANCEL_END+"::"+linkId+"::】接收到PD报球板新增可删除数据商事件信息异常:Exception",e);
        }
    }
}
