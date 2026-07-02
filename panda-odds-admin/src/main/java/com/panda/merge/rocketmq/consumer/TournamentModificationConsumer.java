package com.panda.merge.rocketmq.consumer;


import com.panda.merge.bo.StandardSportTournamentBO;
import com.panda.merge.dto.Request;
import com.panda.merge.service.StandardSportTournamentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.PAND_ODDS_GROUP;
import static com.panda.merge.constant.ConstantSystem.TOPIC_TOURNAMENT_MODIFICATION;

/**
 * TournamentModificationConsumer
 *
 * @description: 联赛更新
 * @date: 1/24/2025
 **/
@Slf4j
@Component
@RocketMQMessageListener(topic = TOPIC_TOURNAMENT_MODIFICATION,
        consumerGroup = PAND_ODDS_GROUP + TOPIC_TOURNAMENT_MODIFICATION)
public class TournamentModificationConsumer implements RocketMQListener<Request<List<StandardSportTournamentBO>>> {


    @Autowired
    private StandardSportTournamentService tournamentService;

    @Override
    public void onMessage(Request<List<StandardSportTournamentBO>> request) {

        log.info("{} tournament modification request {}", request.getLinkId(), request);
        List<StandardSportTournamentBO> data = request.getData();
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        tournamentService.evitCache(data
                                            .stream()
                                            .map(StandardSportTournamentBO::getId)
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.toList()));
    }
}
