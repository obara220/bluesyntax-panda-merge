package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 *
 */
@Data
public class ReplayMatchDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "标准赛事ID不能为空")
    private Long standardMatchId;

    @NotNull(message = "重播开赛时间不能为空")
    private Long replayBeginTime;

    @NotNull(message = "重播赛事ID不能为空")
    private List<Long> replayStandardMatchIds;

}
