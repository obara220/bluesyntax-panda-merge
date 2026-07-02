package com.panda.merge.v2.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.model.MatchSettleAbnormalExample;
import com.panda.merge.v2.entity.MatchSettleAbnormalEntity;
import com.panda.merge.v2.mapper.MatchSettleAbnormalV2Mapper;
import com.panda.merge.v2.repository.MatchSettleAbnormalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MatchSettleAbnormalRepositoryImpl extends ServiceImpl<MatchSettleAbnormalV2Mapper, MatchSettleAbnormalEntity> implements MatchSettleAbnormalRepository {

    @Autowired
    private MatchSettleAbnormalV2Mapper matchSettleAbnormalV2Mapper;

    @Override
    public List<MatchSettleAbnormalEntity> selectByExample(MatchSettleAbnormalExample example) {
        return matchSettleAbnormalV2Mapper.selectByExample(example);
    }

}
