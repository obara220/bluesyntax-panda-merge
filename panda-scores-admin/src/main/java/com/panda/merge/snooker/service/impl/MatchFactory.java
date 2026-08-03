package com.panda.merge.snooker.service.impl;

import com.panda.merge.snooker.service.IMatchCommonProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MatchFactory {

    private final List<AbsMatchCommonProcessor> matchCommonProcessors;

    /**
     * 优先返回专属球种 Processor（snooker/volleyball 等），找不到再回退到 {@link DefaultMatchCommonProcessor}。
     *
     * 必须显式排除 default 再选具体球种：所有具体处理器与 default 的 @Order 都为 MAX_VALUE，
     * 单纯 stream.filter(support).findAny() 的命中顺序不稳定，
     * 可能让 default.support()=true 抢先返回，导致 sportId=9（排球）也走到“当前球种未实现”兜底报错。
     */
    public IMatchCommonProcessor getProcessor(Long sportId) {
        AbsMatchCommonProcessor specific = matchCommonProcessors.stream()
                .filter(p -> !(p instanceof DefaultMatchCommonProcessor))
                .filter(p -> p.support(sportId))
                .findFirst()
                .orElse(null);
        if (specific != null) {
            return specific;
        }
        return matchCommonProcessors.stream()
                .filter(p -> p instanceof DefaultMatchCommonProcessor)
                .findFirst()
                .orElse(null);
    }

}
