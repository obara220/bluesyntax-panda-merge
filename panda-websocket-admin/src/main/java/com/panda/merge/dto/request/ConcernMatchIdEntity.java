package com.panda.merge.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ConcernMatchIdEntity implements Serializable {

    /**
     * 是否为标准数据
     */
    @ApiModelProperty(name = "是否查询标准赛事", notes = "true:查询标准赛事;false:查询第三方赛事")
    private Boolean standard;

    /**
     * 关注的赛事id
     */
    @ApiModelProperty(name = "关注的赛事id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long matchId;

    /**
     * 关注的赛事id
     */
    @ApiModelProperty(name = "是否需要注意详细查询")
    @JsonFormat(shape = JsonFormat.Shape.BOOLEAN)
    private boolean attention;

    public boolean equals(ConcernMatchIdEntity obj) {
        if (this.standard.equals(obj.standard)) {
            if (this.matchId.equals(obj.getMatchId())) {
                return true;
            }
        }
        return false;
    }

    public ConcernMatchIdEntity() {

    }

    public ConcernMatchIdEntity(Boolean standard, Long matchId) {
        this.standard = standard;
        this.matchId = matchId;
    }
}