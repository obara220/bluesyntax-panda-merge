package com.panda.merge.dto.odds;

/**
 * StandardMarketOddsModification
 *
 * @description:
 * @date: 5/30/2025
 **/
public interface StandardMarketOddsModification {

    String getThirdOddsFieldSourceId();

    String getOddsType();

    String getAddition1();

    Long getRelationMarketId();

    void setRelationMarketId(Long relationMarketId);

    void setRemark(String remark);



}
