package com.panda.merge.service.impl;

import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.mapper.I18nMarketCategoryMapper;
import com.panda.merge.mapper.StandardMarketCategoryFieldMapper;
import com.panda.merge.mapper.StandardSportMarketCategoryMapper;
import com.panda.merge.model.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public  class SPStandardMarketServiceImpl {

    @Autowired
    StandardSportMarketCategoryMapper standardSportMarketCategoryMapper;
    @Autowired
    I18nMarketCategoryMapper i18nMarketCategoryMapper;
    @Autowired
    StandardMarketCategoryFieldMapper standardMarketCategoryFieldMapper;

    public MatchSettleSpMarket initSPMarket(Long matchId, StandardMarketMessage data) {
        MatchSettleSpMarket matchSettleSpMarket =new MatchSettleSpMarket();
        //盘口初始化
        matchSettleSpMarket.setId(data.getId());
        matchSettleSpMarket.setStandardMatchId(matchId);
        matchSettleSpMarket.setMarketCategoryId(data.getMarketCategoryId());
        matchSettleSpMarket.setChildMarketCategoryId(data.getChildMarketCategoryId());
        matchSettleSpMarket.setDataSourceCode(data.getDataSourceCode());
        StandardSportMarketCategoryExample example =new StandardSportMarketCategoryExample();
        example.createCriteria().andMarketCategoryIdEqualTo(data.getMarketCategoryId());
        List<StandardSportMarketCategory> list =standardSportMarketCategoryMapper.selectByExample(example);
        if(list.size()!=0){
            Long nameCode = list.get(0).getNameCode();
            I18nMarketCategoryExample i18nMarketCategoryExample =new I18nMarketCategoryExample();
            i18nMarketCategoryExample.createCriteria().andNameCodeEqualTo(nameCode);
            List<I18nMarketCategory> i18nMarketCategories = i18nMarketCategoryMapper.selectByExample(i18nMarketCategoryExample);
            //多语言初始化
            String zs ="";
            String zh="";
            String en ="";
            for (I18nMarketCategory i18nMarketCategory : i18nMarketCategories) {
                if(i18nMarketCategory.getLanguageType().equals("zs")){
                    zs = i18nMarketCategory.getText();
                    zs = changeVariable(zs,i18nMarketCategory.getLanguageType());
                }
                if(i18nMarketCategory.getLanguageType().equals("zh")){
                    zh = i18nMarketCategory.getText();
                    zh = changeVariable(zh,i18nMarketCategory.getLanguageType());
                }
                if(i18nMarketCategory.getLanguageType().equals("en")){
                    en = i18nMarketCategory.getText();
                    en = changeVariable(en,i18nMarketCategory.getLanguageType());
                }

            }
            matchSettleSpMarket.setCategoryNameCn(zs);
            matchSettleSpMarket.setCategoryNameEn(en);
            if(StringUtils.isEmpty(zs)){
                matchSettleSpMarket.setCategoryNameCn(zh);
            }
        }
        //更新时间
        matchSettleSpMarket.setModifyTime(System.currentTimeMillis());
        matchSettleSpMarket.setCreateTime(System.currentTimeMillis());
        return matchSettleSpMarket;
    }

    private String changeVariable(String str, String languageType) {
        if(str ==null || str.isEmpty()){
            return str;
        }
        if(!str.contains("{$competitor1}") && !str.contains("{$competitor2}")){
            return str;
        }
        //替换变量  主客队
        if("zs".equals(languageType)){
            str = str.replace("{$competitor1}","主队");
            str = str.replace("{$competitor2}","客队");
        }else if("zh".equals(languageType)){
            str = str.replace("{$competitor1}","主隊");
            str = str.replace("{$competitor2}","客隊");
        }else if("en".equals(languageType)){
            str = str.replace("{$competitor1}","home");
            str = str.replace("{$competitor2}","away");
        }
        return str;
    }


    public  MatchSettleSpOdds initSPOdds(Long matchId, StandardMarketOddsMessage standardMarketOddsDTO) {
        MatchSettleSpOdds matchSettleSpOdds =new MatchSettleSpOdds();
        //投注项初始化
        matchSettleSpOdds.setId(standardMarketOddsDTO.getId());
        matchSettleSpOdds.setStandardMatchId(matchId);
        matchSettleSpOdds.setMarketId(standardMarketOddsDTO.getMarketId());
        matchSettleSpOdds.setDataSourceCode(standardMarketOddsDTO.getDataSourceCode());
        matchSettleSpOdds.setOrderOdds(standardMarketOddsDTO.getOrderOdds());
        StandardMarketCategoryField field = standardMarketCategoryFieldMapper.selectByPrimaryKey(standardMarketOddsDTO.getOddsFieldsTemplateId());
        Long nameCode = field.getNameCode();

        //多语言初始化
        String zs ="";
        String zh="";
        String en ="";
        I18nMarketCategoryExample i18nMarketCategoryExample =new I18nMarketCategoryExample();
        i18nMarketCategoryExample.createCriteria().andNameCodeEqualTo(nameCode);
        List< I18nMarketCategory> i18nMarketCategories = i18nMarketCategoryMapper.selectByExample(i18nMarketCategoryExample);

        for (I18nMarketCategory i18nName :i18nMarketCategories) {
            if(i18nName.getLanguageType().equals("zs")){
                zs=i18nName.getText();
                zs = changeVariable(zs,i18nName.getLanguageType());
            }
            if(i18nName.getLanguageType().equals("en")){
                en=i18nName.getText();
                en = changeVariable(en,i18nName.getLanguageType());
            }
            if(i18nName.getLanguageType().equals("zh")){
                zh=i18nName.getText();
                zh = changeVariable(zh,i18nName.getLanguageType());
            }
        }
        matchSettleSpOdds.setOddsNameCn(zs);
        matchSettleSpOdds.setOddsNameEn(en);
        if(StringUtils.isEmpty(zs)){
            matchSettleSpOdds.setOddsNameCn(zh);
        }
        //更新时间
        matchSettleSpOdds.setModifyTime(System.currentTimeMillis());
        matchSettleSpOdds.setCreateTime(System.currentTimeMillis());
        matchSettleSpOdds.setCheckNumber(0);
        matchSettleSpOdds.setSettleStatus(0);
        return matchSettleSpOdds;
    }
}