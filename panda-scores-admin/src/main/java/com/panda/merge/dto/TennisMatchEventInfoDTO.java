package com.panda.merge.dto;

import com.panda.merge.model.MatchEventInfo;
import lombok.Data;

@Data
public class TennisMatchEventInfoDTO {
    private MatchEventInfo matchEventInfo;
    /**
     * 是否破发点
     * */
    private boolean isBreakPoint =false;

    private String homeAwayBreakPoint;
    /**
     * 是否破发成功
     * */
    private boolean isBreakSuccess=false;

    private String homeAwayBreakSuccess;

}
