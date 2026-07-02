package com.panda.merge.cache;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class CommonItem implements Serializable {

    private Integer home = 0;
    private Integer away = 0;

    public CommonItem(){ }

    public CommonItem(Integer home,Integer away) {
        if(!Objects.isNull(home)){
            this.home = home;
        }
        if(!Objects.isNull(away)){
            this.away = away;
        }
    }

    public String getScoreStr(){
        if (home == null || away == null) {
            return null;
        }
        return home+"_"+away;
    }

    public Integer getScoreSum() {
        if (Objects.isNull(home) || Objects.isNull(away)) {
            return null;
        }
        return home+away;
    }
}
