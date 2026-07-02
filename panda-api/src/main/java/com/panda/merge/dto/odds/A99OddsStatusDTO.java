package com.panda.merge.dto.odds;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class A99OddsStatusDTO implements Serializable {


    public Long matchId;

    /**
     * 状态 0-关，1-开
     */
    public Integer status;


    public List<Long> categoryIds;

}
