package com.panda.merge.respository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.data.mapper.MatchSettleRollBackInfoBatchV2Mapper;
import com.panda.merge.model.MatchSettleRollBackInfo;
import com.panda.merge.respository.MatchSettleRollBackInfoBatchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MatchSettleRollBackInfoBatchRepositoryImpl extends ServiceImpl<MatchSettleRollBackInfoBatchV2Mapper, MatchSettleRollBackInfo> implements MatchSettleRollBackInfoBatchRepository {
}
