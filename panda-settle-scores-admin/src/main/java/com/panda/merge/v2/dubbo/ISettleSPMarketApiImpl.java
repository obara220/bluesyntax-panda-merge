package com.panda.merge.v2.dubbo;

import com.panda.merge.api.ISettleSPMarketApi;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.settle.EditMatchSettleSPOddsDto;
import com.panda.merge.dto.settle.SPMarketSettleListRequest;
import com.panda.merge.v2.controllerv2.MatchSettleSPMarketController;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@DubboService
@Slf4j
public class ISettleSPMarketApiImpl implements ISettleSPMarketApi {

    @Autowired
    private MatchSettleSPMarketController matchSettleSPMarketController;

    @Override
    public Response searchSPMarketSettleList(SPMarketSettleListRequest spMarketSettleListRequest) {
        return matchSettleSPMarketController.searchSPMarketSettleList(spMarketSettleListRequest);
    }

    @Override
    public Response editSpOddsResult(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto) {
        return matchSettleSPMarketController.editSpOddsResult(editMatchSettleSPOddsDto);
    }

    @Override
    public Response confirmSpOddsResult(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto) {
        return matchSettleSPMarketController.confirmSpOddsResult(editMatchSettleSPOddsDto);
    }

    @Override
    public Response settleSpOddsResult(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto) {
        return matchSettleSPMarketController.settleSpOddsResult(editMatchSettleSPOddsDto);
    }

    @Override
    public Response rollbackSpOddsResult(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto) {
        return matchSettleSPMarketController.rollbackSpOddsResult(editMatchSettleSPOddsDto);
    }

    @Override
    public Response reSettleSpOddsResult(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto) {
        return matchSettleSPMarketController.reSettleSpOddsResult(editMatchSettleSPOddsDto);
    }

}
