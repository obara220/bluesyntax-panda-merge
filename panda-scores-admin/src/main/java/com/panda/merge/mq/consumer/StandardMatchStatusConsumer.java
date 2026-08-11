package com.panda.merge.mq.consumer;

import com.panda.merge.common.enums.MatchStatusEnum;
import com.panda.merge.common.enums.OperateLogTypeEnum;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.scores.EditScoreResultStatusRequest;
import com.panda.merge.dto.scores.StandardScoreCenterDTO;
import com.panda.merge.dto.sourceSwitch.BasketballSwitch;
import com.panda.merge.dto.sourceSwitch.FootballSwitch;
import com.panda.merge.dto.sourceSwitch.TennisSwitch;
import com.panda.merge.dubbo.ScoresCenterApiImpl;
import com.panda.merge.mapper.MatchScoresCenterLogMapper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.StandardMatchInfoRepository;
import com.panda.merge.repository.StandardSportMarketSellRepository;
import com.panda.merge.repository.ThirdMatchInfoRepository;
import com.panda.merge.repository.ScoresRedisHelp;
import com.panda.merge.dto.message.StandardMatchStatusMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 标准赛事状态消费者
 * 监听 STANDARD_MATCH_STATUS topic，处理棒球赛事取消/延期/放弃时隐藏赛果展示
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "STANDARD_MATCH_STATUS",
        consumerGroup = "scores-group-STANDARD_MATCH_STATUS_02",
        consumeThreadMax = 10,
        consumeTimeout = 10000L
)
@DependsOn("scoresAdminApplication")
public class StandardMatchStatusConsumer implements RocketMQListener<Request<StandardMatchStatusMessage>> {

    @Autowired
    StandardMatchInfoRepository standardMatchInfoRepository;
    @Autowired
    StandardSportMarketSellRepository standardSportMarketSellRepository;
//    @Autowired
//    ThirdMatchInfoRepository thirdMatchInfoRepository;
//    @Autowired
//    ScoresRedisHelp scoresRedisHelp;
    @Autowired
    ScoresProducer scoresProducer;
//    @Autowired
//    ScoresCenterApiImpl scoresCenterApiImpl;

    @Autowired
    StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    private MatchScoresCenterLogMapper matchScoresCenterLogMapper;

    @Override
    public void onMessage(Request<StandardMatchStatusMessage> request) {
        if (request == null || request.getData() == null) {
            log.info("StandardMatchStatusConsumer 参数为空，跳过处理");
            return;
        }
        StandardMatchStatusMessage message = request.getData();
        log.info("StandardMatchStatusConsumer 处理标准赛事状态，standardMatchId:{}, sportId:{}, matchStatus:{}",
                message.getStandardMatchId(), message.getSportId(), message.getMatchStatus());
        // 仅处理棒球(sportId=3)的取消/延期/放弃状态
        if (!SportTypeEnum.BASEBALL.getValue().equals(message.getSportId())) {
            return;
        }
        if (!Objects.equals(message.getMatchStatus(), MatchStatusEnum.Cancelled.value)
                && !Objects.equals(message.getMatchStatus(), MatchStatusEnum.Abandoned.value)
                && !Objects.equals(message.getMatchStatus(), MatchStatusEnum.Delayed.value)) {
            return;
        }
        log.info("StandardMatchStatusConsumer 检测到棒球取消/延期/放弃，standardMatchId:{}, matchStatus:{}",
                message.getStandardMatchId(), message.getMatchStatus());
        handleBaseballRollingSwitch(message);
    }

