package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @Description  : 接收操盘非常规结束通知
 * @author       :  idol
 * @Date:  2020年07月16日 下午20:01:27
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchOverMessage implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
     * 标准赛事id
     */
    private Long matchId;

    /**
     * isEnd 1接受 0拒绝
     */
    private String isEnd;


}
