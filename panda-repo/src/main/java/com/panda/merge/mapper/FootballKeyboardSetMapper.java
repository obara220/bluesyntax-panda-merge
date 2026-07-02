package com.panda.merge.mapper;

import com.panda.merge.model.FootballKeyboardSet;
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
public interface FootballKeyboardSetMapper {
    /**
     * 根据用户名查询当前用户热键信息
     *
     * @param userName 用户名
     * @return 返回用户信息
     */

    FootballKeyboardSet selectKeyboardByUserName(@Param("userName") String userName);

    /**
     * 新增用户热键设置数据
     *
     * @param footballKeyboardSet 用户热键设置数据
     * @return 返回新增数量
     */
    int insertKeyboardInfo(FootballKeyboardSet footballKeyboardSet);

    /**
     * 修改用户热键设置数据
     *
     * @param footballKeyboardSet 修改用户热键数据
     * @return 修改修改数量
     */
    int updateKeyboardByUserName(FootballKeyboardSet footballKeyboardSet);

    /**
     * 根据用户名删除当前用户热键信息
     *
     * @param userName 用户名
     * @return 返回用户信息
     */
    int deleteKeyboardByUserName(@Param("userName") String userName);

    /**
     * 批量查找
     *
     * @param userIds 用户ID
     * @return 返回用户信息
     */
    List<FootballKeyboardSet> selectKeyboardByUserIdList(@Param("userIds") List<String> userIds);

    /**
     * 批量删除
     *
     * @param userIds 用户ID
     * @return 删除数量
     */
    int deleteKeyboardByUserIdList(@Param("userIds") List<String> userIds);
}
