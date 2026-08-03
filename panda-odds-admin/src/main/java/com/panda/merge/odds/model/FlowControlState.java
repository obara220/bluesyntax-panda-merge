package com.panda.merge.odds.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * FlowControlState
 *
 * @description:
 * @date: 7/17/2025
 **/
@NoArgsConstructor
@AllArgsConstructor
public class FlowControlState {

    public static  FlowControlState DISABLED = new FlowControlState();
    public int status = 1;

    public int stage = -1;

    public Set<Long> fcMatchIds;

    public FlowControlState(int stage, Set<Long > fcMatchIds) {
        this.stage = stage;
        this.fcMatchIds = fcMatchIds;
        this.status = 0;
    }

    public boolean isMatchFilterDisabled() {
        return this == DISABLED || status == 1 || stage <=0 || CollectionUtils.isEmpty(fcMatchIds) ;
    }
}
