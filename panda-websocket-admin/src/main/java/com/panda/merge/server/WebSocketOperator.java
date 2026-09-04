package com.panda.merge.server;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.panda.merge.cache.MyCacheService;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.RedisKeyConstant;
import com.panda.merge.constant.SubscriptionTypeEnum;
import com.panda.merge.dto.message.*;
import com.panda.merge.dto.request.*;
import com.panda.merge.dto.response.OnlineResponseVo;
import com.panda.merge.dto.response.QueryScoresResponseVo;
import com.panda.merge.service.ScoreEventService;
import com.panda.merge.utils.ApplicationContextHelper;
import com.panda.sports.api.ISystemUserOrgAuthApi;
import com.panda.sports.api.vo.SysOrgAuthVO;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import org.yeauty.annotation.OnClose;
import org.yeauty.annotation.OnError;
import org.yeauty.annotation.OnMessage;
import org.yeauty.annotation.OnOpen;
import org.yeauty.annotation.ServerEndpoint;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @project Name :  panda_data_service
 * @package Name :  com.panda.sports.manager.realtime.server
 * @description : --------  ---------  -------------------------- url =  'ws://localhost:port/realData/';
 * 'ws://localhost:6678/realData/'; ServerEndpoint 后边的连接地址  realData/ 加"/"是为了和其他服务器保持一致.
 */

@Slf4j
@Component
@ServerEndpoint(value = "/realData/", port = "${websocket.port:8989}")
public class WebSocketOperator {

