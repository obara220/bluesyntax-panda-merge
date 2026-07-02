package com.panda.merge.dto.message;


import lombok.Data;

import java.util.List;

/**
 * @name: OddsCalcCategoryGroupUpdateMessage
 * @description: 赔率计算玩法分组更新小心
 * @date: 1/12/2025
 **/
@Data
public class OddsCalcCategoryGroupUpdateMessage {

    private Long sportId;

    private List<Long> categoryIds;

}
