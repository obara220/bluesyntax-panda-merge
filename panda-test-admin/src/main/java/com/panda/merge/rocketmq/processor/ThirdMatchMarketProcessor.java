package com.panda.merge.rocketmq.processor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.InitializeComponent;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.constant.*;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.*;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/13 <br>
 */
@Component
@Slf4j
@Validated
public class ThirdMatchMarketProcessor extends BaseProcessor {

    @Autowired
    private I18nOutrightMarketOddsService i18nOutrightMarketOddsService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private ThirdMarketCategoryService thirdMarketCategoryService;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;


    @Autowired
    private ThirdMarketCategoryFieldService thirdMarketCategoryFieldService;

    @Autowired
    private ThirdSportMarketService thirdSportMarketService;

    @Autowired
    private ThirdSportMarketOddsService thirdSportMarketOddsService;


    @Autowired
    private StandardSportMarketCategoryService standardSportMarketCategoryService;

    @Autowired
    private StandardOutrightMatchInfoService standardOutrightMatchInfoService;

    @Autowired
    private ThirdOutrightMatchInfoService thirdOutrightMatchInfoService;


    @Autowired
    InitializeComponent initializeComponent;

    /**
     * 处理收到的数据源赔率
     *
     * @param request
     */
    @Async("AccessMatchMarketData")
    @ExceptionHelper
    public void accessMatchMarketData(@Valid Request<ThirdMatchMarketDTO> request) {
        String linkId = request.getLinkId();
        // 至于Id 我觉得给UUID是可行的~
        StopWatch swRedis = new StopWatch("赔率服务数据源赔率入口主流程_"+UUIdUtils.getId());
        swRedis.start("校验linkId耗时");
        swRedis.stop();
        ThirdMatchMarketDTO thirdMatchMarketDTO = request.getData();
        String dataSourceCode = thirdMatchMarketDTO.getDataSourceCode();
        String thirdMatchSourceId = thirdMatchMarketDTO.getThirdMatchSourceId();
        Integer marketType = thirdMatchMarketDTO.getMarketList().get(0).getMarketType();
        swRedis.start("校验运动类型，三方标准赛事，开售信息");
        //校验是否数据源运动类型是否存在
        validateSportId(dataSourceCode, String.valueOf(thirdMatchMarketDTO.getSportId()));
        //判断冠军玩法
        boolean isOutRight = Arrays.asList(MarginCategoryConfig.THIRD_OUTRIGHT_CATEGORY).contains(thirdMatchMarketDTO.getMarketList().get(0).getThirdMarketCategorySourceId());
        //兼容冠军玩法，获取三方赛事信息
        ThirdMatchInfo thirdMatchInfo = getThirdMatchInfo(isOutRight, dataSourceCode, thirdMatchSourceId);
        if (thirdMatchInfo == null) {
            log.error("::{}::三方赛事不存在,三方数据源id:{},冠军玩法:{}", linkId, thirdMatchSourceId, isOutRight);
            swRedis.stop();
            return;
        }
        //兼容冠军玩法，获取标准赛事信息
        StandardMatchInfoDetail standardMatchInfo = getStandardMatchInfo(isOutRight, thirdMatchInfo.getReferenceId());
        //兼容冠军玩法，获取赛事开售信息
        StandardSportMarketSell standardSportMarketSell = getStandardSportMarketSell(isOutRight, thirdMatchInfo.getReferenceId());
        swRedis.stop();
        //-----------循环处理盘口数据---------------

        //以下数据库逻辑操作有并发问题，这里需要以赛事维度加redis锁
        swRedis.start("获取分布式锁，循环处理三方盘口，标准盘口");
        //TODO:优化加锁机制：
        // 1.只有生成了标准的才加锁，因为只有在有标准的情况下，才会入缓存，才会要求对缓存的操作必须顺序性执行
        // 2.对标准赛事加锁，不区分数据源
        String lockValue = UUIdUtils.getId()+"_"+linkId;
        boolean isLock = false;
        try{
            for (ThirdMarketDTO thirdMarketDTO : thirdMatchMarketDTO.getMarketList()) {
                // 至于Id 我觉得给UUID是可行的~
                StopWatch sw = new StopWatch(UUID.randomUUID().toString());
                //三方盘口的赛种Id要修改为融合的标识赛种id
                thirdMarketDTO.setSportId(thirdMatchInfo.getSportId());
                String thirdCategorySourceId = thirdMarketDTO.getThirdMarketCategorySourceId();
                //获取盘口的三方玩法
                sw.start("查询盘口的三方玩法耗时");
                ThirdMarketCategory thirdMarketCategory = thirdMarketCategoryService.getItem(dataSourceCode, thirdCategorySourceId);
                sw.stop();
                if (thirdMarketCategory == null) {
                    log.info("::{}::未找到三方玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
                    continue;
                }
                if (null == thirdMarketCategory.getReferenceId() || 0L == thirdMarketCategory.getReferenceId()) {
                    log.info("::{}::三方玩法未绑定标准玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
                    continue;
                }

                //-------------处理三方盘口及投注项数据------------
                // 至于Id 我觉得给UUID是可行的~
                sw.start("处理三方盘口耗时");
                ThirdSportMarketMessage thirdSportMarketMessage = processThirdSportMarket(linkId, dataSourceCode, thirdMatchInfo, thirdMarketDTO, thirdMarketCategory);
                sw.stop();

            }
         }finally {
            if (isLock)
            {
                String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardMatchInfo.getId();
                redisService.unLock(redisLocKey,lockValue);
                log.info("::{}::接收数据源赔率开始,redisLocKey:{},释放分布式锁,lockValue:{}", linkId,redisLocKey, lockValue);
            }
        }

    }
    public StandardSportMarketSell getStandardSportMarketSell(boolean isOutRight, Long standardMatchId) {
        if (isOutRight) {
            StandardOutrightMatchInfo standardOutrightMatchInfo = standardOutrightMatchInfoService.getItem(standardMatchId);
            if (standardOutrightMatchInfo == null) {
                return null;
            }
            StandardSportMarketSell standardSportMarketSell = new StandardSportMarketSell();
            standardSportMarketSell.setMatchInfoId(standardOutrightMatchInfo.getId());
            standardSportMarketSell.setPreMatchDataProviderCode(standardOutrightMatchInfo.getDataSourceCode());
            standardSportMarketSell.setPreMatchSellStatus(standardOutrightMatchInfo.getSellStatus());
            return standardSportMarketSell;
        }
        return standardSportMarketSellService.getItem(standardMatchId);
    }
    public StandardMatchInfoDetail getStandardMatchInfo(boolean isOutRight, Long standardMatchId) {
        StandardMatchInfoDetail standardMatchInfoDetail = new StandardMatchInfoDetail();
        if (isOutRight) {
            StandardOutrightMatchInfo standardOutrightMatchInfo = standardOutrightMatchInfoService.getItem(standardMatchId);
            if (null == standardOutrightMatchInfo) {
                return null;
            }
            //标准赛事信息转换
            BeanUtils.copyProperties(standardOutrightMatchInfo, standardMatchInfoDetail);
            standardMatchInfoDetail.setId(standardOutrightMatchInfo.getId());
            standardMatchInfoDetail.setSportId(standardOutrightMatchInfo.getSportId());
            standardMatchInfoDetail.setDataSourceCode(standardOutrightMatchInfo.getDataSourceCode());
            standardMatchInfoDetail.setOperateMatchStatus(standardOutrightMatchInfo.getMatchMarketStatus());
            standardMatchInfoDetail.setMatchType(1);
            standardMatchInfoDetail.setAutoSellStatus(standardOutrightMatchInfo.getAutoSellStatus());
            //冠军赛事结束时间赋值给beginTime 用于盘口缓存时间计算
            standardMatchInfoDetail.setBeginTime(standardOutrightMatchInfo.getStandrdOutrightMatchEndTime());
            return standardMatchInfoDetail;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        //standardMatchInfo==null
        if (ObjectUtils.isEmpty(standardMatchInfo))
        {
            return null;
        }
        BeanUtils.copyProperties(standardMatchInfo, standardMatchInfoDetail);
        standardMatchInfoDetail.setMatchType(0);
        return standardMatchInfoDetail;
    }
    /**
     * 处理三方盘口和投注项赔率
     *
     * @param dataSourceCode
     * @param thirdMatchInfo
     * @param thirdMarketDTO
     * @param thirdMarketCategory
     * @return
     */
    private ThirdSportMarketMessage processThirdSportMarket(String linkId, String dataSourceCode, ThirdMatchInfo thirdMatchInfo, ThirdMarketDTO thirdMarketDTO, ThirdMarketCategory thirdMarketCategory) {
        log.info("::{}::processThirdSportMarket 三方盘口数据的处理, ThirdMarketDTO:{}", linkId, JSON.toJSONString(thirdMarketDTO));
        //获取赛种玩法
        StandardSportMarketCategory standardSportMarketCategory = standardSportMarketCategoryService.getItem(thirdMarketCategory.getReferenceId(), thirdMatchInfo.getSportId());
        //赛种不支持玩法
        if (standardSportMarketCategory == null || standardSportMarketCategory.getStatus() == 0) {
            log.info("::{}::三方赛事:{},赛种id:{},不支持玩法id:{}", linkId, thirdMatchInfo.getThirdMatchSourceId(), thirdMatchInfo.getSportId(), thirdMarketCategory.getReferenceId());
            return null;
        }
        ThirdSportMarketMessage thirdSportMarketMessage = new ThirdSportMarketMessage();
        //处理三方盘口数据，不存在新增，存在更新
        ThirdSportMarket thirdSportMarket = thirdSportMarketService.getItem(dataSourceCode, thirdMarketDTO.getThirdMarketSourceId(), thirdMatchInfo.getId());
        if (thirdSportMarket == null) {
            thirdSportMarket = thirdSportMarketService.create(linkId, thirdMarketDTO, thirdMatchInfo.getId(), standardSportMarketCategory);
        } else {
            //处理盘口移交状态,当前盘口是滚球还是赛前，赛前就关盘，滚球的忽略handover
            if (Constant.SPORT_MARKET.MARKET_TYPE.PRE_MATCH_BUSINESS.equals(thirdSportMarket.getMarketType())
                    && thirdMarketDTO.getStatus().equals(Constant.SPORT_MARKET.STATUS.HANDEDOVER)) {
                thirdMarketDTO.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                log.info("::{}::三方handover处理,三方盘口源id:{},当前盘口类型:{},三方盘口源状态:{}", linkId, thirdMarketDTO.getThirdMarketSourceId(), thirdSportMarket.getMarketType(), thirdMarketDTO.getStatus());
            }
            if(Constant.SPORT_MARKET.MARKET_TYPE.LIVE_ODD_BUSINESS.equals(thirdSportMarket.getMarketType())
                    && Constant.SPORT_MARKET.MARKET_TYPE.PRE_MATCH_BUSINESS.equals(thirdMarketDTO.getMarketType())
                    && thirdMarketDTO.getStatus().equals(Constant.SPORT_MARKET.STATUS.HANDEDOVER)){
                log.info("::{}::handover处理滚球数据,忽略赛前,三方盘口源id:{},当前盘口类型:{},三方盘口源状态:{}", linkId, thirdMarketDTO.getThirdMarketSourceId(), thirdSportMarket.getMarketType(), thirdMarketDTO.getStatus());
                return null;
            }
            thirdSportMarket.setStatus(thirdMarketDTO.getStatus());
            thirdSportMarket.setThirdMarketSourceStatus(thirdMarketDTO.getStatus());
            thirdSportMarket.setMarketType(thirdMarketDTO.getMarketType());
            thirdSportMarket.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            thirdSportMarket.setOddsName(thirdMarketDTO.getOddsName());
            thirdSportMarket.setAddition1(thirdMarketDTO.getAddition1());
            thirdSportMarket.setAddition2(thirdMarketDTO.getAddition2());
            thirdSportMarket.setAddition3(thirdMarketDTO.getAddition3());
            thirdSportMarket.setAddition4(thirdMarketDTO.getAddition4());
            thirdSportMarket.setNumberOfWinners(thirdMarketDTO.getNumberOfWinners());
            thirdSportMarket.setOfferLineId(thirdMarketDTO.getOfferLineId());
            //异步执行更新
            log.info("::{}::processThirdSportMarket 三方盘口数据的更新, ThirdMarketDTO:{}", linkId, JSON.toJSONString(thirdSportMarket));
            thirdSportMarketService.updateByPrimaryKeySelective(thirdSportMarket);
        }
        BeanUtils.copyProperties(thirdSportMarket,thirdSportMarketMessage);
        thirdSportMarketMessage.setThirdSportMarketOddsList(new ArrayList<ThirdSportMarketOdds>());
        //处理三方盘口投注项
        if (!CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList())) {
            //批量修改投注项
            List<ThirdSportMarketOdds> upOddsList = new ArrayList<>();
            for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList()) {
                //获取三方玩法投注项模板
                ThirdMarketCategoryField thirdMarketCategoryField = thirdMarketCategoryFieldService.getItem(thirdMarketDTO.getDataSourceCode(), thirdMarketOddsDTO.getThirdTempletSourceId(), thirdMarketCategory.getId());
                if (thirdMarketCategoryField == null) {
                    log.info("::{}::三方投注项模板为空，数据源:{}，数据源原始模板id:{}，融合三方玩法id:{}", linkId, thirdMarketDTO.getDataSourceCode(), thirdMarketOddsDTO.getThirdTempletSourceId(), thirdMarketCategory.getId());
                    continue;
                }
                //查询三方盘口投注项信息是否存在，不存在新增，存在更新
                ThirdSportMarketOdds thirdSportMarketOdds = thirdSportMarketOddsService.getItem(dataSourceCode, thirdMarketOddsDTO.getThirdOddsFieldSourceId(), thirdSportMarket.getId());
                if (thirdSportMarketOdds == null) {
                	if(DataSourceCodeEnum.TX.code.equals(dataSourceCode)) {
                		thirdMarketOddsDTO.setModifyTime(thirdMarketDTO.getModifyTime());
                	}
                    thirdSportMarketOdds = thirdSportMarketOddsService.create(dataSourceCode,linkId, thirdMarketDTO.getMarketType() == 2, thirdMarketOddsDTO, thirdSportMarket, thirdMarketCategoryField.getId());
                } else {
                    thirdSportMarketOdds.setOddsValue(thirdMarketOddsDTO.getOddsValue());
                    thirdSportMarketOdds.setOriginalOddsValue(thirdMarketOddsDTO.getOriginalOddsValue());
                    thirdSportMarketOdds.setActive(thirdMarketOddsDTO.getActive());
                    thirdSportMarketOdds.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    thirdSportMarketOdds.setThirdTemplateSourceId(thirdMarketOddsDTO.getThirdTempletSourceId());
                    thirdSportMarketOdds.setOddsType(thirdMarketOddsDTO.getOddsType());
                    if ( thirdMarketDTO.getMarketType() == 2 ) {
                        thirdSportMarketOdds.setAddition1(thirdMarketOddsDTO.getAddition1());
                    }
                    //冠军投注项多语言历史数据兼容
                    List<I18nOutrightMarketOdds> i18nOutrightMarketOddsOld =
                            i18nOutrightMarketOddsService.getListOutrightMarketOdds(Arrays.asList(thirdSportMarketOdds.getNameCode()), thirdMarketOddsDTO.getDataSourceCode());
                    Map<String, I18nOutrightMarketOdds> oldLanguageMap = Maps.newHashMap();
                    if (!CollectionUtils.isEmpty(i18nOutrightMarketOddsOld)) {
                        oldLanguageMap = i18nOutrightMarketOddsOld.stream().collect(Collectors.toMap(I18nOutrightMarketOdds::getLanguageType, i -> i));
                    }
                    if ( thirdMarketDTO.getMarketType() == 2 && !CollectionUtils.isEmpty(thirdMarketOddsDTO.getI18nNames())) {
                        if (thirdSportMarketOdds.getNameCode() == null) {
                            thirdSportMarketOdds.setNameCode(thirdSportMarketOdds.getId());
                        }
                        List<I18nOutrightMarketOdds> i18nMarketOddsList = new ArrayList<>();
                        List<I18nOutrightMarketOdds> i18nOutrightMarketOddsListUpdate = Lists.newArrayList();
                        for (I18nItemDTO dto : thirdMarketOddsDTO.getI18nNames()) {
                            I18nOutrightMarketOdds i18nOutrightMarketOdds = new I18nOutrightMarketOdds();
                            if ( !oldLanguageMap.isEmpty()  && oldLanguageMap.containsKey(dto.getLanguageType())) {
                                BeanUtils.copyProperties(oldLanguageMap.get(dto.getLanguageType()), i18nOutrightMarketOdds);
                                i18nOutrightMarketOdds.setText(dto.getText());
                                i18nOutrightMarketOddsListUpdate.add(i18nOutrightMarketOdds);
                            } else {
                                BeanUtils.copyProperties(dto, i18nOutrightMarketOdds);
                                i18nOutrightMarketOdds.setNameCode(thirdSportMarketOdds.getNameCode());
                                i18nOutrightMarketOdds.setDataSourceCode(thirdSportMarketOdds.getDataSourceCode());
                                i18nMarketOddsList.add(i18nOutrightMarketOdds);
                            }
                        }
                        try{
                            if (!CollectionUtils.isEmpty(i18nMarketOddsList)) {
                                i18nOutrightMarketOddsService.saveBatch(i18nMarketOddsList);
                            }
                            /*if (!CollectionUtils.isEmpty(i18nOutrightMarketOddsListUpdate)) {
                                i18nOutrightMarketOddsService.updateBatchByPrimaryKeys(i18nOutrightMarketOddsListUpdate);
                            }*/
                        }catch (DuplicateKeyException e) {
                            //此处只打印异常，即使入库失败
                            log.info("::{}::insert三方投注项多语言唯一约束冲突，error",linkId,e);
                        }
                    }
                    //异步执行更新
                    //thirdSportMarketOddsService.updateByPrimaryKeySelective(dataSourceCode,thirdSportMarketOdds);
                    upOddsList.add(thirdSportMarketOdds);
                }
                thirdSportMarketMessage.getThirdSportMarketOddsList().add(thirdSportMarketOdds);
            }
            //批量修改投注项
            if (!CollectionUtils.isEmpty(upOddsList)) {
                thirdSportMarketOddsService.upThirdOddsList(linkId, thirdMatchInfo.getDataSourceCode(), upOddsList, thirdMarketDTO.getMarketOddsList());
            }
        }
        return thirdSportMarketMessage;
    }
    public ThirdMatchInfo getThirdMatchInfo(boolean isOutRight, String dataSourceCode, String thirdMatchSourceId) {
        if (isOutRight) {
            ThirdOutrightMatchInfo thirdOutrightMatchInfo = thirdOutrightMatchInfoService.getItem(dataSourceCode, thirdMatchSourceId);
            if (thirdOutrightMatchInfo == null) {
                return null;
            }
            //三方赛事信息转换
            ThirdMatchInfo thirdMatchInfo = new ThirdMatchInfo();
            thirdMatchInfo.setId(thirdOutrightMatchInfo.getId());
            thirdMatchInfo.setSportId(thirdOutrightMatchInfo.getSportId());
            thirdMatchInfo.setReferenceId(thirdOutrightMatchInfo.getReferenceId());
            thirdMatchInfo.setDataSourceCode(thirdOutrightMatchInfo.getDataSourceCode());
            thirdMatchInfo.setThirdMatchSourceId(thirdOutrightMatchInfo.getThirdOutrightSourceId());
            return thirdMatchInfo;
        }
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(dataSourceCode, thirdMatchSourceId);
        if (thirdMatchInfo == null) {
            return null;
        }
        //兼容缓存覆盖问题
        if (thirdMatchInfo.getReferenceId() == null || thirdMatchInfo.getReferenceId() == 0) {
            return thirdMatchInfoService.getItem(thirdMatchInfo.getId());
        }
        return thirdMatchInfo;
    }
}
