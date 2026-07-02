package com.panda.merge.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.dao.ThirdMatchExInfomationDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.mapper.ThirdMatchExInfomationMapper;
import com.panda.merge.model.ThirdMatchExInfomation;
import com.panda.merge.model.ThirdMatchExInfomationExample;
import com.panda.merge.model.ThirdMatchLineup;
import com.panda.merge.service.ThirdMatchExInfomationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 三方赛事比赛情报综合资讯数据
 * @author      tell
 * @since       2021年4月23日13:45:23
 */
@Service
public class ThirdMatchExInfomationServiceImpl extends BaseServiceImpl<ThirdMatchLineup> implements ThirdMatchExInfomationService {

    @Autowired
    private ThirdMatchExInfomationMapper thirdMatchExInfomationMapper;

    @Autowired
    private ThirdMatchExInfomationDao thirdMatchExInfomationDao;


    @Override
    public Page<ThirdMatchExInfomation> getItemPageByModifyTime(PageModel<ThirdMatchInfoDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdMatchExInfomationDao.getItemPageByModifyTime(page.getData());
    }

    @Override
    public int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchExInfomationExample example){
        ThirdMatchExInfomation item = new ThirdMatchExInfomation();
        item.setModifyTime(modifyTime);
        return thirdMatchExInfomationMapper.updateByExampleSelective(item,example);
    }

    @Override
    public void delItemById(String id) {
        thirdMatchExInfomationMapper.deleteByPrimaryKey(id);
    }

}
