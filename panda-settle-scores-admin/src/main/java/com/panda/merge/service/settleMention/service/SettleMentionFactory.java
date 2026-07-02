package com.panda.merge.service.settleMention.service;

import com.panda.merge.constant.SettleMentionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;



/**
 * @description: settle mention factory
 * @author: Henry Wang
 * @create: 2024-08-28 17:26
 **/

@Component
public class SettleMentionFactory {

    @Resource
    private List<AbstractSettleMentionProcessor> abstractSettleMentionProcessors;

    public ISettleMentionProcessor getProcessor(SettleMentionEnum settleMentionEnum) {
        return abstractSettleMentionProcessors.stream().filter(t->t.support(settleMentionEnum)).findAny().orElse(null);
    }

}
