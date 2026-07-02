package com.panda.merge.common.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 实体类对象比较工具
 * @author :  tell
 * @Date:    2020年9月4日11:10:57
 */
@Slf4j
public class EntityEqualsUtils {

    /** 需要过滤的字段名称*/
    private static final Set<String> EXCLUDED_FIELDS =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList("createTime", "modifyTime")));

    /**
     * 获取类全部字段包含父类中的字段
     * @param entity  实体类
     * @return   Field[]
     * */
    private static <T> Field[] getAllFields(T entity){
        Class clazz = entity.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null){
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }


    /**
     * 字段处理逻辑
     */
    private static <T> T setTypeValue(T entity) {
        try {
            //不对原对象进行操作
            T target = (T) entity.getClass().newInstance();
            BeanUtils.copyProperties(entity, target);
            for (Field field : getAllFields(target)) {
                field.setAccessible(true);
                handleField(field, target);
            }
            return target;
        } catch (Exception e) {
            log.error("EntityEqualsUtils,对象比较异常,Exception:",e);
            return entity;
        }
    }

    /**
     * 字段处理逻辑拆分
     */
    private static void handleField(Field field, Object target) throws IllegalAccessException {
        Class<?> type = field.getType();
        Object value = field.get(target);

        // 字符串空值处理
        if (type == String.class && "".equals(value)) {
            field.set(target, null);
            return;
        }

        // 排除字段处理
        if (EXCLUDED_FIELDS.contains(field.getName())) {
            field.set(target, null);
            return;
        }

        // Long类型处理
        if (type == Long.class && value == null) {
            field.set(target, 0L);
            return;
        }

        // Integer类型处理
        if (type == Integer.class && value == null) {
            field.set(target, 0);
        }
    }

    /**
     * 比较相同的两个实体类toJSONString值是否相同
     *    无需比较字段 "createTime", "modifyTime"
     * @param entity1  实体类1
     * @param entity2  实体类2
     * @return   Boolean
     * */
    public static <T> Boolean equalsIsObjToString(T entity1,T entity2){
        if(null == entity1 && null == entity2){
            return true;
        }
        if(null != entity1 && null != entity2){
            T t1 = setTypeValue(entity1);
            T t2 = setTypeValue(entity2);
            if(JSON.toJSONString(t1).equals(JSON.toJSONString(t2))){
                return true;
            }
        }
        return false;
    }

}
