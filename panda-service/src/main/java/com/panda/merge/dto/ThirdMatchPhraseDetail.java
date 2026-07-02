package com.panda.merge.dto;

import com.panda.merge.model.ThirdMatchPhrase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author   tell
 * @since    2021年3月6日15:50:48
 * */
public class ThirdMatchPhraseDetail extends ThirdMatchPhrase {

    @ApiModelProperty(value = "标准赛事ID")
    @Getter
    @Setter
    private Long standardMatchId;

    @ApiModelProperty(value = "三方赛事ID")
    @Getter
    @Setter
    private Long thirdMatchId;
}