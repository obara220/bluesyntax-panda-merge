package com.panda.merge.dto.message;


import lombok.Data;


/**
 * <p>
 * 赛事结算表
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-02-13
 */
@Data

public class MatchSettleInfoMessage {

    private static final long serialVersionUID = 1L;

    private String linkId;

    /**
     * 运动种类id. 联赛所属体育种类id， 对应 sport.id
     */
    private Long sportId;

    /**
     * 赛事id
     */
    private Long matchId;

    /**
     * 结算版本  1结算1.0，2结算2.0
     */
    private Integer type;

    public MatchSettleInfoMessage(String linkId, Long sportId, Long matchId, Integer type) {
        this.linkId = linkId;
        this.sportId = sportId;
        this.matchId = matchId;
        this.type = type;
    }
}
