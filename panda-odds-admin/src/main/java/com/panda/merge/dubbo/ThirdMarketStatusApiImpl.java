package com.panda.merge.dubbo;

import com.panda.merge.api.IThirdMarketStatusApi;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.model.ThirdMarketCategory;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdOutrightMatchInfo;
import com.panda.merge.model.ThirdSportMarket;
import com.panda.merge.rocketmq.producer.StandardMarketOddsProducer;
import com.panda.merge.service.ThirdMarketCategoryService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.service.ThirdOutrightMatchInfoService;
import com.panda.merge.service.ThirdSportMarketNewService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author raulvii
 */
@Slf4j
@Component
@DubboService
public class ThirdMarketStatusApiImpl implements IThirdMarketStatusApi {

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ThirdSportMarketNewService thirdSportMarketService;
    @Autowired
    private ThirdMarketCategoryService thirdMarketCategoryService;
    @Lazy
    @Autowired
    private StandardMarketOddsProducer standardMarketOddsProducer;
    @Autowired
    private ThirdOutrightMatchInfoService thirdOutrightMatchInfoService;

    /**
     * 盘口异常处理，下发关盘
     **/
    @Override
    public Response putThirdMarketStatus(String thirdMarketSourceId) {
        String linkId = String.format("%s_marketClose", IdWorker.get32UUID().toLowerCase());
        log.info("::{}::紧急关盘处理开始,thirdMarketSourceId={}", linkId, thirdMarketSourceId);
        try {
            Long thirdSportId = 0L;
            String thirdMatchSourceId = "";
            ThirdSportMarket thirdSportMarket = thirdSportMarketService.getItem(thirdMarketSourceId);
            if (null == thirdSportMarket || thirdSportMarket.getMatchId() == null) {
                return Response.failed(String.format("::linkId=%s::三方盘口源id=%s,对应的盘口数据不存在", linkId, thirdMarketSourceId));
            }
            if (Constant.SPORT_MARKET.MARKET_TYPE.OUTRIGHT_BUSINESS.equals(thirdSportMarket.getMarketType())) {
                ThirdOutrightMatchInfo thirdOutrightMatchInfo = thirdOutrightMatchInfoService.getItem(thirdSportMarket.getMatchId(), thirdSportMarket.getDataSourceCode());
                if (null == thirdOutrightMatchInfo) {
                    return Response.failed(String.format("::linkId=%s::三方赛事id=%d,三方冠军赛事不存在", linkId, thirdSportMarket.getMatchId()));
                }
                thirdSportId = thirdOutrightMatchInfo.getSportId();
                thirdMatchSourceId = thirdOutrightMatchInfo.getThirdOutrightSourceId();

            } else {
                ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItemByPrimaryKey(thirdSportMarket.getMatchId());
                if (null == thirdMatchInfo) {
                    return Response.failed(String.format("::linkId=%s::三方赛事id=%d,三方赛事不存在", linkId, thirdSportMarket.getMatchId()));
                }
                thirdSportId = thirdMatchInfo.getSportId();
                thirdMatchSourceId = thirdMatchInfo.getThirdMatchSourceId();
            }
            List<ThirdMarketCategory> marketCategoryList = thirdMarketCategoryService.getItem(thirdSportMarket.getDataSourceCode(), thirdSportMarket.getMarketCategoryId());
            //DTO封装
            ThirdMatchMarketDTO thirdMatchMarketDTO = new ThirdMatchMarketDTO();
            thirdMatchMarketDTO.setDataSourceCode(thirdSportMarket.getDataSourceCode());
            thirdMatchMarketDTO.setThirdTournamentSourceId((thirdSportMarket.getTournamentId() == null) ? "" : thirdSportMarket.getTournamentId().toString());
            thirdMatchMarketDTO.setSportId(thirdSportId);
            thirdMatchMarketDTO.setThirdMatchSourceId(thirdMatchSourceId);
            thirdMatchMarketDTO.setModifyTime(System.currentTimeMillis());

            List<ThirdMarketDTO> marketList = new ArrayList<>();
            ThirdMarketDTO thirdMarketDTO = new ThirdMarketDTO();
            BeanUtils.copyProperties(thirdSportMarket, thirdMarketDTO);
            thirdMarketDTO.setStatus(2);
            thirdMarketDTO.setSportId(thirdSportId);
            thirdMarketDTO.setThirdMarketCategorySourceId(marketCategoryList.get(0).getThirdSourceId());
            thirdMarketDTO.setMarketOddsList(new ArrayList<>());
            marketList.add(thirdMarketDTO);
            thirdMatchMarketDTO.setMarketList(marketList);
            standardMarketOddsProducer.closeMarket(linkId, thirdMatchMarketDTO);
        } catch (Exception e) {
            log.error("::{}::putThirdMarketStatus异常, error={}", linkId, e);
            return Response.failed(String.format("::linkId=::%s, error=%s", linkId, e));
        }
        return Response.success(linkId);
    }
}
