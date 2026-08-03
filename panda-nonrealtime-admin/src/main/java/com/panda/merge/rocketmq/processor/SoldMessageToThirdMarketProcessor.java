package com.panda.merge.rocketmq.processor;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdMarketOddsDTO;
import com.panda.merge.dto.message.SoldMessage;
import com.panda.merge.dto.message.ThirdSportMarketMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMarketCategory;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdSportMarketOdds;
import com.panda.merge.rocketmq.common.CommonAsyncService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.ThirdMarketCategoryService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@Component
public class SoldMessageToThirdMarketProcessor {
    @Autowired
    ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    RedisService redisService;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    CommonAsyncService commonAsyncService;
    @Autowired
    ThirdMarketCategoryService thirdMarketCategoryService;

    @Async("ProcessAllThirdMarketThreadPool")
    public void execute(@Valid Request<SoldMessage> soldMessageRequest) {
        SoldMessage soldMessage = soldMessageRequest.getData();
        log.info("::{}::百家赔:soldMessage赔率下发，逻辑处理开始，request={}", soldMessageRequest.getLinkId(), JSON.toJSONString(soldMessageRequest));
        String linkId = soldMessageRequest.getLinkId();
        Long matchId = soldMessage.getMatchId();
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchId);
        if (standardMatchInfo == null) {
            log.info("::{}::百家赔:标准赛事不存在,标准赛事id:{}", soldMessageRequest.getLinkId(), matchId);
            return;
        }
        List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoService.getItems(matchId);
        if (CollectionUtils.isEmpty(thirdMatchInfoList)) {
            return;
        }
        //AO、TX 百家赔
        for (ThirdMatchInfo thirdMatchInfo : thirdMatchInfoList) {
            String dataSource = thirdMatchInfo.getDataSourceCode();
            if (!DataSourceCodeEnum.TX.code.equals(dataSource) && !DataSourceCodeEnum.AO.code.equals(dataSource) && !DataSourceCodeEnum.LS.code.equals(dataSource)) {
                log.error("::{}::百家赔:三方赛事不存在,标准赛事id:{},数据源:{}", soldMessageRequest.getLinkId(), matchId, dataSource);
                continue;
            }
            List<ThirdMarketDTO> thirdMarketDTOList = getThirdMarketDTOS(thirdMatchInfo);
            if (!CollectionUtils.isEmpty(thirdMarketDTOList)) {
                //存储需要下发的三方数据商盘口集合
                List<ThirdSportMarketMessage> thirdSportMarketMessages = new ArrayList<>();
                for (ThirdMarketDTO thirdMarketDTO : thirdMarketDTOList) {
                    ThirdSportMarketMessage thirdSportMarketMessage = new ThirdSportMarketMessage();
                    //三方盘口的赛种Id要修改为融合的标识赛种id
                    thirdMarketDTO.setSportId(thirdMatchInfo.getSportId());
                    String thirdCategorySourceId = thirdMarketDTO.getThirdMarketCategorySourceId();
                    ThirdMarketCategory thirdMarketCategory = thirdMarketCategoryService.getItem(dataSource, thirdCategorySourceId);
                    if (thirdMarketCategory == null) {
                        log.info("::{}::百家赔：未找到三方玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
                        continue;
                    }
                    if (null == thirdMarketCategory.getReferenceId() || 0L == thirdMarketCategory.getReferenceId()) {
                        log.info("::{}::百家赔：三方玩法未绑定标准玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
                        continue;
                    }
                    BeanUtils.copyProperties(thirdMarketDTO, thirdSportMarketMessage);
                    List<ThirdSportMarketOdds> thirdSportMarketOddsList = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList())) {
                        for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList()) {
                            ThirdSportMarketOdds thirdSportMarketOdds = new ThirdSportMarketOdds();
                            BeanUtils.copyProperties(thirdMarketOddsDTO, thirdSportMarketOdds);
                            thirdSportMarketOdds.setDataSourceCode(thirdMarketDTO.getDataSourceCode());
                            thirdSportMarketOddsList.add(thirdSportMarketOdds);
                        }
                    }
                    thirdSportMarketMessage.setMarketCategoryId(thirdMarketCategory.getReferenceId());
                    thirdSportMarketMessage.setThirdSportMarketOddsList(thirdSportMarketOddsList);
                    thirdSportMarketMessage.setThirdMarketSourceStatus(thirdSportMarketMessage.getStatus());
                    thirdSportMarketMessages.add(thirdSportMarketMessage);
                }
                if (!CollectionUtils.isEmpty(thirdSportMarketMessages)) {
                    commonAsyncService.sendMessageToRisk(linkId + "_" + dataSource + "_third", standardMatchInfo, thirdSportMarketMessages, thirdSportMarketMessages.get(0).getModifyTime(),thirdMatchInfo);
                }
            }
        }
    }

    /**
     * 获取缓存数据盘口
     * @param thirdMatchInfo
     * @return
     */
    private List<ThirdMarketDTO> getThirdMarketDTOS(ThirdMatchInfo thirdMatchInfo) {
        List<ThirdMarketDTO> thirdMarketDTOList = new ArrayList<>();
        String dataSourceCode = thirdMatchInfo.getDataSourceCode();
        if (dataSourceCode.equals(DataSourceCodeEnum.TX.code) || dataSourceCode.equals(DataSourceCodeEnum.AO.code)) {
            String thirdMarketKey = Constant.REDIS_KEY.RONGHE_THIRD_STANDARD_MARKET + thirdMatchInfo.getThirdMatchSourceId();
            Object obj = redisService.hGetAll(thirdMarketKey);
            if (null == obj) {
                return null;
            }
            Map<String, Map<String, Map<Integer, ThirdMarketDTO>>> mapMap = (Map<String, Map<String, Map<Integer, ThirdMarketDTO>>>) obj;
            mapMap.forEach((k1, v1) -> {
                if (v1 != null) {
                    v1.forEach((k2, v2) -> {
                        if (v2 != null) {
                            v2.values().forEach(v -> {
                                v.setDataSourceCode(k1);
                            });
                            thirdMarketDTOList.addAll(v2.values());
                        }
                    });
                }
            });
            redisService.del(thirdMarketKey);
        } else if (dataSourceCode.equals(DataSourceCodeEnum.LS.code)) {
            String thirdMarketKey = Constant.REDIS_KEY.RONGHE_LS_THIRD_STANDARD_MARKET + thirdMatchInfo.getThirdMatchSourceId();
            Object obj = redisService.hGetAll(thirdMarketKey);
            if (null == obj) {
                return null;
            }
            Map<String, ThirdMarketDTO> mapMap = (Map<String, ThirdMarketDTO>) obj;
            for (String key : mapMap.keySet()) {
                Map<String,ThirdMarketDTO> thirdMarketMap = (Map<String,ThirdMarketDTO> )mapMap.get(key);
                thirdMarketDTOList.addAll(thirdMarketMap.values());

            }
            redisService.del(thirdMarketKey);
        }
        return thirdMarketDTOList;
    }
}
