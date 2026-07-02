package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleSpMarket;
import com.panda.merge.model.MatchSettleSpMarketExample;
import com.panda.merge.v2.entity.MatchSettleSpMarketEntity;

import java.util.List;

public interface MatchSettleSpMarketRepository extends IService<MatchSettleSpMarketEntity> {

    List<MatchSettleSpMarketEntity> selectByExample(MatchSettleSpMarketExample example);

}
