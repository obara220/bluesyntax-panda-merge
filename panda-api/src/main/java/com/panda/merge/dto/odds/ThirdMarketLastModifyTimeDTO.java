package com.panda.merge.dto.odds;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ThirdMarketLastModifyTimeDTO implements Serializable {



    public Long matchId;


    public List<Long> categoryIds;

    /** 盘口类型：0滚球 1早盘；为空则返回早滚全部 */
    public Integer marketType;

}
