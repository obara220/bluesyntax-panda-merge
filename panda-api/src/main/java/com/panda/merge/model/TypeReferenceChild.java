package com.panda.merge.model;

import com.alibaba.fastjson.TypeReference;

/**
 * 避免TypeReference protected构造方法在非同包下时构建对象失效
 *
 * @author warren
 * @since 2024/11/06 11:38:18
 */
public class TypeReferenceChild<T> extends TypeReference<T> {
}
