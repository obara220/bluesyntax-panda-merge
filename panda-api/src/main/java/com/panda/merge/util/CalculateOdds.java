package com.panda.merge.util;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panda.merge.common.utils.BigDecimalUtils;
import com.panda.merge.util.serializer.MalayOddsDeserializer;
import com.panda.merge.util.serializer.MalayOddsJacksonSerializer;
import com.panda.merge.util.serializer.MalayOddsSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CalculateOdds implements Serializable {
    /**
     * 投注项赔率. 单位: 0.00001
     */
    private Integer paOddsValue;

    /**
     * 马来赔（2位小数，内存用 BigDecimal 存储）
     */
    @JSONField(serialize = false, deserialize = false)
    private BigDecimal malayOddsValue;

    /**
     *  投注项状态： 0未激活(锁盘)、1激活、2投注项封盘
     */
    private Integer active;

    /**
     * 优惠赔率
     */
    private Integer disOddsValue;

    public void setMalayOddsValue(BigDecimal malayOddsValue) {
        this.malayOddsValue = BigDecimalUtils.normalizeMalayOddsDecimal(malayOddsValue);
    }

    public void setMalayOddsValue(Double malayOddsValue) {
        setMalayOddsValue(BigDecimalUtils.normalizeMalayOddsDecimal(malayOddsValue));
    }

    @JSONField(serializeUsing = MalayOddsSerializer.class, deserializeUsing = MalayOddsDeserializer.class)
    @JsonSerialize(using = MalayOddsJacksonSerializer.class)
    public BigDecimal getMalayOddsValue() {
        return malayOddsValue;
    }
}
