package com.panda.merge.service;

import com.panda.merge.model.StandardMatchInfo;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author edison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/19 <br>
 */
public interface SwiftSystemPreResultService {
    List<StandardMatchInfo> saveSystemPreResult(String params);
}
