package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PlaySetStatusConfigDTO  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 赛种
     */
    private Long sportId;
    /**
     * 赛事ID
     */
    private Long matchId;
    /**
     * 玩法集编码
     */
    private String playSetCode;
    /**
     * 玩法集状态
     */
    private Integer status;
    /**
     * 玩法集下包含的玩法
     */
    private List<Long> playIds;

}
