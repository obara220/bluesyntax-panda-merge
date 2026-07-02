package com.panda.merge.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OperatorOnlieSubVo implements Serializable {

    private final static long serialVersionUID = 1L;

    private List<Integer> userIds;
}
