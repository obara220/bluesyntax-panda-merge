package com.panda.merge.service.impl;


import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.mapper.VirtualBatchMatchRelationMapper;
import com.panda.merge.mapper.VirtualBatchNoMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.VirtualBatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class VirtualBatchServiceImpl implements VirtualBatchService {

    @Autowired
    VirtualBatchNoMapper virtualBatchNoMapper;
    @Autowired
    VirtualBatchMatchRelationMapper virtualBatchMatchRelationMapper;

    @Override
    public void setBatchNo(ThirdMatchInfo thirdMatchInfo,String batchNo){
        //1.根据联赛ID和批次号查询批次表是否存在
        VirtualBatchNoExample example = new VirtualBatchNoExample();
        example.createCriteria().andBatchNoEqualTo(batchNo).andThirdTournamentIdEqualTo(thirdMatchInfo.getTournamentId());
        List<VirtualBatchNo> virtualBatchNos= virtualBatchNoMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(virtualBatchNos)){
            //生成新的批次
            VirtualBatchNo virtualBatchNo = new VirtualBatchNo();
            virtualBatchNo.setId(UUIdUtils.getId());
            virtualBatchNo.setThirdTournamentId(thirdMatchInfo.getTournamentId());
            virtualBatchNo.setBetEndTime(thirdMatchInfo.getBeginTime());
            virtualBatchNo.setSportId(thirdMatchInfo.getSportId());
            virtualBatchNo.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
            virtualBatchNo.setBatchNo(batchNo);
            virtualBatchNo.setBetStartTime(TimeUtils.millsSecondsEast8ZoneGmt());
            virtualBatchNo.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            virtualBatchNo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            virtualBatchNoMapper.insertSelective(virtualBatchNo);
            //关联批次号
            this.setRelation(virtualBatchNo,thirdMatchInfo);
        }else {
            //关联批次号
            this.setRelation(virtualBatchNos.get(0),thirdMatchInfo);
        }

    }

    private void setRelation(VirtualBatchNo virtualBatchNo, ThirdMatchInfo thirdMatchInfo) {
        VirtualBatchMatchRelationExample example = new VirtualBatchMatchRelationExample();
        example.createCriteria().andVirtualBatchNoIdEqualTo(virtualBatchNo.getId()).andThirdMatchIdEqualTo(thirdMatchInfo.getId());
        List<VirtualBatchMatchRelation> virtualRelations = virtualBatchMatchRelationMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(virtualRelations)){
            return;
        }
        VirtualBatchMatchRelation virtualBatchMatchRelation = new VirtualBatchMatchRelation();
        virtualBatchMatchRelation.setId(UUIdUtils.getId());
        virtualBatchMatchRelation.setThirdMatchId(thirdMatchInfo.getId());
        virtualBatchMatchRelation.setVirtualBatchNoId(virtualBatchNo.getId());
        virtualBatchMatchRelation.setSportId(thirdMatchInfo.getSportId());
        virtualBatchMatchRelation.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        virtualBatchMatchRelation.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        virtualBatchMatchRelationMapper.insert(virtualBatchMatchRelation);
    }

}
