package com.panda.merge.dto.message;

import lombok.Getter;
import lombok.Setter;

/**
 * @author : Bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.dto.message
 * @description : TODO
 * @date: 2021-02-13 17:13
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Setter
@Getter
public class MatchOddsWarningMessage {
    private Long standardMatchId;
    private Long marketCategoryId;
    /**
     * true:报警
     * false:解除
     */
    private boolean sign;

    private String linkId;
}
