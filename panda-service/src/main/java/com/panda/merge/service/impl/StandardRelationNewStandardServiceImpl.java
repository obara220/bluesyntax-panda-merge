package com.panda.merge.service.impl;

import com.panda.merge.mapper.StandardRelationNewStandardMapper;
import com.panda.merge.model.StandardRelationNewStandard;
import com.panda.merge.model.StandardRelationNewStandardExample;
import com.panda.merge.service.StandardRelationNewStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class StandardRelationNewStandardServiceImpl implements StandardRelationNewStandardService {
    @Autowired
    private StandardRelationNewStandardMapper standardRelationNewStandardMapper;
    @Override
    public StandardRelationNewStandard getItem(Long matchId) {
        StandardRelationNewStandardExample standardRelationNewStandardExample = new StandardRelationNewStandardExample();
        standardRelationNewStandardExample.createCriteria().andSourceStandardIdEqualTo(matchId);
        List<StandardRelationNewStandard> list = standardRelationNewStandardMapper.selectByExample(standardRelationNewStandardExample);
        if (!CollectionUtils.isEmpty(list))
        {
            return list.get(0);
        }
        return null;
    }

    @Override
    public StandardRelationNewStandard getItemByNewId(Long matchId) {
        StandardRelationNewStandardExample standardRelationNewStandardExample = new StandardRelationNewStandardExample();
        standardRelationNewStandardExample.createCriteria().andNewStandardIdEqualTo(matchId);
        List<StandardRelationNewStandard> list = standardRelationNewStandardMapper.selectByExample(standardRelationNewStandardExample);
        if (!CollectionUtils.isEmpty(list))
        {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<StandardRelationNewStandard> listStandardRelationNewStandard(Long sourceStandardId) {
        StandardRelationNewStandardExample standardRelationNewStandardExample = new StandardRelationNewStandardExample();
        standardRelationNewStandardExample.createCriteria().andSourceStandardIdEqualTo(sourceStandardId);
        return standardRelationNewStandardMapper.selectByExample(standardRelationNewStandardExample);
    }
}
