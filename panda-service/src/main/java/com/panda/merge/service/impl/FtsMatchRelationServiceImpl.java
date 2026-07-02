package com.panda.merge.service.impl;

import com.panda.merge.dao.FtsMatchRelationDao;
import com.panda.merge.model.FtsMatchRelation;
import com.panda.merge.service.FtsMatchRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author aldrich
 * 2023/12/17 18:59
 * 范特西赛事与旧赛事关联关系
 */
@Service
public class FtsMatchRelationServiceImpl implements FtsMatchRelationService {

    @Autowired
    private FtsMatchRelationDao ftsMatchRelationDao;

    @Override
    public List<FtsMatchRelation> getFtsMatchRelation(Long matchId) {
        FtsMatchRelation ftsMatchRelation = new FtsMatchRelation();
        ftsMatchRelation.setNewMatchId(matchId);
        return ftsMatchRelationDao.getFtsMatchRelation(ftsMatchRelation);
    }
}
