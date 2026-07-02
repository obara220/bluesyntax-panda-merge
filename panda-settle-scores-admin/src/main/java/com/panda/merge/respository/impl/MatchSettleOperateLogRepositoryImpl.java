package com.panda.merge.respository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.data.mapper.MatchSettleOperateLogV2Mapper;
import com.panda.merge.model.MatchSettleOperateLog;
import com.panda.merge.respository.MatchSettleOperateLogRepository;
import org.springframework.stereotype.Repository;

@Repository("MatchSettleOperateLogRepositoryImplV1")
public class MatchSettleOperateLogRepositoryImpl extends ServiceImpl<MatchSettleOperateLogV2Mapper, MatchSettleOperateLog> implements MatchSettleOperateLogRepository {
}
