package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.model.FootballKeyboardSet;

import java.util.List;

/**
 * PA报球板-用户热键设置
 *
 * @author warren
 * @since 2023/11/27 13:34:11
 */
public interface FootballDashboardHotKeyApi {
    /**
     * 根据用户名查询当前用户热键信息
     *
     * @param userName 用户名
     * @return 返回用户信息
     */
    Response getKeyboardByUserNameAndThirdMatchId(String userName,Long thirdMatchId);

    /**
     * 新增用户热键设置数据
     *
     * @param footballKeyboardSet 用户热键信息
     * @return 返回新增状态
     */
    Response addKeyboardInfo(FootballKeyboardSet footballKeyboardSet);

    /**
     * 修改用户热键设置数据
     *
     * @param footballKeyboardSet 用户热键信息
     * @return 返回修改状态
     */
    Response updateKeyboardByUserName(FootballKeyboardSet footballKeyboardSet);

    /**
     * 根据用户名删除当前用户热键信息
     *
     * @param userName 用户名
     * @return 返回用户信息
     */
    Response removeKeyboardByUserNameAndThirdMatchId(String userName,Long thirdMatchId);

    /**
     * 批量删除
     *
     * @param userIds 用户ID
     * @return 删除数量
     */
    Response deleteKeyboardByUserIdList(List<String> userIds);
}
