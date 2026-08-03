package com.panda.merge.dto.odds;

import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.model.MarketCategorySell;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.ThirdMatchInfo;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class BasketballConfigDTO  implements Serializable {

    public String linkId;

    /**
     * 球头
     */
    public String add1;

    /**
     * 状态 0-关，1-开
     */
    public Integer status;

    /**
     * 赔率浮动值 0.03 格式
     */
    public double value = 0.03;

}
