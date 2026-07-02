package com.panda.merge.dto;

import com.panda.merge.model.StandardSportMarket;
import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @Author Kepa
 * @Date 2021/7/18 18:45
 * @Version 1.0
 */
@Data
public class MergeAndManagerDTO implements Serializable {

    private static final long serialVersionUID = -2951045180035841548L;

    private String key;

    private String value;

    private Long timeout;

    private TimeUnit unit;

    private String linkId;

}