    /**
     * 静态变量 用来记录当前在线连接数 应该把它设计成线程安全的
     */
    private static volatile int onlineCount = 0;

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    public WebSocketOperator() {
        super();
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, HttpHeaders headers) {
        // 加入到服务器管理会话
        log.info("websocket:onOpen,sessionId:{}", session.id().asLongText());
        try {
            sendMessage("连接成功", session);
        } catch (IOException e) {
            log.error("websocket IO异常");
        }

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        session.close();
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session)
    {
        RequestVo requestVo = JSON.parseObject(message, RequestVo.class);

        try {
            Long startTotal = System.currentTimeMillis();
            Long start;
            Long end;
            if(requestVo.getCommand()==null){
                return;
            }
            //1.心跳处理
            if(requestVo.getCommand().equals( SubscriptionTypeEnum.HEART.getCode())){
                start = System.currentTimeMillis();
                handleHeart(session);
                end = System.currentTimeMillis();
                if (end-start>100){
                    log.info("ws handleHeart use total time:{} ms",(end-start));
                }
            }
            //2.比分列表寻轮
            if(requestVo.getCommand().equals( SubscriptionTypeEnum.SCORE_PAGE.getCode())){
                start = System.currentTimeMillis();
                handleScoresPage(session,requestVo);
                end = System.currentTimeMillis();
                if (end-start>100){
                    log.info("ws handleHeart use total time:{} ms",(end-start));
                }
            }
            //3.事件流查询
            if(requestVo.getCommand().equals( SubscriptionTypeEnum.SCORE_EVENT.getCode())){
                start = System.currentTimeMillis();
                handleScoresEvent(session,requestVo);
                end = System.currentTimeMillis();
                if (end-start>100){
                    log.info("ws handleScoresEvent use total time:{} ms",(end-start));
                }
            }//
            if(requestVo.getCommand().equals( SubscriptionTypeEnum.PD_MATCH_SUB.getCode())) {
                start = System.currentTimeMillis();
                handlePdMatchSub(session, requestVo);
                end = System.currentTimeMillis();
                if (end-start>100){
                    log.info("ws handlePdMatchSub use total time:{} ms",(end-start));
                }
            }//
            if(requestVo.getCommand().equals( SubscriptionTypeEnum.PD_MATCH_CANCEL.getCode())) {
                start = System.currentTimeMillis();
                handlePdMatchCancel(session);
                end = System.currentTimeMillis();
                if (end-start>100){
                    log.info("ws handlePdMatchCancel use total time:{} ms",(end-start));
                }
            }
            if(requestVo.getCommand().equals( SubscriptionTypeEnum.SETTLE_MATCH_SUB.getCode())) {
                start = System.currentTimeMillis();
                handleSettleMatchSub(session, requestVo);
                end = System.currentTimeMillis();
                if (end-start>100){
                    log.info("ws handleSettleMatchSub use total time:{} ms",(end-start));
                }
            }//
            if(requestVo.getCommand().equals( SubscriptionTypeEnum.SETTLE_MATCH_CANCEL.getCode())) {
                start = System.currentTimeMillis();
                handleSettleMatchCancel(session);
                end = System.currentTimeMillis();
                if (end-start>100){
                    log.info("ws handleSettleMatchCancel use total time:{} ms",(end-start));
                }
            }
            if(requestVo.getCommand().equals( SubscriptionTypeEnum.SETTLE_MATCH_LIST_SUB.getCode())) {
                start = System.currentTimeMillis();
                handleSettleMatchListSub(session, requestVo);
                end = System.currentTimeMillis();
                if (end-start>100){
                    log.info("ws handleSettleMatchListSub use total time:{} ms",(end-start));
                }
            }
            if(requestVo.getCommand().equals( SubscriptionTypeEnum.SETTLE_MATCH_LIST_CANCEL.getCode())) {
                start = System.currentTimeMillis();
                handleSettleMatchListCancel(session);
                end = System.currentTimeMillis();
                if (end-start>100){
                    log.info("ws handleSettleMatchListCancel use total time:{} ms",(end-start));
                }
            }
            if(requestVo.getCommand().equals( SubscriptionTypeEnum.AUTO_SETTLE_DATA_SOURCE_SUB.getCode())) {
                start = System.currentTimeMillis();
                handleAutoSettleDataSourceSub(session);
                end = System.currentTimeMillis();
                if (end-start>100){
                    log.info("ws handleAutoSettleDataSourceSub use total time:{} ms",(end-start));
                }
            }
            if(requestVo.getCommand().equals( SubscriptionTypeEnum.MATCH_SETTLE_ROLL_BACK_STATUS_PUSH.getCode())) {
                start = System.currentTimeMillis();
                handleMatchSettleRollBackSourceSub(session);
                end = System.currentTimeMillis();
                if (end-start>100){
                    log.info("ws handleMatchSettleRollBackSourceSub use total time:{} ms",(end-start));
                }
            }
            if(requestVo.getCommand().equals( SubscriptionTypeEnum.BASKETBALL_PERIOD_SCORES_PUSH.getCode())) {
                start = System.currentTimeMillis();
                handleBasketballPeriodScoresSub(session);
                end = System.currentTimeMillis();
                if (end-start>100){
                    log.info("ws handleBasketballPeriodScoresSub use total time:{} ms",(end-start));
                }
            }
            if(requestVo.getCommand().equals( SubscriptionTypeEnum.MATCH_STANDARD_SCORES_PUSH.getCode())) {
                start = System.currentTimeMillis();
                handleMatchScoresSub(session,requestVo);
                end = System.currentTimeMillis();
                if (end-start>100){
                    log.info("ws handleMatchScoresSub use total time:{} ms",(end-start));
                }
            }
            if (requestVo.getCommand().equals(SubscriptionTypeEnum.CAOPAN_ONLINE_PUSH.getCode())) {
                start = System.currentTimeMillis();
                log.info("WS 操盘手消息接收");
                handleCaopanOnileSub(session, requestVo);
                end = System.currentTimeMillis();
                if (end - start > 100) {
                    log.info("ws handleCaopanOnileSub use total time:{} ms", (end - start));
                }
            }
            Long endTotal = System.currentTimeMillis();
            log.info("ws realData use total time:{} ms",(endTotal-startTotal));
        } catch (Exception e) {
            log.error("command:{}，消息接收处理出错------------------------------:{}", e);
            session.close();
        }
    }

