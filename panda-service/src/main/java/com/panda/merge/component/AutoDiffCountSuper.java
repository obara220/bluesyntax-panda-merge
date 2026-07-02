package com.panda.merge.component;

import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.model.EuropeConvertMalay;
import com.panda.merge.model.PandaOddsConvert;
import com.panda.merge.service.EuropeConvertMalayService;
import com.panda.merge.service.PandaOddsConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author :  Jimmy
 * @Project Name :  panda_data_realtime_marketodds
 * @Package Name :  com.panda.sport.data.realtime.service.autodiff.count
 * @Description :  TODO
 * @Date: 2020-01-22 17:16
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public class AutoDiffCountSuper {
    @Autowired
    private RedisService redisService;
    @Autowired
    private PandaOddsConvertService pandaOddsConvertService;
    @Autowired
    private EuropeConvertMalayService europeConvertMalayService;

    private volatile static List<PandaOddsConvert> pandaOddsConvertList;
    private volatile static Map<Double,Double> europeConvertMalayMap;


    public void countAutoDiff(Long standardMatchId, Long standardMarketId,String oddsType, List<StandardMarketOddsDataMessage> standardSportMarketOddsList) {

    }

    public Double countMarginValue(List<StandardMarketOddsDataMessage> standardSportMarketOddsList) {
        Double oddSum = 0D;
        for (StandardMarketOddsDataMessage standardSportMarketOdds : standardSportMarketOddsList) {
            oddSum += standardSportMarketOdds.getMalayOddsValue();
        }
        Double margin = 0D;
        if (oddSum >= 1) {
            margin = 2 - oddSum;
        } else {
            margin = 0 - oddSum;
        }
        return margin;
    }
}
