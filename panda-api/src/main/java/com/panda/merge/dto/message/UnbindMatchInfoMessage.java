package com.panda.merge.dto.message;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class UnbindMatchInfoMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** AO 赛事的源id **/
    private String AOThirdMatchSourceId;

    /** 解绑赛事的三方源id **/
    private String unbindThirdMatchSourceId;

    /** 解绑赛事的三方s数据源 **/
    private String unbindDataSourceCode;

    /** 被解绑的标准赛事id **/
    private Long unbindTargetMatchId;

}
