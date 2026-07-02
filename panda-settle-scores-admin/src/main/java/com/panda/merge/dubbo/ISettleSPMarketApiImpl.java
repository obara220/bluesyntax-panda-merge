//package com.panda.merge.dubbo;
//
//import com.panda.merge.api.ISettleSPMarketApi;
//import com.panda.merge.common.enums.OperateLogTypeEnum;
//import com.panda.merge.constant.MatchSettleScoreConstant;
//import com.panda.merge.dto.MatchSettleSpOddsDto;
//import com.panda.merge.dto.Response;
//import com.panda.merge.dto.SPSettleMarketDto;
//import com.panda.merge.dto.settle.AutoSettleDataSourceDto;
//import com.panda.merge.dto.settle.EditMatchSettleSPOddsDto;
//import com.panda.merge.dto.settle.SPMarketSettleListRequest;
//import com.panda.merge.mapper.MatchSettleSpMarketMapper;
//import com.panda.merge.mapper.MatchSettleSpOddsMapper;
//import com.panda.merge.mapper.StandardMatchInfoMapper;
//import com.panda.merge.model.*;
//import com.panda.merge.mq.producer.MatchSettleSPOddsProducer;
//import com.panda.merge.mq.producer.MatchSettleWsProducer;
//import com.panda.merge.respository.StandardMatchInfoRepository;
//import com.panda.merge.service.IMatchSettleLogService;
//import com.panda.merge.service.StandardMatchInfoService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@DubboService
//@Slf4j
//public class ISettleSPMarketApiImpl implements ISettleSPMarketApi {
//    @Autowired
//    MatchSettleSpMarketMapper matchSettleSpMarketMapper;
//    @Autowired
//    MatchSettleSpOddsMapper matchSettleSpOddsMapper;
//    @Autowired
//    MatchSettleSPOddsProducer matchSettleSPOddsProducer;
//    @Autowired
//    IMatchSettleLogService iMatchSettleLogService;
//    @Autowired
//    StandardMatchInfoMapper standardMatchInfoMapper;
//    @Autowired
//    MatchSettleWsProducer matchSettleWsProducer;
//    @Autowired
//    StandardMatchInfoService standardMatchInfoService;
//    @Override
//    public Response searchSPMarketSettleList(SPMarketSettleListRequest spMarketSettleListRequest) {
//        List<SPSettleMarketDto> list =new ArrayList<>();
//        MatchSettleSpMarketExample example =new MatchSettleSpMarketExample();
//        example.createCriteria().andStandardMatchIdEqualTo(spMarketSettleListRequest.getStandardMatchId());
//        List< MatchSettleSpMarket> matchSettleSpMarkets =matchSettleSpMarketMapper.selectByExample(example);
//
//        for (MatchSettleSpMarket matchSettleSpMarket : matchSettleSpMarkets) {
//            SPSettleMarketDto spSettleMarketDto =new SPSettleMarketDto();
//            BeanUtils.copyProperties(matchSettleSpMarket,spSettleMarketDto);
//            spSettleMarketDto.setId(matchSettleSpMarket.getId().toString());
//            //查询投注项
//            MatchSettleSpOddsExample oddsExample =new MatchSettleSpOddsExample();
//            oddsExample.createCriteria().andStandardMatchIdEqualTo(spMarketSettleListRequest.getStandardMatchId())
//            .andMarketIdEqualTo(matchSettleSpMarket.getId());
//            List<MatchSettleSpOdds> oddsList =matchSettleSpOddsMapper.selectByExample(oddsExample);
//            List<MatchSettleSpOddsDto> oddsDtos =new ArrayList<>();
//            for (MatchSettleSpOdds odds : oddsList) {
//                MatchSettleSpOddsDto dto =new MatchSettleSpOddsDto();
//                BeanUtils.copyProperties(odds,dto);
//                dto.setId(odds.getId().toString());
//                dto.setOddsNameCn(changeVariable(dto.getOddsNameCn(),"zs"));
//                dto.setOddsNameEn(changeVariable(dto.getOddsNameEn(),"en"));
//                oddsDtos.add(dto);
//            }
//            spSettleMarketDto.setOddsList(oddsDtos);
//            list.add(spSettleMarketDto);
//        }
//        return Response.success(list);
//    }
//    private String changeVariable(String str, String languageType) {
//        if(str ==null || str.isEmpty()){
//            return str;
//        }
//        if(!str.contains("{$competitor1}") && !str.contains("{$competitor2}")){
//            return str;
//        }
//        //替换变量  主客队
//        if("zs".equals(languageType)){
//            str = str.replace("{$competitor1}","主队");
//            str = str.replace("{$competitor2}","客队");
//        }else if("zh".equals(languageType)){
//            str = str.replace("{$competitor1}","主隊");
//            str = str.replace("{$competitor2}","客隊");
//        }else if("en".equals(languageType)){
//            str = str.replace("{$competitor1}","home");
//            str = str.replace("{$competitor2}","away");
//        }
//        return str;
//    }
//    @Override
//    public Response editSpOddsResult(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto) {
//        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(editMatchSettleSPOddsDto.getStandardMatchId());
//        MatchSettleSpOdds odds =matchSettleSpOddsMapper.selectByPrimaryKey(editMatchSettleSPOddsDto.getOddsId());
//        MatchSettleSpOdds oddsBefore = new MatchSettleSpOdds();
//        BeanUtils.copyProperties(odds,oddsBefore);
////        if(odds.getSettleStatus()!=null&&odds.getSettleStatus()==3){
////            return Response.failed("已经结算");
////        }
//        odds.setSettleResult(editMatchSettleSPOddsDto.getSettleResult());
//        odds.setOperater(editMatchSettleSPOddsDto.getOperatorName());
//        odds.setUserid(editMatchSettleSPOddsDto.getOperatorId());
//        odds.setSettleStatus(1);
//        odds.setModifyTime(System.currentTimeMillis());
//        matchSettleSpOddsMapper.updateByPrimaryKey(odds);
//        //推送wS
//        AutoSettleDataSourceDto autoSettleDataSourceDto =new AutoSettleDataSourceDto();
//        autoSettleDataSourceDto.setStandardMatchId(editMatchSettleSPOddsDto.getStandardMatchId().toString());
//        matchSettleWsProducer.pushSPSettleMatchStatus(autoSettleDataSourceDto);
//        //记录编辑日志
//        iMatchSettleLogService.spOddsResultAddLog(editMatchSettleSPOddsDto,oddsBefore,odds,standardMatchInfo,OperateLogTypeEnum.EDIT.getCode().toString());
//        return Response.success();
//    }
//
//    @Override
//    public Response confirmSpOddsResult(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto) {
//        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(editMatchSettleSPOddsDto.getStandardMatchId());
//        MatchSettleSpOdds odds =matchSettleSpOddsMapper.selectByPrimaryKey(editMatchSettleSPOddsDto.getOddsId());
//        if(odds.getSettleStatus()!=null&&odds.getSettleStatus()==3){
//            return Response.failed("已经结算");
//        }
//        if(odds.getSettleStatus()!=null&&odds.getSettleStatus()!=1){
//            return Response.failed("未编辑或者已经确认");
//        }
//        odds.setOperater(editMatchSettleSPOddsDto.getOperatorName());
//        odds.setUserid(editMatchSettleSPOddsDto.getOperatorId());
//        odds.setSettleStatus(2);
//        odds.setModifyTime(System.currentTimeMillis());
//        matchSettleSpOddsMapper.updateByPrimaryKey(odds);
//        //推送wS
//        AutoSettleDataSourceDto autoSettleDataSourceDto =new AutoSettleDataSourceDto();
//        autoSettleDataSourceDto.setStandardMatchId(editMatchSettleSPOddsDto.getStandardMatchId().toString());
//        matchSettleWsProducer.pushSPSettleMatchStatus(autoSettleDataSourceDto);
//        //记录确认日志
//        iMatchSettleLogService.spOddsResultAddLog(editMatchSettleSPOddsDto,odds,odds,standardMatchInfo,OperateLogTypeEnum.CONFIRM_SCORE.getCode().toString());
//        return Response.success();
//    }
//
//    @Override
//    public Response settleSpOddsResult(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto) {
//        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(editMatchSettleSPOddsDto.getStandardMatchId());
//        MatchSettleSpOdds odds =matchSettleSpOddsMapper.selectByPrimaryKey(editMatchSettleSPOddsDto.getOddsId());
//        if(odds.getSettleStatus()!=null&&odds.getSettleStatus()==3){
//            return Response.failed("已经结算");
//        }
//        if(odds.getSettleStatus()!=null&&odds.getSettleStatus()!=2){
//            return Response.failed("未确认");
//        }
//        odds.setOperater(editMatchSettleSPOddsDto.getOperatorName());
//        odds.setUserid(editMatchSettleSPOddsDto.getOperatorId());
//        odds.setSettleStatus(3);
//        odds.setModifyTime(System.currentTimeMillis());
//        odds.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
//        //结算总次数，不可回滚
//        odds.setSettleCount(odds.getSettleCount()==null?1:(odds.getSettleCount()+1));
//        //结算次数，判断是否一次结算 还是二次结算
//        odds.setSettleTimes(odds.getSettleTimes()==null?1:(odds.getSettleTimes()+1));
//        matchSettleSpOddsMapper.updateByPrimaryKey(odds);
//        //下发结算MQ
//        matchSettleSPOddsProducer.sendMatchSettleSPOdds(odds);
//        //推送wS
//        AutoSettleDataSourceDto autoSettleDataSourceDto =new AutoSettleDataSourceDto();
//        autoSettleDataSourceDto.setStandardMatchId(editMatchSettleSPOddsDto.getStandardMatchId().toString());
//        matchSettleWsProducer.pushSPSettleMatchStatus(autoSettleDataSourceDto);
//        //记录结算日志
//        iMatchSettleLogService.spOddsResultAddLog(editMatchSettleSPOddsDto,odds,odds,standardMatchInfo,OperateLogTypeEnum.SCORE_SETTLE.getCode().toString());
//        return Response.success();
//    }
//
//    @Override
//    public Response rollbackSpOddsResult(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto) {
//        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(editMatchSettleSPOddsDto.getStandardMatchId());
//        MatchSettleSpOddsExample example = new MatchSettleSpOddsExample();
//        example.createCriteria().andStandardMatchIdEqualTo(editMatchSettleSPOddsDto.getStandardMatchId()).andMarketIdEqualTo(editMatchSettleSPOddsDto.getMarketId()).andSettleStatusEqualTo(3);
//        List<MatchSettleSpOdds> oddsList = matchSettleSpOddsMapper.selectByExample(example);
//        if (null==oddsList||oddsList.isEmpty()){
//            return Response.failed("未结算");
//        }else{
//            oddsList.forEach(odds ->{
//                if(null!=odds.getSettleStatus()&&odds.getSettleStatus()==3){
//                    MatchSettleSpOdds oddsBefore = new MatchSettleSpOdds();
//                    BeanUtils.copyProperties(odds,oddsBefore);
//                    odds.setOperater(editMatchSettleSPOddsDto.getOperatorName());
//                    odds.setUserid(editMatchSettleSPOddsDto.getOperatorId());
//                    odds.setSettleStatus(0);
//                    odds.setSettleTimes(0);
//                    odds.setSettleResult(null);
//                    odds.setModifyTime(System.currentTimeMillis());
//                    odds.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.ROLL_BACK);
//                    matchSettleSpOddsMapper.updateByPrimaryKey(odds);
//
//                    //下发结算MQ
//                    matchSettleSPOddsProducer.sendMatchSettleSPOdds(odds);
//                    //推送wS
//                    AutoSettleDataSourceDto autoSettleDataSourceDto =new AutoSettleDataSourceDto();
//                    autoSettleDataSourceDto.setStandardMatchId(editMatchSettleSPOddsDto.getStandardMatchId().toString());
//                    matchSettleWsProducer.pushSPSettleMatchStatus(autoSettleDataSourceDto);
//                    //记录回滚日志
//                    iMatchSettleLogService.spOddsResultAddLog(editMatchSettleSPOddsDto,oddsBefore,odds,standardMatchInfo,OperateLogTypeEnum.ROLLBACK_SCORES_SETTLE.getCode().toString());
//                }
//
//        });
//    }
//        return Response.success();
//    }
//
//    @Override
//    public Response reSettleSpOddsResult(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto) {
//        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(editMatchSettleSPOddsDto.getStandardMatchId());
//        MatchSettleSpOddsExample example = new MatchSettleSpOddsExample();
//        example.createCriteria().andStandardMatchIdEqualTo(editMatchSettleSPOddsDto.getStandardMatchId()).andMarketIdEqualTo(editMatchSettleSPOddsDto.getMarketId()).andSettleStatusEqualTo(3);
//        List<MatchSettleSpOdds> oddsList = matchSettleSpOddsMapper.selectByExample(example);
//        if (null==oddsList||oddsList.isEmpty()){
//            return Response.failed("未结算");
//        }else{
//            oddsList.forEach(odds ->{
//                if(null!=odds.getSettleStatus()&&odds.getSettleStatus()==3){
//                    odds.setOperater(editMatchSettleSPOddsDto.getOperatorName());
//                    odds.setUserid(editMatchSettleSPOddsDto.getOperatorId());
//                    odds.setModifyTime(System.currentTimeMillis());
//                    odds.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
//                    matchSettleSpOddsMapper.updateByPrimaryKey(odds);
//                    //下发结算MQ
//                    matchSettleSPOddsProducer.sendMatchSettleSPOdds(odds);
//                    //记录重发日志
//                    iMatchSettleLogService.spOddsResultAddLog(editMatchSettleSPOddsDto,odds,odds,standardMatchInfo,OperateLogTypeEnum.ROLLBACK_EXECUTE.getCode().toString());
//                }
//            });
//        }
//        return Response.success();
//    }
//
//
//}
