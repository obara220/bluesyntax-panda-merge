package com.panda.merge.respository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.data.mapper.MatchSettleGrayWeightV2Mapper;
import com.panda.merge.model.MatchSettleGrayWeight;
import com.panda.merge.respository.MatchSettleGrayWeightRepository;
import org.springframework.stereotype.Repository;

@Repository("MatchSettleGrayWeightRepositoryImplV1")
public class MatchSettleGrayWeightRepositoryImpl extends ServiceImpl<MatchSettleGrayWeightV2Mapper, MatchSettleGrayWeight> implements MatchSettleGrayWeightRepository {
}
