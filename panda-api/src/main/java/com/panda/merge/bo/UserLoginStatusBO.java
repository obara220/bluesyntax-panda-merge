package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;

/**
 *  user login status
 */
@Data
public class UserLoginStatusBO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userName;

    private Integer loginStatus;
}
