package com.panda.merge.mq.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 3574 玩法集tMax开关配置，风控下发
 */
@Data
public class TournamentTemplateAcceptConfigScore implements Serializable {

    private String linkId;
    /**
     * 开关配置详情
     */
    private List<TournamentTemplateAcceptConfigScoreDTO> list;
    private Long matchId;


}
