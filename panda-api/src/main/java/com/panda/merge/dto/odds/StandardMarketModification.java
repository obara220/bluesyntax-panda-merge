package com.panda.merge.dto.odds;

import com.panda.merge.common.enums.Constant;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * MarketModification
 *
 * @description: 盘口相关接口
 * @date: 3/12/2025
 **/
public interface StandardMarketModification {

    Long getId();

    Long getMarketCategoryId();

    Integer getMarketSource();

    Integer getStatus();

    void setStatus(Integer status);

    Integer getThirdMarketSourceStatus();

    void setThirdMarketSourceStatus(Integer status);

    String getDataSourceCode();

    String getRemark();

    void setRemark(String remark);

    Integer getMergeMarketStatus();

    void setMergeMarketStatus(Integer status);

    int getControlStatus();
    void setControlStatus(int status);

    default void addRemark(String remark) {
        if (StringUtils.isEmpty(getRemark())) {
            setRemark(remark);
        } else {
            setRemark(getRemark() + ";" + remark);
        }
    }

    default boolean isFinal() {
        return MarketControlStatusEnum.FINAL.code == getControlStatus();
    }

    default boolean isValidated() {
        return getControlStatus() >= MarketControlStatusEnum.VALIDATED.code;
    }

    default boolean isOldClose() {
        return Objects.equals(MergeMarketStatusEnum.DATA_SOURCE_SWITCH_OLD_CLOSE.code, getMergeMarketStatus());
    }

    default void oldClose() {
        setMergeMarketStatus(MergeMarketStatusEnum.DATA_SOURCE_SWITCH_OLD_CLOSE.code);
        setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
        setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
        addRemark(MergeMarketStatusEnum.DATA_SOURCE_SWITCH_OLD_CLOSE.name());
    }

    default void oldClose(String remark) {
        setMergeMarketStatus(MergeMarketStatusEnum.DATA_SOURCE_SWITCH_OLD_CLOSE.code);
        setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
        setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
        addRemark(remark);
    }

    default void invalidDataSource() {
        setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
        addRemark(MergeMarketStatusEnum.DATASOURCE_INVALID.name());
        setControlStatus(MarketControlStatusEnum.FINAL.code);
    }


    String getAddition1();

    String getAddition2();

    String getAddition3();

    String getAddition4();

    String getAddition5();

    Integer getMarketType();

    String getThirdMarketSourceId();

    Long getRelationMarketId();

    void setRelationMarketId(Long relationMarketId);
}
