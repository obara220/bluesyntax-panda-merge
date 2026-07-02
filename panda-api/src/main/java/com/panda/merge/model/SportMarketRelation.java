package com.panda.merge.model;

import java.io.Serializable;

public class SportMarketRelation implements Serializable {
    private Long id;

    private String marketRelationKey;

    private Long relationMarketId;

    private Long createTime;

    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarketRelationKey() {
        return marketRelationKey;
    }

    public void setMarketRelationKey(String marketRelationKey) {
        this.marketRelationKey = marketRelationKey;
    }

    public Long getRelationMarketId() {
        return relationMarketId;
    }

    public void setRelationMarketId(Long relationMarketId) {
        this.relationMarketId = relationMarketId;
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
        sb.append(", marketRelationKey=").append(marketRelationKey);
        sb.append(", relationMarketId=").append(relationMarketId);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}