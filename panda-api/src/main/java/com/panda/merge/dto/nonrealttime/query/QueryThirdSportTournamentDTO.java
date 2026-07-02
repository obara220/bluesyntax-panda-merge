package com.panda.merge.dto.nonrealttime.query;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 查询三方联赛信息参数类
 * @author   tell
 * @since    2020年10月17日16:12:22
 */
@Data
public class QueryThirdSportTournamentDTO implements Serializable {

    @NotNull(message = "标准联赛ID不能为空")
    private List<Long> standardTournamentIds;

    private Long sportId;

    @NotNull(message = "数据来源不能为空")
    private String dataSourceCode;

}
