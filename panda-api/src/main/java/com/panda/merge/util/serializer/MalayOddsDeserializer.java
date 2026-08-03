package com.panda.merge.util.serializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.panda.merge.common.utils.BigDecimalUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * 马来赔 Fastjson 反序列化：统一规整为2位小数 BigDecimal。
 */
public class MalayOddsDeserializer implements ObjectDeserializer {

    @Override
    public BigDecimal deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object value = parser.parse();
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return BigDecimalUtils.normalizeMalayOddsDecimal((BigDecimal) value);
        }
        if (value instanceof Number) {
            return BigDecimalUtils.normalizeMalayOddsDecimal(((Number) value).doubleValue());
        }
        return BigDecimalUtils.normalizeMalayOddsDecimal(Double.parseDouble(value.toString()));
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
