package com.panda.merge.rocketmq.common;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.ThirdMatchInfoDetail;
import com.panda.merge.dto.ThirdSportPlayerDetail;
import com.panda.merge.mapper.StandardSportSeasonMapper;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.producer.SendThirdCacheMarketOddsProducer;
import com.panda.merge.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 事物数据统一处理 避免异常数据缺失
 * @author  tell
 * @since   2020年9月3日14:17:58
 * */
@Component
public class TransactionalProcessor {

    @Autowired
    public RedisService redisService;
    @Autowired
    private ThirdSportTournamentService thirdSportTournamentService;
    @Autowired
    private LanguageInternationService languageInternationService;

    /**
     * 新增或修改列表三方联赛信息相关部分数据(统一处理 避免异常数据缺失)
     * @param item                       联赛信息
     * @param languageInternationList    联赛相关国际化列表（联赛名称，赛季名称）
     * */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrupdateThirdTournament(ThirdSportTournament item, List<LanguageInternation> languageInternationList,String linkId){
        //联赛数据入库
        thirdSportTournamentService.saveOrupdate(item);
        //联赛相关国际化入库
        if(!CollectionUtils.isEmpty(languageInternationList)){
            languageInternationService.saveOrupdateList(languageInternationList,linkId);
        }
    }


    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ThirdMatchTeamRelationService thirdMatchTeamRelationService;
    @Autowired
    private ThirdSportTeamService thirdSportTeamService;
    @Autowired
    private SendThirdCacheMarketOddsProducer sendThirdCacheMarketOddsProducer;

    /**
     * 通过主键新增或修改三方赛事信息相关部分数据(统一处理 避免异常数据缺失)
     * @param item       三方赛事信息
     * */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrupdateThirdMatch(String linkId, ThirdMatchInfoDetail item, Boolean isNewThirdMatch, Long thirdSportId) {
        //赛事球队关系入库
        if(!CollectionUtils.isEmpty(item.getMtRelationList())){
            List<ThirdMatchTeamRelation> thirdMatchTeamRelations = thirdMatchTeamRelationService.saveOrupdateList(item.getMtRelationList(), linkId);
            item.setMtRelationList(thirdMatchTeamRelations);
        }
        //场地多语言入库
        if(!CollectionUtils.isEmpty(item.getPsitionNameList())){
            List<LanguageInternation> languageInternations = languageInternationService.saveOrupdateList(item.getPsitionNameList(), linkId);
            item.setPsitionNameList(languageInternations);
        }
        //赛事信息入库
        thirdMatchInfoService.saveOrupdate(item,linkId);
        //新增三方赛事处理未入库缓存赔率
        if(isNewThirdMatch){
            sendThirdCacheMarketOddsProducer.sendThirdCacheMarket(linkId, item, thirdSportId);
        }
    }

    @Autowired
    private ThirdOutrightMatchInfoService thirdOutrightMatchInfoService;
    @Autowired
    private I18nnamesOutrightMatchNameService i18nnamesOutrightMatchNameService;

    /**
     * 通过主键新增或修改冠军赛事信息相关部分数据(统一处理 避免异常数据缺失)
     * @param item       三方赛事信息
     * @param languageInternationList       赛事多语言
     * */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrupdateOutrightMatch(ThirdOutrightMatchInfo item,List<I18nnamesOutrightMatchName> languageInternationList){
        //赛事信息入库
        thirdOutrightMatchInfoService.saveOrupdate(item);
        //赛事多语言入库
        if(!CollectionUtils.isEmpty(languageInternationList)){
            i18nnamesOutrightMatchNameService.saveOrupdateList(languageInternationList);
        }
    }


    @Autowired
    private ThirdSportPlayerService thirdSportPlayerService;
    @Autowired
    private ThirdTeamPlayerRelationService thirdTeamPlayerRelationService;

    /**
     * 新增或修改列表三方球队人员信息相关部分数据(统一处理 避免异常数据缺失)
     * @param item                       球员信息
     * @param languageInternationList   球队人员国际化信息列表（球员名称）
     * */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrupdateThirdPlayer(ThirdSportPlayerDetail item, List<LanguageInternation> languageInternationList, String linkId, JSONObject affectedObject){
        //人员信息入库
        item.setLinkId(linkId);
        thirdSportPlayerService.saveOrupdate(item,affectedObject);
        if(null != item.getTeamPlayerRelation()){
            //球队人员关系信息入库
            thirdTeamPlayerRelationService.saveOrupdate(item.getTeamPlayerRelation());
        }
        //球员国际化信息入库
        if(!CollectionUtils.isEmpty(languageInternationList)){
            languageInternationService.saveOrupdateList(languageInternationList,linkId);
        }
    }


    @Autowired
    private ThirdSportSeasonService thirdSportSeasonService;

    @Transactional(rollbackFor = Exception.class)
    public void saveOrupdateThirdSeason(ThirdSportSeason item, List<LanguageInternation> languageInternationList,String linkId){
        //联赛数据入库
        thirdSportSeasonService.saveOrupdate(item);
        //联赛相关国际化入库
        if(!CollectionUtils.isEmpty(languageInternationList)){
            languageInternationService.saveOrupdateList(languageInternationList,linkId);
        }
    }

    @Autowired
    private StandardSportSeasonMapper standardSportSeasonMapper;

    @Transactional(rollbackFor = Exception.class)
    public void saveStandardSeasonAndInternation(StandardSportSeason item, List<LanguageInternation> languageInternationList,String linkId,DataSource dataSource){
        //联赛数据入库
        standardSportSeasonMapper.insertSelective(item);
        //联赛相关国际化入库
        if(!CollectionUtils.isEmpty(languageInternationList)){
            languageInternationService.saveOrupdateList(languageInternationList,linkId);
        }
    }
}
