package com.panda.merge.service;

import com.panda.merge.dto.ThirdSportTeamDetail;
import com.panda.merge.model.ThirdSportTeam;

import java.util.List;

/**
 * 三方球队信息
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/29 <br>
 * @see com.panda.merge.service <br>
 */
public interface ThirdSportTeamService {

    /**
     *根据三方库球队ID查询三方库球队信息
     * @param id   三方库球队ID
     * @return ThirdSportTeam
     * */
    ThirdSportTeam getItem(Long id);

    /**
     *根据三方库球队ID查询三方库球队信息 (直接查库)
     * @param id   三方库球队ID
     * @return ThirdSportTeam
     * */
    ThirdSportTeam getItemByPrimaryKey(Long id);

    /**
     *根据三方库球队ID列表查询三方库球队信息
     * @param thirdTeamIds   三方库球队ID列表
     * @return ThirdSportTeam
     * */
    List<ThirdSportTeamDetail> getItems(List<Long> thirdTeamIds);

    /**
     * 根据 数据源+标准运动类型+三方数据源球队ID 获取三方球队信息
     * @param dataSourceCode            数据来源
     * @param sportId                   运动类型
     * @param thirdTeamSourceIds        三方数据源球队ID列表
     * @return ThirdSportTeam
     * */
    List<ThirdSportTeam> getItemsByThirdTeamSourceIds(List<String> dataSourceCodes, Long sportId, List<String> thirdTeamSourceIds);

    /**
     * 根据 数据源+标准运动类型+三方数据源球队ID 获取三方球队信息
     * @param dataSourceCode   数据来源
     * @param sportId          运动类型
     * @param thirdTeamSourceId          三方数据源球队ID
     * @return ThirdSportTeam
     * */
    ThirdSportTeam getOneItem(String dataSourceCode, Long sportId, String thirdTeamSourceId);

    /**
     * 根据 数据源+标准运动类型+三方数据源球队ID 获取三方球队信息（未放缓存）
     * @param dataSourceCode   数据来源
     * @param sportId          运动类型
     * @param thirdTeamSourceId          三方数据源球队ID
     * @return ThirdSportTeam
     * */
    ThirdSportTeam getItemByExample(String dataSourceCode, Long sportId, String thirdTeamSourceId);

    /**
     * 新增或修改
     * @param item  对象信息
     * @return ThirdSportTeamDetail
     * */
    ThirdSportTeam saveOrupdate(ThirdSportTeam item);

    /**
     * 根据 数据源+标准运动类型+三方数据源球队ID 获取三方球队信息（未放缓存）
     * @param dataSourceCode   数据来源
     * @param thirdTeamSourceId          三方数据源球队ID
     * @return ThirdSportTeam
     * */
    ThirdSportTeam getItemByExampleNoSportId(String dataSourceCode,String thirdTeamSourceId);

}
