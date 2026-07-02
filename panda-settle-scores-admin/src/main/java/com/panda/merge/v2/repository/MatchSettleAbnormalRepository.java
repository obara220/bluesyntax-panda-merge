package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleAbnormalExample;
import com.panda.merge.v2.entity.MatchSettleAbnormalEntity;

import java.util.List;

public interface MatchSettleAbnormalRepository extends IService<MatchSettleAbnormalEntity> {

    List<MatchSettleAbnormalEntity> selectByExample(MatchSettleAbnormalExample example);

}
