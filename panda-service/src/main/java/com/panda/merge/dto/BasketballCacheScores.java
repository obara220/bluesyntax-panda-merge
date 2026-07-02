package com.panda.merge.dto;


import com.panda.merge.cache.CommonItem;
import lombok.Data;

import java.io.Serializable;

@Data
public class BasketballCacheScores implements Serializable {
    private CommonItem firstScores;
    private CommonItem secondScores ;
    private CommonItem thirdScores;
    private CommonItem fourthScores;
    private CommonItem periodOneScore;
    private CommonItem periodTwoScore;
    private CommonItem wholeScores;
}
