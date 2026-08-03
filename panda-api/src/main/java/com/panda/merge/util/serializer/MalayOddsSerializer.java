package com.panda.merge.util.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.panda.merge.common.utils.BigDecimalUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * 马来赔 Fastjson 序列化：按2位小数 plain 写入 JSON 数字。
 */
public class MalayOddsSerializer implements ObjectSerializer {

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
            throws IOException {
        SerializeWriter out = serializer.getWriter();
        if (object == null) {
            out.writeNull();
            return;
        }
        BigDecimal normalized;
        if (object instanceof BigDecimal) {
            normalized = BigDecimalUtils.normalizeMalayOddsDecimal((BigDecimal) object);
        } else if (object instanceof Number) {
            normalized = BigDecimalUtils.normalizeMalayOddsDecimal(((Number) object).doubleValue());
        } else {
            normalized = BigDecimalUtils.normalizeMalayOddsDecimal(Double.parseDouble(object.toString()));
        }
        out.write(normalized.toPlainString());
    }
}
