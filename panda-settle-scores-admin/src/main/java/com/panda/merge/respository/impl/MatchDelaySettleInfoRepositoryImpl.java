package com.panda.merge.respository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.data.mapper.MatchDelaySettleInfoV2Mapper;
import com.panda.merge.model.MatchDelaySettleInfo;
import com.panda.merge.respository.MatchDelaySettleInfoRepository;
import org.springframework.stereotype.Repository;

@Repository("MatchDelaySettleInfoRepositoryImplV1")
public class MatchDelaySettleInfoRepositoryImpl extends ServiceImpl<MatchDelaySettleInfoV2Mapper, MatchDelaySettleInfo> implements MatchDelaySettleInfoRepository {
}
