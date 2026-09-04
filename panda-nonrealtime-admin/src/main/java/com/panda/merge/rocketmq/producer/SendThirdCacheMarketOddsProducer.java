package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdMatchInfoDetail;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 推送三方赛事 缓存赔率
 */
@Slf4j
@Component
public class SendThirdCacheMarketOddsProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    private RedisService redisService;

    /**
     * 新增赛事后下发处理
     *
     * @param thirdMatchInfoDetail
     */
    public void sendThirdCacheMarket(String linkId, ThirdMatchInfoDetail thirdMatchInfoDetail, Long thirdSportId) {
        String thirdMatchSourceId = thirdMatchInfoDetail.getThirdMatchSourceId();
        String dataSourceCode = thirdMatchInfoDetail.getDataSourceCode();
        String thirdMarketKey = Constant.REDIS_KEY.RONGHE_THIRD_MARKET + thirdMatchSourceId + "_" + dataSourceCode;
        List<ThirdMarketDTO> sendThirdMarket = new ArrayList<>();
        //TX缓存方式 Map<三方玩法,Map<坑位,盘口数据>>
        if (DataSourceCodeEnum.TX.code.equals(dataSourceCode)) {
            Map<String, Map<Integer, ThirdMarketDTO>> objectMap = redisService.hGetAll(thirdMarketKey);
            if (!CollectionUtils.isEmpty(objectMap)) {
                objectMap.values().forEach(placeMap -> {
                    placeMap.values().forEach(marketDTO -> {
                        sendThirdMarket.add(marketDTO);
                    });
                });
            }
        } else {
            //SR/BG/BC 缓存方式 Map<三方盘口ID,盘口信息>
            Map<String, ThirdMarketDTO> objectMap = redisService.hGetAll(thirdMarketKey);
            if (!CollectionUtils.isEmpty(objectMap)) {
                objectMap.values().forEach(marketDTO -> {
                    sendThirdMarket.add(marketDTO);
                });
            }
        }
        log.info("::{}::新增赛事后下发缓存赔率,三方赛事ID:{},缓存赔率条数:{}", linkId, thirdMatchSourceId, sendThirdMarket.size());
        if (!CollectionUtils.isEmpty(sendThirdMarket)) {
            //构建下发主流程赔率数据
            Request<ThirdMatchMarketDTO> request = new Request<>();
            request.setLinkId(linkId);
            request.setDataSourceTime(TimeUtils.millsSecondsEast8ZoneGmt());
            ThirdMatchMarketDTO thirdMatchMarketDTO = new ThirdMatchMarketDTO();
            thirdMatchMarketDTO.setSportId(thirdSportId);
            thirdMatchMarketDTO.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            thirdMatchMarketDTO.setThirdMatchSourceId(thirdMatchInfoDetail.getThirdMatchSourceId());
            thirdMatchMarketDTO.setDataSourceCode(thirdMatchInfoDetail.getDataSourceCode());
            thirdMatchMarketDTO.setMarketList(sendThirdMarket);
            request.setData(thirdMatchMarketDTO);
            MessageBuilder<Request<ThirdMatchMarketDTO>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
            log.info("::{}::开始组装新增三方赛事赔率消息并下发,topic:THIRD_MATCH_MARKET_API,thirdMatchSourceId:{},dataSourceCode:{},thirdSportId:{}",
                    linkId,thirdMatchInfoDetail.getThirdMatchSourceId(),thirdMatchInfoDetail.getDataSourceCode(),thirdSportId);
            rocketMqTemplate.asyncSend("THIRD_MATCH_MARKET_API:", builder.build(), new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("::{}::,send successful", linkId);
                }

                @Override
                public void onException(Throwable throwable) {
                    log.error("::{}::TOPIC={}，send fail; ", linkId, "THIRD_MATCH_MARKET_API", throwable);
                }
            });
        }
    }

}
