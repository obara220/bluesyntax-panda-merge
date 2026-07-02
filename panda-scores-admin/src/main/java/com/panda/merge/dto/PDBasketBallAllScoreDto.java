package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PDBasketBallAllScoreDto implements Serializable {
    /**
     *主队比分
     */
    private  List<PDBasketBallScoreDto> homeScoreList;
    /**
     *客队比分
     */
    private  List<PDBasketBallScoreDto> awayScoreList;
}
