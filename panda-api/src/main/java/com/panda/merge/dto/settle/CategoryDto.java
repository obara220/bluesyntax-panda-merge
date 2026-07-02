package com.panda.merge.dto.settle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto implements Serializable {


    /**
     * 进球 0: 未冻结  1: 冻结
     * */
    private Integer goal;
    /**
     * 角球 0: 未冻结  1: 冻结
     * */
    private Integer corner;
    /**
     * 罚牌 0: 未冻结  1: 冻结
     * */
    private Integer faCard;



}
