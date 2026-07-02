package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class CategoryMessage  implements Serializable {
    private Long categoryId;

    private String dataSourceCode;
}
