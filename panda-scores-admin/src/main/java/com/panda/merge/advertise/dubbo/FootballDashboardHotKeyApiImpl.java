package com.panda.merge.advertise.dubbo;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.panda.merge.api.FootballDashboardHotKeyApi;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ScoreEventCodeSourceEnum;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ResultCode;
import com.panda.merge.dto.advertise.v2.HotkeysSaveDto;
import com.panda.merge.model.FootballKeyboardSet;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.UserKeyboardSet;
import com.panda.merge.repository.PdMatchInfoRepository;
import com.panda.merge.snooker.service.IMatchCommonProcessor;
import com.panda.merge.snooker.service.impl.MatchFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

import static com.panda.merge.advertise.common.Constant.ACTION_MONITER_KEY;
import static com.panda.merge.config.RedisConfig.REDIS_WEEK_TIME;

/**
 * PA报球板2.0-用户热键设置服务
 *
 * @author warren
 * @since 2023/11/27 14:49:29
 */

@Service
@DubboService
@Slf4j
public class FootballDashboardHotKeyApiImpl implements FootballDashboardHotKeyApi {
    @Autowired
    private RedisService redisService;

    @Autowired
    private PdMatchInfoRepository pdMatchInfoRepository;

    @Autowired
    private MatchFactory matchFactory;

//    @Autowired
//    private UserKeyboardSetMapper userKeyboardSetMapper;

//    @Autowired
//    private ThirdMatchInfoMapper thirdMatchInfoMapper;

