package com.panda.merge.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.dao.ThirdMatchFrontStatisticsDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.mapper.ThirdMatchFrontStatisticsMapper;
import com.panda.merge.model.ThirdMatchFrontStatistics;
import com.panda.merge.model.ThirdMatchFrontStatisticsExample;
import com.panda.merge.service.ThirdMatchFrontStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 三方赛事正面交手数据
 * @author      tell
 * @since       2021年4月23日13:45:23
 */
@Service
public class ThirdMatchFrontStatisticsServiceImpl extends BaseServiceImpl<ThirdMatchFrontStatistics> implements ThirdMatchFrontStatisticsService {

    @Autowired
    private ThirdMatchFrontStatisticsMapper thirdMatchFrontStatisticsMapper;

    @Autowired
    private ThirdMatchFrontStatisticsDao thirdMatchFrontStatisticsDao;


    @Override
    public Page<ThirdMatchFrontStatistics> getFrontStatisticsPageByModifyTime(PageModel<ThirdMatchInfoDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdMatchFrontStatisticsDao.getFrontStatisticsPageByModifyTime(page.getData());
    }

    @Override
    public int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchFrontStatisticsExample example){
        ThirdMatchFrontStatistics item = new ThirdMatchFrontStatistics();
        item.setModifyTime(modifyTime);
        return thirdMatchFrontStatisticsMapper.updateByExampleSelective(item,example);
    }

    @Override
    public void delItemById(String id) {
        thirdMatchFrontStatisticsMapper.deleteByPrimaryKey(id);
    }

}
