package com.panda.merge.service.settleMention.dto;

import com.panda.merge.config.SettleMentionProperty;
import com.panda.merge.constant.SettleMentionEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @description: mention dto abstract class
 * @author: Henry Wang
 * @create: 2024-08-28 11:50
 **/
@Data
@Slf4j
public abstract class AbstractSettleMentionDto<T> {
    public T getMentionStatusFieldByMentionEnum(SettleMentionEnum settleMentionEnum) {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(SettleMentionProperty.class)) {
                    continue;
                }
                SettleMentionProperty item = field.getAnnotation(SettleMentionProperty.class);
                if (Arrays.asList(item.eventCode()).contains(settleMentionEnum.getValue())){
                    return (T) field.get(this);
                }
            }
            return null;
        } catch (Exception e) {
            log.error("[AbstractSettleMentionDto] getMentionStatusFieldByMentionEnum error: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public void setDetailNull() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(SettleMentionProperty.class)) {
                    continue;
                }
                AbstractMentionStatus mentionStatus = (BasketballMentionStatus) field.get(this);
                mentionStatus.setDetailNull();
            }
        } catch (Exception e) {
            log.error("[AbstractSettleMentionDto] setDetailNull error: ", e);
        }
    };
}
