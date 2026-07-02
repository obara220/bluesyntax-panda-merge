package com.panda.merge.respository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.data.entity.MatchSettleEventEntity;
import com.panda.merge.data.mapper.MatchSettleEventV2Mapper;
import com.panda.merge.respository.MatchSettleEventRepository;
import org.springframework.stereotype.Repository;

@Repository("MatchSettleEventRepositoryImplV1")
public class MatchSettleEventRepositoryImpl extends ServiceImpl<MatchSettleEventV2Mapper, MatchSettleEventEntity> implements MatchSettleEventRepository {
}
