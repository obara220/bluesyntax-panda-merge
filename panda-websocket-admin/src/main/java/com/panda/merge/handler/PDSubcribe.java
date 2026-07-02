package com.panda.merge.handler;

import cn.hutool.json.JSONArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.panda.merge.cache.MyCacheService;
import com.panda.merge.dto.advertise.PDFootBallMatchEventDto;
import com.panda.merge.dto.request.*;
import com.panda.merge.dto.response.*;
import com.panda.merge.dto.response.StandardSettleScoresPushDto;
import com.panda.merge.dto.settle.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;



@Service
@Slf4j
public class PDSubcribe   {




    public synchronized void sendPdEvent(String body) {
        //1.先转json 得到 三方赛事ID
        PDFootBallMatchEventDto pdFootBallMatchEventDto = JSONObject.toJavaObject( JSON.parseObject(body)  , PDFootBallMatchEventDto.class);
        PDMatchEventResponseVo responseVo=new PDMatchEventResponseVo();
        responseVo.setData(pdFootBallMatchEventDto);
        //2.根据三方赛事ID遍历缓存的user 然后递推session 记得报错捕获防止意外
        for (PdSubCacheVo value : MyCacheService.sessionIdPdMatchMap.values()) {
            try {
//                synchronized (value.getSession()) {
                    if (value.getSession().isOpen()) {
                        if (value.getThirdMatchId().toString().equals(pdFootBallMatchEventDto.getThirdMatchId())) {
                            value.getSession().sendText(JSONObject.toJSONString(responseVo, SerializerFeature.WriteMapNullValue));
                        }
                    }
//                }
            }catch (Exception e){
                log.error("::sendPdEvent::",e);
            }

        }
    }

    public synchronized void sendPdScore(String body) {
        JSONObject  jsonObject = JSONObject.parseObject(body);
        Long thirdMatchId =jsonObject.getLong("thirdMatchId");
        PDMatchScoreResponseVo responseVo=new PDMatchScoreResponseVo();
        jsonObject.put("isDanger",jsonObject.get("danger"));
        responseVo.setData(jsonObject);
        for (PdSubCacheVo value : MyCacheService.sessionIdPdMatchMap.values()) {
            try {
//                synchronized (value.getSession()) {
                    if (value.getSession().isOpen()) {
                        if (value.getThirdMatchId().equals(thirdMatchId)) {
                            value.getSession().sendText(JSONObject.toJSONString(responseVo, SerializerFeature.WriteMapNullValue));
                        }
                    }
//                }
            }catch (Exception e){
                log.error("::sendPdScore::",e);
            }

        }
    }


    public synchronized void sendStandardSettleScores(StandardSettleScoresPushDto data) {
        SettleMatchScoresResponseVo responseVo =new SettleMatchScoresResponseVo();
        responseVo.setData(data);
        for (SettleMatchSubCacheVo value : MyCacheService.sessionIdSettleMatchMap.values()) {
            try {
//                synchronized (value.getSession()) {
                    if (value.getSession().isOpen()) {
                        if (value.getStandardMatchId().equals(data.getStandardMatchId())) {
                            value.getSession().sendText(JSONObject.toJSONString(responseVo, SerializerFeature.WriteMapNullValue));
                        }
                    }
//                }
            }catch (Exception e){
                log.error("::sendStandardSettleScores::",e);
            }

        }
    }

    public synchronized void sendStandardSettleEvent(StandardSettleEventPushDto data) {
        SettleMatchEventResponseVo responseVo =new SettleMatchEventResponseVo();
        responseVo.setData(data);
        for (SettleMatchSubCacheVo value : MyCacheService.sessionIdSettleMatchMap.values()) {
            try {
//                synchronized (value.getSession()) {
                    if (value.getSession().isOpen()) {
                        if (value.getStandardMatchId().equals(data.getStandardMatchId())) {
                            value.getSession().sendText(JSONObject.toJSONString(responseVo, SerializerFeature.WriteMapNullValue));
                        }
//                    }
                }
            }catch (Exception e){
                log.error("::sendStandardSettleEvent::",e);
            }

        }
    }

    public synchronized void sendThirdSettleEvent(ThirdMatchSettleEventDto data) {
        SettleMatchThirdEventResponseVo responseVo =new SettleMatchThirdEventResponseVo();
        responseVo.setData(data);
        for (SettleMatchSubCacheVo value : MyCacheService.sessionIdSettleMatchMap.values()) {
            try {
//                synchronized (value.getSession()) {
                    if (value.getSession().isOpen()) {
                        if (value.getStandardMatchId().equals(data.getStandardMatchId())) {
                            value.getSession().sendText(JSONObject.toJSONString(responseVo, SerializerFeature.WriteMapNullValue));
                        }
//                    }
                }
            }catch (Exception e){
                log.error("::sendThirdSettleEvent::",e);
            }

        }
    }

    public synchronized void sendThirdSettleScores(ThirdMatchSettleScoresDto data) {
        SettleMatchThirdScoresResponseVo responseVo =new SettleMatchThirdScoresResponseVo();
        responseVo.setData(data);
        for (SettleMatchSubCacheVo value : MyCacheService.sessionIdSettleMatchMap.values()) {
            try {
//                synchronized (value.getSession()) {
                    if (value.getSession().isOpen()) {
                        if (value.getStandardMatchId().equals(data.getStandardMatchId())) {
                            value.getSession().sendText(JSONObject.toJSONString(responseVo, SerializerFeature.WriteMapNullValue));
                        }
                    }
//                }
            }catch (Exception e){
                log.error("::sendThirdSettleScores::",e);
            }

        }
    }

