package com.panda.merge.rocketmq.consumer;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.EnableSecondRocketMQCluster;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO_API;

/**
 * PD 赛事阶段回退 55493
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO_API,
        consumerGroup = "odds-group-" + CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO_API,
        consumeThreadMax = 20,
        consumeTimeout = 10000L)
@DependsOn("oddsAdminApplication")
@EnableSecondRocketMQCluster
public class MatchPeriodBackConsumer implements RocketMQListener<Request<MatchEventInfoDTO>> {

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    RedisService redisService;

    @Override
    public void onMessage(Request<MatchEventInfoDTO> request) {
        String linkId = request.getLinkId();
        MatchEventInfoDTO matchEventInfoDTO = request.getData();
        String dataSourceCode = matchEventInfoDTO.getDataSourceCode();
        String thirdMatchSourceId = matchEventInfoDTO.getThirdMatchSourceId();
        //查询三方赛事，找到标准赛事id
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(dataSourceCode, thirdMatchSourceId);
        if (null == thirdMatchInfo || 0 == thirdMatchInfo.getReferenceId() || null == thirdMatchInfo.getReferenceId()) {
            log.info("::{}::MatchPeriodBackConsumer,PD三方赛事或标准赛事不存在!", linkId);
            return;
        }
        //删除自动关盘缓存
        String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + thirdMatchInfo.getReferenceId());
        redisService.del(redisKey);
        log.info("::{}::MatchPeriodBackConsumer,删除自动关盘缓存成功，标准赛事ID：{}", linkId, thirdMatchInfo.getReferenceId());
    }
}
