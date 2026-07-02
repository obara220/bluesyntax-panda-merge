package com.panda.merge.common.enums;

public enum PreSaleTraderLogConfigMessageEnum {
    INSERT_PRE_TRADER("页面新增赛前操盘手:【userName】在【nowTime】新增【preTrader】为赛前操盘手，开售时间为【preMatchTime】，操盘平台为【riskManageCode】",
            "Set Pregame Trader:【userName】at【nowTime】set【preTrader】as Pregame Trader，Pregame time is【preMatchTime】，trading platform is【riskManageCode】"),
    INSERT_PRE_TRADER_ONLINE("在线编辑新增赛前操盘手:【userName】在【nowTime】新增【preTrader】为赛前操盘手，开售时间为【preMatchTime】，操盘平台为【riskManageCode】",
            "Set Pregame Trader On Excel:【userName】at【nowTime】set【preTrader】as Pregame Trader，Pregame time is【preMatchTime】，trading platform is【riskManageCode】"),
    UPDATE_PRE_TRADER("页面修改赛前操盘手:【userName】在【nowTime】将【preTraderOld】修改成【preTrader】为赛前操盘手",
            "Change Pregame Trader:【userName】at【nowTime】change trader from【preTraderOld】to【preTrader】"),
    UPDATE_PRE_TRADER_TIME("页面修改赛前开售时间:【userName】在【nowTime】将赛前开售时间从【preMatchTimeOld】修改到【preMatchTime】,修改原因为【setOnSaleTimeLog】",
            "Change Pregame Time:【userName】at【nowTime】change time from【preMatchTimeOld】to【preMatchTime】,the reason is 【setOnSaleTimeLog】"),
    CANCEL_PRE_TRADER("页面取消赛前操盘手设置:【userName】在【nowTime】将赛前操盘手取消设置",
            "Cancle Pregame Trader:【userName】at【nowTime】cancled Pregame Trader"),
    PRE_SELL("操盘开售页面开售:【userName】在【nowTime】将该赛事早盘进行开售，开售了【marketCategorySize】个玩法,link-Id为【linkId】",
            "Sold Match:【userName】at【nowTime】sold this match，total【marketCategorySize】markets,link-Id is【linkId】"),
    PRE_SELL_MTS_ONLINE("在线编辑新增赛前操盘手MTS早盘自动开售:【userName】在【nowTime】将该赛事早盘操盘平台设置为MTS",
            "Set Pregame Trader On Excel as MTS:【userName】at【nowTime】set trading platform to MTS"),
    INSERT_LIVE_TRADER("页面新增滚球操盘手:【userName】在【nowTime】新增【liveTrader】为滚球操盘手，开售时间为【liveMatchTime】，操盘平台为【riskManageCode】",
            "Set In-Play Trader:【userName】at【nowTime】set【liveTrader】as In-Play Trader，In-Play time is【liveMatchTime】，set trading platform as【riskManageCode】"),
    INSERT_LIVE_TRADER_ONLINE("在线编辑新增滚球操盘手:【userName】在【nowTime】新增【liveTrader】为滚球操盘手，开售时间为【liveMatchTime】，操盘平台为【riskManageCode】",
            "Set In-Play Trader On Excel:【userName】at【nowTime】set【liveTrader】as In-Play Trader，In-Play time is【liveMatchTime】，trading platform is【riskManageCode】"),
    UPDATE_LIVE_TRADER("页面修改滚球操盘手:【userName】在【nowTime】将【liveTraderOld】修改成【liveTrader】为滚球操盘手",
            "Change In-Play Trader:【userName】at【nowTime】change trader from【liveTraderOld】to【liveTrader】"),
    UPDATE_LIVE_TRADER_CODE("页面修改滚球操盘平台:【userName】在【nowTime】将滚球操盘平台从【riskManageCodeOld】修改到【riskManageCode】",
            "Change In-Play Trading Platform:【userName】at【nowTime】change platform from【riskManageCodeOld】to【riskManageCode】"),
    CANCEL_LIVE_TRADER("页面取消滚球操盘手设置:【userName】在【nowTime】将滚球操盘手取消设置",
            "Cancel  In-Play Trader:【userName】at【nowTime】canceled  In-Play Trader"),
    LIVE_SELL("操盘开售页面开售:【userName】在【nowTime】将该赛事滚球进行开售，赔率源为【dataSourceCode】,开售了【marketCategorySize】个玩法,link-Id为【linkId】",
            "Sold Match:【userName】at【nowTime】sold this match，data source is【dataSourceCode】,total【marketCategorySize】markets,link-Id is【linkId】"),
    UPDATE_AUDITOR("页面修改赛果审核员:【userName】在【nowTime】将该赛事赛果审核员从【auditorOld】修改成【auditor】,原因为【setAuditorLog】",
            "Change match Auditor:【userName】at【nowTime】change match auditor from【auditorOld】to【auditor】,the reason is【setAuditorLog】"),
    INSERT_AUDITOR("页面新增赛果审核员:【userName】在【nowTime】新增【auditor】为赛果审核员",
            "Set match Auditor:【userName】at【nowTime】set【auditor】as match auditor"),
    INSERT_AUDITOR_ONLINE("在线编辑新增赛果审核员:【userName】在【nowTime】新增【auditor】为赛果审核员",
            "Set match Auditor On Excel:【userName】at【nowTime】set【auditor】as match auditor"),
    PRE_SALE_OVER("系统完赛或者数据源完赛:【系统】在【nowTime】将该赛事进行完赛处理,link-Id为【linkId】",
            "Match Ended by System or data source:【System】at【nowTime】ended match,link-Id is【linkId】"),
    MOVE_IN("赛程移入预开售:【userName】在【nowTime】从赛程将该标准赛事移入预开售，标准赛事管理id为【matchManageId】，下挂的三方赛事数据源为【dataSouceCode】",
            "Move match Into Presale:【userName】at【nowTime】move match into Presale，Match-Manage ID is【matchManageId】，data source is【dataSouceCode】"),
    MOVE_OUT("赛事从预开售移出:【userName】在【nowTime】该赛事移出预开售",
            "Move match out Presale:【userName】at【nowTime】move match out Presale"),
    FINISHED_MATCH("手工完赛:【userName】在【nowTime】将赛事进行了手工完赛操作,link-Id为【linkId】",
            "Ended Match:【userName】at【nowTime】ended match,link-Id is【linkId】"),
    REOPEN_MATCH("重新开赛:【userName】在【nowTime】将赛事进行了重新开赛操作,link-Id为【linkId】",
            "Reopen Match:【userName】at【nowTime】reopened match,link-Id is【linkId】"),
    UPDATE_BUSINESS_EVENT_CODE("变更实时事件源:【userName】在【nowTime】将实时事件源从【businessEventCodeOld】改为【businessEventCode】,link-Id为【linkId】",
            "Change Events DataSource:【userName】at【nowTime】change events datasource from【businessEventCodeOld】to【businessEventCode】,link-Id is【linkId】"),
    UPDATE_PRE_DATA_SOURCE_CODE_WEIGHT("早盘数据源权重变更:【userName】在【nowTime】将【dataSourceCode】权重从【0】改为【1】,【dataSourceOld】权重从【1】改为【0】,link-Id为【linkId】",
            "Change DataSource of Presale:【userName】at【nowTime】change datasource 【dataSourceCode】from【0】to【1】,【dataSourceOld】from【1】to【0】,link-Id is【linkId】"),
    UPDATE_LIVE_DATA_SOURCE_CODE_WEIGHT("滚球数据源权重变更:【userName】在【nowTime】将【dataSourceCode】权重从【0】改为【1】,【dataSourceOld】权重从【1】改为【0】,link-Id为【linkId】",
            "Change DataSource of In-Play:【userName】at【nowTime】change datasource【dataSourceCode】from【0】to【1】,【dataSourceOld】from【1】to【0】,link-Id is【linkId】"),
    UPDATE_PRE_TRADER_DUBBO("早盘操盘页面:【userName】在【nowTime】将赛事负责人【preTraderOld】变更为【preTrader】",
            "Change Pregame Trader:【userName】at【nowTime】change Pregame trader from【preTraderOld】to【preTrader】"),
    UPDATE_LIVE_TRADER_DUBBO("滚球操盘页面:【userName】在【nowTime】将赛事负责人【liveTraderOld】变更为【liveTrader】",
            "Change In-Play Trader:【userName】at【nowTime】change In-Play trader from【liveTraderOld】to【liveTrader】"),
    UPDATE_PLAY_DATA_SOURCE_CODE_PRE_SINGLE("操盘修改早盘玩法数据源:用户id/用户名称 为【userId】/【userName】在【nowTime】修改玩法【MarketCategoryId】赔率源为【newDataSourceCode】,link-id为【linkId】",
            "Change DataSource of pregame markets:【userId】/【userName】at【nowTime】changed 【MarketCategoryId】datasource to 【newDataSourceCode】 ,link-id is【linkId】"),
    UPDATE_PLAY_DATA_SOURCE_CODE_PRE("操盘批量修改早盘玩法数据源:用户id/用户名称 为【userId】/【userName】在【nowTime】批量修改早盘玩法赔率源,link-id为【linkId】",
            "Change DataSource of pregame markets in batches:【userId】/【userName】at【nowTime】changed datasource  of markets in batches,link-id is【linkId】"),
    UPDATE_PLAY_DATA_SOURCE_CODE_LIVE_SINGLE("操盘修改滚球玩法数据源:用户id/用户名称 为【userId】/【userName】在【nowTime】修改玩法【MarketCategoryId】赔率源为【newDataSourceCode】,link-id为【linkId】",
            "Change DataSource of  In-play markets:【userId】/【userName】at【nowTime】changed 【MarketCategoryId】datasource to 【newDataSourceCode】 ,link-id is【linkId】"),
    UPDATE_PLAY_DATA_SOURCE_CODE_LIVE("操盘批量修改滚球玩法数据源:用户id/用户名称 为 【userId】/【userName】在【nowTime】批量修改滚球玩法赔率源,link-id为【linkId】",
            "Change DataSource of In-play markets in batches:【userId】/【userName】at【nowTime】changed datasource  of markets in batches,link-id is【linkId】"),
    UPDATE_RISK_MANAGE_CODE("操盘切换操盘平台:用户id/用戶名称 为【userId】/【userName】在【nowTime】切换操盘平台，从【oldRiskManageCode】修改到【newRiskManageCode】,link-id为【linkId】",
            "Change Trader Platform:【userId】/【userName】at【nowTime】changed trader platform from【oldRiskManageCode】to【newRiskManageCode】,link-id is【linkId】"),
    UPDATE_MATCH_STATUS_DATA_SOURCE("操盘修改赛事状态源:【userName】在【nowTime】将赛事状态源修改为【dataSourceCode】,link-Id为【linkId】",
            "Change Source of Match State:【userName】at【nowTime】Change Source to【dataSourceCode】,link-Id is【linkId】"
    ),
    UPDATE_LIVE_TRADER_CODE_MTS("页面修改滚球操盘平台为MTS:【userName】在【nowTime】将该赛事操盘平台设置为MTS",
            "Change In-Play Trading Platform as MTS:【userName】at【nowTime】set trading platform to MTS"),
    ;

    private String messageZh;
    private String messageEn;

    PreSaleTraderLogConfigMessageEnum(String messageZh, String messageEn) {
        this.messageZh = messageZh;
        this.messageEn = messageEn;
    }

    public String getMessageZh() {
        return messageZh;
    }

    public String getMessageEn() {
        return messageEn;
    }
}
