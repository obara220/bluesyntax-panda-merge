package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @Description  : 接收赛程参数
 * @author       :  idol
 * @Date:  2020年07月16日 下午20:01:27
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class SaleUpdateLiveBusinessEventMessage implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
     * 标准赛事id
     */
    private Long matchId;

    /**
     * 修改前的商业数据源或者状态源
     */
    private String businessEventCodeOld;

    /**
     * 修改后的商业事件源或者状态源
     */
    private String businessEventCode;
    /**
     * 赛种类型
     */
    private  Long sportId;

    /**
     * 触发自动切换事件源的事件ID
     */
    private  Long businessEventId;

}