    /**
     * 处理棒球滚球赛果展示开关
     * 检测棒球match_status事件是否为取消/延期/放弃状态
     * 如果是，则更新StandardMatchScores.showStatus=0，下发SHOW_SCORE_STATUS MQ并保存操作日志
     */
    private void handleBaseballRollingSwitch(StandardMatchStatusMessage message) {
        Long standardMatchId = message.getStandardMatchId();
        Long sportId = message.getSportId();
        // 查询标准赛事信息
        StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(standardMatchId);
        if (standardMatchInfo == null) {
            log.warn("StandardMatchStatusConsumer 标准赛事不存在，standardMatchId:{}", standardMatchId);
            return;
        }
        //赛果开关与三方赛事无关
//        // 查询三方赛事信息
//        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectByStandardIdAndDataSourceCode(
//                standardMatchId, message.getDataSourceCode());
//        if (thirdMatchInfo == null) {
//            log.warn("StandardMatchStatusConsumer 三方赛事不存在，standardMatchId:{}", standardMatchId);
//            return;
//        }
        // 查询开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(standardMatchId);
        if (standardSportMarketSell == null) {
            log.warn("StandardMatchStatusConsumer 开售信息不存在，standardMatchId:{}", standardMatchId);
            return;
        }
        int showStatus = 0;

         //棒球暂未集成比分中心页面，无需存standard_match_scores表
//        // 查询标准比分缓存
//        StandardMatchScores scores = scoresRedisHelp.getCatchStandScoreByMatchId(standardMatchId);
//        if (scores == null) {
//            // 标准比分数据不存在，初始化
//            scores = new StandardMatchScores();
//            scores.setDataSourceCode(standardSportMarketSell.getBusinessEvent());
//            scores.setMatchId(standardMatchId);
//            scores.setThirdMatchId(thirdMatchInfo.getId());
//            scores.setSportId(sportId);
//            scores.setShowStatus(showStatus);
//            scores.setMatchManageId(standardMatchInfo.getMatchManageId());
//            scores.setCreateTime(System.currentTimeMillis());
//            scores.setUpdateTime(System.currentTimeMillis());
//            scores.setDataSourceAccoSwitch(getSwitchsAsSportId(sportId));
//            scores.setSendSettleCount(0);
//            scoresRedisHelp.saveCatchStandScore(scores);
//            log.info("StandardMatchStatusConsumer 标准比分数据为空，初始化完成，standardMatchId:{}", standardMatchId);
//        } else {
//            scores.setShowStatus(showStatus);
//            scores.setUpdateTime(System.currentTimeMillis());
//            scoresRedisHelp.saveCatchStandScore(scores);
//            log.info("StandardMatchStatusConsumer 更新showStatus为0完成，standardMatchId:{}", standardMatchId);
//        }

        //关闭开售表的开关
        List<Long> matchIds = Collections.singletonList(standardMatchId);
        standardSportMarketSellMapper.updateShowResultStatusAll(matchIds,System.currentTimeMillis(),showStatus);

        // 下发SHOW_SCORE_STATUS MQ
        EditScoreResultStatusRequest request = new EditScoreResultStatusRequest();
        request.setType(0);
        request.setStatus(showStatus);
        request.setStandardMatchId(standardMatchId);
        request.setSportId(sportId);
        scoresProducer.sendMatchShowStatus(request, standardMatchId + "_standardMatchStatus");
        log.info("StandardMatchStatusConsumer 下发SHOW_SCORE_STATUS完成,关闭赛果展示，standardMatchId:{}", standardMatchId);
        // 切换开关保存日志，委托 ScoresCenterApiImpl.editMatchResultShowStatusLog
        StandardScoreCenterDTO centerDto = new StandardScoreCenterDTO();
        centerDto.setStandardMatchId(standardMatchId);
        centerDto.setSportId(sportId);
        centerDto.setShowStatus(showStatus);
        centerDto.setOperatorName(message.getDataSourceCode());
        centerDto.setMatchManageId(standardMatchInfo.getMatchManageId());
        editMatchResultShowStatusLog(centerDto);
        log.info("StandardMatchStatusConsumer 保存操作日志完成，standardMatchId:{}", standardMatchId);
    }

    public void editMatchResultShowStatusLog(StandardScoreCenterDTO logDto) {
        log.info("保存操作日志:{}",logDto);
        Integer showStatus = logDto.getShowStatus();
        MatchScoresCenterLog matchScoresCenterLog = new MatchScoresCenterLog();
        String matchManageId = logDto.getMatchManageId();
        matchScoresCenterLog.setMatchManageId(matchManageId);
        matchScoresCenterLog.setOperateId(matchManageId);
        matchScoresCenterLog.setOperateName("");
        matchScoresCenterLog.setOperateParaName(OperateLogTypeEnum.SCORES_CENTER_MATCH_SETTING.getCode() + "");
        matchScoresCenterLog.setOperateType(OperateLogTypeEnum.SCORES_CENTER_MATCH_SETTING.getCode() + "");
//        if(showStatus==1){
//            //开
//            matchScoresCenterLog.setOperateRearText(OperateLogTypeEnum.SCORES_CENTER_OPEN.getCode()+ "");
//            matchScoresCenterLog.setOperateForwText(OperateLogTypeEnum.SCORES_CENTER_CLOSE.getCode()+ "");
//        }else{
//            //关
//            matchScoresCenterLog.setOperateRearText(OperateLogTypeEnum.SCORES_CENTER_CLOSE.getCode()+ "");
//            matchScoresCenterLog.setOperateForwText(OperateLogTypeEnum.SCORES_CENTER_OPEN.getCode()+ "");
//        }
        matchScoresCenterLog.setOperateRearText(String.valueOf(showStatus));
        showStatus++;
        String before = String.valueOf(new StringBuffer(Integer.toBinaryString(showStatus)).reverse().toString().charAt(0));
        matchScoresCenterLog.setOperateForwText(before);
        matchScoresCenterLog.setMatchManageId(matchManageId);
        matchScoresCenterLog.setOperateModule(OperateLogTypeEnum.SCORES_SETTLE_10038.getCode() + "");
        matchScoresCenterLog.setOperateUserName("match status auto close");
        matchScoresCenterLog.setIpAddress("");
        matchScoresCenterLog.setOperateMatchId(matchManageId);
        matchScoresCenterLog.setCreateTime(System.currentTimeMillis());
        matchScoresCenterLog.setModifyTime(System.currentTimeMillis());
        matchScoresCenterLogMapper.insert(matchScoresCenterLog);
    }

//    private static String getSwitchsAsSportId(Long sportId) {
//        String dataSourceAccoSwitch = "";
//        if (SportTypeEnum.FOOTBALL.getValue().equals(sportId)) {
//            FootballSwitch footballSwitch = new FootballSwitch();
//            dataSourceAccoSwitch = com.alibaba.fastjson.JSON.toJSONString(footballSwitch);
//        } else if (SportTypeEnum.BASKETBALL.getValue().equals(sportId)) {
//            BasketballSwitch basketballSwitch = new BasketballSwitch();
//            dataSourceAccoSwitch = com.alibaba.fastjson.JSON.toJSONString(basketballSwitch);
//        } else {
//            TennisSwitch switchs = new TennisSwitch();
//            dataSourceAccoSwitch = com.alibaba.fastjson.JSON.toJSONString(switchs);
//        }
//        return dataSourceAccoSwitch;
//    }
}
