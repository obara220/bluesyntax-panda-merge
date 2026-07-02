package com.panda.merge.dto.message;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author :  idol
 * @Project Name :  data-realtime
 * @Description :  接收完赛参数
 * @Date: 2020-12-26 12:00
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@Accessors(chain = true)
public class ChangeMatchOverMessage implements Serializable {

    /**
     * 标准赛事id
     */
    private Long matchId;

    /**
     * 完赛标识    0 正常状态（未完赛）   1 完赛
     */
    private Integer matchOver;

    /**
     * 运动种类id
     */
    private Long sportId;

    /**
     * linkId
     */
    private String linkId;
}
