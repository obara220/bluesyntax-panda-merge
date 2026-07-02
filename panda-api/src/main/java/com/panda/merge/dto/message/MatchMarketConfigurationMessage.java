package com.panda.merge.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.dto
 * @Description :  TODO
 * @Date: 2020-09-17 13:17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchMarketConfigurationMessage implements Serializable {

    /**
     * 赛事id
     */
    @NotNull(message = "标准赛事ID不能为null")
    private Long standardMatchId;
    /**
     * 盘口类型1：早盘；0：滚球
     */
    @NotNull(message = "盘口类型不能为空")
    @JsonProperty("matchType")
    private Integer marketType;
    /**
     * SR权重
     */
    private Integer srWeight;
    /**
     * BC权重
     */
    private Integer bcWeight;
    /**
     * BG权重
     */
    private Integer bgWeight;
    /**
     * BG权重
     */
    private Integer txWeight;
    /**
     * 比分源1:SR(LiveData)  2:UOF    注意：比分源还有为null的情况，需适配
     */
    private Integer scoreSource;

    /**
     * 事件审核时间配置信息
     */
    private List<MatchMarketEventConfigurationMessage> templateEventList;
}
