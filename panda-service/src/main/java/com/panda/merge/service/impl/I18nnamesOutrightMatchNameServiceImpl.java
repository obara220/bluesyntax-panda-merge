package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.I18nnamesOutrightMatchNameDao;
import com.panda.merge.mapper.I18nnamesOutrightMatchNameMapper;
import com.panda.merge.model.I18nnamesOutrightMatchName;
import com.panda.merge.model.I18nnamesOutrightMatchNameExample;
import com.panda.merge.service.I18nnamesOutrightMatchNameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 三方赛事，标准赛事，投注项多语言
 * @author : bevan
 *  @since    2020年9月16日17:37:59
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class I18nnamesOutrightMatchNameServiceImpl extends BaseServiceImpl<I18nnamesOutrightMatchName> implements I18nnamesOutrightMatchNameService {
    @Autowired
    private I18nnamesOutrightMatchNameMapper i18nnamesOutrightMatchNameMapper;

    @Autowired
    private I18nnamesOutrightMatchNameDao i18nnamesOutrightMatchNameDao;

    @Override
    @Cacheable(key = "'I18nnamesOutrightMatchNameMap:' +#matchCategoryFiled", unless = "#result == null || #result.size() == 0")
    public Map<String, I18nnamesOutrightMatchName> getLanguageType2Item(String dataSourceCode, Long matchCategoryFiled) {
        I18nnamesOutrightMatchNameExample example = new I18nnamesOutrightMatchNameExample();
        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andMatchCategoryFiledEqualTo(matchCategoryFiled);
        List<I18nnamesOutrightMatchName> list = i18nnamesOutrightMatchNameMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.stream().collect(Collectors.toMap(I18nnamesOutrightMatchName::getLanguageType, i -> i));
    }

    @Override
    public void saveOrupdateList(List<I18nnamesOutrightMatchName> list) {
        /** 根据创建时间来区分新增或修改（创建时间不为空是为新增）*/
        List<I18nnamesOutrightMatchName> addList = list.stream().filter(obj -> Objects.isNull(obj.getId())).collect(Collectors.toList());
        List<I18nnamesOutrightMatchName> updateList = list.stream().filter(obj -> !Objects.isNull(obj.getId())).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(addList)){
            i18nnamesOutrightMatchNameDao.insertList(addList);
            Set<Long> matchCategoryFileds = addList.stream().map(obj -> obj.getMatchCategoryFiled()).collect(Collectors.toSet());
            //删除缓存
            for (Long matchCategoryFiled: matchCategoryFileds) {
                redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::I18nnamesOutrightMatchNameMap:"+ matchCategoryFiled);
            }
        }
        if(!CollectionUtils.isEmpty(updateList)){
            i18nnamesOutrightMatchNameDao.updateList(updateList);
            //刷新缓存
            for (I18nnamesOutrightMatchName item: updateList) {
                refreshHashCache(RedisConfig.REDIS_KEY_DATABASE + "::I18nnamesOutrightMatchNameMap:"+ item.getMatchCategoryFiled(),item.getLanguageType(),item);
            }
        }

    }

}
