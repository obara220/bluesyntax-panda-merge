package com.panda.merge.dto;

import com.panda.merge.model.MatchEventInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CheckIsGreyDto {
    /**
     * 2  5分钟灰色区间
     * 1 是灰色区间
     * 0 不是灰色区间
     * */
    Integer isGrey=0;
    List<String> settleNum =new ArrayList<>();
    Long standardMatchId;

    //比分灰色区间
    Integer scoresGrey=0;

    Integer thisDataSourceIsGray=0;
    MatchEventInfo matchEventInfo;

}
