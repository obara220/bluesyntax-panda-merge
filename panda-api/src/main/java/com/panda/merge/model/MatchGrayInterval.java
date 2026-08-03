package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchGrayInterval implements Serializable {
    private Integer id;

    @ApiModelProperty(value = "数据商编码")
    private String dataSourceCode;

    private Integer tournamentLevel;

    private Integer min15Goal;

    private Integer min15Corner;

    private Integer min15Bookings;

    private Integer min5Goal;

    private Long createTime;

    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Integer getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Integer tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public Integer getMin15Goal() {
        return min15Goal;
    }

    public void setMin15Goal(Integer min15Goal) {
        this.min15Goal = min15Goal;
    }

    public Integer getMin15Corner() {
        return min15Corner;
    }

    public void setMin15Corner(Integer min15Corner) {
        this.min15Corner = min15Corner;
    }

    public Integer getMin15Bookings() {
        return min15Bookings;
    }

    public void setMin15Bookings(Integer min15Bookings) {
        this.min15Bookings = min15Bookings;
    }

    public Integer getMin5Goal() {
        return min5Goal;
    }

    public void setMin5Goal(Integer min5Goal) {
        this.min5Goal = min5Goal;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", tournamentLevel=").append(tournamentLevel);
        sb.append(", min15Goal=").append(min15Goal);
        sb.append(", min15Corner=").append(min15Corner);
        sb.append(", min15Bookings=").append(min15Bookings);
        sb.append(", min5Goal=").append(min5Goal);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}