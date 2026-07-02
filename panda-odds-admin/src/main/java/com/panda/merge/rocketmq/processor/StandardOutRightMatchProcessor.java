package com.panda.merge.rocketmq.processor;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.SellStatusEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.StandardOutrightMatchDTO;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardOutrightMarket;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdSportMarket;
import com.panda.merge.service.StandardOutrightMarketService;
import com.panda.merge.service.ThirdSportMarketNewService;
import com.panda.merge.service.ThirdSportMarketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/28 <br>
 * @see com.panda.merge.rocketmq.processor <br>
 */
@Component
@Slf4j
@Validated
public class StandardOutRightMatchProcessor extends BaseProcessor {

    @Autowired
    private ThirdSportMarketNewService thirdSportMarketService;

    @Autowired
    private SoldMessageToOddsProcessor soldMessageToOddsProcessor;
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    @Autowired
    private StandardOutrightMarketService standardOutrightMarketService;

    @Autowired
    private SelfBuildMarketOperateProcessor selfBuildMarketOperateProcessor;

    private static final String DATA_SOURCE_PA = "PA";

    public void processStandardOutRightMatch(@Valid Request<StandardOutrightMatchDTO> request) {
        String linkId = request.getLinkId();
        log.info("::{}::processStandardOutRightMatch 处理标准冠军赛事消息,req:{}", linkId, JSON.toJSONString(request));
        validateLinkId("processStandardOutRightMatch", request);
        StandardOutrightMatchDTO standardOutrightMatchDTO = request.getData();
        Long thirdMatchId = standardOutrightMatchDTO.getThirdOutrightMatchId();
        Long standardMatchId = standardOutrightMatchDTO.getStandardOutrightMatchId();
        String dataSourceCode = standardOutrightMatchDTO.getDataSourceCode();
        //查询三方盘口
        List<ThirdSportMarket> thirdSportMarketList = thirdSportMarketService.getItemList(thirdMatchId, standardMatchId);
        if (CollectionUtils.isEmpty(thirdSportMarketList)) {
            log.info("::{}::processStandardOutRightMatch thirdMatchId:{}, standardMatchId:{},三方盘口列表为空", linkId, thirdMatchId, standardMatchId);
            return;
        }

        if ( DATA_SOURCE_PA.equals(dataSourceCode) && StringUtils.isNotEmpty(standardOutrightMatchDTO.getThirdMarketSourceId())) {
            thirdSportMarketList = thirdSportMarketList.stream()
                    .filter( tsm -> standardOutrightMatchDTO.getThirdMarketSourceId().equals(tsm.getThirdMarketSourceId()) ).collect(Collectors.toList());
        }

        ThirdMatchInfo thirdMatchInfo = thirdMatchMarketProcessor.getThirdMatchInfoByMatchId(true, standardMatchId, dataSourceCode);
        if (null == thirdMatchInfo) {
            log.info("::{}::processStandardOutRightMatch standardMatchId:{},未找到三方冠军赛事", linkId, standardMatchId);
            return;
        }
        StandardMatchInfo standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(true, standardMatchId);
        if (null == standardMatchInfo) {
            log.info("::{}::processStandardOutRightMatch standardMatchId:{},未找到标准赛事", linkId, standardMatchId);
            return;
        }
        //Step1:生成标准盘口
        Map<String, StandardMarketDataMessage> marketDataMessageMap = soldMessageToOddsProcessor.processMarketBySold(linkId,standardMatchInfo, thirdSportMarketList);

        //Step2:初始化盘口开售表
        if (!CollectionUtils.isEmpty(marketDataMessageMap)) {
            log.info("::{}::processStandardOutRightMatch 初始化盘口开售表,标准赛事id:{}, size:{}", linkId, standardMatchId, marketDataMessageMap.size());
            List<StandardOutrightMarket> standardOutrightMarketList = new ArrayList<>();
            marketDataMessageMap.forEach((k, v) -> {
                StandardOutrightMarket standardOutrightMarket = new StandardOutrightMarket();
                standardOutrightMarket.setId(v.getRelationMarketId());
                standardOutrightMarket.setStandardMatchId(standardMatchId);
                standardOutrightMarket.setMarketCategoryId(v.getMarketCategoryId());
                standardOutrightMarket.setMarketStatus(v.getStatus());
                standardOutrightMarket.setNameCode(v.getNameCode());
                standardOutrightMarket.setLinkId(linkId);
                standardOutrightMarket.setMarketSellStatus(SellStatusEnum.UNSOLD.value);
                standardOutrightMarketList.add(standardOutrightMarket);
            });
            standardOutrightMarketService.saveBatch(standardOutrightMarketList);
            //记录创建盘口日志
            if (DataSourceCodeEnum.PA.name().equals(standardMatchInfo.getDataSourceCode())) {
                String operateType = "盘口创建";
                String operateText = "盘口id:";
                selfBuildMarketOperateProcessor.workRecordOfOutright (standardOutrightMatchDTO.getOperatorId(), standardOutrightMatchDTO.getOperatorName(),
                        standardOutrightMarketList.get(0).getId(), operateType, operateText + standardOutrightMarketList.get(0).getId());
            }
        }
        log.info("::{}:: processStandardOutRightMatch success.size:{}", linkId, marketDataMessageMap.size());
    }
}

