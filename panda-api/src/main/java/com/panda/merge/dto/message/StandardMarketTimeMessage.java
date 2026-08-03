package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class StandardMarketTimeMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long relationMarketId;

    private Long requestDataSourceTime;

    private Long marketModifyTime;
}
