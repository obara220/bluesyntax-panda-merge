package com.panda.merge.respository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.data.mapper.MatchSettleScoreV2Mapper;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.respository.MatchSettleScoreRepository;
import org.springframework.stereotype.Repository;

@Repository("MatchSettleScoreRepositoryImplV1")
public class MatchSettleScoreRepositoryImpl extends ServiceImpl<MatchSettleScoreV2Mapper, MatchSettleScore> implements MatchSettleScoreRepository {
}
