package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CloseCategoryDTO implements Serializable {
    private Long matchId;
    private List<String> categoryList;
}
