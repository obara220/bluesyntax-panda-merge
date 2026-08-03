package com.panda.merge.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @Description  :  赛事监控DTO
 * @author       :  damian
 * @Date:  2022年06月12日 下午16:34:06
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchMonitorDTO implements Serializable {
	 
	private static final long serialVersionUID = 5188624964566856388L;


    //标准赛事id
    private Long matchId;

    //标准玩法id
    private Long categoryId;

    //操作人ID
    private Long operaterId;

	@Override
	public String toString() {
		return "MatchMonitorDTO [matchId=" + matchId + ", categoryId=" + categoryId + ", operaterId="
				+ operaterId + "]";
	}


}
