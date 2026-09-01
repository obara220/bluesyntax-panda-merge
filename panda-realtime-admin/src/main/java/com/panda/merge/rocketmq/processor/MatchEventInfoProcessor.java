package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.bo.StandardMatchPeriodBO;
import com.panda.merge.cache.CommonItem;
import com.panda.merge.cache.FootballCacheScores;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.RateLimiterHandler;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.MyHashUtil;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.InitializeComponent;
import com.panda.merge.component.StandardEventCashOutStatusProcessor;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.config.ThreadPoolConfig;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.*;
import com.panda.merge.exception.Asserts;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.mapper.StandardRelationNewStandardMapper;
import com.panda.merge.mapper.ThirdRelationNewThirdMapper;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.producer.*;
import com.panda.merge.service.*;
import com.panda.merge.utils.MatchEventUtils;
import com.panda.sport.manager.api.IMarketCategorySellApi;
import com.panda.sport.manager.api.dto.ChangeBusinessEventSaleDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.panda.merge.config.RedisConfig.REDIS_HOUR_TIME;
import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 第三方赛事盘中事件接收
 *
 * @author Aison
 * @since 2020年10月22日11:04:05
 */
@Slf4j
@Component
@Validated
public class MatchEventInfoProcessor extends BaseProcessor {

    @Autowired
    private InitializeComponent initializeComponent;
    @Autowired
    private ThirdSportTeamService thirdSportTeamService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private MatchEventInfoService matchEventInfoService;
    @Autowired
    private MatchSaleOverProducer matchSaleOverProducer;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private MatchEventInfoProducer matchEventInfoProducer;
    @Autowired
    private PaDataServiceLogProducer paDataServiceLogProducer;
    @Autowired
    private AoEventsHistoryProducer aoEventsHistoryProducer;

    @DubboReference
    private ITradeMarketConfigApi iTradeMarketConfigApi;
    @Autowired
    public RedisService redisService;
    @Autowired
    public StandardEventCashOutStatusProcessor standardEventCashOutStatusProcessor;
    @Autowired
    public StandardMatchEventOddsLiveProducer standardMatchEventOddsLiveProducer;

    @Autowired
    public RealtimeBaseProduecr realtimeBaseProduecr;
    @Autowired
    private StandardSportTournamentService standardSportTournamentService;

    @Autowired
    ThirdMatchStatusProcessor thirdMatchStatusProcessor;

    @Autowired
    private WarningService warningService;

    @DubboReference
    private IMarketCategorySellApi iMarketCategorySellApi;

    @Resource(name = "ProcessTradeSystemThreadPool")
    private TaskExecutor processTradeSystemThreadPool;
    @Autowired
    private ApplicationContext applicationContext;

    /**
     *  优化单79713，需要告警的足球事件
     * */
    @NacosValue(value = "${alerts.event.code}", autoRefreshed = true)
    private String alertsEventCodes;

    /**
     *  需要被忽略的事件源
     * */
    @NacosValue(value = "${suspend.event.code:1}", autoRefreshed = true)
    private String suspendEventCodes;


    /**
     *  拷贝赛事开关（false:关，true：开）
     * */
    @NacosValue(value = "${copy.match.switch:false}", autoRefreshed = true)
    private boolean copyMatchSwitch;

    /**
     * panda数据库状态是否异常（false:否，true:是）
     * */
    @NacosValue(value = "${panda.db.error.realtime:false}", autoRefreshed = true)
    private Boolean pandaDbIsError;

    /** linkId 最大长度*/
    @NacosValue(value = "${panda.link.max.size:50}", autoRefreshed = true)
    private Integer linIdMaxSize;

    /**
     *  4248 【赛程】赛事中断场景优化开关（false:关，true：开）
     * */
    @NacosValue(value = "${panda.interrupted.event.switch:true}", autoRefreshed = true)
    private boolean interruptedEventSwitch;

    /**
     *  r01足球,第二次的100阶段事件和999事件睡眠开关（false:关，true：开）
     * */
    @NacosValue(value = "${event.r01_sleep_switch:false}", autoRefreshed = true)
    private boolean r01SleepSwitch;



    @Resource
    private RateLimiterHandler rateLimiterHandler;


    /**
     * 第三方赛事盘中事件列表接收（list增量事件）
     * @param ext 消费到的数据
     * @param spareMq 是否备用
     */
    @Async("EventInfoThreadPool")
    @ExceptionHelper
    public void putMatchEventListInfo(MessageExt ext,boolean spareMq) {
        String topic = null;
        String linkId = null;
        //当前时间
        long now = System.currentTimeMillis();
        try {
            topic = ext.getTopic();
            linkId = ext.getProperties().get("KEYS");
            log.info("linkId=【" + linkId + "】【TOPIC=" + topic + "】批量事件原始数据处理开始,spareMq={},messageId={}",spareMq, ext.getMsgId());
            String message = new String(ext.getBody(), StandardCharsets.UTF_8);
            if (StringUtils.isBlank(message)) {
                log.info("linkId=【" + linkId + "】【TOPIC=" + topic + "】接收到事件列表数据为空！");
            } else {
                // 解析消息
                JSONObject jsonObject = JSONObject.parseObject(message);
                JSONArray data = jsonObject.getJSONArray("data");
                // 创建MatchEventMessage列表
                List<MatchEventInfoDTO> matchEventDtoList = new ArrayList<>();
                String thirdMatchSourceId = data.getJSONObject(0).getString("thirdMatchSourceId");
                String dataSourceCode = data.getJSONObject(0).getString("dataSourceCode");
                // 3929 【融合】数据商异常下发告警&数据下发限频
                if (!rateLimiterHandler.filter(thirdMatchSourceId,dataSourceCode)) {
                    log.info("【{}】putMatchEventListInfo，该事件被限流，数据不下发！源赛事ID={}", linkId,thirdMatchSourceId);
                    return ;
                }
                for (int i=0;i<data.size();i++) {
                    matchEventDtoList.add(data.getObject(i, MatchEventInfoDTO.class));
                }
                if(StringUtils.isBlank(linkId)){
                    linkId = jsonObject.getString("linkId");
                }
                // 调用数据处理逻辑
                Request<List<MatchEventInfoDTO>> request = new Request<>();
                request.setSpareMq(spareMq);
                request.setLinkId(linkId);
                request.setTag(thirdMatchSourceId);
                request.setDataType(topic);
                request.setData(matchEventDtoList);
                putMatchEventListInfo(request);
            }
        } catch (Exception e) {
            log.error("linkId=【" + linkId + "】【TOPIC=" + topic + "】批量事件列表数据处理异常,Exception:", e);
        } finally {
            //输出Rocketmq节点参数日志
            getRocketmqTimeData(ext,spareMq,topic,linkId,now);
            log.error("linkId=【" + linkId + "】【TOPIC=" + topic + "】批量事件列表数据处理结束,spareMq={}",spareMq);
        }
    }


    /**
     * 第三方赛事盘中事件接收（单条事件）
     * @param ext 消费到的数据
     * @param spareMq 是否备用
     */
    @Async("EventInfoThreadPool")
    @ExceptionHelper
    public void putMatchEventInfo(MessageExt ext,boolean spareMq) {
        String topic = null;
        String linkId = null;
        //当前时间
        long now = System.currentTimeMillis();
        try {
            topic = ext.getTopic();
            linkId = ext.getProperties().get("KEYS");
            log.info("linkId=【" + linkId + "】【TOPIC=" + topic + "】单条事件原始数据处理开始,spareMq={},messageId={}",spareMq, ext.getMsgId());
            String message = new String(ext.getBody(), StandardCharsets.UTF_8);
            if (StringUtils.isBlank(message)) {
                log.info("linkId=【" + linkId + "】【TOPIC=" + topic + "】接收到事件数据为空！");
            } else {
                // 解析消息
                JSONObject jsonObject = JSONObject.parseObject(message);
                MatchEventInfoDTO matchEventInfo = jsonObject.getObject("data", MatchEventInfoDTO.class);
                String thirdMatchSourceId = matchEventInfo.getThirdMatchSourceId();
                String dataSourceCode = matchEventInfo.getDataSourceCode();
                // 3929 【融合】数据商异常下发告警&数据下发限频
                if (!rateLimiterHandler.filter(thirdMatchSourceId,dataSourceCode)) {
                    log.info("【{}】putMatchEventInfo，该事件被限流，数据不下发！源赛事ID={}", linkId,thirdMatchSourceId);
                    return ;
                }
                // 调用数据处理逻辑
                Request<MatchEventInfoDTO> request = new Request<>();
                request.setSpareMq(spareMq);
                request.setLinkId(jsonObject.getString("linkId"));
                log.info("linkId=【" + linkId + "】【TOPIC=" + topic + "】单条事件原始数据处理开始,request={},",JSON.toJSONString(request));
                request.setTag(thirdMatchSourceId);
                request.setDataType(topic);
                request.setData(matchEventInfo);
                putMatchEventInfo(request);
            }
        } catch (Exception e) {
            log.error("linkId=【" + linkId + "】【TOPIC=" + topic + "】单条事件数据处理异常,Exception:", e);
        } finally {
            //输出Rocketmq节点参数日志
            getRocketmqTimeData(ext,spareMq,topic,linkId,now);
            log.info("linkId=【" + linkId + "】【TOPIC=" + topic + "】单条事件数据处理结束,spareMq={}",spareMq);
        }
    }


    /**
     * 第三方赛事盘中事件列表接收（list增量事件）
     */
    public void putMatchEventListInfo(@Valid Request<List<MatchEventInfoDTO>> request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = Response.success();
        try {
            List<MatchEventInfoDTO> matchEventInfoDtos = request.getData();
            //当前数据源赛事ID
            String thirdMatchSourceId = matchEventInfoDtos.get(0).getThirdMatchSourceId();
            //当前传入数据源类型
            String dataSourceCode = matchEventInfoDtos.get(0).getDataSourceCode();
            log.info("linkId=【{}】putMatchEventListInfo，事件列表信息处理开始,thirdMatchSourceId={},dataSourceCode={},spareMq={},全量事件条数={}", request.getLinkId(), thirdMatchSourceId,dataSourceCode,request.getSpareMq(), matchEventInfoDtos.size());
            //校验当前赛事对应事件数据是否需要下发
            if(!checkMatchEventInfo(request.getLinkId(),thirdMatchSourceId,dataSourceCode)){
                return;
            }
            //赛事级别分布式锁，避免事件下发顺序错乱
            String matchTryLock = "MatchEventInfoDTO_" + dataSourceCode + "_" + thirdMatchSourceId;
            //必须释放赛事级别锁后才能下发后续事件
            boolean flag = false;
            try {
                //校验该赛事是否 正在切换事件源
                String matchSoldTryLock = "Sold::" + matchTryLock;
                Boolean flagSold = redisService.hasKey(matchSoldTryLock);
                //缓存事件的key
                String matchEventKey = RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfoDTO:" + matchTryLock;
                if(!flagSold){
                    //如果获取到list增量事件。锁5分钟（必须等list增量事件处理结束才能释放锁，避免后续单个增量事件和list增量事件入库顺序错乱）
                    flag = redisService.tryLock(matchTryLock, matchTryLock, 60, 5);
                    if (flag) {
                        //获取之前未获取到锁被缓存的事件到当前队列
                        List<MatchEventInfoDTO> matchEventDtos = getMatchEventDtos(null, matchEventKey, request.getLinkId());
                        matchEventDtos.addAll(matchEventInfoDtos);
                        log.info("linkId=【{}】putMatchEventListInfo，事件列表信息,本次处理事件条数={},matchTryLock={}", request.getLinkId(), matchEventDtos.size(), matchTryLock);
                        //正常下发的事件+缓存中的事件业务逻辑信息处理
                        process2MatchEvent(request, matchEventDtos, thirdMatchSourceId, dataSourceCode);
                    }
                }
                log.info("linkId=【{}】putMatchEventListInfo，事件列表信息，本次处理事件条数={}，matchTryLock={}，flag={}，flagSold={}", request.getLinkId(),matchEventInfoDtos.size(), matchTryLock, flag, flagSold);
                //如果 正在切换事件源 或者 未获取到锁 则缓存到redis等待后续事件一起下发
                if(flagSold || !flag){
                    for (MatchEventInfoDTO item : matchEventInfoDtos) {
                        if (StringUtils.isBlank(item.getCopyLinkId())) {
                            item.setCopyLinkId(request.getLinkId());
                        }
                        redisService.hSet(matchEventKey, item.getThirdEventId(), item);
                    }
                }
            } finally {
                if(flag){
                    //释放redis锁
                    redisService.unLock(matchTryLock, matchTryLock);
                }
            }

            //拷贝的赛事向MQ投递赛事事件列表消息
            if(copyMatchSwitch){
                copyStandardMatchInfoEventList2Mq(request.getLinkId(), request.getData());
            }
        } catch (Exception e) {
            response.setCode(ResultCode.FAILED.getCode());
            response.setMsg(e.getMessage());
            throw e;
        } finally {
            stopWatch.stop();
            response.setDataSourceTime(stopWatch.getTotalTimeMillis());
            //统计处理耗时
            paDataServiceLogProducer.sendPaDataServiceLog(
                    getPaDataServiceLogDTO(request.getLinkId(), realtime, THIRD_MATCH_EVENT_LIST_INFO_API, "三方赛事盘中事件列表接入",
                            stopWatch.getTotalTimeMillis(), Integer.parseInt(String.valueOf(response.getCode())), response.getMsg())
            );
            log.info("linkId=【{}】putMatchEventListInfo，事件列表信息处理结束，共耗时={}", request.getLinkId(), JSON.toJSONString(response));
        }
    }

