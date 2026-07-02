package com.panda.merge.common.utils;

@FunctionalInterface
public interface ListBeanUtilsCallBack<S, T> {
    void callBack(S t, T s);
}
