package com.panda.merge.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class OperatorOnlineVo implements Serializable {
    private final static long serialVersionUID = 1L;

    private String linkId;

    private Integer userId;

    //登录状态 0-登录，1-登出
    private Integer loginStatus;
}