    private void handleCaopanOnileSub(Session session, RequestVo requestVo) throws IOException {
        OperatorOnlieSubVo request = JSONObject.toJavaObject(JSON.parseObject(requestVo.getPara().toString()), OperatorOnlieSubVo.class);
        log.info("操盘手消息处理:request{}", JSONUtil.toJsonStr(request));
        List<Integer> userIds = request.getUserIds();
        if (userIds == null) {
            return;
        }
        OperatorOnlineCatchVo subCacheVo = new OperatorOnlineCatchVo();
        subCacheVo.setCreateTime(System.currentTimeMillis());
        subCacheVo.setLostTimes(0);
        subCacheVo.setSession(session);
        subCacheVo.setSessionId(session.id().asLongText());
        subCacheVo.setUserIds(userIds);
        MyCacheService.sessionOperatorOnlineMap.put(session.id().asLongText(), subCacheVo);
        Set<Integer> redisOnlineUsers = getRedisOnlineUsers();
        List<OperatorOnlineVo> onlineUser = new ArrayList<>();
        userIds.forEach(userId -> {
            OperatorOnlineVo operatorOnlineVo = new OperatorOnlineVo();
            operatorOnlineVo.setUserId(userId);
            if (redisOnlineUsers.contains(userId)) {
                operatorOnlineVo.setLoginStatus(0);
            }else{
                operatorOnlineVo.setLoginStatus(1);
            }
            onlineUser.add(operatorOnlineVo);
        });
        OnlineResponseVo onlineResponseVo = new OnlineResponseVo();
        onlineResponseVo.setData(onlineUser);
        log.info("返回操盘手信息:{}", JSONUtil.toJsonStr(onlineUser));
        sendMessage(JSONObject.toJSONString(onlineResponseVo), session);
        log.info("返回操盘手信息结束:{}", JSONUtil.toJsonStr(onlineResponseVo));
    }

    private void handleSettleMatchListCancel(Session session) {
        MyCacheService.sessionIdSettleMatchListMap.remove(session.id().asLongText());
    }

    private void handleSettleMatchListSub(Session session, RequestVo requestVo) throws IOException {

    }


    private void handleMatchScoresSub(Session session, RequestVo requestVo) throws IOException {
        StandardMatchScoreRequestVo request = JSONObject.toJavaObject(JSON.parseObject(requestVo.getPara().toString()), StandardMatchScoreRequestVo.class);
        Long standardMatchId = request.getMatchId();
        if(standardMatchId == null){
            return;
        }
        StandardMatchScoreCatchVo subCacheVo=new StandardMatchScoreCatchVo();
        subCacheVo.setCreateTime(System.currentTimeMillis());
        subCacheVo.setLostTimes(0);
        subCacheVo.setSession(session);
        subCacheVo.setSessionId(session.id().asLongText());
        subCacheVo.setStandardMatchId(standardMatchId);

        MyCacheService.sessionIdMatchScoreMap.put(session.id().asLongText(),subCacheVo);
        sendMessage(JSONObject.toJSONString(new MatchScoreSubMessage(1l)), session);
    }
    private void handleMatchScoresCancel(Session session)
    {
        MyCacheService.sessionIdMatchScoreMap.remove(session.id().asLongText());
    }

    /**
     * 处理数据商自动结算订阅
     * @param session
     * @throws IOException
     */
    private void handleAutoSettleDataSourceSub(Session session) throws IOException
    {
        AutoSettleDataSourceSubCacheVo subCacheVo=new AutoSettleDataSourceSubCacheVo();
        subCacheVo.setCreateTime(System.currentTimeMillis());
        subCacheVo.setLostTimes(0);
        subCacheVo.setSession(session);
        subCacheVo.setSessionId(session.id().asLongText());

        MyCacheService.sessionIdAutoSettleDataSourceMap.put(session.id().asLongText(),subCacheVo);
        sendMessage(JSONObject.toJSONString(new AutoSettleDataSourceSubMessage(1l)), session);
    }

    /**
     * 处理赛事回滚状态订阅
     * @param session
     * @throws IOException
     */
    private void handleMatchSettleRollBackSourceSub(Session session) throws IOException
    {
        MatchSettleRollBackVo subCacheVo=new MatchSettleRollBackVo();
        subCacheVo.setCreateTime(System.currentTimeMillis());
        subCacheVo.setLostTimes(0);
        subCacheVo.setSession(session);
        subCacheVo.setSessionId(session.id().asLongText());

        MyCacheService.sessionIdMatchSettleRollBackSourceMap.put(session.id().asLongText(),subCacheVo);
        sendMessage(JSONObject.toJSONString(new MatchSettleRollBackMessage(1l)), session);
    }
    /**
     * 处理赛事回滚状态订阅
     * @param session
     * @throws IOException
     */
    private void handleBasketballPeriodScoresSub(Session session) throws IOException
    {
        MatchSettleRollBackVo subCacheVo=new MatchSettleRollBackVo();
        subCacheVo.setCreateTime(System.currentTimeMillis());
        subCacheVo.setLostTimes(0);
        subCacheVo.setSession(session);
        subCacheVo.setSessionId(session.id().asLongText());

        MyCacheService.sessionIdMatchSettleRollBackSourceMap.put(session.id().asLongText(),subCacheVo);
        sendMessage(JSONObject.toJSONString(new BasketballPeriodScoresMessage(1l)), session);
    }
    private void handleSettleMatchCancel(Session session)
    {
        MyCacheService.sessionIdSettleMatchMap.remove(session.id().asLongText());
    }

