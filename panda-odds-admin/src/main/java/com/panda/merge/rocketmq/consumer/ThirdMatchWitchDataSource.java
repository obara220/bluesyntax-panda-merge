package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.producer.StandardMarketOddsProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_WITCH_DATA_SOURCE;

/**
 * 爬虫内部数据源
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_WITCH_DATA_SOURCE,
        consumerGroup = "odds-group-" + THIRD_MATCH_WITCH_DATA_SOURCE,
        consumeThreadMax = 10,
        consumeTimeout = 10000L)
@DependsOn("oddsAdminApplication")
public class ThirdMatchWitchDataSource extends BaseProcessor implements RocketMQListener<Request<JSONObject>> {

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardMarketOddsProducer standardMarketOddsProducer;
    @Autowired
    private RedisService redisService;

    @Override
    public void onMessage(Request<JSONObject> request) {
        String linkId = request.getLinkId();
        log.info("::{}::ThirdMatchWitchDataSource:{}", linkId, JSONObject.toJSONString(request));
        JSONObject object = request.getData();
        //三方赛事id
        String thirdMatchSourceId = object.getString("thirdMatchSourceId");
        //切换数据源
        String witchDataSourceCode = object.getString("witchDataSourceCode");
        //三方赛事不存在不处理
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItemByThirdMatchSourceId(thirdMatchSourceId);
        if (null == thirdMatchInfo || null == thirdMatchInfo.getReferenceId() || 0L == thirdMatchInfo.getReferenceId()) {
            log.info("::{}::ThirdMatchWitchDataSource,三方赛事不存在:{}", linkId, thirdMatchSourceId);
            return;
        }
        //标准赛事不存在不处理
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(thirdMatchInfo.getReferenceId());
        if (null == standardMatchInfo) {
            log.info("::{}::ThirdMatchWitchDataSource,标准赛事不存在:{}", linkId, thirdMatchSourceId);
            return;
        }
        String key = Constant.REDIS_KEY.THIRD_MATCH_WITCH_DATA_SOURCE_KEY + standardMatchInfo.getId() + "_" + thirdMatchInfo.getDataSourceCode();
        redisService.set(key, witchDataSourceCode, marketCacheTime(standardMatchInfo.getBeginTime()));
        //查找AO三方赛事
        ThirdMatchInfo thirdMatchInfoAO1 = thirdMatchInfoService.getItem(standardMatchInfo.getId(), DataSourceCodeEnum.AO.code);
        if (null == thirdMatchInfoAO1) {
            log.info("::{}::未绑定AO赛事不下发,标准赛事ID:{}", linkId, standardMatchInfo.getId());
            return;
        }
        JSONObject obj = new JSONObject();
        obj.put("dataSource",thirdMatchInfo.getDataSourceCode());
        obj.put("internalDataSourceCode",witchDataSourceCode);
        obj.put("thirdMatchId",thirdMatchInfoAO1.getId());
        standardMarketOddsProducer.sendInternalDataSourceCodeAoAsync(linkId,standardMatchInfo.getId(),obj);
    }

}
