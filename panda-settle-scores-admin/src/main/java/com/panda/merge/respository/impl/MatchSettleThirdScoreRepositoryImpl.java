package com.panda.merge.respository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.data.mapper.MatchSettleThirdScoreV2Mapper;
import com.panda.merge.model.MatchSettleThirdScore;
import com.panda.merge.respository.MatchSettleThirdScoreRepository;
import org.springframework.stereotype.Repository;

@Repository("MatchSettleThirdScoreRepositoryImplV1")
public class MatchSettleThirdScoreRepositoryImpl extends ServiceImpl<MatchSettleThirdScoreV2Mapper, MatchSettleThirdScore> implements MatchSettleThirdScoreRepository {
}
