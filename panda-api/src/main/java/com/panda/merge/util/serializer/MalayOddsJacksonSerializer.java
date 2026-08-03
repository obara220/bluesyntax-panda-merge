package com.panda.merge.util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.panda.merge.common.utils.BigDecimalUtils;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 马来赔 Jackson 序列化（MQ 下发）：Double/BigDecimal 均按2位小数 plain 写入。
 */
public class MalayOddsJacksonSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        BigDecimal normalized;
        if (value instanceof BigDecimal) {
            normalized = BigDecimalUtils.normalizeMalayOddsDecimal((BigDecimal) value);
        } else if (value instanceof Number) {
            normalized = BigDecimalUtils.normalizeMalayOddsDecimal(((Number) value).doubleValue());
        } else {
            normalized = BigDecimalUtils.normalizeMalayOddsDecimal(Double.parseDouble(value.toString()));
        }
        gen.writeRawValue(normalized.toPlainString());
    }
}