    /**
     * 赛事列表结算通知
     * @param data
     */
    public synchronized void sendMatchListSettle(MatchListSettleDto data) {
        SettleMatchListResponseVo responseVo =new SettleMatchListResponseVo();
        responseVo.setData(data);
        for (SettleMatchListSubCacheVo value : MyCacheService.sessionIdSettleMatchListMap.values()) {
            try {
//                synchronized (value.getSession()) {
                    if (value.getSession().isOpen()) {
                        if (value.getStandardMatchIdList().contains(data.getStandardMatchId()) ) {
                            value.getSession().sendText(JSONObject.toJSONString(responseVo, SerializerFeature.WriteMapNullValue));
                        }
                    }
//                }
            }catch (Exception e){
                log.error("::sendMatchListSettle::",e);
            }

        }
    }

    /**
     * 推送数据商自动结算开关状态
     * @param data
     */
    public synchronized void sendAutoSettleDataSourceStatus(AutoSettleDataSourceDto data){
        AutoSettleDataSourceResponseVo responseVo =new AutoSettleDataSourceResponseVo();
        responseVo.setData(data);
        for (AutoSettleDataSourceSubCacheVo value : MyCacheService.sessionIdAutoSettleDataSourceMap.values()) {
            try {
//                synchronized (value.getSession()) {
                    if (value.getSession().isOpen()) {
                        value.getSession().sendText(JSONObject.toJSONString(responseVo, SerializerFeature.WriteMapNullValue));
                    }
//                }
            }catch (Exception e){
                log.error("::sendAutoSettleDataSourceStatus::",e);
            }
        }
    }

    /**
     * 推送赛事回滚状态
     * @param data
     */
    public synchronized void sendMatchSettleRollBackStatus(MatchSettleRollBackDto data){
        MatchSettleRollBackResponseVo responseVo =new MatchSettleRollBackResponseVo();
        responseVo.setData(data);
        for (MatchSettleRollBackVo value : MyCacheService.sessionIdMatchSettleRollBackSourceMap.values()) {
            try {
//                synchronized (value.getSession()) {
                    if (value.getSession().isOpen()) {
                        value.getSession().sendText(JSONObject.toJSONString(responseVo, SerializerFeature.WriteMapNullValue));
                    }
//                }
            }catch (Exception e){
                log.error("::sendMatchSettleRollBackStatus::",e);
            }
        }
    }

    public synchronized void sendMatchScore(String body) {
        JSONObject  jsonObject = JSONObject.parseObject(body);
        if(jsonObject==null){
            return;
        }
        Long standardMatchId =jsonObject.getLong("standardMatchId");
        if(standardMatchId==null){
            return;
        }
        StandardScoreResponseVo responseVo=new StandardScoreResponseVo();
        responseVo.setData(jsonObject);
        for (StandardMatchScoreCatchVo value : MyCacheService.sessionIdMatchScoreMap.values()) {
            try {
                if (value.getSession().isOpen()) {
                    if (value.getStandardMatchId().equals(standardMatchId)) {
                        value.getSession().sendText(JSONObject.toJSONString(responseVo, SerializerFeature.WriteMapNullValue));
                    }
                }
            } catch (Exception e) {
                log.error("::sendMatchScore::", e);
            }
        }
    }

    public synchronized void sendSPSettleMatchPush(AutoSettleDataSourceDto data) {
        AutoSettleDataSourceResponseVo responseVo =new AutoSettleDataSourceResponseVo();
        responseVo.setData(data);
        for (SettleMatchSubCacheVo value : MyCacheService.sessionIdSettleMatchMap.values()) {
            try {
                if(data.getStandardMatchId().equals(value.getStandardMatchId().toString())){
//                synchronized (value.getSession()) {
                    if (value.getSession().isOpen()) {
                        SPSettleMatchResponseVo spSettleMatchResponseVo =new SPSettleMatchResponseVo();
                        spSettleMatchResponseVo.setStandardMatchId(data.getStandardMatchId());
                        value.getSession().sendText(JSONObject.toJSONString(spSettleMatchResponseVo, SerializerFeature.WriteMapNullValue));
                        log.info("sendSPSettleMatchPush推送数据:{}",JSONObject.toJSONString(spSettleMatchResponseVo, SerializerFeature.WriteMapNullValue));

                    }
                }
//                }
            }catch (Exception e){
                log.error("::sendSPSettleMatchPush::",e);
            }
        }
    }

    /**
     * 推送数据商连接状态
     * @param data
     */
    public synchronized void sendDataSourceConnectionStatus(DataSourceConnectionStatusDto data){
        DataSourceConnectionStatusResponseVo responseVo = new DataSourceConnectionStatusResponseVo();
        responseVo.setData(data);
        for (MatchSettleRollBackVo value : MyCacheService.sessionIdMatchSettleRollBackSourceMap.values()) {
            try {
                if (value.getSession().isOpen()) {
                    value.getSession().sendText(JSONObject.toJSONString(responseVo, SerializerFeature.WriteMapNullValue));
                    log.info("推送数据商连接状态,standardMatchId:{},datasourceCount:{}",
                            data.getStandardMatchId(),
                            data.getDatasourceStatusMap() != null ? data.getDatasourceStatusMap().size() : 0);
                }
            }catch (Exception e){
                log.error("::sendDataSourceConnectionStatus::",e);
            }
        }
    }
}