package com.panda.merge.respository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.data.entity.MatchSettleCheckInfoEntity;
import com.panda.merge.data.mapper.MatchSettleCheckInfoV2Mapper;
import com.panda.merge.respository.MatchSettleCheckInfoRepository;
import org.springframework.stereotype.Repository;

@Repository("MatchSettleCheckInfoRepositoryImplV1")
public class MatchSettleCheckInfoRepositoryImpl extends ServiceImpl<MatchSettleCheckInfoV2Mapper, MatchSettleCheckInfoEntity> implements MatchSettleCheckInfoRepository {
}
