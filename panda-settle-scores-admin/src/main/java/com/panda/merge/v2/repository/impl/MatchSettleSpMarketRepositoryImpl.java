package com.panda.merge.v2.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.model.MatchSettleSpMarketExample;
import com.panda.merge.v2.entity.MatchSettleSpMarketEntity;
import com.panda.merge.v2.mapper.MatchSettleSpMarketV2Mapper;
import com.panda.merge.v2.repository.MatchSettleSpMarketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchSettleSpMarketRepositoryImpl extends ServiceImpl<MatchSettleSpMarketV2Mapper, MatchSettleSpMarketEntity> implements MatchSettleSpMarketRepository {

    @Autowired
    private MatchSettleSpMarketV2Mapper matchSettleSpMarketV2Mapper;

    @Override
    public List<MatchSettleSpMarketEntity> selectByExample(MatchSettleSpMarketExample example) {
        return matchSettleSpMarketV2Mapper.selectByExample(example);
    }


}
