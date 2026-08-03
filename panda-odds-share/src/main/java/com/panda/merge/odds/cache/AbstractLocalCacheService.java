package com.panda.merge.odds.cache;

import com.panda.merge.dto.message.LocalCacheRefreshMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AbstractLocalCacheService
 *
 * @description:
 * @date: 7/17/2025
 **/
@Service
@Slf4j
public abstract class AbstractLocalCacheService implements CacheService, BeanNameAware {

    private String beanName;

    @Autowired
    private LocalCacheRefreshProducer localCacheRefreshProducer;

    public String getBeanName() {
        return beanName;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    protected void senRefreshMessage(String key,String linkId) {
        localCacheRefreshProducer.send(new LocalCacheRefreshMessage(linkId,getBeanName(), key, null));
    }

}
