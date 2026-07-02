package com.panda.merge.cache;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CommonItemBigDecimal implements Serializable {
    private BigDecimal away;
    private BigDecimal home;
    public CommonItemBigDecimal(){
        away=new BigDecimal(0);
        home=new BigDecimal(0);
    }

    public CommonItemBigDecimal(BigDecimal home, BigDecimal away) {
        this.home = home;
        this.away = away;
    }
    public String doCountScoreStr(){
        return home+"-"+away;
    }
}
