package com.panda.merge.mapper;

import com.panda.merge.model.UserKeyboardSet;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PA足球报球板-用户热键设置
 *
 * @author warren
 * @since 2023/11/27 14:04:02
 */
@Repository
public interface UserKeyboardSetMapper {
    /**
     * 根据用户名查询当前用户热键信息
     *
     * @param userName 用户名
     * @return 返回用户信息
     */

    UserKeyboardSet selectKeyboardByUserNameAndSportId(@Param("userName") String userName,@Param("sportId") Long sportId);

    /**
     * 新增用户热键设置数据
     *
     * @param userKeyboardSet 用户热键设置数据
     * @return 返回新增数量
     */
    int insertKeyboardInfo(UserKeyboardSet userKeyboardSet);

    /**
     * 修改用户热键设置数据
     *
     * @param userKeyboardSet 修改用户热键数据
     * @return 修改修改数量
     */
    int updateKeyboardByUserName(UserKeyboardSet userKeyboardSet);

    /**
     * 根据用户名删除当前用户热键信息
     *
     * @param userName 用户名
     * @return 返回用户信息
     */
    int deleteKeyboardByUserNameAndSportId(@Param("userName") String userName, @Param("sportId") Long sportId);

    /**
     * 批量查找
     *
     * @param userIds 用户ID
     * @return 返回用户信息
     */
    List<UserKeyboardSet> selectKeyboardByUserIdList(@Param("userIds") List<String> userIds);

    /**
     * 批量删除
     *
     * @param userIds 用户ID
     * @return 删除数量
     */
    int deleteKeyboardByUserIdList(@Param("userIds") List<String> userIds);
}