    @Override
    public Response getKeyboardByUserNameAndThirdMatchId(@RequestParam("userName") String userName,@RequestParam("thirdMatchId") Long thirdMatchId) {
//        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(thirdMatchId);
        ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(thirdMatchId,null);
        Long sportId = thirdMatchInfo.getSportId();
        IMatchCommonProcessor processor = matchFactory.getProcessor(sportId);
        return processor.getHotkeysBySportIdAndUsername(sportId, userName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response addKeyboardInfo(FootballKeyboardSet footballKeyboardSet) {
        UserKeyboardSet userKeyboardSet = new UserKeyboardSet();
        BeanUtils.copyProperties(footballKeyboardSet, userKeyboardSet);
        String userName = userKeyboardSet.getUserName();
        Long sportId = footballKeyboardSet.getSportId();
        String key = "FOOTBALL_KEYBOARD_SET:" + userName;
        try {
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(footballKeyboardSet.getThirdMatchId(), null);
            if (Objects.isNull(thirdMatchInfo)) {
                log.error("linkId=::{}::addKeyboardInfo::三方赛事表里不存在，thirdMatchId:{}",footballKeyboardSet.getLinkedId(),footballKeyboardSet.getThirdMatchId());
                return Response.failed("三方赛事表里不存在");
            }
            // 存放事件时间，用于报球板事件监控
            long eventTime=System.currentTimeMillis();
            String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
            if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
                log.info("::{}::addKeyboardInfo,key:{},eventTime:{}",footballKeyboardSet.getLinkedId(),actionMonitorKey,eventTime);
                redisService.set(actionMonitorKey,eventTime);
            }
            // 加redis锁，执行插入操作
            if (redisService.tryLock(key, key, 2, 3)) {
                UserKeyboardSet keyboardSet = pdMatchInfoRepository.getKeyboardByUserNameAndSportId(userName, sportId, null);
                if (ObjectUtil.isNotEmpty(keyboardSet)) {
                    return Response.failed("用户信息已存在");
                }
                IMatchCommonProcessor processor = matchFactory.getProcessor(sportId);
                Response resp = processor.saveHotkeys(buildSaveDto(footballKeyboardSet, sportId));
                if (!resp.isSuccess()) {
                    return resp;
                }
                return Response.success(1, ResultCode.SUCCESS.getMessage());
            }
        } catch (Exception e) {
            ByteArrayOutputStream exception = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(exception));
            log.error("PA报球板，新增用户热键设置：sportId={}, userName={}, 错误信息={}",
                    userKeyboardSet.getSportId(), userKeyboardSet.getUserName(), exception);
        } finally {
            redisService.unLock(key, key);
        }
        return Response.failed("服务器错误");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateKeyboardByUserName(FootballKeyboardSet footballKeyboardSet) {
        UserKeyboardSet userKeyboardSet = new UserKeyboardSet();
        BeanUtils.copyProperties(footballKeyboardSet, userKeyboardSet);
        String key = "FOOTBALL_KEYBOARD_SET:" + userKeyboardSet.getUserName();
        try {
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(footballKeyboardSet.getThirdMatchId(), null);
            if (Objects.isNull(thirdMatchInfo)) {
                log.error("linkId=::{}::updateKeyboardByUserName::三方赛事表里不存在，thirdMatchId:{}",footballKeyboardSet.getLinkedId(),footballKeyboardSet.getThirdMatchId());
                return Response.failed("三方赛事表里不存在");
            }
            // 存放事件时间，用于报球板事件监控
            long eventTime=System.currentTimeMillis();
            String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
            if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
                log.info("::{}::updateKeyboardByUserName,key:{},eventTime:{}",footballKeyboardSet.getLinkedId(),actionMonitorKey,eventTime);
                redisService.set(actionMonitorKey,eventTime);
            }
            // 加redis锁，执行更新操作
            if (redisService.tryLock(key, key, 2, 3)) {
                String userName = userKeyboardSet.getUserName();
                Long sportId = footballKeyboardSet.getSportId();
                UserKeyboardSet keyboardSet = pdMatchInfoRepository.getKeyboardByUserNameAndSportId(userName,sportId,null);
                if (ObjectUtil.isEmpty(keyboardSet)) {
                    return Response.failed("用户信息不存在");
                }
                IMatchCommonProcessor processor = matchFactory.getProcessor(sportId);
                Response resp = processor.saveHotkeys(buildSaveDto(footballKeyboardSet, sportId));
                if (!resp.isSuccess()) {
                    return resp;
                }
                return Response.success(1, ResultCode.SUCCESS.getMessage());
            }
        } catch (Exception e) {
            ByteArrayOutputStream exception = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(exception));
            log.error("PA报球板，修改用户热键设置：sportId={}, userName={}, 错误信息={}",
                    userKeyboardSet.getSportId(), userKeyboardSet.getUserName(), exception);
        } finally {
            redisService.unLock(key, key);
        }
        return Response.failed("服务器错误");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response removeKeyboardByUserNameAndThirdMatchId(@RequestParam("userName") String userName, @RequestParam("thirdMatchId") Long thirdMatchId) {
        String key = "FOOTBALL_KEYBOARD_SET:" + userName;
        try {
            // 加redis锁，执行删除操作
            if (redisService.tryLock(key, key, 2, 3)) {
//                ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(thirdMatchId);
                ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(thirdMatchId,null);
                Long sportId = thirdMatchInfo.getSportId();
                IMatchCommonProcessor processor = matchFactory.getProcessor(sportId);
                return processor.deleteHotkeysBySportIdAndUsername(sportId, userName);
            }
        } catch (Exception e) {
            ByteArrayOutputStream exception = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(exception));
            log.error("PA报球板，删除用户热键设置：userName={}, 错误信息={}", userName, exception,e);
        } finally {
            redisService.unLock(key, key);
        }
        return Response.failed("服务器错误");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteKeyboardByUserIdList(List<String> userIds) {
        String key = "FOOTBALL_KEYBOARD_SET:" + userIds;
        try {
            // 加redis锁，执行删除操作
            if (redisService.tryLock(key, key, 2, 3)) {
//                List<UserKeyboardSet> keyboardSetList = userKeyboardSetMapper.selectKeyboardByUserIdList(userIds);
                List<UserKeyboardSet> keyboardSetList = pdMatchInfoRepository.getAllKeyboardInfo(userIds);
                if (CollectionUtil.isEmpty(keyboardSetList)) {
                    return Response.failed("用户信息不存在");
                }
                int count = pdMatchInfoRepository.removeAllKeyboardInfo(userIds);
                if (count == 0) {
                    return Response.failed("删除失败");
                }
                return Response.success(count, ResultCode.SUCCESS.getMessage());
            }
        } catch (Exception e) {
            ByteArrayOutputStream exception = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(exception));
            log.error("PA报球板，批量删除用户热键设置：userName={}, 错误信息={}", userIds, exception);
        } finally {
            redisService.unLock(key, key);
        }
        return Response.failed("服务器错误");
    }

    private HotkeysSaveDto buildSaveDto(FootballKeyboardSet footballKeyboardSet, Long sportId) {
        HotkeysSaveDto dto = new HotkeysSaveDto();
        dto.setSportId(sportId);
        dto.setUserId(footballKeyboardSet.getUserId());
        dto.setUserName(footballKeyboardSet.getUserName());
        dto.setKeyboardInfo(footballKeyboardSet.getKeyboardInfo());

        // 透传链路/操作人信息（用于落库 createUser/modifyUser 等）
        dto.setLinkedId(footballKeyboardSet.getLinkedId());
        dto.setOperatorId(footballKeyboardSet.getOperatorId());
        dto.setOperatorName(footballKeyboardSet.getOperatorName());
        dto.setIpAddress(footballKeyboardSet.getIpAddress());
        dto.setRequestId(footballKeyboardSet.getRequestId());
        dto.setLanguage(footballKeyboardSet.getLanguage());
        return dto;
    }
}
