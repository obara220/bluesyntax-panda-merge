package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class PandaOddsConvert implements Serializable {
    private Long id;

    private Double europeStart;

    private Double europeEnd;

    private Double malaysia;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getEuropeStart() {
        return europeStart;
    }

    public void setEuropeStart(Double europeStart) {
        this.europeStart = europeStart;
    }

    public Double getEuropeEnd() {
        return europeEnd;
    }

    public void setEuropeEnd(Double europeEnd) {
        this.europeEnd = europeEnd;
    }

    public Double getMalaysia() {
        return malaysia;
    }

    public void setMalaysia(Double malaysia) {
        this.malaysia = malaysia;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", europeStart=").append(europeStart);
        sb.append(", europeEnd=").append(europeEnd);
        sb.append(", malaysia=").append(malaysia);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}