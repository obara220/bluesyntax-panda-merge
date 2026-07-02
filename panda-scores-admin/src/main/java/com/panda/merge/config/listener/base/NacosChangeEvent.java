package com.panda.merge.config.listener.base;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Hunta
 * @since 9/12/2023
 */

@Data
@AllArgsConstructor
public class NacosChangeEvent {
    private String valueBefore;
    private String valueAfter;
}

