package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardOutrightMatchCategory implements Serializable {
    @ApiModelProperty(value = "标准玩法id")
    private Long id;

    @ApiModelProperty(value = "标准赛事id")
    private Long standardMatchId;

    @ApiModelProperty(value = "玩法开售状态Sold开售Unsold未售")
    private String categorySellStatus;

    @ApiModelProperty(value = "赛果下发数量0未下发")
    private Integer matchResultStatus;

    @ApiModelProperty(value = "支持赔率类型,1：支持欧式、英式、美式、香港、马来、印尼赔率；2：支持欧式、英式、美式赔率")
    private String supportOdds;

    @ApiModelProperty(value = "PC模板展示")
    private Integer templatePc;

    @ApiModelProperty(value = "h5模板展示")
    private Integer templateH5;

    @ApiModelProperty(value = "玩法是否有效")
    private Integer status;

    @ApiModelProperty(value = "排序值")
    private Integer orderNo;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modfiyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public String getCategorySellStatus() {
        return categorySellStatus;
    }

    public void setCategorySellStatus(String categorySellStatus) {
        this.categorySellStatus = categorySellStatus;
    }

    public Integer getMatchResultStatus() {
        return matchResultStatus;
    }

    public void setMatchResultStatus(Integer matchResultStatus) {
        this.matchResultStatus = matchResultStatus;
    }

    public String getSupportOdds() {
        return supportOdds;
    }

    public void setSupportOdds(String supportOdds) {
        this.supportOdds = supportOdds;
    }

    public Integer getTemplatePc() {
        return templatePc;
    }

    public void setTemplatePc(Integer templatePc) {
        this.templatePc = templatePc;
    }

    public Integer getTemplateH5() {
        return templateH5;
    }

    public void setTemplateH5(Integer templateH5) {
        this.templateH5 = templateH5;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModfiyTime() {
        return modfiyTime;
    }

    public void setModfiyTime(Long modfiyTime) {
        this.modfiyTime = modfiyTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", categorySellStatus=").append(categorySellStatus);
        sb.append(", matchResultStatus=").append(matchResultStatus);
        sb.append(", supportOdds=").append(supportOdds);
        sb.append(", templatePc=").append(templatePc);
        sb.append(", templateH5=").append(templateH5);
        sb.append(", status=").append(status);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", createTime=").append(createTime);
        sb.append(", modfiyTime=").append(modfiyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}