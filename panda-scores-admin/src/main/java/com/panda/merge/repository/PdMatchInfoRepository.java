package com.panda.merge.repository;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.MatchScoresSourceTypeMapper;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.mapper.UserKeyboardSetMapper;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchScoresInfoExample;
import com.panda.merge.model.MatchScoresSourceType;
import com.panda.merge.model.MatchScoresSourceTypeExample;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.MatchTimeInfoExample;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.UserKeyboardSet;
import com.panda.merge.mq.producer.MatchScoresInfoProducer;
import com.panda.merge.mq.producer.MatchTimeInfoProducer;
import com.panda.merge.mq.producer.StandardMatchInfoProducer;
import com.panda.merge.mq.producer.ThirdMatchInfoProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.utils.MessageGZIP;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

import static com.panda.merge.constant.RepositoryConstant.FOOTBALL_KEYBOARD_SET;
import static com.panda.merge.constant.RepositoryConstant.MATCH_SCORES_INFO;
import static com.panda.merge.constant.RepositoryConstant.MATCH_SCORES_SOURCE_TYPE;
import static com.panda.merge.constant.RepositoryConstant.MATCH_TIME_INFO;

/**
 * 去DB操作类，redis有数据查redis，没有则查库并更新redis
 *
 * @author warren
 * @since 2025/01/18 22:08:34
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PdMatchInfoRepository {
    private final RedisService redisService;

    private final MatchScoresInfoMapper matchScoresInfoMapper;

    private final ThirdMatchInfoMapper thirdMatchInfoMapper;

    private final MatchTimeInfoMapper matchTimeInfoMapper;

    private final StandardMatchInfoMapper standardMatchInfoMapper;

    private final MatchScoresSourceTypeMapper matchScoresSourceTypeMapper;

    private final UserKeyboardSetMapper userKeyboardSetMapper;

    private final StandardSportMarketSellMapper standardSportMarketSellMapper;

    private final ThirdMatchInfoProducer thirdMatchInfoProducer;

    private final StandardMatchInfoProducer standardMatchInfoProducer;

    private final MatchScoreInfoRepository matchScoreInfoRepository;

    private final MatchTimeInfoRepository matchTimeInfoRepository;

    private final MatchScoresInfoProducer matchScoresInfoProducer;

    private final MatchTimeInfoProducer matchTimeInfoProducer;

    private final ThirdMatchInfoService thirdMatchInfoService;
    private final StandardMatchInfoService standardMatchInfoService;
    private final StandardSportMarketSellService standardSportMarketSellService;

    /**
     * 根据三方赛事ID获取三方赛事信息
     *
     * @param thirdMatchId 三方赛事ID
     * @param time         失效时间，单位：秒
     * @return 三方赛事信息
     */
    public ThirdMatchInfo getThirdMatchInfo(Long thirdMatchId, Integer time) {
        //不在自身服务设置key，统一使用实时服务提供的公共方法
        return thirdMatchInfoService.getItem(thirdMatchId);
    }

    /**
     * 根据关联ID获取三方赛事
     *
     * @param referenceId    关联ID
     * @param datasourceCode 数据类型
     * @param time           失效时间，单位：秒
     * @return 三方赛事
     */
    public ThirdMatchInfo getThirdMatchInfo(Long referenceId, String datasourceCode, Integer time) {
        return thirdMatchInfoService.getItem(referenceId, datasourceCode);
    }

    /**
     * 更新三方赛事redis值
     *
     * @param thirdMatchInfo 三方赛事
     * @param time           失效时间，单位：秒
     */
    public void setRedisThirdMatchInfo(ThirdMatchInfo thirdMatchInfo, Integer time) {
        String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + thirdMatchInfo.getId();
        if (ObjectUtils.isEmpty(time)) {
            redisService.set(key, thirdMatchInfo);
        } else {
            redisService.set(key, thirdMatchInfo, time);
        }
    }

    /**
     * 根据三方赛事ID获取赛事比分信息
     *
     * @param thirdMatchId 三方赛事ID
     * @param sourceType   数据源类型
     * @param time         失效时间，单位：秒
     * @return 赛事比分信息
     */
    public MatchScoresInfo getMatchScoresInfo(Long thirdMatchId, Integer sourceType, Integer time) {
        String key = MATCH_SCORES_INFO + thirdMatchId + "_" + sourceType;
        Object o = redisService.get(key);
        MatchScoresInfo matchScoresInfo;
        if (ObjectUtils.isEmpty(o)) {
            log.info("::{}::报球版查询比分redis不存在，开始查询数据库", thirdMatchId);
            matchScoresInfo = loadMatchScoresFromDb(thirdMatchId);
            if (matchScoresInfo == null) {
                return null;
            }
            cacheMatchScores(key, matchScoresInfo, time);
        } else {
            matchScoresInfo = parseMatchScoresFromRedis(o);
            log.info("::{}::报球版查询比分redis存在，查询redis数据:{}", thirdMatchId, JSONObject.toJSONString(matchScoresInfo));
            // scoresJson 为空说明缓存数据不完整（可能是旧数据或初始化未完成），回源 DB 补齐
            if (StringUtils.isBlank(matchScoresInfo.getScoresJson())) {
                log.warn("::{}::报球版scoresJson为空，回源DB重新加载", thirdMatchId);
                matchScoresInfo = loadMatchScoresFromDb(thirdMatchId);
                if (matchScoresInfo != null) {
                    cacheMatchScores(key, matchScoresInfo, time);
                }
            }
        }
        return matchScoresInfo;
    }

    /**
     * 从数据库加载 MatchScoresInfo，按 thirdMatchId 查询取第一条。
     */
    private MatchScoresInfo loadMatchScoresFromDb(Long thirdMatchId) {
        MatchScoresInfoExample example = new MatchScoresInfoExample();
        example.createCriteria().andThirdMatchIdEqualTo(thirdMatchId);
        List<MatchScoresInfo> list = matchScoresInfoMapper.selectByExample(example);
        log.info("::{}::报球版查询比分数据库结果:{}", thirdMatchId, JSONObject.toJSONString(list));
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    /**
     * 将 MatchScoresInfo 写入 Redis，同时写入 thirdMatchId_key 和 id_key 两份缓存。
     */
    private void cacheMatchScores(String key, MatchScoresInfo matchScoresInfo, Integer time) {
        String json = ((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString();
        byte[] bytes = MessageGZIP.compressToByte(json);
        if (ObjectUtils.isEmpty(time)) {
            redisService.set(key, bytes);
            redisService.set(MATCH_SCORES_INFO + matchScoresInfo.getId(), bytes);
        } else {
            redisService.set(key, bytes, time);
            redisService.set(MATCH_SCORES_INFO + matchScoresInfo.getId(), bytes, time);
        }
    }

    /**
     * 从 Redis 对象解析 MatchScoresInfo，兼容普通序列化与 GZIP 二进制两种格式。
     */
    private MatchScoresInfo parseMatchScoresFromRedis(Object o) {
        if (o instanceof MatchScoresInfo) {
            return JSON.parseObject(JSON.toJSONString(o), MatchScoresInfo.class);
        }
        String str = MessageGZIP.uncompressToString((byte[]) o);
        return JSON.toJavaObject(JSONObject.parseObject(str), MatchScoresInfo.class);
    }

    /**
     * 插入数据更新redis
     *
     * @param matchScoresInfo 比分数据
     * @param time            失效时间，单位：秒
     */
    public void addMatchScoresInfo(MatchScoresInfo matchScoresInfo, Integer time) {
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 根据主键获取比分数据
     *
     * @param id   主键
     * @param time 失效时间，单位：秒
     * @return 比分数据
     */
    public MatchScoresInfo getMatchScoresInfoByPrimaryKey(Long id, Integer time) {
        String primaryKey = MATCH_SCORES_INFO + id;
        Object o = redisService.get(primaryKey);
        MatchScoresInfo matchScoresInfo;
        if (org.apache.commons.lang3.ObjectUtils.isEmpty(o)) {
            matchScoresInfo = matchScoresInfoMapper.selectByPrimaryKey(id);
            String thirdMatchIdKey = MATCH_SCORES_INFO + matchScoresInfo.getThirdMatchId() + "_" + matchScoresInfo.getDataSourceType();
            if (org.apache.commons.lang3.ObjectUtils.isEmpty(time)) {
                redisService.set(primaryKey, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()));
                redisService.set(thirdMatchIdKey, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()));
            } else {
                redisService.set(primaryKey, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()), time);
                redisService.set(thirdMatchIdKey, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()), time);
            }
        } else {
            if (o instanceof MatchScoresInfo) {
                matchScoresInfo = JSON.parseObject(JSON.toJSONString(o), MatchScoresInfo.class);
            } else {
                String str = MessageGZIP.uncompressToString((byte[]) o);
                matchScoresInfo = JSON.toJavaObject(JSONObject.parseObject(str), MatchScoresInfo.class);
            }
        }
        return matchScoresInfo;
    }

    /**
     * 更新比分数据
     *
     * @param matchScoresInfo 比分数据
     * @param time            失效时间，单位：秒
     */
    public void setRedisAndMatchScoresInfo(MatchScoresInfo matchScoresInfo, Integer time) {
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    public void onlyUpdateMatchScoresInfoRedis(MatchScoresInfo matchScoresInfo) {
        //1.先更新缓存
        String key = MATCH_SCORES_INFO + matchScoresInfo.getThirdMatchId() + "_" + matchScoresInfo.getDataSourceType();
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(matchScoresInfo);
        redisService.set(key, MessageGZIP.compressToByte(jsonObject.toJSONString()));
        redisService.set(MATCH_SCORES_INFO + matchScoresInfo.getId(), MessageGZIP.compressToByte(jsonObject.toJSONString()));
    }

    public void onlyUpdateMatchScoresInfoDB(MatchScoresInfo matchScoresInfo) {
        // 2.再MQ推送更新数据库
        matchScoresInfoProducer.updateScoresInfoByMq(matchScoresInfo);
    }

    /**
     * 更新比分信息redis值
     *
     * @param matchScoresInfo 比分信息
     * @param time            失效时间，单位：秒
     */
    public void setRedisMatchScoresInfo(MatchScoresInfo matchScoresInfo, Integer time) {
        String key = MATCH_SCORES_INFO + matchScoresInfo.getThirdMatchId() + "_" + matchScoresInfo.getDataSourceType();
        if (ObjectUtils.isEmpty(time)) {
            redisService.set(key, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()));
            redisService.set(MATCH_SCORES_INFO + matchScoresInfo.getId(), MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()));
        } else {
            redisService.set(key, matchScoresInfo, time);
            redisService.set(MATCH_SCORES_INFO + matchScoresInfo.getId(), MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()), time);
        }
    }

    /**
     * 根据三方赛事ID获取赛事时间信息
     *
     * @param thirdMatchId 三方赛事ID
     * @param sourceType   数据源类型
     * @param time         失效时间，单位：秒
     * @return 赛事时间信息
     */
    public MatchTimeInfo getMatchTimeInfo(Long thirdMatchId, Integer sourceType, Integer time) {
        String key = MATCH_TIME_INFO + thirdMatchId + "_" + sourceType;
        Object o = redisService.get(key);
        MatchTimeInfo matchTimeInfo;
        if (org.apache.commons.lang3.ObjectUtils.isEmpty(o)) {
            matchTimeInfo = loadMatchTimeInfoAndRefreshCache(thirdMatchId, key, time);
        } else if (o instanceof MatchTimeInfo) {
            matchTimeInfo = JSON.parseObject(JSON.toJSONString(o), MatchTimeInfo.class);
        } else {
            try {
                matchTimeInfo = JSONUtil.toBean(JSONUtil.toJsonStr(o), MatchTimeInfo.class);
            } catch (Exception e) {
                // 缓存里可能是其他写入方（如 MatchTimeInfoRepository 曾经的 gzip byte[] 格式）留下的脏数据，
                // 解析失败时清掉脏缓存并回源查库，避免整个接口报错
                log.warn("赛事时间缓存反序列化失败，key={}，清除脏缓存并回源查库", key, e);
                redisService.del(key);
                matchTimeInfo = loadMatchTimeInfoAndRefreshCache(thirdMatchId, key, time);
            }
        }
        return matchTimeInfo;
    }

    private MatchTimeInfo loadMatchTimeInfoAndRefreshCache(Long thirdMatchId, String key, Integer time) {
        MatchTimeInfoExample example = new MatchTimeInfoExample();
        example.createCriteria().andThirdMatchIdEqualTo(thirdMatchId);
        List<MatchTimeInfo> matchTimeInfoList = matchTimeInfoMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(matchTimeInfoList)) {
            return null;
        }
        MatchTimeInfo matchTimeInfo = matchTimeInfoList.get(0);
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(matchTimeInfo);
        if (org.apache.commons.lang3.ObjectUtils.isEmpty(time)) {
            redisService.set(key, jsonObject.toJSONString(), RepositoryConstant.REDIS_THREE_TIME);
        } else {
            redisService.set(key, jsonObject.toJSONString(), time);
        }
        return matchTimeInfo;
    }

    /**
     * 插入数据更新redis
     *
     * @param matchTimeInfo 赛事时间
     * @param time          失效时间，单位：秒
     */
    public void addMatchTimeInfo(MatchTimeInfo matchTimeInfo, Integer time) {
        matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
//        String key = MATCH_TIME_INFO + matchTimeInfo.getThirdMatchId() + "_" + matchTimeInfo.getDataSourceType();
//        Object o = redisService.get(key);
//        if (ObjectUtils.isEmpty(o)) {
//            matchTimeInfoMapper.insert(matchTimeInfo);
//            if (ObjectUtils.isEmpty(time)) {
//                redisService.set(key, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchTimeInfo)).toJSONString()));
//                redisService.set(MATCH_TIME_INFO + matchTimeInfo.getId(), MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchTimeInfo)).toJSONString()));
//            } else {
//                redisService.set(key, matchTimeInfo, time);
//                redisService.set(MATCH_TIME_INFO + matchTimeInfo.getId(), MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchTimeInfo)).toJSONString()), time);
//            }
//        }
    }


    /**
     * 更新赛事时间数据
     *
     * @param matchTimeInfo 赛事时间
     * @param time          失效时间，单位：秒
     */
    public void setRedisAndMatchTimeInfo(MatchTimeInfo matchTimeInfo, Integer time) {
        matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
    }

    public void onlyUpdateMatchTimeInfoDB(MatchTimeInfo matchTimeInfo) {
        matchTimeInfoProducer.updateMatchTimesInfoByMq(matchTimeInfo);
    }

    /**
     * 更新赛事时间redis值
     *
     * @param matchTimeInfo 赛事时间
     * @param time          失效时间，单位：秒
     */
    public void setRedisMatchTimeInfo(MatchTimeInfo matchTimeInfo, Integer time) {
        String key = MATCH_TIME_INFO + matchTimeInfo.getThirdMatchId() + "_" + matchTimeInfo.getDataSourceType();
        String timekey = MATCH_TIME_INFO + matchTimeInfo.getId();
        String jsonStr = ((JSONObject) JSONObject.toJSON(matchTimeInfo)).toJSONString();
        if (org.apache.commons.lang3.ObjectUtils.isEmpty(time)) {
            redisService.set(key, jsonStr);
            redisService.set(timekey, jsonStr);
        } else {
            redisService.set(key, jsonStr, time);
            redisService.set(timekey, jsonStr, time);
        }
    }

    /**
     * 根据主键获取标准赛事信息
     *
     * @param referenceId 主键
     * @param time        失效时间，单位：秒
     * @return 标准赛事信息
     */
    public StandardMatchInfo getStandardMatchInfo(Long referenceId, Integer time) {
//        String key = RedisConfig.REDIS_KEY_DATABASE + "::StandardMatchInfo:" + referenceId;
//        Object o = redisService.get(key);
//        StandardMatchInfo standardMatchInfo;
//        if (ObjectUtils.isEmpty(o)) {
//            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(referenceId);
//            if (ObjectUtils.isEmpty(standardMatchInfo)) {
//                return standardMatchInfo;
//            }
//            if (ObjectUtils.isEmpty(time)) {
//                redisService.set(key, standardMatchInfo);
//            } else {
//                redisService.set(key, standardMatchInfo, time);
//            }
//        } else {
////            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
//            standardMatchInfo = JSON.parseObject(JSON.toJSONString(o), StandardMatchInfo.class);
//        }
        //不在自身服务设置key，统一使用实时服务提供的公共方法
        return standardMatchInfoService.getItem(referenceId);
    }

    /**
     * 更新标准赛事数据
     *
     * @param standardMatchInfo 标准赛事
     * @param time              失效时间，单位：秒
     */
    public void setRedisAndStandardMatchInfo(StandardMatchInfo standardMatchInfo, Integer time) {
        standardMatchInfoProducer.updateMatchTimesInfoByMq(standardMatchInfo);
    }

    /**
     * 更新标准赛事redis值
     *
     * @param standardMatchInfo 标准赛事
     * @param time              失效时间，单位：秒
     */
    public void setRedisStandardMatchInfo(StandardMatchInfo standardMatchInfo, Integer time) {
        String key = RedisConfig.REDIS_KEY_DATABASE + "::StandardMatchInfo:" + standardMatchInfo.getId();
        if (ObjectUtils.isEmpty(time)) {
            redisService.set(key, standardMatchInfo);
        } else {
            redisService.set(key, standardMatchInfo, time);
        }
    }

    /**
     * 根据标准赛事ID获取开售信息
     *
     * @param matchInfoId 标准赛事短ID
     * @param time        失效时间，单位：秒
     * @return 开售信息
     */
    public StandardSportMarketSell getStandardSportMarketSell(Long matchInfoId, Integer time) {
        //不在自身服务设置key，统一使用实时服务提供的公共方法
        return standardSportMarketSellService.getItem(matchInfoId);
    }

    /**
     * 更新redis和开售表数据
     *
     * @param standardSportMarketSell 开售表数据
     * @param time                    失效时间，单位：秒
     */
    public void setRedisAndStandardSportMarketSell(StandardSportMarketSell standardSportMarketSell, Integer time) {
        standardSportMarketSellService.refreshCache(standardSportMarketSell);
    }

    /**
     * 根据三方赛事ID获取比分切换关联表信息
     *
     * @param thirdMatchId 三方赛事ID
     * @param time         失效时间，单位：秒
     * @return 比分切换关联表信息
     */
    public MatchScoresSourceType getMatchScoresSourceType(Long thirdMatchId, Integer time) {
        String key = MATCH_SCORES_SOURCE_TYPE + thirdMatchId;
        Object o = redisService.get(key);
        MatchScoresSourceType matchScoresSourceType;
        if (org.springframework.util.ObjectUtils.isEmpty(o)) {
            MatchScoresSourceTypeExample example = new MatchScoresSourceTypeExample();
            example.createCriteria().andThirdMatchIdEqualTo(thirdMatchId);
            List<MatchScoresSourceType> matchScoresSourceTypeList = matchScoresSourceTypeMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(matchScoresSourceTypeList)) {
                return null;
            }
            matchScoresSourceType = matchScoresSourceTypeList.get(0);
            if (org.springframework.util.ObjectUtils.isEmpty(time)) {
                redisService.set(key, matchScoresSourceType);
            } else {
                redisService.set(key, matchScoresSourceType, time);
            }
        } else {
//            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
            matchScoresSourceType = JSON.parseObject(JSON.toJSONString(o), MatchScoresSourceType.class);
        }
        return matchScoresSourceType;
    }
    /**
     * 获取热键信息
     *
     * @param userName 用户名
     * @param sportId  球种
     * @param time     失效时间，单位：秒
     * @return 热键信息
     */
    public UserKeyboardSet getKeyboardByUserNameAndSportId(String userName, Long sportId, Integer time) {
        String key = FOOTBALL_KEYBOARD_SET + sportId;
        Object o = redisService.get(key);
        UserKeyboardSet userKeyboardSet;
        if (org.apache.commons.lang3.ObjectUtils.isEmpty(o)) {
            userKeyboardSet = userKeyboardSetMapper.selectKeyboardByUserNameAndSportId(userName, sportId);
            if (org.apache.commons.lang3.ObjectUtils.isEmpty(time)) {
                redisService.set(key, userKeyboardSet);
            } else {
                redisService.set(key, userKeyboardSet, time);
            }
        } else {
//            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
            userKeyboardSet = JSON.parseObject(JSON.toJSONString(o), UserKeyboardSet.class);
        }
        return userKeyboardSet;
    }

    /**
     * 更新redis和库热键信息
     *
     * @param userKeyboardSet 热键信息
     * @param time            失效时间，单位：秒
     */
    public int setRedisAndKeyboard(UserKeyboardSet userKeyboardSet, Integer time) {
        String key = FOOTBALL_KEYBOARD_SET + userKeyboardSet.getSportId();
        int count = userKeyboardSetMapper.updateKeyboardByUserName(userKeyboardSet);
        if (org.apache.commons.lang3.ObjectUtils.isEmpty(time)) {
            redisService.set(key, userKeyboardSet);
        } else {
            redisService.set(key, userKeyboardSet, time);
        }
        return count;
    }

    /**
     * 更新热键信息
     *
     * @param userKeyboardSet 热键信息
     * @param time            失效时间，单位：秒
     */
    public void setRedisKeyboard(UserKeyboardSet userKeyboardSet, Integer time) {
        String key = FOOTBALL_KEYBOARD_SET + userKeyboardSet.getSportId();
        if (org.apache.commons.lang3.ObjectUtils.isEmpty(time)) {
            redisService.set(key, userKeyboardSet);
        } else {
            redisService.set(key, userKeyboardSet, time);
        }
    }

    /**
     * 新增热键信息
     *
     * @param userKeyboardSet 热键信息
     * @param time            失效时间，单位：秒
     * @return 插入记录
     */
    public int addKeyboardInfo(UserKeyboardSet userKeyboardSet, Integer time) {
        String key = FOOTBALL_KEYBOARD_SET + userKeyboardSet.getSportId();
        int count = userKeyboardSetMapper.insertKeyboardInfo(userKeyboardSet);
        if (ObjectUtils.isEmpty(time)) {
            redisService.set(key, userKeyboardSet);
        } else {
            redisService.set(key, userKeyboardSet, time);
        }
        return count;
    }

    /**
     * 删除热键信息
     *
     * @param username 用户名
     * @param sportId  球种
     * @return 删除记录
     */
    public int removeKeyboardInfo(String username, Long sportId) {
        String key = FOOTBALL_KEYBOARD_SET + sportId;
        int count = userKeyboardSetMapper.deleteKeyboardByUserNameAndSportId(username, sportId);
        redisService.del(key);
        return count;
    }

    /**
     * 批量查找
     *
     * @param userIds 用户ID
     * @return 返回用户信息
     */
    public List<UserKeyboardSet> getAllKeyboardInfo(List<String> userIds) {
        List<UserKeyboardSet> keyboardSetList = userKeyboardSetMapper.selectKeyboardByUserIdList(userIds);
        return keyboardSetList;
    }

    public int removeAllKeyboardInfo(List<String> userIds) {
        int count = userKeyboardSetMapper.deleteKeyboardByUserIdList(userIds);
        return count;
    }

    /**
     * 按球种批量查找热键
     *
     * @param userIds 用户ID列表
     * @param sportId 球种ID
     * @return 热键列表
     */
    public List<UserKeyboardSet> getAllKeyboardInfoBySportId(List<String> userIds, Long sportId) {
        return userKeyboardSetMapper.selectKeyboardByUserIdListAndSportId(userIds, sportId);
    }

    /**
     * 按球种批量删除热键（仅删除指定球种，不跨球种误删）
     *
     * @param userIds 用户ID列表
     * @param sportId 球种ID
     * @return 删除数量
     */
    public int removeAllKeyboardInfoBySportId(List<String> userIds, Long sportId) {
        String redisKey = FOOTBALL_KEYBOARD_SET + sportId;
        int count = userKeyboardSetMapper.deleteKeyboardByUserIdListAndSportId(userIds, sportId);
        redisService.del(redisKey);
        return count;
    }
}
