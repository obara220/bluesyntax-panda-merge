package com.panda.merge.dto.odds;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ThirdMarketLastModifyTimeDTO implements Serializable {



    public Long matchId;


    public List<Long> categoryIds;


}
