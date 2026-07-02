package com.panda.merge.utils;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author :  dorich
 * @project Name :  panda_data_service
 * @package Name :  com.panda.sports.manager.utils
 * @description :  TODO
 * @date: 2019-10-15 14:54
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public class ApplicationContextHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public ApplicationContextHelper() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHelper.applicationContext = applicationContext;
    }

    public static Object getBean(String beanName) {
        return applicationContext != null ? applicationContext.getBean(beanName) : null;
    }

    public static Object getBean(Class beanName) {
        return applicationContext != null ? applicationContext.getBean(beanName) : null;
    }
    public static void  setBean(Object object) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(object);
    }
}
