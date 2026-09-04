package com.panda.merge.dubbo;

import com.panda.merge.api.IOddsHistoryDataProcessApi;
import com.panda.merge.model.ThirdSportMarketOdds;
import com.panda.merge.service.ThirdSportMarketOddsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@DubboService
public class OddsHistoryDataProcessApiImpl implements IOddsHistoryDataProcessApi {
    @Autowired
    private ThirdSportMarketOddsService thirdSportMarketOddsService;
    @Override
    public void OddsHistoryDataProcess(List<ThirdSportMarketOdds> thirdSportMarketOdds) {
        System.out.println("---------------------"+thirdSportMarketOdds.size());
        thirdSportMarketOddsService.insert(thirdSportMarketOdds);
    }
}