    private void handleSettleMatchSub(Session session, RequestVo requestVo) throws IOException
    {
        SettleMatchSubVo  request = JSONObject.toJavaObject( JSON.parseObject( requestVo.getPara().toString())  ,SettleMatchSubVo.class);
        SettleMatchSubCacheVo  pdSubCacheVo=new SettleMatchSubCacheVo();
        pdSubCacheVo.setCreateTime(System.currentTimeMillis());
        pdSubCacheVo.setLostTimes(0);
        pdSubCacheVo.setSession(session);
        pdSubCacheVo.setSessionId(session.id().asLongText());
        pdSubCacheVo.setStandardMatchId(request.getStandardMatchId());
        pdSubCacheVo.setEventCode(request.getEventCode());
        MyCacheService.sessionIdSettleMatchMap.put(session.id().asLongText(),pdSubCacheVo);
        sendMessage(JSONObject.toJSONString(new SettleSubMessage(1l)), session);
    }


    private void handlePdMatchCancel(Session session )
    {
        MyCacheService.sessionIdPdMatchMap.remove(session.id().asLongText());
    }

    private void handlePdMatchSub(Session session, RequestVo requestVo)
    {
        PdMatchSubVo request = JSONObject.toJavaObject( JSON.parseObject( requestVo.getPara().toString())  ,PdMatchSubVo.class);
        PdSubCacheVo pdSubCacheVo=new PdSubCacheVo();
        pdSubCacheVo.setCreateTime(System.currentTimeMillis());
        pdSubCacheVo.setLostTimes(0);
        pdSubCacheVo.setSession(session);
        pdSubCacheVo.setSessionId(session.id().asLongText());
        pdSubCacheVo.setThirdMatchId(request.getThirdMatchId());
        MyCacheService.sessionIdPdMatchMap.put(session.id().asLongText(),pdSubCacheVo);
    }

    private void handleScoresEvent(Session session, RequestVo requestVo) throws IOException
    {
        PdSubCacheVo request =  MyCacheService.sessionIdPdMatchMap.get(session.id().asLongText());
        if(request!=null){
            request.setLostTimes(0);
            request.setCreateTime(System.currentTimeMillis());
        }
        Object response =  getScoreEventService().queryEvent(requestVo);
        sendMessage(JSONObject.toJSONString(response), session);
    }

    private void handleScoresPage(Session session ,RequestVo requestVo) throws IOException
    {

        PdSubCacheVo request = MyCacheService.sessionIdPdMatchMap.get(session.id().asLongText());
        if(request!=null)
        {
            request.setLostTimes(0);
            request.setCreateTime(System.currentTimeMillis());
        }
        Object data=  getScoreEventService().queryScore(requestVo);
        QueryScoresResponseVo response=new QueryScoresResponseVo();
        response.setData(data);
        sendMessage(JSONObject.toJSONString(response), session);

    }

    private void handleHeart(Session session) throws IOException
    {
        PdSubCacheVo request =  MyCacheService.sessionIdPdMatchMap.get(session.id().asLongText());
        if(request!=null)
        {
            request.setLostTimes(0);
            request.setCreateTime(System.currentTimeMillis());
        }
        SettleMatchSubCacheVo settleSubCacheVo  =  MyCacheService.sessionIdSettleMatchMap.get(session.id().asLongText());
        if(settleSubCacheVo!=null){
            settleSubCacheVo.setLostTimes(0);
            settleSubCacheVo.setCreateTime(System.currentTimeMillis());
        }
        StandardMatchScoreCatchVo standardMatchScoreCatchVo  =  MyCacheService.sessionIdMatchScoreMap.get(session.id().asLongText());
        if(standardMatchScoreCatchVo!=null){
            standardMatchScoreCatchVo.setLostTimes(0);
            standardMatchScoreCatchVo.setCreateTime(System.currentTimeMillis());
        }
        sendMessage(JSONObject.toJSONString(new HeartMessage(1l)), session);

        return;
    }

