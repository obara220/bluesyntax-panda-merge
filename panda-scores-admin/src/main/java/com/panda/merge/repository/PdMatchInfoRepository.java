package com.panda.merge.repository;

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
import com.panda.merge.model.StandardSportMarketSellExample;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdMatchInfoExample;
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
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

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
//        String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + thirdMatchId;
//        Object o = redisService.get(key);
//        ThirdMatchInfo thirdMatchInfo;
//        if (ObjectUtils.isEmpty(o)) {
//            thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(thirdMatchId);
//            if (ObjectUtils.isEmpty(thirdMatchInfo)) {
//                return null;
//            }
//            if (ObjectUtils.isEmpty(time)) {
//                redisService.set(key, thirdMatchInfo);
//                redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + thirdMatchInfo.getReferenceId(), thirdMatchInfo);
//            } else {
//                redisService.set(key, thirdMatchInfo, time);
//                redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + thirdMatchInfo.getReferenceId(), thirdMatchInfo, time);
//            }
//        } else {
////            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
//            thirdMatchInfo = JSON.parseObject(JSON.toJSONString(o), ThirdMatchInfo.class);
//        }
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
//        String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + referenceId;
//        Object o = redisService.get(key);
//        ThirdMatchInfo thirdMatchInfo;
//        if (ObjectUtils.isEmpty(o)) {
//            ThirdMatchInfoExample example = new ThirdMatchInfoExample();
//            example.createCriteria().andReferenceIdEqualTo(referenceId).andDataSourceCodeEqualTo(datasourceCode);
//            List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoMapper.selectByExample(example);
//            if (CollectionUtils.isEmpty(thirdMatchInfoList)) {
//                return null;
//            }
//            thirdMatchInfo = thirdMatchInfoList.get(0);
//            if (ObjectUtils.isEmpty(time)) {
//                redisService.set(key, thirdMatchInfo);
//            } else {
//                redisService.set(key, thirdMatchInfo, time);
//            }
//        } else {
////            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
//            thirdMatchInfo = JSON.parseObject(JSON.toJSONString(o), ThirdMatchInfo.class);
//        }
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
        if (org.apache.commons.lang3.ObjectUtils.isEmpty(o)) {
            log.info("::{}::报球版查询比分开始redis不存在，开始查询数据库",thirdMatchId);
            MatchScoresInfoExample example = new MatchScoresInfoExample();
            example.createCriteria().andThirdMatchIdEqualTo(thirdMatchId);
            List<MatchScoresInfo> matchScoresInfoList = matchScoresInfoMapper.selectByExample(example);
            log.info("::{}::报球版查询比分开始redis不存在，查询到数据库数据:{}",thirdMatchId, JSONObject.toJSONString(matchScoresInfoList));
            if (CollectionUtils.isEmpty(matchScoresInfoList)) {
                return null;
            }
            matchScoresInfo = matchScoresInfoList.get(0);
            if (org.apache.commons.lang3.ObjectUtils.isEmpty(time)) {
                redisService.set(key, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()));
                redisService.set(MATCH_SCORES_INFO + matchScoresInfo.getId(), MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()));
            } else {
                redisService.set(key, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()), time);
                redisService.set(MATCH_SCORES_INFO + matchScoresInfo.getId(), MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()), time);
            }
        } else {
//            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
            if (o instanceof MatchScoresInfo) {
                matchScoresInfo = JSON.parseObject(JSON.toJSONString(o), MatchScoresInfo.class);
                log.info("::{}::报球版查询比分开始redis存在，查询redis数据:{}",thirdMatchId,JSONObject.toJSONString(matchScoresInfo));
            } else {
                String str = MessageGZIP.uncompressToString((byte[]) o);
                matchScoresInfo = JSON.toJavaObject(JSONObject.parseObject(str), MatchScoresInfo.class);
                log.info("::{}::报球版查询比分开始redis存在，查询redis二进字数据:{}",thirdMatchId,JSONObject.toJSONString(matchScoresInfo));
            }

        }
        return matchScoresInfo;
    }

    /**
     * 插入数据更新redis
     *
     * @param matchScoresInfo 比分数据
     * @param time            失效时间，单位：秒
     */
    public void addMatchScoresInfo(MatchScoresInfo matchScoresInfo, Integer time) {
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
//        String key = MATCH_SCORES_INFO + matchScoresInfo.getThirdMatchId() + "_" + matchScoresInfo.getDataSourceType();
//        Object o = redisService.get(key);
//        if (ObjectUtils.isEmpty(o)) {
//            matchScoresInfoMapper.insert(matchScoresInfo);
//            if (ObjectUtils.isEmpty(time)) {
//                redisService.set(key, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()));
//                redisService.set(MATCH_SCORES_INFO + matchScoresInfo.getId(), MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()));
//            } else {
//                redisService.set(key, matchScoresInfo, time);
//                redisService.set(MATCH_SCORES_INFO + matchScoresInfo.getId(), MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()), time);
//            }
//        }
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
            MatchTimeInfoExample example = new MatchTimeInfoExample();
            example.createCriteria().andThirdMatchIdEqualTo(thirdMatchId);
            List<MatchTimeInfo> matchTimeInfoList = matchTimeInfoMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(matchTimeInfoList)) {
                return null;
            }
            matchTimeInfo = matchTimeInfoList.get(0);
            if (org.apache.commons.lang3.ObjectUtils.isEmpty(time)) {
                redisService.set(key, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchTimeInfo)).toJSONString()));
            } else {
                redisService.set(key, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchTimeInfo)).toJSONString()), time);
            }
        } else {
//            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
            if (o instanceof MatchTimeInfo) {
                matchTimeInfo = JSON.parseObject(JSON.toJSONString(o), MatchTimeInfo.class);
            } else {
                String str = MessageGZIP.uncompressToString((byte[]) o);
                matchTimeInfo = JSON.toJavaObject(JSONObject.parseObject(str), MatchTimeInfo.class);
            }
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

    public void onlyUpdateMatchTimeInfoRedis(MatchTimeInfo matchTimeInfo) {
        String key = MATCH_TIME_INFO +matchTimeInfo.getId();
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(matchTimeInfo);
        redisService.set(key, MessageGZIP.compressToByte(jsonObject.toJSONString()), RepositoryConstant.REDIS_THREE_TIME);
        String thirdMatchIdKey = MATCH_TIME_INFO + matchTimeInfo.getThirdMatchId() + "_" + matchTimeInfo.getDataSourceType();
        redisService.set(thirdMatchIdKey, MessageGZIP.compressToByte(jsonObject.toJSONString()), RepositoryConstant.REDIS_THREE_TIME);
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
        if (org.apache.commons.lang3.ObjectUtils.isEmpty(time)) {
            redisService.set(key, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchTimeInfo)).toJSONString()));
            redisService.set(timekey, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchTimeInfo)).toJSONString()));
        } else {
            redisService.set(key, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchTimeInfo)).toJSONString()), time);
            redisService.set(timekey, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchTimeInfo)).toJSONString()),time);
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
//        String key = RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketSell:" + matchInfoId;
//        Object o = redisService.get(key);
//        StandardSportMarketSell standardSportMarketSell;
//        if (ObjectUtils.isEmpty(o)) {
//            StandardSportMarketSellExample example = new StandardSportMarketSellExample();
//            example.createCriteria().andMatchInfoIdEqualTo(matchInfoId);
//            List<StandardSportMarketSell> list = standardSportMarketSellMapper.selectByExample(example);
//            if (CollectionUtils.isEmpty(list)) {
//                return null;
//            }
//            standardSportMarketSell = list.get(0);
//            if (ObjectUtils.isEmpty(time)) {
//                redisService.set(key, standardSportMarketSell);
//            } else {
//                redisService.set(key, standardSportMarketSell, time);
//            }
//        } else {
////            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
//            standardSportMarketSell = JSON.parseObject(JSON.toJSONString(o), StandardSportMarketSell.class);
//        }
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
//        String key = RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketSell:" + standardSportMarketSell.getMatchInfoId();
//        standardSportMarketSellMapper.updateByPrimaryKey(standardSportMarketSell);
//        if (ObjectUtils.isEmpty(time)) {
//            redisService.set(key, standardSportMarketSell);
//        } else {
//            redisService.set(key, standardSportMarketSell, time);
//        }
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
}
