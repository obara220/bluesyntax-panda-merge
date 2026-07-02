package com.panda.merge.config.listener.base;

/**
 * @author Hunta
 * @since 9/12/2023
 */
public interface NacosListener {
    String nacosKeyToListen();
    void onChange(NacosChangeEvent nacosChangeEvent);
}