    /**
     * 第三方赛事盘中事件接收（单条事件）
     */
    public Response putMatchEventInfo(@Valid Request<MatchEventInfoDTO> request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = Response.success();
        try {
            MatchEventInfoDTO matchEventInfoDTO = request.getData();
            //当前数据源赛事ID
            String thirdMatchSourceId = matchEventInfoDTO.getThirdMatchSourceId();
            //当前传入数据源类型
            String dataSourceCode = matchEventInfoDTO.getDataSourceCode();
            log.info("linkId=【{}】putMatchEventInfo，事件信息处理开始，thirdMatchSourceId={},dataSourceCode={},spareMq={}", request.getLinkId(), thirdMatchSourceId,dataSourceCode,request.getSpareMq());

            String ended999KeyForCheck = String.format(ConstantSystem.getMatchPeriod999KeyForCheck(), dataSourceCode,thirdMatchSourceId);
            String ended100KeyForCheck = String.format(ConstantSystem.getMatchPeriod100KeyForCheck(), dataSourceCode,thirdMatchSourceId);
            if (r01SleepSwitch
                    && DataSourceCodeEnum.RB.code.equals(dataSourceCode)
                    && StandardSportTypeEnum.FootBall.getCode().equals(matchEventInfoDTO.getSportId())
                    && EventCodeEnum.MATCH_STATUS.code.equalsIgnoreCase(matchEventInfoDTO.getEventCode())
                    && (MatchPeriodForMatchOverEnum.Ended999.value.equals(matchEventInfoDTO.getMatchPeriodId()) || MatchPeriodForMatchOverEnum.Ended.value.equals(matchEventInfoDTO.getMatchPeriodId()))) {

                if (redisService.get(ended999KeyForCheck) != null) {

                    if (redisService.get(ended100KeyForCheck) != null) {
                        try {
                            log.info("linkId=【{}】putMatchEventInfo，已经下发了100阶段和999阶段事件,睡眠3000毫秒", request.getLinkId());
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            //校验当前赛事对应事件数据是否需要下发
            if(!checkMatchEventInfo(request.getLinkId(),thirdMatchSourceId,dataSourceCode)){
                response.setCode(ResultCode.VALIDATE_FAILED.getCode());
                response.setMsg(ResultCode.VALIDATE_FAILED.getMessage());
                return response;
            }
            //赛事级别分布式锁，避免事件下发顺序错乱
            String matchTryLock = "MatchEventInfoDTO_" + dataSourceCode + "_" + thirdMatchSourceId;
            //必须释放赛事级别锁后才能下发后续事件
            boolean flag = false;
            try {
                //校验该赛事是否 正在切换事件源
                Boolean flagSold = redisService.hasKey("Sold::" + matchTryLock);
                //缓存事件的key
                String matchEventKey = RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfoDTO:" + matchTryLock;
                if(!flagSold){
                    //如果是999完赛事件则无需校验锁
                    if (EventCodeEnum.MATCH_STATUS.code.equalsIgnoreCase(matchEventInfoDTO.getEventCode()) && MatchPeriodForMatchOverEnum.Ended999.value.equals(matchEventInfoDTO.getMatchPeriodId())) {
                        log.info("linkId=【{}】putMatchEventInfo，事件信息处理,999完赛事件无需校验锁,直接放行", request.getLinkId());
                        flag = true;
                    }else{
                        //获取分布式锁
                        flag = redisService.tryLock(matchTryLock, matchTryLock, 5, 2);
                    }
                    if (flag) {
                        //获取之前未获取到锁被缓存的事件到当前队列
                        List<MatchEventInfoDTO> matchEventDtos = getMatchEventDtos(matchEventInfoDTO, matchEventKey, request.getLinkId());
                        log.info("linkId=【{}】putMatchEventInfo，事件信息处理,本次处理事件条数={},matchTryLock={}", request.getLinkId(), matchEventDtos.size(), matchTryLock);
                        process2MatchEvent(request, matchEventDtos, thirdMatchSourceId, dataSourceCode);
                    }
                }
                log.info("linkId=【{}】putMatchEventInfo，事件信息处理，matchTryLock={}，flag={}，flagSold={}", request.getLinkId(), matchTryLock, flag, flagSold);
                //如果 正在切换事件源 或者 未获取到锁 则缓存到redis等待后续事件一起下发
                if(flagSold || !flag){
                    if (StringUtils.isBlank(matchEventInfoDTO.getCopyLinkId())) {
                        matchEventInfoDTO.setCopyLinkId(request.getLinkId());
                    }
                    redisService.hSet(matchEventKey, matchEventInfoDTO.getThirdEventId(), matchEventInfoDTO);
                }
            } finally {
                if (flag) {
                    //释放redis锁
                    redisService.unLock(matchTryLock, matchTryLock);
                }
            }

            //拷贝的赛事向MQ投递赛事事件消息
            if(copyMatchSwitch){
                copyStandardMatchInfoEvent2Mq(request);
            }
        } catch (Exception e) {
            response.setCode(ResultCode.FAILED.getCode());
            response.setMsg(e.getMessage());
            throw e;
        } finally {
            stopWatch.stop();
            response.setDataSourceTime(stopWatch.getTotalTimeMillis());
            //统计处理耗时
            paDataServiceLogProducer.sendPaDataServiceLog(
                    getPaDataServiceLogDTO(request.getLinkId(), realtime, THIRD_MATCH_EVENT_INFO_API, "三方赛事盘中事件接入",
                            stopWatch.getTotalTimeMillis(), Integer.parseInt(String.valueOf(response.getCode())), response.getMsg())
            );
            log.info("linkId=【{}】putMatchEventInfo，事件信息处理结束，共耗时={}", request.getLinkId(), JSON.toJSONString(response));
            return response;
        }
    }

    /**
     * 校验当前赛事对应事件数据是否需要下发
     * */
    private boolean checkMatchEventInfo(String linkId,String thirdMatchSourceId,String dataSourceCode){
        //当前传入数据源类型
        if(suspendEventCodes.contains(dataSourceCode)){
            log.info("linkId=【{}】checkMatchEventInfo，该数据源事件无需处理，数据不下发！源赛事ID={},suspendEventCodes={},dataSourceCode={}", linkId,thirdMatchSourceId,suspendEventCodes,dataSourceCode);
            return false;
        }
        if (!DataSourceCodeEnum.getPdCodeList().contains(dataSourceCode)) {
            String ended999Key = String.format(ConstantSystem.getMatchPeriod999Key(), dataSourceCode,thirdMatchSourceId);
            if(redisService.hasKey(ended999Key)){
                log.info("linkId=【{}】checkMatchEventInfo，error:第三方赛事已经下发999完赛,后续事件无需处理！ended999Key={}", linkId, ended999Key);
                return false;
            }
        }
        return true;
    }

    /**
     * 获取之前未获取到锁被缓存的事件到当前队列
     *
     * @param matchEventInfoDTO 当前传入的事件信息
     * @param matchEventKey     缓存事件信息的key
     * @param linkId            线路ID
     */
    private List<MatchEventInfoDTO> getMatchEventDtos(MatchEventInfoDTO matchEventInfoDTO, String matchEventKey, String linkId) {
        List<MatchEventInfoDTO> matchEventDtos = new LinkedList<>();
        //之前未获取到锁被缓存的事件
        Map<String, MatchEventInfoDTO> oldDtoMap = redisService.hGetAll(matchEventKey);
        if (!CollectionUtils.isEmpty(oldDtoMap)) {
            try{
                Boolean del = redisService.del(matchEventKey);
                log.info("linkId=【{}】getMatchEventDtos，事件信息缓存清理结束,是否清理缓存成功={},matchEventKey={}", linkId, del,matchEventKey);
            }catch (Exception e){
                log.info("【linkId="+linkId+"】getMatchEventDtos，事件信息缓存清理异常,Exception:",e);
            }
            log.info("linkId=【{}】getMatchEventDtos，事件信息处理，缓存中事件条数={},数据源赛事ID={}", linkId, oldDtoMap.size(),matchEventInfoDTO == null ? "null" : matchEventInfoDTO.getThirdMatchSourceId());
            matchEventDtos.addAll(oldDtoMap.values());
            if (null != matchEventInfoDTO) {
                matchEventInfoDTO.setCopyLinkId(linkId);
            }
        }
        if (null != matchEventInfoDTO) {
            matchEventDtos.add(matchEventInfoDTO);
        }
        if(matchEventDtos.size() > 0){
            matchEventDtos = matchEventDtos.stream().sorted(Comparator.comparingLong(MatchEventInfoDTO::getEventTime)).collect(Collectors.toList());
        }
        return matchEventDtos;
    }


    /**
     * 事件信息业务逻辑处理
     */
    public void process2MatchEvent(Request request, List<MatchEventInfoDTO> matchEventInfoDtos, String thirdMatchSourceId, String dataSourceCode) {
        String linkId = request.getLinkId();
        if (CollectionUtils.isEmpty(matchEventInfoDtos)) {
            log.error("linkId=【{}】process2MatchEvent，error:传入事件信息为空！三方数据源赛事id={},数据来源={}", linkId, thirdMatchSourceId, dataSourceCode);
            return;
        }
        //linkId长度兜底，因为数据库最大长度是60
        if(linkId.length() > linIdMaxSize){
            String subLinkId = linkId.substring(0, linIdMaxSize);
            log.info("linkId=【{}】process2MatchEvent补发事件下发,linkId超长,截取后linkId={}", linkId,subLinkId);
            linkId = subLinkId;
        }
        log.info("linkId=【{}】process2MatchEvent，事件业务逻辑处理开始,thirdMatchSourceId={},dataSourceCode={},本次处理事件条数={}", linkId, thirdMatchSourceId, dataSourceCode,matchEventInfoDtos.size());
        //库中三方赛事
        ThirdMatchInfo oldThirdMatchInfo = thirdMatchInfoService.getItem(dataSourceCode, thirdMatchSourceId);
        if (null == oldThirdMatchInfo) {
            log.info("linkId=【{}】process2MatchEvent，error:三方赛事不存在，三方数据源赛事id={},数据来源={}", linkId, thirdMatchSourceId, dataSourceCode);
            return;
        }
        //本次需要修改的三方赛事信息
        ThirdMatchInfo upThirdMatchInfo = new ThirdMatchInfo();
        upThirdMatchInfo.setId(oldThirdMatchInfo.getId());
        upThirdMatchInfo.setMatchOver(oldThirdMatchInfo.getMatchOver());
        upThirdMatchInfo.setMatchPeriod(oldThirdMatchInfo.getMatchPeriod());
        //兼容缓存覆盖问题
        if (null == oldThirdMatchInfo.getReferenceId() || oldThirdMatchInfo.getReferenceId() == 0) {
            oldThirdMatchInfo = thirdMatchInfoService.getItemByPrimaryKey(oldThirdMatchInfo.getId());
        }
        //运动类型
        Long sportId = oldThirdMatchInfo.getSportId();
        //获取标准赛事信息
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(oldThirdMatchInfo.getReferenceId());
        //商业事件源编码(为空默认为标准赛事对应数据源)
        String businessEventCode = null;
        //标准赛事数据来源编码默认为商业事件源编码
        if (null != standardMatchInfo) {
            businessEventCode = standardMatchInfo.getDataSourceCode();
        }
        //获取开售信息,根据开售信息判断是否推送MQ消息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(oldThirdMatchInfo.getReferenceId());
        if (null != standardSportMarketSell) {
            //已经开售且管理ID为空，查库刷新缓存
            if (StringUtils.isBlank(standardMatchInfo.getMatchManageId())) {
                standardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(standardMatchInfo.getId());
            }
            //开售商业事件源编码
            if (StringUtils.isNotBlank(standardSportMarketSell.getBusinessEvent())) {
                businessEventCode = standardSportMarketSell.getBusinessEvent();
            }
            log.info("linkId=【{}】process2MatchEvent，事件业务逻辑处理，三方赛事id={},标准赛事状态={}", linkId, oldThirdMatchInfo.getThirdMatchSourceId(),standardMatchInfo.getMatchStatus());
        }else {
            log.info("linkId=【{}】process2MatchEvent，事件业务逻辑处理，三方赛事id={},开售信息为空！", linkId, oldThirdMatchInfo.getThirdMatchSourceId());
        }
        StandardMatchInfo upStandardMatchInfo = new StandardMatchInfo();
        //标准赛事数据来源编码默认为商业事件源编码
        if (null != standardMatchInfo) {
            upStandardMatchInfo.setId(standardMatchInfo.getId());
            upStandardMatchInfo.setMatchOver(standardMatchInfo.getMatchOver());
            upStandardMatchInfo.setMatchPeriodId(standardMatchInfo.getMatchPeriodId());
        }
        //需要延迟下发的赛事阶段
        Map<Long, Long> periodId2Time = MatchPeriodForMatchOverEnum.getSleepMatchPeriodId2Time();
        //事件是否是列表
        Boolean eventListFlag = false;
        //按事件发生事件升序
        if (matchEventInfoDtos.size() > ONE) {
            if (!DataSourceCodeEnum.BG.code.equalsIgnoreCase(dataSourceCode)) {
                matchEventInfoDtos = matchEventInfoDtos.stream().sorted(Comparator.comparingLong(MatchEventInfoDTO::getEventTime)).collect(Collectors.toList());
            }
            eventListFlag = true;
        }
        //是否含有缓存中的增量事件
        Boolean copyLinkIdFlag = false;
        if (StringUtils.isNotBlank(matchEventInfoDtos.get(0).getCopyLinkId())) {
            copyLinkIdFlag = true;
        }
        log.info("linkId=【{}】process2MatchEvent，组装需要下发的事件列表开始,三方赛事id={},pandaDbIsError={}", linkId, oldThirdMatchInfo.getThirdMatchSourceId(),pandaDbIsError);
        //需要告警的事件,取最新的一条
        MatchEventInfoDetail alertsEventInfo = new MatchEventInfoDetail();
        //需要下发的事件列表
        List<MatchEventInfo> matchEventPushList = new LinkedList<>();
        //需要保存的事件列表
        List<MatchEventInfo> matchEventInfoList = new LinkedList<>();
        Long ended999 = MatchPeriodForMatchOverEnum.Ended999.value;
        Long ended100 = MatchPeriodForMatchOverEnum.Ended.value;
        List<Long> endedList = Lists.newArrayList(ended100, ended999);
        //需求：2659 【操盘风控管理优化】足球 - PD报球板新增可删除数据商事件
        boolean flag_2659 = false;
        //记录三方事件是否下发过999
        String ended999Key = String.format(ConstantSystem.getMatchPeriod999Key(), dataSourceCode,thirdMatchSourceId);

        String ended999KeyForCheck = String.format(ConstantSystem.getMatchPeriod999KeyForCheck(), dataSourceCode,thirdMatchSourceId);
        String ended100KeyForCheck = String.format(ConstantSystem.getMatchPeriod100KeyForCheck(), dataSourceCode,thirdMatchSourceId);

        //记录当前三方赛事最新事件发生时间,单号：81639
        String eventTimeKey = RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:eventTime:" + oldThirdMatchInfo.getDataSourceCode()+"_"+ oldThirdMatchInfo.getThirdMatchSourceId();
        //是否补发事件(true:切换事件源，或者延迟消费的事件，false:开售事件，或者正常通道下发事件）
        boolean isReissue = false;
        for (int i = 0; i < matchEventInfoDtos.size(); i++) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            MatchEventInfoDTO matchEventInfoDTO = matchEventInfoDtos.get(i);
            String newLinkId = linkId;
            try {
                if (eventListFlag) {
                    //缓存中的增量事件
                    if (copyLinkIdFlag) {
                        log.info("linkId=【{}】process2MatchEvent，-------单条事件处理开始-------,历史事件linkId={}", newLinkId, matchEventInfoDTO.getCopyLinkId());
                        newLinkId = matchEventInfoDTO.getCopyLinkId();
                    }else{
                        newLinkId = linkId + "_" + i;
                        log.info("linkId=【{}】process2MatchEvent，-------单条事件处理开始-------,原始事件linkId={}", newLinkId, linkId);
                    }
                }
                //98584 数据支撑后台三方赛事事件linkId为空(兜底)
                if (StringUtils.isBlank(newLinkId)) {
                    newLinkId = linkId + "_" + i;
                }
                //事件延迟时间
                long eventTimeDiff = System.currentTimeMillis() - matchEventInfoDTO.getEventTime();
                if(eventTimeDiff > (3 * 1000L)){
                    log.info("linkId=【{}】process2MatchEvent，-------单条事件处理开始-------事件处理延迟超过3S,源赛事ID={},数据源事件ID={},事件发生时间={},eventTimeDiff={},dataSourceCode={},spareMq={}"
                            , newLinkId, matchEventInfoDTO.getThirdMatchSourceId(),matchEventInfoDTO.getThirdEventId(),matchEventInfoDTO.getEventTime(),eventTimeDiff,dataSourceCode,request.getSpareMq());
                }
                //事件发生时间小于当前时间2小时则不处理
                if (!DataSourceCodeEnum.getPdCodeList().contains(dataSourceCode)) {
                    if(eventTimeDiff > (2 * 60 * 60 * 1000L)){
                        log.info("linkId=【{}】process2MatchEvent，单条事件处理逻辑,事件发生时间小于当前时间2小时无需处理！,源赛事ID={},事件发生时间={}", newLinkId, matchEventInfoDTO.getThirdMatchSourceId(),matchEventInfoDTO.getEventTime());
                        continue;
                    }
                }
                //如果BT数据源当前事件阶段为空，需赋值标准阶段
                if (DataSourceCodeEnum.BT.getCode().equals(matchEventInfoDTO.getDataSourceCode())) {
                    if (null != standardMatchInfo) {
                        if (null == matchEventInfoDTO.getMatchPeriodId()) {
                            matchEventInfoDTO.setMatchPeriodId(standardMatchInfo.getMatchPeriodId());
                        }
                        //BT数据源需求：目前只处理足球,标准事件源的MATCH_STATUS事件缓存事件发生时间，BT数据源下发的是分钟数，需要根据标准的转换为时间戳
                        if (StandardSportTypeEnum.FootBall.getCode().equals(sportId)) {
                            String cacheStatusKey = "STANDARD_MATCH_STATUS_2070:" + standardMatchInfo.getId();
                            Map<String, Long> matchPeriodId2Time = redisService.hGetAll(cacheStatusKey);
                            if (!CollectionUtils.isEmpty(matchPeriodId2Time)) {
                                //获取标准事件中当前阶段开始的发生时间戳
                                Long matchStatusEventTime = matchPeriodId2Time.get(matchEventInfoDTO.getMatchPeriodId() + "");
                                //当前阶段开始的发生时间戳 + 分钟毫秒数
                                matchEventInfoDTO.setEventTime(matchStatusEventTime + matchEventInfoDTO.getEventTime() * MINS_1);
                            }
                        }
                    }
                }
                //赛事阶段
                Long matchPeriodId = matchEventInfoDTO.getMatchPeriodId();
                //事件编码
                String eventCode = matchEventInfoDTO.getEventCode();
                //赛事阶段不为0,则标记当前赛事的赛事统计入口不需要修改赛事阶段信息
                if (!MatchPeriodForMatchOverEnum.NOT_STARTED.value.equals(matchPeriodId)) {
                    //是否不需要赛事统计入口更新赛事阶段(1:是),只要当前赛事存在正常事件信息，就不需要使用赛事统计入口更新赛事阶段
                    redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::" + MATCH_STATISTICS_INFO_API + ":" + oldThirdMatchInfo.getId(), ONE);
                }
                //过滤掉赛事阶段大于0的 early_betstatus事件
                if (null != matchPeriodId && matchPeriodId > 0 && EventCodeEnum.EARLY_BET_STATUS.code.equalsIgnoreCase(eventCode)) {
                    log.info("linkId=【{}】process2MatchEvent，error:过滤掉阶段大于0的{}事件,事件id={}", newLinkId, EventCodeEnum.EARLY_BET_STATUS.code, matchEventInfoDTO.getThirdEventId());
                    continue;
                }
                //不可逆的赛事阶段
                List<String> noMatchPeriods = Lists.newArrayList(MatchPeriodForMatchOverEnum.Abandoned.value.toString());
                //库中赛事阶段为90且传入赛事阶段不为999
                if (noMatchPeriods.contains(oldThirdMatchInfo.getMatchPeriod()) && !ended999.equals(matchPeriodId)) {
                    log.error("linkId=【{}】process2MatchEvent，error:当前赛事阶段不可逆，当前赛事阶段id={}，三方赛事id={}", newLinkId, oldThirdMatchInfo.getMatchPeriod(), matchEventInfoDTO.getThirdMatchSourceId());
                    continue;
                }
                //检查阶段是否符合要求
                Map<Long, SystemItemDict> matchPeriodMap = initializeComponent.getMatchPeriodData().get(sportId);
                if (CollectionUtils.isEmpty(matchPeriodMap) || null == matchPeriodMap.get(matchPeriodId)) {
                    log.info("linkId=【{}】process2MatchEvent，error:第三方赛事盘中事件，赛事阶段数据不匹配,阶段id={}", newLinkId, matchPeriodId);
                    continue;
                }
                //检查事件类型是否符合要求
                Map<String, MatchEventType> matchEventTypeMap = initializeComponent.getMatchEventTypeData().get(sportId);
                if (CollectionUtils.isEmpty(matchEventTypeMap) || null == matchEventTypeMap.get(eventCode)) {
                    log.info("linkId=【{}】process2MatchEvent，error:第三方赛事盘中事件的eventCode非标准事件编码，事件类型id={}", newLinkId, eventCode);
                    continue;
                }
//                106533 【日常】【生产】删除事件比进球事件先下发200毫秒，事件并发导致拦截被删除事件失效
                boolean eventDeleteLockFlag = false;
                String deleteEventLock = String.format(ConstantSystem.getDeleteEventLockKey(), matchEventInfoDTO.getDataSourceCode(),matchEventInfoDTO.getThirdMatchSourceId(),matchEventInfoDTO.getExtrainfo());
                try {
                    eventDeleteLockFlag = redisService.tryLock(deleteEventLock, deleteEventLock, 2, 3);
                    //获取缓存中未找到源事件的删除事件信息,优化单：81636
                    String deleteEventKey = String.format(ConstantSystem.getDeleteEventKey(), matchEventInfoDTO.getDataSourceCode(),matchEventInfoDTO.getThirdMatchSourceId(),matchEventInfoDTO.getThirdEventId());
                    if(redisService.hasKey(deleteEventKey)){
                        redisService.del(deleteEventKey);
                        log.info("linkId=【{}】process2MatchEvent，当前三方事件已被删除,无需处理，事件ID={}", newLinkId, matchEventInfoDTO.getThirdEventId());
                        continue;
                    }
                    /**
                     * 删除事件-需要缓存的事件-缓存1小时
                     * 1：单号 85728 数据入库延迟导致没有查到需要被删除的比分 兼容处理，足球比分事件
                     * 2：去DB需求 其它赛种比分事件也需要缓存,避免删除事件直接查库影响性能
                     * */
                    if (EventCodeEnum.getDeleteEventCodes().contains(eventCode)){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("eventCode",eventCode);
                        jsonObject.put("matchPeriodId",matchEventInfoDTO.getMatchPeriodId());
                        String thirdMatchScoresEventKey = String.format(ConstantSystem.getThirdMatchScoresEventKey(), matchEventInfoDTO.getDataSourceCode(),matchEventInfoDTO.getThirdMatchSourceId(),matchEventInfoDTO.getThirdEventId());
                        redisService.set(thirdMatchScoresEventKey,jsonObject.toJSONString(),RedisConfig.REDIS_HOUR_TIME);
                    }
                } finally {
                    if(eventDeleteLockFlag){
                        redisService.unLock(deleteEventLock,deleteEventLock);
                    }
                }

                /**
                 * 优化单：71841 ,根据赛制和赛事进行时长来判断补时事件（injury_time）是否有效
                 * */
                if(StandardSportTypeEnum.FootBall.getCode().equals(sportId)){

                    //dubbo 兜底处理的事件,96695 【优化】【数据支撑】实时服务事件MQ消费延迟兜底优化
                    List<String> dubboEventCodes = EventCodeEnum.getDubboEventCodes();
                    String dubboEventKey = String.format(ConstantSystem.getDubboEventKey(), matchEventInfoDTO.getDataSourceCode(),matchEventInfoDTO.getThirdMatchSourceId(),matchEventInfoDTO.getThirdEventId());
                    //4722需求中，v02一直要更新集锦内容
                    if(dubboEventCodes.contains(eventCode) && !DataSourceCodeEnum.TS.getCode().equals(matchEventInfoDTO.getDataSourceCode())){
                        //校验当前事件是否存在重复
                        if(!redisService.tryLockOnce(dubboEventKey,dubboEventKey,RedisConfig.REDIS_MY_TIME)){
                            log.info("linkId=【{}】process2MatchEvent,赛事已经下发过该事件,当前事件不在下发={}", newLinkId,JSON.toJSONString(matchEventInfoDTO));
                            continue;
                        }
                    }

                    //足球伤停特殊处理
                    if (EventCodeEnum.INJURY_TIME.code.equalsIgnoreCase(eventCode)){
                        //92420单（手动输入补时，不需校验比赛时间，可即时生效）
                        if(!matchEventInfoDTO.getThirdEventId().contains("PA_Event_input:")){
                            //当前赛制的比赛时长
                            Long minMinute = MatchLengthEnum.getMinMinute(standardMatchInfo.getSportId(), standardMatchInfo.getMatchLength(),matchPeriodId + "");
                            if(null != minMinute){
                                //最小分钟数的秒数
                                Long minSecond = minMinute * SIXTY;
                                //当前事件进行时长需要 大于 最小分钟数秒数
                                if(matchEventInfoDTO.getSecondsFromStart() < minSecond){
                                    log.error("linkId=【{}】process2MatchEvent，error:数据源补时事件（injury_time）赛事进行时长非法,事件无需下发！数据源赛事ID={},赛事进行时长={},minSecond={}",
                                            newLinkId, matchEventInfoDTO.getThirdMatchSourceId(),matchEventInfoDTO.getSecondsFromStart(),minSecond);
                                    continue;
                                }
                            }
                        }
                    }
                }

                //如果是赛事阶段事件
                if (EventCodeEnum.MATCH_STATUS.code.equalsIgnoreCase(eventCode)) {
                    //如果已经下发过999事件，则无需下发
                    if (isOverMatchEventInfo(matchEventInfoDTO,newLinkId)) {
                        //需求：2659 【操盘风控管理优化】足球 - PD报球板新增可删除数据商事件
                        if (DataSourceCodeEnum.getPdCodeList().contains(matchEventInfoDTO.getDataSourceCode())) {
                            flag_2659 = true;
                        } else {
                            log.info("linkId=【{}】process2MatchEvent，error:赛事已经下发过结束999赛事阶段事件,当前事件不在下发 赛事源id={}", newLinkId, matchEventInfoDTO.getThirdMatchSourceId());
                            continue;
                        }
                    }

                    if (
                            StandardSportTypeEnum.FootBall.getCode().equals(sportId)
                            && DataSourceCodeEnum.RB.code.equals(dataSourceCode)
                            && EventCodeEnum.MATCH_STATUS.code.equalsIgnoreCase(matchEventInfoDTO.getEventCode())
                    ) {

                        if (MatchPeriodForMatchOverEnum.Ended999.value.equals(matchEventInfoDTO.getMatchPeriodId())) {

                            redisService.set(ended999KeyForCheck,ended999KeyForCheck, 4);
                            log.info("linkId=【{}】process2MatchEvent，set ended999KeyForCheck", newLinkId);
                        } else if (MatchPeriodForMatchOverEnum.Ended.value.equals(matchEventInfoDTO.getMatchPeriodId())) {

                            redisService.set(ended100KeyForCheck,ended100KeyForCheck, 4);
                            log.info("linkId=【{}】process2MatchEvent，set ended100KeyForCheck", newLinkId);
                        }
                    }

                    //赛事阶段为999,100，则延迟下发
                    Long sleepTime = periodId2Time.get(matchPeriodId);
                    if (null != sleepTime) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            log.error("::" + newLinkId + "::process2MatchEvent：MATCH_STATUS : " + matchPeriodId + "延迟" + sleepTime + "毫秒下发异常！Exception:", e);
                        }
                    } else {
                        try {
                            //羽毛球match_status事件延迟一秒
                            if (StandardSportTypeEnum.Badminton.getCode().equals(sportId)) {
                                Thread.sleep(SECOND_1);
                            }
                        } catch (InterruptedException e) {
                            log.error("::" + newLinkId + "::process2MatchEvent：MATCH_STATUS : " + matchPeriodId + "延迟" + SECOND_1 + "毫秒下发异常！Exception:", e);
                        }
                    }
                }
                //获取事件类型
                MatchEventType matchEventType = matchEventTypeMap.get(eventCode);
                //查询球队信息
                ThirdSportTeam thirdSportTeam = null;
                if (StringUtils.isNotBlank(matchEventInfoDTO.getThirdTeamId())) {
                    thirdSportTeam = thirdSportTeamService.getOneItem(matchEventInfoDTO.getDataSourceCode(), sportId, matchEventInfoDTO.getThirdTeamId());
                    //如果标准赛事信息不为空。标准球队信息则也不应为空或0
                    if (null != standardMatchInfo && null != thirdSportTeam && (ZERO.equals(thirdSportTeam.getReferenceId()) || null == thirdSportTeam.getReferenceId())) {
                        thirdSportTeam = thirdSportTeamService.getItemByExample(matchEventInfoDTO.getDataSourceCode(), sportId, matchEventInfoDTO.getThirdTeamId());
                    }
                    log.info("linkId=【{}】process2MatchEvent，是否需要球队信息={},事件关联的球队信息={}", newLinkId, matchEventType.getRequiredTeam(), JSON.toJSONString(thirdSportTeam));
                }
                //美足赛事进行时间需要倒计时，这里如果是美足赛事将每节的剩余时间赋值给赛事进行时间  modify_by riben 2020-10-06
                if (StandardSportTypeEnum.Soccer.getCode().equals(sportId)) {
                    matchEventInfoDTO.setSecondsFromStart(matchEventInfoDTO.getPeriodRemainingSeconds());
                }
                //保存三方事件信息
                MatchEventInfo matchEventInfo = getMatchEventInfo(matchEventInfoDTO, oldThirdMatchInfo, thirdSportTeam, sportId, newLinkId,matchEventInfoList);
                log.info("linkId=【{}】process2MatchEvent，生成三方赛事事件信息完成,三方数据源赛事id={},三方事件信息={}", newLinkId, oldThirdMatchInfo.getThirdMatchSourceId(), JSON.toJSONString(matchEventInfo));
                //足球999事件特殊处理，AO初盘需要做事件统计
                applicationContext.getBean(MatchEventInfoProcessor.class).runAoMatchEventHistory(matchEventInfo);
                //标准赛事信息相关处理逻辑
                if (null != standardMatchInfo) {
                    if (matchEventInfoDTO.getDataSourceCode().equalsIgnoreCase(businessEventCode)) {
                        //足球标准事件特殊处理
                        if(StandardSportTypeEnum.FootBall.getCode().equals(sportId)){
                            //单号：75315 足球赛事：如果当前三方赛事阶段为中场休息，当收到赛事阶段为上半场的非删除事件直接过滤不下发给下游，PD、PD2报球板兼容处理(部分球种删除事件未按标准事件处理)
                            boolean eventCodeFlag = booleanEventCodeType(matchEventInfoDTO);
                            if("31".equals(oldThirdMatchInfo.getMatchPeriod()) && matchPeriodId == 6 && eventCodeFlag){
                                log.error("linkId=【{}】process2MatchEvent,优化单75315,当前足球赛事阶段为中场休息,上半场非删除事件直接过滤不保存不下发，事件类型为:={}，数据源编码:={}",
                                        linkId, matchEventInfoDTO.getEventCode(), matchEventInfoDTO.getDataSourceCode());
                                continue;
                            }
                            log.info(":={}【测试环境专用日志】::process2MatchEvent，优化单79713需要告警的事件,标准赛事ID={},alertsEventCodes={},eventCode={},赛事阶段={},eventCodeFlag={}"
                                    , linkId,standardMatchInfo.getId(),alertsEventCodes, matchEventInfoDTO.getEventCode(),oldThirdMatchInfo.getMatchPeriod(),eventCodeFlag);

                            //优化单79713，其中goal,corner,yellow_card,red_card只有删除事件才会触发告警
                            if (null != standardSportMarketSell && StringUtils.isNotBlank(standardSportMarketSell.getLiveTrader())) {
                                List<String> alertsEventCodeList = Arrays.asList(alertsEventCodes.split(","));
                                if(alertsEventCodeList.contains(matchEventInfo.getEventCode())){
                                    log.info("linkId=【{}】process2MatchEvent，优化单79713需要告警的事件,标准赛事ID={},eventCode={},canceled()={}", linkId,standardMatchInfo.getId(),eventCode, matchEventInfo.getCanceled());
                                    //优化单80413,其中goal,corner,yellow_card只有删除事件才会触发告警
                                    if(EventCodeEnum.alertsDeleteEventCodes().contains(matchEventInfo.getEventCode())){
                                        if(matchEventInfo.getCanceled() == 1){
                                            BeanUtils.copyProperties(matchEventInfo, alertsEventInfo);
                                        }
                                    }else{
                                        BeanUtils.copyProperties(matchEventInfo, alertsEventInfo);
                                    }
                                }
                            }

                            //88998 【生产】【产品】【操盘风控】足球主玩法-客户端不展示关盘赛事尾声球头兜底优化，3959 【操盘风控】【客户端】足球滚球期间-主玩法客户端不展示关盘优化
                            Long minMinute = MatchLengthEnum.getMinMinute2(standardMatchInfo.getSportId(), standardMatchInfo.getMatchLength(),matchPeriodId + "");
                            if(null != minMinute){
                                //最小分钟数的秒数
                                Long minSecond = minMinute * SIXTY;
                                //当前事件进行时长需要 大于 最小分钟数秒数
                                if(matchEventInfoDTO.getSecondsFromStart() >= minSecond){
                                    String standardSecondsMatchStartKey = String.format(ConstantSystem.getStandardSecondsMatchStartKey(), standardMatchInfo.getId());
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("matchPeriodId",matchPeriodId);
                                    jsonObject.put("secondsMatchStart",matchEventInfoDTO.getSecondsFromStart());
                                    redisService.set(standardSecondsMatchStartKey,jsonObject,RedisConfig.REDIS_HOUR_TIME);
                                }
                                log.info("linkId=【{}】process2MatchEvent,88998单缓存指定阶段事件进行时长,数据源赛事ID={},赛事阶段={},赛事进行时长={},minSecond={}",
                                        newLinkId, matchEventInfoDTO.getThirdMatchSourceId(),matchPeriodId,matchEventInfoDTO.getSecondsFromStart(),minSecond);
                            }

                            try {
                                //106537 足球赛事比分与统计比分不一致时预警并赛事级关盘
                                checkEventScoreValidation(matchEventInfo, oldThirdMatchInfo, linkId);
                            } catch (Exception e){
                                log.error("linkId=【{}】process2MatchEvent,checkEventScoreValidation异常,e={}",
                                        newLinkId, e.toString());
                            }

                        }

                        //篮球标准事件特殊处理
                        if(StandardSportTypeEnum.Basketball.getCode().equals(sportId)){
                            //跨阶段事件特殊处理  && 106379单处理,篮球999&100阶段不拦截
                            if(!oldThirdMatchInfo.getMatchPeriod().equals(matchPeriodId+"") && !endedList.contains(matchPeriodId)){
                                //缓存最新的事件发生时间,单号：81639
                                Long redisEventTime = (Long) redisService.get(eventTimeKey);
                                //如果当前事件发生时间小于缓存中事件发生时间，则特别标识
                                if(redisEventTime != null && matchEventInfoDTO.getEventTime().compareTo(redisEventTime) < 0){
                                    isReissue = true;
                                    log.info("linkId=【{}】process2MatchEvent，当前三方事件延迟消费，事件ID={},当前事件发生时间={}，上一条消费事件发生时间={},事件编码={},当前赛事阶段={},库中赛事阶段={}"
                                            , newLinkId, matchEventInfoDTO.getThirdEventId(),matchEventInfoDTO.getEventTime(),redisEventTime,eventCode,matchPeriodId,oldThirdMatchInfo.getMatchPeriod());
                                    //篮球非比分事件跨阶段过滤不下发
                                    if(!EventCodeEnum.SCORE_CHANGE.code.equalsIgnoreCase(eventCode)){
                                        continue;
                                    }
                                }
                            }
                        }
                        //完赛处理
                        if (ended999.equals(matchPeriodId)) {
                            //===================1220需求 普通足球新增判断是否异常结束事件===========================、
                            //如果为1，表示是操盘确认完赛的回调
                            if (!ONE.equals(matchEventInfoDTO.getIsErrorEndEvent())) {
                                if (StandardSportTypeEnum.FootBall.getCode().equals(standardMatchInfo.getSportId()) && ONE.equals(standardMatchInfo.getMatchType())) {
                                    //阶段为999.当前时事件进行时间不能小于全局赛事时长
                                    Long time = MatchLengthEnum.getTime(standardMatchInfo.getSportId(), standardMatchInfo.getMatchLength());
                                    //103304 【生产】【产品】【操盘风控】足球-等待加时期间异常下发完赛临时兜底-手动完赛
                                    String manuallyEndFlagKey = String.format(ConstantSystem.getStandardManuallyEndFlagKey(), standardMatchInfo.getId());
                                    //非PD事件源 & 缓存存在值  需要触发异常完赛
                                    if(!DataSourceCodeEnum.getPdCodeList().contains(matchEventInfo.getDataSourceCode()) && redisService.hasKey(manuallyEndFlagKey)){
                                        //赋值一小时毫秒数
                                        time = HOUR_1;
                                    }
                                    if (matchEventInfo.getSecondsFromStart() < time) {
                                        matchEventInfoProducer.pushMatchEventErrorEndData(linkId, matchEventInfo, oldThirdMatchInfo);
                                        matchEventInfoService.save(matchEventInfo);
                                        log.info("linkId=【{}】process2MatchEvent，标准赛事id={},商业事件源={},time={},异常的完赛事件={}", newLinkId, standardMatchInfo.getId(), businessEventCode,time, JSON.toJSONString(matchEventInfo));
                                        continue;
                                    }
                                }
                            }
                            //===================1220需求 普通足球新增判断是否异常结束事件===========================
                            //设值标准赛事完赛
                            upStandardMatchInfo.setMatchOver(YesNoEnum.Y.value);
                            //缓存赛事结束时间 用于事件审核
                            redisService.set(String.format(MATCH_OVER_TIME, standardMatchInfo.getId()),TimeUtils.millsSecondsEast8ZoneGmt(), RedisConfig.REDIS_WEEK_TIME);
                            if (null != standardSportMarketSell) {
                                //通知预售开售 赛事完赛消息
                                matchSaleOverProducer.sendMatchSaleOverMessage(newLinkId, standardMatchInfo);
                            }
                        }

                        //需求：2659 【操盘风控管理优化】足球 - PD报球板新增可删除数据商事件
                        if(flag_2659){
                            upStandardMatchInfo.setMatchOver(YesNoEnum.N.value);
                        }

                        //设置标准赛事信息
                        if(!isReissue){
                            upStandardMatchInfo.setMatchPeriodId(matchEventInfo.getMatchPeriodId());
                            if (matchEventInfo.getSecondsFromStart() != null){
                                upStandardMatchInfo.setSecondsMatchStart(Math.toIntExact(matchEventInfo.getSecondsFromStart()));
                            }
                            upStandardMatchInfo.setSecondsMatchModifyTime(matchEventInfo.getEventTime());
                        }
                        //92233 【产品】【生产】足球完赛兜底机制优化
                        if(DataSourceCodeEnum.getPdCodeList().contains(matchEventInfoDTO.getDataSourceCode()) &&
                                MatchPeriodForMatchOverEnum.Interrupted.value.equals(matchPeriodId)){
                            upStandardMatchInfo.setInterruptionCancellationStatus(YesNoEnum.Y.value);

                            upThirdMatchInfo.setInterruptionCancellationStatus(YesNoEnum.Y.value);
                        }
                        log.info("linkId=【{}】process2MatchEvent，需要的更新标准赛事信息={},赛事阶段={},比赛已进行时长={},是否完赛={}", linkId, standardMatchInfo.getId(), upStandardMatchInfo.getMatchPeriodId(), upStandardMatchInfo.getSecondsMatchStart(), upStandardMatchInfo.getMatchOver());
                    } else {
                        log.info("linkId=【{}】process2MatchEvent，没有更新标准赛事,标准赛事id={},商业事件源={}", newLinkId, standardMatchInfo.getId(), businessEventCode);
                    }

                    //是否标准事件
                    if (null != standardSportMarketSell && matchEventInfo.getDataSourceCode().equalsIgnoreCase(businessEventCode)) {
                        matchEventInfo.setSendData(YesNoEnum.Y.name());
                        //异步处理自动关盘玩法
                        autoCloseMarketDispose(standardMatchInfo, newLinkId, matchEventInfo);
                        //提前结算特殊事件处理
                        standardEventCashOutStatusProcessor.cashOutStatusUnavailable(standardMatchInfo, newLinkId, eventCode);

                        //收到PD事件下发滚球标识给下游
                        if(dataSourceCode.equals(DataSourceCodeEnum.PD.getCode())){
                            standardMatchEventOddsLiveProducer.sendStandardOddsLiveStatus(linkId, standardMatchInfo, dataSourceCode);
                        }
                    }

                    //1852 进球事件触发强转关盘
                    if (null != standardSportMarketSell && EventCodeEnum.GOAL.code.equalsIgnoreCase(eventCode)) {
                        final String linkIdf = newLinkId;
                        final Long matchIdf = standardMatchInfo.getId();
                        //异步处理自动关盘玩法
                        TaskExecutor taskExecutor = threadPoolConfig.getInitSportMarketRelation();
                        taskExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                iTradeMarketConfigApi.autoCloseOldMarket(linkIdf, matchIdf, TimeUtils.millsSecondsEast8ZoneGmt());
                            }
                        });
                    }

                    //103715 转优化处理 当事件源下发跨阶段修改比分或删除比分时，将在嘀嘀群组预警
                    try {
                        if (
                                StandardSportTypeEnum.Basketball.code.equals(matchEventInfo.getSportId()) &&
                                DataSourceCodeEnum.getCrossPeriodScoreChangedCode().contains(matchEventInfo.getDataSourceCode()) &&
                                matchEventInfo.getStandardMatchId() != null && matchEventInfo.getStandardMatchId() !=0L &&
                                EventCodeEnum.SCORE_CORRECTION.code.equals(matchEventInfo.getEventCode()) &&
                                BasketBallPeroidEnum.getCrossPeriod().contains(oldThirdMatchInfo.getMatchPeriod()) &&
                                BasketBallPeroidEnum.getCrossPeriod().contains(matchEventInfo.getMatchPeriodId().toString())
                        ) {

                            if (!Objects.equals(oldThirdMatchInfo.getMatchPeriod(), matchEventInfo.getMatchPeriodId().toString())) {
                                WarningEventDTO warningEventDTO = new WarningEventDTO();
                                warningEventDTO.setLinkId(linkId);
                                warningEventDTO.setWarningType(WarningTypeEnum.CROSS_PERIOD_SCORE_CHANGED);

                                Map<String, Object> warnContext = new HashMap<>();
                                warnContext.put("dataSourceCode", DataSourceCodeEnum.getDataSourceCodeEnumByCode(matchEventInfo.getDataSourceCode()).getMaskedCode());
                                warnContext.put("homeAwayInfo", standardMatchInfo.getHomeAwayInfo());
                                warnContext.put("matchManageId", standardMatchInfo.getMatchManageId());

                                BasketBallPeroidEnum curPeriod = BasketBallPeroidEnum.getEnum(matchEventInfo.getMatchPeriodId().intValue());
                                BasketBallPeroidEnum originalPeriod = BasketBallPeroidEnum.getEnum(Integer.parseInt(oldThirdMatchInfo.getMatchPeriod()));
                                warnContext.put("originalMatchPeriod", curPeriod != null ? curPeriod.getValueZh() : matchEventInfo.getMatchPeriodId());
                                warnContext.put("curMatchPeriod", originalPeriod != null ? originalPeriod.getValueZh() : oldThirdMatchInfo.getMatchPeriod());
                                warnContext.put("reason","赛事跨阶段下发修正比分");
                                warningEventDTO.setContexts(Lists.newArrayList(warnContext));

                                warningService.warn(warningEventDTO);
                            }

                        }
                    } catch (Exception e) {
                        log.error("篮球跨阶段修改比分预警异常, linkId={}", linkId, e);
                    }

                    try {
                        //108174 PD手动中断赛事后, 当收到商业数据源下发的事件时, 切换事件源
                        changeBusinessDataSourceFromPD(linkId, matchEventInfo, standardMatchInfo, oldThirdMatchInfo, standardSportMarketSell);
                    } catch (Exception e){
                        log.error("linkId=【{}】process2MatchEvent,changeBusinessDataSourceFromPD异常,e={}",
                                newLinkId, e.toString());
                    }
                }

                //本次需要修改的三方赛事字段
                if(!isReissue){
                    upThirdMatchInfo.setMatchPeriod(String.valueOf(matchEventInfo.getMatchPeriodId()));
                    if (matchEventInfo.getSecondsFromStart() != null){
                        upThirdMatchInfo.setSecondsMatchStart(Math.toIntExact(matchEventInfo.getSecondsFromStart()));
                    }
                    upThirdMatchInfo.setSecondsMatchModifyTime(matchEventInfo.getEventTime());
                }
                //当前赛事阶段为999，则设值完赛状态字段
                if (ended999.equals(matchPeriodId)) {
                    log.info("linkId=【{}】process2MatchEvent，赛事阶段999，设值三方赛事完赛状态,三方赛事原始id={}", newLinkId, oldThirdMatchInfo.getThirdMatchSourceId());
                    upThirdMatchInfo.setMatchOver(YesNoEnum.Y.value);
                }
                //需求：2659 【操盘风控管理优化】足球 - PD报球板新增可删除数据商事件
                if(flag_2659){
                    log.info("linkId=【{}】process2MatchEvent，赛事原始id{},当前阶段{}回退到阶段{}，回退三方赛事完赛状态。", newLinkId, oldThirdMatchInfo.getThirdMatchSourceId(),oldThirdMatchInfo.getMatchPeriod(),matchPeriodId);
                    upThirdMatchInfo.setMatchOver(YesNoEnum.N.value);
                    redisService.del(ended999Key);
                }
                log.info("linkId=【{}】process2MatchEvent，当前三方赛事信息={},赛事阶段={},比赛已进行时长={},是否完赛={}", linkId, oldThirdMatchInfo.getId(), upThirdMatchInfo.getMatchPeriod(), upThirdMatchInfo.getSecondsMatchStart(), upThirdMatchInfo.getMatchOver());

                //injury_time事件处理,手动下发后，当前阶段下发的injury_time事件不下发(事件下发集合)
                extractedMatchEventPushList(linkId, eventCode, matchEventInfo, matchEventPushList);
                //三方事件保存集合
                matchEventInfoList.add(matchEventInfo);
                stopWatch.stop();
                log.info("linkId=【{}】process2MatchEvent，-------单条事件处理结束，耗时={}-------,数据源事件ID={}", newLinkId, stopWatch.getTotalTimeMillis(), matchEventInfoDTO.getThirdEventId());

            } catch (Exception e) {
                log.error("::" + newLinkId + "::process2MatchEvent，error:事件信息处理异常，当前数据源事件ID:" + matchEventInfoDTO.getThirdEventId() + "，Exception:", e);
            }
        }

        log.info("linkId=【{}】process2MatchEvent，组装需要下发的事件列表结束,数据源赛事id={},需要入库事件条数={},需要推送事件条数={},liveEventSource={}", linkId, oldThirdMatchInfo.getThirdMatchSourceId(), matchEventInfoList.size(), matchEventPushList.size(),oldThirdMatchInfo.getLiveEventSource());
        if (!CollectionUtils.isEmpty(matchEventInfoList)) {
            //缓存最新的事件发生时间,单号：81639
            if(StandardSportTypeEnum.Basketball.getCode().equals(sportId)){
                redisService.set(eventTimeKey,matchEventInfoList.get(matchEventInfoList.size()-1).getEventTime(), TWO * RedisConfig.REDIS_HOUR_TIME);
            }

            try {
                if(!isReissue){
                    //无需过滤的事件源
                    if (!DataSourceCodeEnum.getPdCodeList().contains(dataSourceCode)) {
                        //缓存事件完赛标识,默认缓存1天
                        Set<Long> matchPeriodIds = matchEventInfoList.stream()
                                .filter(obj -> EventCodeEnum.MATCH_STATUS.code.equalsIgnoreCase(obj.getEventCode())).map(obj -> obj.getMatchPeriodId()).collect(Collectors.toSet());
                        if(matchPeriodIds.contains(ended999)){
                            redisService.set(ended999Key,ONE);
                        }
                    }
                    //本次赛事阶段和上一次赛事阶段不一致才需要修改赛事信息
                    if(!oldThirdMatchInfo.getMatchPeriod().equals(upThirdMatchInfo.getMatchPeriod())){
                        String thirdMatchPeriodKey = String.format(ConstantSystem.getThirdMatchPeriodKey(), oldThirdMatchInfo.getDataSourceCode(),oldThirdMatchInfo.getThirdMatchSourceId());
                        redisService.set(thirdMatchPeriodKey,upThirdMatchInfo.getMatchPeriod());
                        //修改三方赛事
                        BeanUtil.copyProperties(upThirdMatchInfo,oldThirdMatchInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                        realtimeBaseProduecr.send(upThirdMatchInfo,linkId,DATA_THIRD_MATCH_INFO_DB,thirdMatchSourceId,oldThirdMatchInfo.getDataSourceCode());
                        log.info("linkId=【{}】process2MatchEvent,需要更新的三方赛事信息={},thirdMatchPeriodKey={}", linkId, JSON.toJSONString(upThirdMatchInfo),thirdMatchPeriodKey);

                        //3803【比分网】比分网后台-联赛管理
                        ThirdMatchInfo upThirdMatchInfoPls = new ThirdMatchInfo();
                        BeanUtils.copyProperties(upThirdMatchInfo,upThirdMatchInfoPls);
                        upThirdMatchInfoPls.setDataSourceCode(oldThirdMatchInfo.getDataSourceCode());
                        upThirdMatchInfoPls.setThirdMatchSourceId(oldThirdMatchInfo.getThirdMatchSourceId());
                        realtimeBaseProduecr.send(upThirdMatchInfoPls,linkId,THIRD_MATCH_INFO_PERIODID_PLS,thirdMatchSourceId,oldThirdMatchInfo.getDataSourceCode());
//                        log.info("【测试环境调试专用日志】:={}::process2MatchEvent,更新后三方赛事信息={}", linkId, JSON.toJSONString(oldThirdMatchInfo));
                    }else{
                        log.info("linkId=【{}】process2MatchEvent,赛事阶段一致，无需更新三方赛事信息,old={},new={}", linkId, oldThirdMatchInfo.getMatchPeriod(),upThirdMatchInfo.getMatchPeriod());
                    }
                    //根据商业事件源更新标准赛事的赛事阶段
                    if (null != standardMatchInfo) {
                        if(StandardSportTypeEnum.FootBall.getCode().equals(sportId) && dataSourceCode.equalsIgnoreCase(businessEventCode)){
                            // 96856 【生产】【产品】足球赛事未开赛阶段收到开赛事件，系统自动触发赛事级关盘
                            if(standardMatchInfo.getBeginTime() > System.currentTimeMillis() && upStandardMatchInfo.getMatchPeriodId() == 6
                                    && upStandardMatchInfo.getSecondsMatchStart() > 0 && standardMatchInfo.getMatchStatus() == 0){
                                //修改标准赛事
                                BeanUtil.copyProperties(upStandardMatchInfo,standardMatchInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                                standardMatchInfo.setMatchStatus(MatchStatusEnum.Live.value);

                                //通知早转滚
                                standardMatchEventOddsLiveProducer.sendStandardOddsLiveStatus(linkId, standardMatchInfo, dataSourceCode,1);

                                //下发赛事状态给业务
                                thirdMatchStatusProcessor.pushMatchStatusInfo(linkId, standardMatchInfo, standardSportMarketSell, standardMatchInfo.getDataSourceCode(), System.currentTimeMillis());

                                //事件这里直接修改标准赛事状态
                                upStandardMatchInfo.setMatchStatus(MatchStatusEnum.Live.value);
                                realtimeBaseProduecr.send(upStandardMatchInfo,linkId,DATA_STANDARD_MATCH_INFO_DB,standardMatchInfo.getId()+"",dataSourceCode);
                                log.info("linkId=【{}】process2MatchEvent_96856，需要更新的标准赛事信息={}", linkId, JSON.toJSONString(upStandardMatchInfo));
                            }

                            //4248 【赛程】赛事中断场景优化: 事件编码=match_status & 赛事阶段=80 ，属于停表中断，如标准赛事状态不是中断则需要生成标准赛事中断状态下发
                            if(interruptedEventSwitch){
                                MatchEventInfo eventInfo = matchEventPushList.stream().filter(obj ->
                                        StringUtils.equals(EventCodeEnum.MATCH_STATUS.code, obj.getEventCode()) &&
                                                Objects.equals(MatchPeriodForMatchOverEnum.Interrupted.value, obj.getMatchPeriodId())
                                ).findFirst().orElse(null);
                                if(eventInfo != null){
                                    //标准赛事状态不是中断
                                    if(!Objects.equals(MatchStatusEnum.Interrupted.value,standardMatchInfo.getMatchStatus())){
                                        //补发中断
                                        BeanUtil.copyProperties(upStandardMatchInfo,standardMatchInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                                        standardMatchInfo.setMatchStatus(MatchStatusEnum.Interrupted.value);

                                        //下发赛事状态给业务
                                        thirdMatchStatusProcessor.pushMatchStatusInfo(linkId, standardMatchInfo, standardSportMarketSell, standardMatchInfo.getDataSourceCode(), System.currentTimeMillis());

                                        //事件这里直接修改标准赛事状态
                                        upStandardMatchInfo.setMatchStatus(MatchStatusEnum.Interrupted.value);
                                        realtimeBaseProduecr.send(upStandardMatchInfo,linkId,DATA_STANDARD_MATCH_INFO_DB,standardMatchInfo.getId()+"",dataSourceCode);
                                        log.info("linkId=【{}】process2MatchEvent_4248，赛事中断,需要更新的标准赛事信息={}", linkId, JSON.toJSONString(upStandardMatchInfo));

                                        //缓存标识5小时
                                        String interruptedKey = String.format(ConstantSystem.getInterruptedKey(), standardMatchInfo.getId());
                                        redisService.set(interruptedKey,ONE,FIVES * RedisConfig.REDIS_HOUR_TIME);
                                    }
                                }else{
                                    //如果库中标准阶段是中断.当前标准阶段不是中断(中断恢复)
                                    if(Objects.equals(MatchPeriodForMatchOverEnum.Interrupted.value, standardMatchInfo.getMatchPeriodId())
                                        && !standardMatchInfo.getMatchPeriodId().equals(upStandardMatchInfo.getMatchPeriodId())){
                                        redisService.del(String.format(ConstantSystem.getInterruptedKey(), standardMatchInfo.getId()));

                                        //补发滚球
                                        BeanUtil.copyProperties(upStandardMatchInfo,standardMatchInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                                        standardMatchInfo.setMatchStatus(MatchStatusEnum.Live.value);

                                        //下发赛事状态给业务
                                        thirdMatchStatusProcessor.pushMatchStatusInfo(linkId, standardMatchInfo, standardSportMarketSell, standardMatchInfo.getDataSourceCode(), System.currentTimeMillis());

                                        //事件这里直接修改标准赛事状态
                                        upStandardMatchInfo.setMatchStatus(MatchStatusEnum.Live.value);
                                        realtimeBaseProduecr.send(upStandardMatchInfo,linkId,DATA_STANDARD_MATCH_INFO_DB,standardMatchInfo.getId()+"",dataSourceCode);
                                        log.info("linkId=【{}】process2MatchEvent_4248，中断恢复,需要更新的标准赛事信息={}", linkId, JSON.toJSONString(upStandardMatchInfo));
                                    }
                                }
                            }
                        }

                        if (dataSourceCode.equalsIgnoreCase(businessEventCode) && !standardMatchInfo.getMatchPeriodId().equals(upStandardMatchInfo.getMatchPeriodId())){
                            String standardMatchPeriodKey = String.format(ConstantSystem.getStandardMatchPeriodKey(), standardMatchInfo.getId());
                            redisService.set(standardMatchPeriodKey,upStandardMatchInfo.getMatchPeriodId());
                            //修改标准赛事
                            BeanUtil.copyProperties(upStandardMatchInfo,standardMatchInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

                            //96200 【生产】【产品】赛事事件下发999结束，但赛事状态未下发， 事件999完赛，通知赛事状态完赛
                            if(MatchPeriodForMatchOverEnum.Ended999.value.equals(upStandardMatchInfo.getMatchPeriodId())){
                                //下发赛事状态给业务
                                standardMatchInfo.setMatchStatus(MatchStatusEnum.Ended.value);
                                thirdMatchStatusProcessor.pushMatchStatusInfo(linkId, standardMatchInfo, standardSportMarketSell,
                                        standardMatchInfo.getDataSourceCode(), System.currentTimeMillis());

                                //事件这里直接修改标准赛事状态
                                upStandardMatchInfo.setMatchStatus(MatchStatusEnum.Ended.value);
                            }

                            realtimeBaseProduecr.send(upStandardMatchInfo,linkId,DATA_STANDARD_MATCH_INFO_DB,standardMatchInfo.getId()+"",dataSourceCode);
                            log.info("linkId=【{}】process2MatchEvent，需要更新的标准赛事信息={},standardMatchPeriodKey={}", linkId, JSON.toJSONString(upStandardMatchInfo),standardMatchPeriodKey);
                            //3803【比分网】比分网后台-联赛管理
                            if (standardMatchInfo.getPlsStandardMatchId()!=null && standardMatchInfo.getPlsStandardMatchId()!=0) {
                                StandardSportTournament standardSportTournament = standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());

                                StandardMatchPeriodBO standardMatchPeriodBO = new StandardMatchPeriodBO();
                                BeanUtils.copyProperties(standardMatchInfo, standardMatchPeriodBO);
                                standardMatchPeriodBO.setStandardMatchId(standardMatchInfo.getId());
                                if (standardSportTournament != null) {
                                    standardMatchPeriodBO.setPlsStandardTournamentId(standardSportTournament.getPlsStandardTournamentId());
                                }
                                realtimeBaseProduecr.send(standardMatchPeriodBO,linkId,STANDARD_MATCH_INFO_PERIODID_PLS,standardMatchInfo.getId()+"",dataSourceCode);
                            }
                        } else {
                            log.info("linkId=【{}】process2MatchEvent，没有更新标准赛事,标准赛事id={},当前数据源编码={},商业事件源={},old={},new={}", linkId, standardMatchInfo.getId(), dataSourceCode, businessEventCode, standardMatchInfo.getMatchPeriodId(),upStandardMatchInfo.getMatchPeriodId());
                        }
                    }
                } else{
                    log.info("linkId=【{}】process2MatchEvent,事件延迟消费,无需更新三方赛事和标准赛事信息,三方赛事id={}", linkId,oldThirdMatchInfo.getThirdMatchSourceId());
                }
            } catch (Exception e) {
                log.error("::" + linkId + "::process2MatchEvent，error:修改赛事数据异常，当前数据源赛事ID:" + oldThirdMatchInfo.getThirdMatchSourceId() + "，Exception:", e);
            }

            try {
                //1.推送三方赛事事件到队列 THIRD_MATCH_EVENT_INFO
                for (MatchEventInfo matchEventInfo : matchEventPushList) {
                    matchEventInfoProducer.pushThirdMatchEvent(matchEventInfo.getLinkId(), matchEventInfo, oldThirdMatchInfo);
                    log.info("linkId=【{}】process2MatchEvent，推送事件到队列结束1 THIRD_MATCH_EVENT_INFO，三方赛事原始id={}", matchEventInfo.getLinkId(), oldThirdMatchInfo.getThirdMatchSourceId());
                }
                //2.推送生成标准赛事事件到队列 MATCH_EVENT_INFO_TO_RISK
                if (null != standardMatchInfo) {
                    //缓存中含有增量事件,linkId需要区分开下发
                    if (copyLinkIdFlag) {
                        for (MatchEventInfo matchEventInfo : matchEventPushList) {
                            matchEventInfoProducer.pushMatchEventDataToRisk(matchEventInfo.getLinkId(), Lists.newArrayList(matchEventInfo), oldThirdMatchInfo,isReissue);
                            log.info("linkId=【{}】process2MatchEvent，推送事件到队列结束1 MATCH_EVENT_INFO_TO_RISK，三方赛事原始id={}", matchEventInfo.getLinkId(), oldThirdMatchInfo.getThirdMatchSourceId());
                        }
                    } else {
                        matchEventInfoProducer.pushMatchEventDataToRisk(linkId, matchEventPushList, oldThirdMatchInfo,isReissue);
                        log.info("linkId=【{}】process2MatchEvent，推送事件到队列结束x MATCH_EVENT_INFO_TO_RISK，三方赛事原始id={},下发条数={}", linkId, oldThirdMatchInfo.getThirdMatchSourceId(),matchEventPushList.size());
                    }

                    //BC事件特殊处理
                    Boolean eventFlag = bcEventProcessor(linkId, standardMatchInfo, oldThirdMatchInfo);
                    if (eventFlag) {
                        //3.推送生成标准赛事并事件源编码一致的事件到 MATCH_EVENT_INFO（商业事件源编码判断事件是否下发）
                        if (null != standardSportMarketSell && dataSourceCode.equalsIgnoreCase(businessEventCode)) {
                            //缓存中含有增量事件,linkId需要区分开
                            if (copyLinkIdFlag) {
                                for (MatchEventInfo matchEventInfo : matchEventPushList) {
                                    matchEventInfoProducer.pushMatchEventData(matchEventInfo.getLinkId(), Lists.newArrayList(matchEventInfo), oldThirdMatchInfo,isReissue,true);
                                    log.info("linkId=【{}】process2MatchEvent，推送事件到队列结束1 MATCH_EVENT_INFO ，三方赛事原始id={}", matchEventInfo.getLinkId(), oldThirdMatchInfo.getThirdMatchSourceId());
                                }
                            } else {
                                matchEventInfoProducer.pushMatchEventData(linkId, matchEventPushList, oldThirdMatchInfo,isReissue,true);
                                log.info("linkId=【{}】process2MatchEvent，推送事件到队列结束x MATCH_EVENT_INFO ，三方赛事原始id={},下发条数={}", linkId, oldThirdMatchInfo.getThirdMatchSourceId(),matchEventPushList.size());
                            }
                        }
                    }else{
                        //96088单问题处理 如果是这种情况需要将事件表为非标准事件，方便后续开售补发
                        for(MatchEventInfo item : matchEventInfoList){
                            item.setSendData(YesNoEnum.N.name());
                        }
                    }
                }
            } catch (Exception e) {
                //缓存事件的key后半截
                String matchTryLock = "MatchEventInfoDTO_" + dataSourceCode + "_" + thirdMatchSourceId;
                //缓存事件的key
                String matchEventKey = RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfoDTO:" + matchTryLock;
                //缓存推送到MQ异常的事件信息
                for (MatchEventInfoDTO item : matchEventInfoDtos) {
                    if (StringUtils.isBlank(item.getCopyLinkId())) {
                        item.setCopyLinkId(linkId);
                    }
                    redisService.hSet(matchEventKey, item.getThirdEventId(), item);
                }
                log.error("::" + linkId + "::process2MatchEvent，error:推送事件信息到MQ异常，当前数据源赛事ID:" + oldThirdMatchInfo.getThirdMatchSourceId() + "，Exception:", e);
            }

            //三方事件数据入库逻辑
            if (!DataSourceCodeEnum.TS.code.equalsIgnoreCase(dataSourceCode)) {
                matchEventInfoProducer.saveMatchEventInfoList(linkId,matchEventInfoList,oldThirdMatchInfo);
            } else {
                //V02正常事件直接入库
                List<MatchEventInfo> resList_1 = matchEventInfoList.stream().filter(obj -> ONE.equals(obj.getSourceType())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(resList_1)) {
                    matchEventInfoProducer.saveMatchEventInfoList(linkId,resList_1,oldThirdMatchInfo);
                }
                //如果是V02的UOF事件则支持随时修改，所以不能直接新增入库
                List<MatchEventInfo> resList_0 = matchEventInfoList.stream().filter(obj -> ZERO.equals(obj.getSourceType())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(resList_0)) {
                    log.info("linkId=【{}】process2MatchEvent,V02集锦事件入库,三方赛事id={},条数={},pandaDbIsError={}", linkId,oldThirdMatchInfo.getThirdMatchSourceId(),resList_0.size(),pandaDbIsError);
                    if(!pandaDbIsError){
                        matchEventInfoService.upOrSaveBatch(resList_0,linkId);
                    }else{
                        matchEventInfoProducer.saveMatchEventInfoList(linkId,resList_0,oldThirdMatchInfo);
                    }
                }
            }

            //优化单79713，需要告警的足球事件
            if(null != alertsEventInfo && StringUtils.isNotBlank(alertsEventInfo.getEventCode())){
                matchEventInfoProducer.alertsEventInfo2MQ(alertsEventInfo,standardMatchInfo,standardSportMarketSell.getLiveTrader(),matchEventInfoList,true);
            }else{
                //标准事件源非告警事件特殊处理
                if (null != standardSportMarketSell && dataSourceCode.equalsIgnoreCase(businessEventCode)) {
                    BeanUtils.copyProperties(matchEventInfoList.get(matchEventInfoList.size()-1), alertsEventInfo);
                    matchEventInfoProducer.alertsEventInfo2MQ(alertsEventInfo,standardMatchInfo,standardSportMarketSell.getLiveTrader(),matchEventInfoList,false);
                }

            }
        }
    }

    /**
     * 108174
     * 2. PD手动中断赛事后, 当收到商业数据源下发的事件时, 切换事件源
     */
    private void changeBusinessDataSourceFromPD(String linkId, MatchEventInfo matchEventInfo, StandardMatchInfo standardMatchInfo,
                                                ThirdMatchInfo oldThirdMatchInfo, StandardSportMarketSell standardSportMarketSell){
        Long referenceId = oldThirdMatchInfo.getReferenceId();
        String thirdMatchSourceId = oldThirdMatchInfo.getThirdMatchSourceId();
        String dataSourceCode = matchEventInfo.getDataSourceCode();
        if (!interruptedEventSwitch
                || !DataSourceCodeEnum.getBusinessCode().contains(dataSourceCode)
                || Objects.equals(MatchPeriodForMatchOverEnum.Interrupted.value, matchEventInfo.getMatchPeriodId())
                || !Objects.equals(MatchPeriodForMatchOverEnum.Interrupted.value, standardMatchInfo.getMatchPeriodId())
                || !DataSourceCodeEnum.PD.getCode().equalsIgnoreCase(standardSportMarketSell.getBusinessEvent())){
            log.info("linkId=【{}】changeBusinessDataSourceFromPD, 不切换商业数据源, interruptedEventSwitch={}, eventMatchPeriodId={}, standardMatchPeriodId={}, standardDataSourceCode={}, businessEvent={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}",
                    linkId, interruptedEventSwitch, matchEventInfo.getMatchPeriodId(), standardMatchInfo.getMatchPeriodId(),
                    standardMatchInfo.getDataSourceCode(), standardSportMarketSell.getBusinessEvent(),
                    referenceId, dataSourceCode, thirdMatchSourceId);
            return;
        }

        log.info("linkId=【{}】changeBusinessDataSourceFromPD, 切换商业数据源, matchEventInfo={}, standardMatchPeriodId={}, standardDataSourceCode={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}",
                linkId, JSON.toJSONString(matchEventInfo), standardMatchInfo.getMatchPeriodId(), standardMatchInfo.getDataSourceCode(),
                referenceId, dataSourceCode, thirdMatchSourceId);

        String lockKey = String.format(ConstantSystem.getBusinessEventLockKey(), referenceId);

        processTradeSystemThreadPool.execute(()->{
            long start = System.currentTimeMillis();
            try {
                if (!redisService.tryLockOnce(lockKey, lockKey, 10)){
                    log.info("linkId=【{}】changeBusinessDataSourceFromPD, 不可重复切换商业数据源, lockKey={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}",
                            linkId, lockKey, referenceId, dataSourceCode, thirdMatchSourceId);
                    return;
                }

                Request requestBusinessDto = new Request<>();
                ChangeBusinessEventSaleDTO changeBusinessEventSaleDTO = new ChangeBusinessEventSaleDTO();
                changeBusinessEventSaleDTO.setSportId(1L);
                changeBusinessEventSaleDTO.setUserId("-1");
                changeBusinessEventSaleDTO.setDataType(dataSourceCode);
                changeBusinessEventSaleDTO.setId(Long.valueOf(standardSportMarketSell.getMatchManageId()));
                requestBusinessDto.setData(changeBusinessEventSaleDTO);
                requestBusinessDto.setLinkId(linkId);
                Response response = iMarketCategorySellApi.changeBusinessEvent(requestBusinessDto);
                if (response.isSuccess() && response.getCode() == 200){
                    StandardSportMarketSell tempMarketSell = standardSportMarketSellService.getItem(oldThirdMatchInfo.getReferenceId());
                    StandardMatchInfo tempStandardMatchInfo = standardMatchInfoService.getItem(oldThirdMatchInfo.getReferenceId());
                    log.info("linkId=【{}】changeBusinessDataSourceFromPD, 切换商业数据源请求成功, marketSell={}, standardMatchInfo={}, duration={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}",
                            linkId, JSON.toJSONString(tempMarketSell), JSON.toJSONString(tempStandardMatchInfo), System.currentTimeMillis() - start, referenceId, dataSourceCode, thirdMatchSourceId);
                } else {
                    log.info("linkId=【{}】changeBusinessDataSourceFromPD, 切换商业数据源请求失败, duration={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}",
                            linkId, System.currentTimeMillis() - start, referenceId, dataSourceCode, thirdMatchSourceId);
                }
            } catch (Exception e){
                log.error("linkId=【{}】changeBusinessDataSourceFromPD, 切换商业数据源请求异常, duration={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}, e={}",
                        linkId, System.currentTimeMillis() - start, referenceId, dataSourceCode, thirdMatchSourceId, e.toString());
            }
        });
    }


    /**
     * 106537
     * 商业数据源（S01 G01 K01 R01 B02）下发的所有事件中新增附带进球比分，当收到进球比分与标准统计进球比分对比，比分连续5次不一致时弹窗预警并对应自动赛事级关盘
     * 111278 改封盘
     */
    private void checkEventScoreValidation(MatchEventInfo matchEventInfo, ThirdMatchInfo thirdMatchInfo, String linkId){
        String dataSourceCode = matchEventInfo.getDataSourceCode();
        if (!DataSourceCodeEnum.getCrossPeriodScoreChangedCode().contains(dataSourceCode)){
            return;
        }

        Long referenceId = thirdMatchInfo.getReferenceId();
        String thirdMatchSourceId = thirdMatchInfo.getThirdMatchSourceId();
        if (StringUtils.isBlank(matchEventInfo.getAddition7()) || StringUtils.isBlank(matchEventInfo.getAddition8())
                || referenceId == null || StringUtils.isBlank(thirdMatchSourceId)){
            log.info("linkId=【{}】checkEventScoreValidation, 部分参数为空, addition7={}, addition8={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}",
                    linkId, matchEventInfo.getAddition7(), matchEventInfo.getAddition8(), referenceId, dataSourceCode, thirdMatchSourceId);
            return;
        }

        //常规赛
        List<Long> commonPeriods = Lists.newArrayList(MatchPeriodForMatchOverEnum.MATCH_1H.value,
                MatchPeriodForMatchOverEnum.MATCH_2H.value, MatchPeriodForMatchOverEnum.HALF_TIME.value);
        boolean isCommonPeriod = commonPeriods.contains(matchEventInfo.getMatchPeriodId());

        //表示加时赛的阶段, 参考MatchEventInfoProducer.matchEventScores2Redis
        List<Long> overTimePeriods = Lists.newArrayList(MatchPeriodForMatchOverEnum.OverTime_1H.value,
                MatchPeriodForMatchOverEnum.OverTime_HT.value, MatchPeriodForMatchOverEnum.OverTime_2H.value);
        boolean isOvertimePeriod = overTimePeriods.contains(matchEventInfo.getMatchPeriodId());

        if (!isCommonPeriod && !isOvertimePeriod){
            log.info("linkId=【{}】checkEventScoreValidation, 非常规赛，非加时赛，不校验, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}",
                    linkId, referenceId, dataSourceCode, thirdMatchSourceId);
            return;
        }

        //包括常规赛比分+加时赛比分，不包含点球大战比分
        Integer eventHomeScore = null;
        Integer eventAwayScore = null;
        try {
            eventHomeScore = Integer.parseInt(matchEventInfo.getAddition7());
            eventAwayScore = Integer.parseInt(matchEventInfo.getAddition8());
        } catch (Exception e){
            log.error("linkId=【{}】checkEventScoreValidation, 比分转换异常, addition7={}, addition8={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}",
                    linkId, matchEventInfo.getAddition7(), matchEventInfo.getAddition8(), referenceId, dataSourceCode, thirdMatchSourceId);
            return;
        }

        //比分中心的标准比分缓存key
        String cacheScoresKey = DigestUtil.md5Hex(Constant.REDIS_KEY.FOOTBALL_STANDARD_MATCH_SCORES + referenceId);
        FootballCacheScores footballCacheScores = null;
        Object scores = redisService.get(cacheScoresKey);
        if (scores instanceof String) {
            try {
                footballCacheScores = JSON.parseObject((String) scores, FootballCacheScores.class);
                log.info("linkId=【{}】checkEventScoreValidation, 获取缓存中比分, FOOTBALL_STANDARD_MATCH_SCORES, cacheScoresKey={}, footballCacheScores={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}",
                        linkId, cacheScoresKey, JSON.toJSONString(footballCacheScores), referenceId, dataSourceCode, thirdMatchSourceId);
            } catch (Exception e) {
                log.error("linkId=【{}】checkEventScoreValidation, 获取缓存比分异常, cacheScoresKey={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}, Exception={}",
                        linkId, cacheScoresKey, referenceId, dataSourceCode, thirdMatchSourceId, e);
            }
        }

        if (footballCacheScores == null || footballCacheScores.getGoal() == null){
            log.info("linkId=【{}】checkEventScoreValidation, 未获取到比分缓存, FOOTBALL_STANDARD_MATCH_SCORES, cacheScoresKey={}, scores={}, matchEventInfo={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}",
                    linkId, cacheScoresKey, scores, JSON.toJSONString(matchEventInfo), referenceId, dataSourceCode, thirdMatchSourceId);
            return;
        }

        CommonItem cacheCommonGoal = footballCacheScores.getGoal();
        CommonItem cacheExtraGoal = footballCacheScores.getOverTimeGoal();
        //总比分：比分中心的常规赛比分 + 加时赛比分，不包含点球
        Integer totalHomeScore = null;
        Integer totalAwayScore = null;

        if (isOvertimePeriod) {
            try {
                if (null != cacheExtraGoal) {
                    totalHomeScore = cacheExtraGoal.getHome() + cacheCommonGoal.getHome();
                    totalAwayScore = cacheExtraGoal.getAway() + cacheCommonGoal.getAway();
                    log.info("linkId=【{}】checkEventScoreValidation, 加时赛比分, commonHome={}, commonAway={}, extraHome={}, extraAway={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}",
                            linkId, cacheCommonGoal.getHome(), cacheCommonGoal.getAway(), cacheExtraGoal.getHome(), cacheExtraGoal.getAway(), referenceId, dataSourceCode, thirdMatchSourceId);
                }
            } catch (Exception e){
                log.error("linkId=【{}】checkEventScoreValidation, 比分校验异常, cacheCommonGoal={}, cacheExtraGoal={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}, Exception={}",
                        linkId, JSON.toJSONString(cacheCommonGoal), JSON.toJSONString(cacheExtraGoal), referenceId, dataSourceCode, thirdMatchSourceId, e);
            }

        } else {
            //常规赛
            totalHomeScore = cacheCommonGoal.getHome();
            totalAwayScore = cacheCommonGoal.getAway();
        }

        log.info("linkId=【{}】checkEventScoreValidation, 总比分, totalHomeScore={}, totalAwayScore={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}",
                linkId, totalHomeScore, totalAwayScore, referenceId, dataSourceCode, thirdMatchSourceId);

        if (totalHomeScore == null || totalAwayScore == null){
            return;
        }

        boolean isScoreEqual = Objects.equals(eventHomeScore, totalHomeScore)
                && Objects.equals(eventAwayScore, totalAwayScore);

        String scoreCountKey = String.format(ConstantSystem.getScoreValidationKey(), dataSourceCode, thirdMatchSourceId,
                totalHomeScore, totalAwayScore);

        String indexKey = String.format(ConstantSystem.getScoreValidationIndexKey(), dataSourceCode, thirdMatchSourceId);
        if (!redisService.sIsMember(indexKey, scoreCountKey)){
            deleteEventScoreDiffKey(dataSourceCode, thirdMatchSourceId);
        }

        if (isScoreEqual){
            redisService.del(scoreCountKey);
            redisService.sRemove(indexKey, scoreCountKey);
            log.info("linkId=【{}】checkEventScoreValidation, 比分相同计数清零, scoreCountKey={}, keySet={}, matchEventInfo={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}",
                    linkId, scoreCountKey, redisService.sMembers(indexKey), JSON.toJSONString(matchEventInfo), referenceId, dataSourceCode, thirdMatchSourceId);
            return;
        }

        Long count = redisService.incr(scoreCountKey, 1);
        redisService.expire(scoreCountKey, 2 * RedisConfig.REDIS_HOUR_TIME);
        redisService.sAdd(indexKey, scoreCountKey, 2, TimeUnit.HOURS);
        log.info("linkId=【{}】checkEventScoreValidation, 比分不一致计数, scoreCountKey={}, count={}, keySet={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}",
                linkId, scoreCountKey, count, redisService.sMembers(indexKey), referenceId, dataSourceCode, thirdMatchSourceId);
        if (count >= 5) {
            String notifyKey = scoreCountKey + ":sent";
            if (redisService.setIfNotExist(notifyKey, "1", 2, TimeUnit.HOURS)){
                matchEventInfoProducer.pushScoreValidationError(linkId, thirdMatchInfo);
                log.warn("linkId=【{}】checkEventScoreValidation, 比分连续5次不一致触发告警, scoreCountKey={}, keySet={}, matchEventInfo={}, footballCacheScore={}, referenceId={}, dataSourceCode={}, thirdMatchSourceId={}",
                        linkId, scoreCountKey, redisService.sMembers(indexKey), JSON.toJSONString(matchEventInfo), JSON.toJSONString(footballCacheScores), referenceId, dataSourceCode, thirdMatchSourceId);
            }
        }
    }

    private void deleteEventScoreDiffKey(String dataSourceCode, String thirdMatchSourceId){
        String indexKey = String.format(ConstantSystem.getScoreValidationIndexKey(), dataSourceCode, thirdMatchSourceId);
        Set<Object> keysToDelete = redisService.sMembers(indexKey);
        if (keysToDelete != null && !keysToDelete.isEmpty()) {
            for (Object obj : keysToDelete) {
                if (obj instanceof String){
                    redisService.del((String) obj);
                }
            }
        }
    }

    /**
     * 判断当前传事件是否为删除事件
     *
     * @param matchEventInfoDTO 事件信息
     * @return boolean
     */
    private boolean booleanEventCodeType(MatchEventInfoDTO matchEventInfoDTO){
//        if(matchEventInfoDTO.getEventCode().equals(DataSourceCodeEnum.PD.getCode())
//                || matchEventInfoDTO.getEventCode().equals(DataSourceCodeEnum.PD2.getCode())){
//            return matchEventInfoDTO.getCanceled() !=1;
//        }
//        return !EventTypeEnum.DELETE_EVENT.name().equalsIgnoreCase(matchEventInfoDTO.getEventCode())
//                && !EventTypeEnum.DELETE_EVENT_ALERT.name().equalsIgnoreCase(matchEventInfoDTO.getEventCode());
        return !EventTypeEnum.DELETE_EVENT.name().equalsIgnoreCase(matchEventInfoDTO.getEventCode()) || matchEventInfoDTO.getCanceled() != 1;
    }

    /**
     * 提取待下发事件集合
     * @param linkId linkId
     * @param eventCode 事件编码
     * @param matchEventInfo 事件信息
     * @param matchEventPushList 推送事件信息集合
     */
    private void extractedMatchEventPushList(String linkId, String eventCode, MatchEventInfo matchEventInfo,
                                             List<MatchEventInfo> matchEventPushList) {
        /**
         * 68780 【产品】【生产】操盘后台新增人工录入补时时长,处理推送事件信息
         * 1、手动修改补时时间事件默认下发
         * 2、如当前阶段有手动修改伤停补时时间，三方赛事的补时事件不下发
         */
        if (INJURY_TIME.equals(eventCode)) {
            //赛事阶段ID转换
            String key = matchEventInfo.getStandardMatchId() + "_" + matchEventInfo.getMatchPeriodId() + INJURY_TIME;
            if (StringUtils.isNotBlank(matchEventInfo.getThirdEventId()) && matchEventInfo.getThirdEventId().contains("PA_Event_input:")) {
                matchEventPushList.add(matchEventInfo);
                redisService.set(key,eventCode,RedisConfig.REDIS_HOUR_TIME);
                log.info("linkId=【{}】手动修改补时时间，三方赛事ID={}，赛事ID={},赛事阶段={}", linkId,matchEventInfo.getThirdMatchId(),
                        matchEventInfo.getStandardMatchId(), matchEventInfo.getMatchPeriodId());
            } else {
                if (!redisService.hasKey(key)) {
                    matchEventPushList.add(matchEventInfo);
                } else {
                    matchEventInfo.setLinkId(matchEventInfo.getLinkId() + "_" + YesNoEnum.N.name());
                    log.info("linkId=【{}】当前赛事补时事件已补手动修改，补时事件无需下发，三方赛事ID={}，赛事ID={},赛事阶段={}",linkId,
                            matchEventInfo.getThirdMatchId(), matchEventInfo.getStandardMatchId(), matchEventInfo.getMatchPeriodId());
                }
            }
            return;
        }

        /**
         * 111030 penalty_shootout_starting_team事件，点球大战开始球队未带主客球队信息不下发该事件（数据侧）
         */
        if (PENALTY_SHOOTOUT_STARTING_TEAM.equalsIgnoreCase(eventCode)){
            if (StringUtils.isNotBlank(matchEventInfo.getHomeAway())){
                matchEventPushList.add(matchEventInfo);
            } else {
                matchEventInfo.setLinkId(matchEventInfo.getLinkId() + "_" + YesNoEnum.N.name());
                log.info("linkId=【{}】点球大战开始球队事件未指定主客队，无需下发，三方赛事ID={}，赛事ID={},赛事阶段={}",linkId,
                        matchEventInfo.getThirdMatchId(), matchEventInfo.getStandardMatchId(), matchEventInfo.getMatchPeriodId());
            }
            return;
        }

        matchEventPushList.add(matchEventInfo);

    }


    /**
     * 处理足球999事件的时候下发全量进球角球罚牌到 AO初盘统计
     */
    @Async("AoMatchEventHistory")
    void runAoMatchEventHistory(MatchEventInfo matchEventInfo) {
        try {
            if (StandardSportTypeEnum.FootBall.getCode().equals(matchEventInfo.getSportId())
                    && EventCodeEnum.MATCH_STATUS.code.equalsIgnoreCase(matchEventInfo.getEventCode())
                    && MatchPeriodForMatchOverEnum.Ended999.value.equals(matchEventInfo.getMatchPeriodId())) {
                //异步执行逻辑
                List<MatchEventInfo> list = matchEventInfoService.getEventHistoryByEndEvent(matchEventInfo);
                //取消事件处理
                List<MatchEventInfo> allMatchEvents = MatchEventUtils.doCancelEvent(list);
                //封装成AO初盘事件统计
                AoMatchEventsHistoryDto aoMatchEvents = new AoMatchEventsHistoryDto(matchEventInfo, allMatchEvents);
                //下发到下游
                aoEventsHistoryProducer.pushModifyMatchInfoMessage(aoMatchEvents);
            }
        } catch (Exception e) {
            log.error("runAoMatchEventHistory error={},link {}", e, matchEventInfo.getLinkId());
        }
    }

    /**
     * 查询是否下发过结束阶段事件
     *
     * @param matchEventInfoDTO
     * @return
     */
    private boolean isOverMatchEventInfo(MatchEventInfoDTO matchEventInfoDTO,String newLinkId) {
        try{
            String ended999Key = String.format(ConstantSystem.getMatchPeriod999Key(), matchEventInfoDTO.getDataSourceCode(),matchEventInfoDTO.getThirdMatchSourceId());
            if(redisService.hasKey(ended999Key)){
                return true;
            }
            //传入阶段999，事件编码，赛事源ID，是否取消
//            List<MatchEventInfo> matchEventInfoList = matchEventInfoService.getMatchEventInfoByThird(
//                    MatchPeriodForMatchOverEnum.Ended999.value, matchEventInfoDTO.getEventCode(), matchEventInfoDTO.getThirdMatchSourceId(), matchEventInfoDTO.getDataSourceCode(), YesNoEnum.N.value
//            );
//            if (!CollectionUtils.isEmpty(matchEventInfoList)) {
//                return true;
//            }
        } catch (Exception e) {
            log.error("linkId【"+newLinkId+"】process2MatchEvent,查询是否下发过999阶段异常！Exception:", e);
        }
        return false;
    }

    /**
     * 组装需要下发的事件信息
     * @param matchEventInfoList 本次批量处理的事件列表
     * @return MatchEventInfo
     *
     */
    public MatchEventInfo getMatchEventInfo(MatchEventInfoDTO matchEventInfoDTO, ThirdMatchInfo thirdMatchInfo, ThirdSportTeam thirdSportTeam, Long sportId,
                                            String linkId,List<MatchEventInfo> matchEventInfoList) {
        //兼容扩展字段值赋值不统一问题，bug: 77885
        if(StringUtils.isNotBlank(matchEventInfoDTO.getExtrainfo()) && StringUtils.isBlank(matchEventInfoDTO.getExtraInfo())){
            matchEventInfoDTO.setExtraInfo(matchEventInfoDTO.getExtrainfo());
        }
        if(StringUtils.isNotBlank(matchEventInfoDTO.getExtraInfo()) && StringUtils.isBlank(matchEventInfoDTO.getExtrainfo())){
            matchEventInfoDTO.setExtrainfo(matchEventInfoDTO.getExtraInfo());
        }
        MatchEventInfo matchEventInfo = new MatchEventInfo();
        BeanUtils.copyProperties(matchEventInfoDTO, matchEventInfo);
        matchEventInfo.setSourceType(Integer.valueOf(matchEventInfoDTO.getSourceType()));
        matchEventInfo.setId(UUIdUtils.getId());
        matchEventInfo.setCreateTime(System.currentTimeMillis());
        matchEventInfo.setStandardMatchId(thirdMatchInfo.getReferenceId());
        matchEventInfo.setThirdMatchId(thirdMatchInfo.getId());
        if (null != thirdSportTeam) {
            matchEventInfo.setThirdTeamId(thirdSportTeam.getId());
            matchEventInfo.setStandardTeamId(thirdSportTeam.getReferenceId());
        }
        matchEventInfo.setSportId(sportId);
        matchEventInfo.setLinkId(linkId);
        matchEventInfo.setSendData(YesNoEnum.N.name());
        matchEventInfo.setModifyTime(System.currentTimeMillis());
        //104446 赛事中断的记录中断前的阶段
        if (Objects.equals(EventCodeEnum.MATCH_STATUS.code, matchEventInfo.getEventCode()) &&
                Objects.equals(MatchPeriodForMatchOverEnum.Interrupted.value, matchEventInfo.getMatchPeriodId())) {
            matchEventInfo.setExtraInfo(thirdMatchInfo.getMatchPeriod());
        }
        //V02的UOF事件则是视频集锦，可直接修改原有事件信息,不需要走删除事件逻辑,直接入库 需求：2409
        if (DataSourceCodeEnum.TS.code.equalsIgnoreCase(matchEventInfoDTO.getDataSourceCode()) && ZERO.equals(matchEventInfo.getSourceType())) {
            MatchEventInfo oldMatchEventInfo = null;
            if(!pandaDbIsError){
                oldMatchEventInfo = matchEventInfoService.getItem(matchEventInfo.getThirdEventId(), matchEventInfo.getDataSourceCode(), matchEventInfo.getThirdMatchSourceId());
            }
            if(oldMatchEventInfo != null){
                matchEventInfo.setId(oldMatchEventInfo.getId());
                matchEventInfo.setCreateTime(oldMatchEventInfo.getCreateTime());
                matchEventInfo.setModifyTime(null);
            }else{
                matchEventInfo.setId(MyHashUtil.fnv1aHash64(
                        matchEventInfo.getDataSourceCode()+matchEventInfo.getSportId()+matchEventInfo.getThirdMatchSourceId()+matchEventInfo.getThirdEventId())
                );
                matchEventInfo.setCreateTime(null);
            }
            log.info("linkId=【{}】process2MatchEvent,V02集锦事件可直接修改原有事件信息,不需要走删除事件逻辑,直接入库需求2409,三方赛事id={}", linkId,matchEventInfo.getThirdMatchSourceId());
        } else {
            matchEventInfoService.processDeleteEvent(linkId, matchEventInfoDTO, matchEventInfo,matchEventInfoList,pandaDbIsError,thirdMatchInfo);
        }
        return matchEventInfo;
    }


    @Autowired
    private ThreadPoolConfig threadPoolConfig;


    /**
     * 自动关盘玩法数据处理
     *
     * @param standardMatchInfo
     * @param linkId
     * @param matchEventInfoDTO
     */
    public void autoCloseMarketDispose(StandardMatchInfo standardMatchInfo, String linkId, MatchEventInfo matchEventInfoDTO) {
        //异步处理自动关盘玩法
        TaskExecutor taskExecutor = threadPoolConfig.getInitSportMarketRelation();
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // 支持足球、篮球 自动关盘处理
                List<Long> closeMarketSportIds = Lists.newArrayList(StandardSportTypeEnum.FootBall.code, StandardSportTypeEnum.Basketball.code);
                if (!closeMarketSportIds.contains(standardMatchInfo.getSportId())) {
                    log.info("linkId=【{}】autoCloseMarketDispose，error:标准比赛id={},阶段id={},运动id={},不支持自动关盘!", linkId, standardMatchInfo.getId(), matchEventInfoDTO.getMatchPeriodId(), standardMatchInfo.getSportId());
                    return;
                }
                //如果阶段是0，不自动关盘
                if (Long.valueOf(String.valueOf(ZERO)).equals(matchEventInfoDTO.getMatchPeriodId())) {
                    log.info("linkId=【{}】autoCloseMarketDispose，error:标准比赛id={},阶段id:0,不自动关盘!", linkId, standardMatchInfo.getId());
                    return;
                }
                if (matchEventInfoDTO.getEventCode().equals("possession")) {
                    log.info("linkId=【{}】autoCloseMarketDispose，error:标准比赛id={},阶段id={},忽略事件编码possession!", linkId, standardMatchInfo.getId(), matchEventInfoDTO.getMatchPeriodId());
                    return;
                }
                //事件比赛 已进行时长
                Long secondsFromStart = matchEventInfoDTO.getSecondsFromStart();
                //如果当前的赛事进行阶段是赛事进行中，且赛事进行时间为空则退出自动关盘
                if (null == secondsFromStart && SportPeriodWholeEnum.getSprotPeriodBySportId(matchEventInfoDTO.getSportId()).getPeriods().indexOf(matchEventInfoDTO.getMatchPeriodId()) >= ZERO) {
                    log.info("linkId=【{}】autoCloseMarketDispose，error:标准比赛id={},阶段id={},比赛进行时间为空或者0!", linkId, standardMatchInfo.getId(), matchEventInfoDTO.getMatchPeriodId());
                    return;
                }
                //篮球自动开盘缓存
                Set<Long> marketCategoryIdSet = autoOpenMarket(linkId, secondsFromStart, standardMatchInfo, matchEventInfoDTO.getMatchPeriodId());
                if (!marketCategoryIdSet.isEmpty()){
                    iTradeMarketConfigApi.autoOpenMarket(linkId, standardMatchInfo.getId(), marketCategoryIdSet, TimeUtils.millsSecondsEast8ZoneGmt());
                }
                //获取需要自动关盘的标准玩法
                Set<Long> marketCategoryIds = getAutoCloseMarketDisposeBySportId(linkId, secondsFromStart, standardMatchInfo, matchEventInfoDTO.getMatchPeriodId(), matchEventInfoDTO.getSourceType()+"");
                if (marketCategoryIds == null) {
                    marketCategoryIds = new HashSet<Long>();
                }
                Pair<Set<Long>, Map<String, JSONObject>> childCloseMarketCategory = getAutoCloseChildMarketCategoryDisposeBySportId(linkId, secondsFromStart, standardMatchInfo, matchEventInfoDTO.getMatchPeriodId(), matchEventInfoDTO.getSourceType()+"");
                if (null != childCloseMarketCategory) {
                    iTradeMarketConfigApi.autoCloseChildMarketCategory(linkId+"_childCloseMarket", standardMatchInfo.getId(), childCloseMarketCategory, TimeUtils.millsSecondsEast8ZoneGmt());
                }
                //1852 兜底功能，进入下一个阶段时，关闭上一个阶段的玩法盘口
                Set<Long> marketCategoryIds1 = getAutoCloseBeforePeriodCategory(linkId, standardMatchInfo, matchEventInfoDTO.getMatchPeriodId());
                if (!CollectionUtils.isEmpty(marketCategoryIds1)) {
                    marketCategoryIds.addAll(marketCategoryIds1);
                }
                //下发标准玩法关盘
                if (CollectionUtils.isEmpty(marketCategoryIds)) {
                    log.info("linkId=【{}】autoCloseMarketDispose，error:标准比赛id={},阶段id={},下发自动关盘数据为空!", linkId, standardMatchInfo.getId(), matchEventInfoDTO.getMatchPeriodId());
                    return;
                }
                iTradeMarketConfigApi.autoCloseMarket(linkId, standardMatchInfo.getId(), marketCategoryIds, TimeUtils.millsSecondsEast8ZoneGmt());
            }
        });
    }


    @Autowired
    private StandardRelationNewStandardMapper standardRelationNewStandardMapper;

    @Autowired
    private ThirdRelationNewThirdMapper thirdRelationNewThirdMapper;

    /**
     * 拷贝的赛事向MQ投递赛事事件列表消息
     */
    public void copyStandardMatchInfoEventList2Mq(String linkId, List<MatchEventInfoDTO> data) {
        if (!CollectionUtils.isEmpty(data)) {
            String copyLinkId = StringUtils.join(linkId, "_copy");
            String dataSourceCode = data.get(0).getDataSourceCode();
            String thirdMatchSourceId = data.get(0).getThirdMatchSourceId();
            log.info("linkId=【{}】copyStandardMatchInfoEventList2Mq,dataSourceCode={},thirdMatchSourceId={},copy事件列表信息开始", copyLinkId, dataSourceCode, thirdMatchSourceId);
            //库中原始三方赛事
            ThirdMatchInfo oldThirdMatchInfo = thirdMatchInfoService.getItem(dataSourceCode, thirdMatchSourceId);
            if (null == oldThirdMatchInfo) {
                log.info("linkId=【{}】copyStandardMatchInfoEventList2Mq,三方赛事信息为空!", copyLinkId);
                return;
            }
            //标准赛事信息
            Long standardMatchId = oldThirdMatchInfo.getReferenceId();
            if (null != standardMatchId && !Long.valueOf(ZERO).equals(standardMatchId)) {
                //判断是否有拷贝的赛事
                StandardRelationNewStandardExample relationExample = new StandardRelationNewStandardExample();
                relationExample.createCriteria().andSourceStandardIdEqualTo(standardMatchId);
                List<StandardRelationNewStandard> copyStandards = standardRelationNewStandardMapper.selectByExample(relationExample);
                if (!CollectionUtils.isEmpty(copyStandards)) {
                    int i = 1;
                    for (StandardRelationNewStandard copyStandard : copyStandards) {
                        log.info("linkId=【{}】 copyStandardMatchInfoEventList2Mq，copy标准赛事关系信息={}", copyLinkId, JSON.toJSONString(copyStandard));
                        //查询copy的标准赛事是否存在
                        StandardMatchInfo copyStandardMatchInfo = standardMatchInfoService.getItem(copyStandard.getNewStandardId());
                        if (null == copyStandardMatchInfo) {
                            log.info("linkId=【{}】 copyStandardMatchInfoEventList2Mq，标准赛事id={}，copy标准赛事不存在!", copyLinkId, copyStandard.getNewStandardId());
                            return;
                        }

                        //拷贝的三方赛事关系
                        ThirdRelationNewThirdExample thirdRelationExample = new ThirdRelationNewThirdExample();
                        thirdRelationExample.createCriteria().andSourceThirdIdEqualTo(oldThirdMatchInfo.getId());
                        List<ThirdRelationNewThird> copyThirdList = thirdRelationNewThirdMapper.selectByExample(thirdRelationExample);
                        if (CollectionUtils.isEmpty(copyThirdList)) {
                            log.info("linkId=【{}】 copyStandardMatchInfoEventList2Mq，三方赛事id={}，copy三方赛事关系不存在!", copyLinkId, oldThirdMatchInfo.getId());
                            return;
                        }
                        for (ThirdRelationNewThird copyThird : copyThirdList) {
                            ThirdMatchInfo copyThirdMatchInfo = thirdMatchInfoService.getItem(copyThird.getNewThirdId());
                            if (null == copyThirdMatchInfo) {
                                log.info("linkId=【{}】 copyStandardMatchInfoEventList2Mq，copy三方赛事id={}，copy三方赛事不存在!", copyLinkId, copyThird.getNewThirdId());
                                return;
                            }

                            if (!DataSourceCodeEnum.BG.code.equalsIgnoreCase(dataSourceCode)) {
                                //按事件发生时间升序排序事件列表
                                data = data.stream().sorted(Comparator.comparingDouble(MatchEventInfoDTO::getEventTime)).collect(Collectors.toList());
                            }
                            //重新赋值三方赛事源ID
                            for (MatchEventInfoDTO item : data) {
                                item.setThirdMatchSourceId(copyThirdMatchInfo.getThirdMatchSourceId());
                            }
                            //重新创建参数对象
                            Request<List<MatchEventInfoDTO>> newRequest = new Request<>();
                            newRequest.setData(data);
                            newRequest.setLinkId(copyLinkId + "_" + i++);
                            newRequest.setDataSourceCode(dataSourceCode);
                            //重新执行copy后的赛事事件列表逻辑
                            putMatchEventListInfo(newRequest);
                        }
                    }
                } else {
                    log.info("linkId=【{}】copyStandardMatchInfoEventList2Mq,根据原始标准赛事ID={}未找到拷贝赛事信息！", copyLinkId, standardMatchId);
                }
            } else {
                log.info("linkId=【{}】copyStandardMatchInfoEventList2Mq,原始三方赛事没有对应标准赛事ID={}", copyLinkId, standardMatchId);
            }
            log.info("linkId=【{}】copyStandardMatchInfoEventList2Mq,copy事件列表信息结束", copyLinkId);
        }
    }


    /**
     * 拷贝的赛事向MQ投递赛事事件消息
     */
    public void copyStandardMatchInfoEvent2Mq(Request<MatchEventInfoDTO> request) {
        String copyLinkId = StringUtils.join(request.getLinkId(), "_copy");
        MatchEventInfoDTO data = request.getData();
        log.info("linkId=【{}】copyStandardMatchInfoEvent2Mq,dataSourceCode={},thirdMatchSourceId={},copy事件信息开始", copyLinkId, data.getDataSourceCode(), data.getThirdMatchSourceId());
        //库中原始三方赛事
        ThirdMatchInfo oldThirdMatchInfo = thirdMatchInfoService.getItem(data.getDataSourceCode(), data.getThirdMatchSourceId());
        if (null == oldThirdMatchInfo) {
            log.info("linkId=【{}】copyStandardMatchInfoEvent2Mq,三方赛事信息为空!", copyLinkId);
            return;
        }
        //标准赛事信息
        Long standardMatchId = oldThirdMatchInfo.getReferenceId();
        if (null != standardMatchId && !Long.valueOf(ZERO).equals(standardMatchId)) {
            //判断是否有拷贝的赛事
            StandardRelationNewStandardExample relationExample = new StandardRelationNewStandardExample();
            relationExample.createCriteria().andSourceStandardIdEqualTo(standardMatchId);
            List<StandardRelationNewStandard> copyStandards = standardRelationNewStandardMapper.selectByExample(relationExample);
            if (!CollectionUtils.isEmpty(copyStandards)) {
                int i = 1;
                for (StandardRelationNewStandard copyStandard : copyStandards) {
                    log.info("linkId=【{}】 copyStandardMatchInfoEvent2Mq，copy标准赛事关系信息={}", copyLinkId, JSON.toJSONString(copyStandard));
                    //查询copy的标准赛事是否存在
                    StandardMatchInfo copyStandardMatchInfo = standardMatchInfoService.getItem(copyStandard.getNewStandardId());
                    if (null == copyStandardMatchInfo) {
                        log.info("linkId=【{}】 copyStandardMatchInfoEvent2Mq，标准赛事id={}，copy标准赛事不存在!", copyLinkId, copyStandard.getNewStandardId());
                        return;
                    }
                    //拷贝的三方赛事关系
                    ThirdRelationNewThirdExample thirdRelationExample = new ThirdRelationNewThirdExample();
                    thirdRelationExample.createCriteria().andSourceThirdIdEqualTo(oldThirdMatchInfo.getId());
                    List<ThirdRelationNewThird> copyThirdList = thirdRelationNewThirdMapper.selectByExample(thirdRelationExample);
                    if (CollectionUtils.isEmpty(copyThirdList)) {
                        log.info("linkId=【{}】 copyStandardMatchInfoEvent2Mq，三方赛事id={}，copy三方赛事关系不存在!", copyLinkId, oldThirdMatchInfo.getId());
                        return;
                    }
                    for (ThirdRelationNewThird copyThird : copyThirdList) {
                        ThirdMatchInfo copyThirdMatchInfo = thirdMatchInfoService.getItem(copyThird.getNewThirdId());
                        if (null == copyThirdMatchInfo) {
                            log.info("linkId=【{}】 copyStandardMatchInfoEvent2Mq，copy三方赛事id={}，copy三方赛事不存在!", copyLinkId, copyThird.getNewThirdId());
                            return;
                        }
                        //重新创建参数对象
                        Request<MatchEventInfoDTO> newRequest = new Request<>();
                        data.setThirdMatchSourceId(copyThirdMatchInfo.getThirdMatchSourceId());
                        newRequest.setData(data);
                        newRequest.setLinkId(copyLinkId + "_" + i++);
                        newRequest.setDataSourceCode(copyThirdMatchInfo.getDataSourceCode());
                        //重新执行copy后的赛事事件逻辑
                        putMatchEventInfo(newRequest);
                    }
                }
            } else {
                log.info("linkId=【{}】copyStandardMatchInfoEvent2Mq,根据原始标准赛事ID={}未找到拷贝赛事信息！", copyLinkId, standardMatchId);
            }
        } else {
            log.info("linkId=【{}】copyStandardMatchInfoEvent2Mq,原始三方赛事没有对应标准赛事ID={}", copyLinkId, standardMatchId);
        }
        log.info("linkId=【{}】copyStandardMatchInfoEvent2Mq,copy事件信息结束", copyLinkId);
    }
}
