package com.panda.merge.service.impl;


import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.MatchStatisticsInfoDetailDTO;
import com.panda.merge.mapper.MatchStatisticsInfoDetailMapper;
import com.panda.merge.model.MatchStatisticsInfoDetail;
import com.panda.merge.model.MatchStatisticsInfoDetailExample;
import com.panda.merge.service.MatchStatisticsInfoDetailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/10 <br>
 * @see com.panda.merge.service.impl <br>
 */
@Service
public class MatchStatisticsInfoDetailServiceImpl implements MatchStatisticsInfoDetailService {

    @Autowired
    private MatchStatisticsInfoDetailMapper matchStatisticsInfoDetailMapper;

    @Override
    public List<MatchStatisticsInfoDetail> getItemList(Long matchStatisticsInfoId) {
        MatchStatisticsInfoDetailExample query = new MatchStatisticsInfoDetailExample();
        query.createCriteria().andMatchStatisticsInfoIdEqualTo(matchStatisticsInfoId);
        return matchStatisticsInfoDetailMapper.selectByExample(query);
    }

    @Override
    public MatchStatisticsInfoDetail create(MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO,Long matchStatisticsInfoId) {
        MatchStatisticsInfoDetail msDetail = new MatchStatisticsInfoDetail();
        msDetail.setId(UUIdUtils.getId());
        BeanUtils.copyProperties(matchStatisticsInfoDetailDTO ,msDetail);
        msDetail.setMatchStatisticsInfoId(matchStatisticsInfoId);
        msDetail.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        matchStatisticsInfoDetailMapper.insertSelective(msDetail);
        return msDetail;
    }

    @Override
    public MatchStatisticsInfoDetail update(MatchStatisticsInfoDetail matchStatisticsInfoDetail) {
        matchStatisticsInfoDetail.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        matchStatisticsInfoDetailMapper.updateByPrimaryKey(matchStatisticsInfoDetail);
        return matchStatisticsInfoDetail;
    }
}
