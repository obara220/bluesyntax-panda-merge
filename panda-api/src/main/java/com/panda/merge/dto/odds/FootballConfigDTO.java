package com.panda.merge.dto.odds;

import lombok.Data;

import java.io.Serializable;

@Data
public class FootballConfigDTO implements Serializable {


    /**
     * 球头
     */
    public String add1;

    /**
     * 状态 false-关，true-开
     */
    public Boolean status;


}
