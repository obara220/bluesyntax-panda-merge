package com.panda.merge.service.impl;

import com.panda.merge.mapper.StandardMatchTeamRelationMapper;
import com.panda.merge.model.StandardMatchTeamRelation;
import com.panda.merge.model.StandardMatchTeamRelationExample;
import com.panda.merge.service.StandardMatchTeamRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <Description> 三方赛事球队关联关系信息
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
@Service
public class StandardMatchTeamRelationServiceImpl implements StandardMatchTeamRelationService {

    @Autowired
    private StandardMatchTeamRelationMapper standardMatchTeamRelationMapper;

    @Override
    public Map<String,StandardMatchTeamRelation> getPosition2ItemByStandardMatchId(Long standardMatchId){
        StandardMatchTeamRelationExample example = new StandardMatchTeamRelationExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        List<StandardMatchTeamRelation> resList = standardMatchTeamRelationMapper.selectByExample(example);
        return resList.stream().collect(Collectors.toMap(thi->thi.getMatchPosition().toLowerCase(), i -> i));
    }

    @Override
    public StandardMatchTeamRelation updateItem(StandardMatchTeamRelation upItem){
        standardMatchTeamRelationMapper.updateByPrimaryKeySelective(upItem);
        return upItem;
    }


}
