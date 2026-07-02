package com.panda.merge.v2.repository.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.dto.Response;
import com.panda.merge.model.MatchSettleSpOdds;
import com.panda.merge.model.MatchSettleSpOddsExample;
import com.panda.merge.v2.entity.MatchSettleSpOddsEntity;
import com.panda.merge.v2.mapper.MatchSettleSpOddsV2Mapper;
import com.panda.merge.v2.repository.MatchSettleSpOddsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.CompositeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MatchSettleSpOddsRepositoryImpl extends ServiceImpl<MatchSettleSpOddsV2Mapper, MatchSettleSpOddsEntity> implements MatchSettleSpOddsRepository {

    @Autowired
    private MatchSettleSpOddsV2Mapper matchSettleSpOddsV2Mapper;

    @Override
    public List<MatchSettleSpOddsEntity> selectByExample(MatchSettleSpOddsExample example) {
        return matchSettleSpOddsV2Mapper.selectByExample(example);
    }

    @Override
    public Map<Long, List<MatchSettleSpOddsEntity>> toMap(List<Long> markIdList, Long standardMatchId) {
        LambdaQueryWrapper<MatchSettleSpOddsEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .in(MatchSettleSpOddsEntity::getMarketId, markIdList)
                .eq(MatchSettleSpOddsEntity::getStandardMatchId, standardMatchId);
        List<MatchSettleSpOddsEntity> oddsList = list(queryWrapper);

        if (CollectionUtils.isEmpty(oddsList)){
            log.warn("searchSPMarketSettleList方法中未找到matchSettleSpOddsEntity数据,查询条件:{}", JSONUtil.toJsonStr(queryWrapper));
            return null;
        }

        return oddsList.stream().collect(Collectors.groupingBy(MatchSettleSpOddsEntity::getMarketId));
    }

}
