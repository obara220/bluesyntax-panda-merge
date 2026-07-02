package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MalayConvertEurope implements Serializable {
    private Long id;

    @ApiModelProperty(value = "马来赔")
    private Double malayValue;

    @ApiModelProperty(value = "欧赔")
    private Double europeValue;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMalayValue() {
        return malayValue;
    }

    public void setMalayValue(Double malayValue) {
        this.malayValue = malayValue;
    }

    public Double getEuropeValue() {
        return europeValue;
    }

    public void setEuropeValue(Double europeValue) {
        this.europeValue = europeValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", malayValue=").append(malayValue);
        sb.append(", europeValue=").append(europeValue);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}