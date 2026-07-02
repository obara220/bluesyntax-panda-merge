package com.panda.merge.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Description  :  数据接入格式：赛事盘口
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class ThirdMatchMarketDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
     * 运动种类id。 对应sport.id
     * 如果玩法不区分体育类型，传0，否则传对应体育类型标识
     */
    @NotNull(message="赛种不能为空")
    private Long sportId;
    
    /**
     * 第三方比赛原始ID
     */
    @NotNull(message="三方赛事源id不能为空")
    private String thirdMatchSourceId;
 
    /**
     *  第三方联赛原始ID
     */
    private String thirdTournamentSourceId;
    
    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    @NotNull(message="数据源不能为空")
    private String dataSourceCode;
    
    /**
     * 上游时间戳
     */
    private Long modifyTime;

	/**
	 * 盘口列表
	 */
    @Valid
    @NotNull(message="盘口列表不能为空")
	private List<ThirdMarketDTO> marketList;


}