    /**
     * 发生错误
     *
     * @param session
     * @param error
     * @return void
     * @description
     * @author dorich
     * @date 2019/9/30
     **/
    @OnError
    public void onError(Session session, Throwable error)
    {
        session.close();
    }


    public void sendMessage(String message, Session session) throws IOException
    {
        if (session.isOpen())
        {
            session.sendText(message);
        } else {
            log.info("websocket:发送失败(连接被关闭),消息内容:{}", message.substring(0, Math.min(350, message.length() - 1)));
            session.close();
        }
    }
    private ScoreEventService eventService;

    private ScoreEventService getScoreEventService(){
        if(eventService!=null){
            return eventService;
        }else {
            ScoreEventService scoreEventService = (ScoreEventService) ApplicationContextHelper.getBean(ScoreEventService.class);
            eventService=scoreEventService;
            return scoreEventService;
        }

    }

    @Autowired
    private RedisTemplate redisTemplate;



//    @Reference(check = false, lazy = true, timeout = 10000)
    @DubboReference(check = false)
    private ISystemUserOrgAuthApi iSystemUserOrgAuthApi;

    private ISystemUserOrgAuthApi getSystemUserOrgAuthApi() {
        if (iSystemUserOrgAuthApi != null) {
            return iSystemUserOrgAuthApi;
        }
        ISystemUserOrgAuthApi systemUserOrgAuthApi =
                (ISystemUserOrgAuthApi) ApplicationContextHelper.getBean(ISystemUserOrgAuthApi.class);
        iSystemUserOrgAuthApi = systemUserOrgAuthApi;
        return systemUserOrgAuthApi;
    }

    private Set<Integer> getRedisOnlineUsers() {
        log.info("获取redisOnlineUser");
        Set<Object> objectSet = redisTemplate.opsForSet().members(RedisKeyConstant.CAO_PAN_ONLINE);
        Long expireSeconds = redisTemplate.getExpire(RedisKeyConstant.CAO_PAN_ONLINE, TimeUnit.SECONDS);
        // Redis 有值但没有过期时间(或已过期)时，强制重新拉取，避免长期不刷新
        if (CollectionUtil.isEmpty(objectSet) || expireSeconds == null || expireSeconds <= 0) {
             return  syncOnlineUsers();
        }
        Set<Integer> onlineUsers = objectSet.stream()
                .map(obj -> (Integer) obj)
                .collect(Collectors.toSet());
        log.info("返回redisOnlineUser:{}", JSONUtil.toJsonStr(onlineUsers));
        return onlineUsers;
    }

    private Set<Integer> syncOnlineUsers() {
        log.info("syncOnlineUsers: start to fetch online users from auth api");
        Set<Integer> onlineUsers = new HashSet<>();
        ISystemUserOrgAuthApi systemUserOrgAuthApi = getSystemUserOrgAuthApi();
        if (systemUserOrgAuthApi == null) {
            log.error("syncOnlineUsers: iSystemUserOrgAuthApi bean is null");
            return onlineUsers;
        }
        List<SysOrgAuthVO> sysOrgAuthVOS = systemUserOrgAuthApi.getMembersOnline();
        if (sysOrgAuthVOS != null) {
            onlineUsers = sysOrgAuthVOS.stream().map(SysOrgAuthVO::getUserId).collect(Collectors.toSet());
        }
        log.info("syncOnlineUsers: fetched online users, count={}", onlineUsers.size());
        log.info("syncOnlineUsers: fetched userIds={}", JSONUtil.toJsonStr(onlineUsers));
        // 覆盖模式：先删旧值，再写入新快照，避免集合持续追加
        redisTemplate.delete(RedisKeyConstant.CAO_PAN_ONLINE);
        if (CollectionUtil.isNotEmpty(onlineUsers)) {
            redisTemplate.opsForSet().add(RedisKeyConstant.CAO_PAN_ONLINE, onlineUsers.toArray(new Object[0]));
        }
        redisTemplate.expire(RedisKeyConstant.CAO_PAN_ONLINE, 15, TimeUnit.SECONDS);
        log.info("syncOnlineUsers: redis refreshed, key={}, count={}, ttl={}s",
                RedisKeyConstant.CAO_PAN_ONLINE, onlineUsers.size(), 15);

        return onlineUsers;
    }
}
