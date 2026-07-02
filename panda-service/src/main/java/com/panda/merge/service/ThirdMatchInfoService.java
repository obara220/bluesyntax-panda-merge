package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.common.OddsWrapper;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchInfoDetail;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.model.ThirdMatchInfo;

import java.util.List;

/**
 * <Description> 三方赛事信息
 * @author      tell
 * @since       2020年9月10日10:35:50
 */
public interface ThirdMatchInfoService {


    /**
     * 根据修改时间筛选，分页查询三方赛事信息
     * @param  page  分页对象信息
     * @return Page<ThirdMatchInfo>
     * */
    Page<ThirdMatchInfoDetail> getItemPageByModifyTime(PageModel<ThirdMatchInfoDTO> page);


    /**
     * 根据 数据源+三方数据源赛事ID 获取三方赛事信息
     * @param dataSourceCode
     * @param thirdMatchSourceId
     * @return
     */
    ThirdMatchInfoDetail getItemDetail(String dataSourceCode, String thirdMatchSourceId);

    /**
     * 根据 数据源+三方数据源赛事ID 获取三方赛事信息
     * @param dataSourceCode
     * @param thirdMatchSourceId
     * @return
     */
    ThirdMatchInfo getItem(String dataSourceCode, String thirdMatchSourceId);

    List<ThirdMatchInfo> getItemsByMarketDTO(List<OddsWrapper<ThirdMatchMarketDTO>> marketDto);
    List<ThirdMatchInfo> getItemsByStandardIdAndDataSourceCode(List<Long> standardMatchIds, String dataSourceCode);

    ThirdMatchInfo getItem(Long referenceId, String dataSourceCode);

    ThirdMatchInfo getItemNoCache(Long referenceId, String dataSourceCode);

    ThirdMatchInfo getItemByThirdMatchSourceId(String thirdMatchSourceId);

    List<ThirdMatchInfo> getItems(List<Long> referenceIds, String dataSourceCode);

    List<ThirdMatchInfo> getItemsByThirdMatchSourceIds(List<String> dataSourceCodes,List<String> thirdMatchSourceIds);

    /**
     * 获取标准赛事ID 对应的所有三方赛事信息
     * @param referenceId
     * @return
     */
    List<ThirdMatchInfo> getItems(Long referenceId);

    ThirdMatchInfo getItem(Long id);

    List<ThirdMatchInfo> getItems(List<Long> ids);

    ThirdMatchInfo getItemByPrimaryKey(Long id);

    /**
     * 通过主键新增部分数据
     * @param item  对象信息
     * @return ThirdMatchInfoDetail
     * */
    ThirdMatchInfoDetail saveOrupdate(ThirdMatchInfoDetail item,String linkId);

    /**
     *  刷新缓存
     * @param item
     */
    ThirdMatchInfoDetail refreshCacheDetail(ThirdMatchInfoDetail item);

    /**
     *  通过主键更新部分数据
     *  @param thirdMatchInfo
     */
    ThirdMatchInfo updateByPrimaryKeySelective(ThirdMatchInfo thirdMatchInfo,String linkId);

    /**
     * 处理关联三方赛事完赛（同一标准赛事下超过3个数据源完赛的自动把其他数据源的三方赛事进行完赛处理）
     * @param referenceId  标准赛事id
     */
    void processThirdMatchOver(Long referenceId);

    /**
     *  刷新缓存
     * @param thirdMatchInfo
     */
    ThirdMatchInfo refreshCache(ThirdMatchInfo thirdMatchInfo);

    List<ThirdMatchInfo> getThirdMatchInfoForSettle(Long standardMatchId,String businessEvent);

}
