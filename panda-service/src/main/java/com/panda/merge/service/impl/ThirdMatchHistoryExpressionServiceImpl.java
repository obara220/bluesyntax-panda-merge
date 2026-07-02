package com.panda.merge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdMatchHistoryExpressionDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import com.panda.merge.mapper.ThirdMatchHistoryExpressionMapper;
import com.panda.merge.model.ThirdMatchHistoryExpression;
import com.panda.merge.model.ThirdMatchHistoryExpressionExample;
import com.panda.merge.service.ThirdMatchHistoryExpressionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 三方联赛球队历史表现数据
 * @author tell
 * @since  2020年10月18日09:01:35
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdMatchHistoryExpressionServiceImpl implements ThirdMatchHistoryExpressionService {

    @Autowired
    private ThirdMatchHistoryExpressionMapper thirdMatchHistoryExpressionMapper;

    @Autowired
    private ThirdMatchHistoryExpressionDao thirdMatchHistoryExpressionDao;

    @Override
    public Page<ThirdMatchHistoryExpression> getHistoryExpressionPageByModifyTime(PageModel<QueryThirdRankingInfoDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdMatchHistoryExpressionDao.getHistoryExpressionPageByModifyTime(page.getData());
    }

    @Override
    public List<ThirdMatchHistoryExpression> getItems(List<String> ids){
        ThirdMatchHistoryExpressionExample example = new ThirdMatchHistoryExpressionExample();
        example.createCriteria().andIdIn(ids);
        return thirdMatchHistoryExpressionMapper.selectByExample(example);
    }


    @Override
    public ThirdMatchHistoryExpression saveOrUpdate(ThirdMatchHistoryExpression upItem) {
        if (null != upItem.getCreateTime()) {
            thirdMatchHistoryExpressionMapper.insertSelective(upItem);
        } else {
            ThirdMatchHistoryExpression item = new ThirdMatchHistoryExpression();
            //三方数据源赛季ID，运动类型，三方数据源球队ID,创建时间无需修改
            BeanUtil.copyProperties(upItem,item,"thirdSourceSeasonId","sportId","thirdTeamSourceId");
            thirdMatchHistoryExpressionMapper.updateByPrimaryKeySelective(item);
        }
        return upItem;
    }

    @Override
    public int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchHistoryExpressionExample example){
        ThirdMatchHistoryExpression item = new ThirdMatchHistoryExpression();
        item.setModifyTime(modifyTime);
        return thirdMatchHistoryExpressionMapper.updateByExampleSelective(item,example);
    }

}
