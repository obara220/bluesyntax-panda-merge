package com.panda.merge.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class RateLimiterThirdMatchDTO {
    public String thirdSourceMatchId;
    public String dataSourceCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RateLimiterThirdMatchDTO that = (RateLimiterThirdMatchDTO) o;
        return Objects.equals(thirdSourceMatchId, that.thirdSourceMatchId) && Objects.equals(dataSourceCode, that.dataSourceCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(thirdSourceMatchId, dataSourceCode);
    }
}
