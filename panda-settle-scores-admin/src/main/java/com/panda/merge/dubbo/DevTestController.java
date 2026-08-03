//package com.panda.merge.dubbo;
//
//import com.panda.merge.api.IFootballMatchScoresSettleApi;
//import com.panda.merge.api.IFootballNewMatchScoresSettleApi;
//import com.panda.merge.api.ISettleCenterApi;
//import com.panda.merge.api.ISettleTemplateApi;
//import com.panda.merge.dto.CommonThirdScoresDto;
//import com.panda.merge.dto.Request;
//import com.panda.merge.dto.Response;
//import com.panda.merge.dto.settle.*;
//import com.panda.merge.model.MatchEventInfo;
//import com.panda.merge.mq.consumer.StandardMatchEventConsumer;
//import com.panda.merge.mq.consumer.StandardMatchScoreConsumer;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import javax.validation.Valid;
//import java.util.List;
//
///**
// * @description: dev test controller
// * @author: Henry Wang
// * @create: 2024-09-06 14:29
// **/
//@RestController
//@RequestMapping("/v1/dev/test")
//public class DevTestController {
//
//    @Resource
//    private StandardMatchEventConsumer standardMatchEventConsumer;
//
//    @Resource
//    private StandardMatchScoreConsumer standardMatchScoreConsumer;
//
//    @Resource
//    private IFootballNewMatchScoresSettleApi iFootballNewMatchScoresSettleApi;
//
//    @Resource
//    private IFootballMatchScoresSettleApi iFootballMatchScoresSettleApi;
//
//    @Resource
//    private ISettleTemplateApi iSettleTemplateApi;
//
//    @Resource
//    private ISettleCenterApi iSettleCenterApi;
//
//    @PostMapping("/consumeEvent")
//    public void consumeEvent(@RequestBody Request<List<MatchEventInfo>> mq){
//        standardMatchEventConsumer.onMessage(mq);
//    }
//
//    @PostMapping("/consumeScore")
//    public void consumeScore(@RequestBody Request<CommonThirdScoresDto> commonStandardScoresDtoRequest){
//        standardMatchScoreConsumer.onMessage(commonStandardScoresDtoRequest);
//    }
//
//    @PostMapping("/getSettleEventMentionStatus")
//    public Response<AbstractMentionQueryDto> getSettleEventMentionStatus(@RequestBody MentionQueryRequest mentionQueryRequest){
//        return iFootballNewMatchScoresSettleApi.getSettleEventMentionStatus(mentionQueryRequest);
//    }
//
//    @PostMapping("/cancelSettleEventMention")
//    public Response<String> cancelSettleEventMention(@RequestBody SettleEventDeleteRequest settleEventDeleteRequest){
//        return iFootballNewMatchScoresSettleApi.cancelSettleEventMention(settleEventDeleteRequest);
//    }
//
//    @PostMapping("/searchMatchSettleEvent")
//    public List<MatchSettleEventDto> searchMatchSettleEvent(@RequestBody MatchSettleScoreSearchDto settleScoreSearchDto){
//        return iFootballMatchScoresSettleApi.searchMatchSettleEvent(settleScoreSearchDto);
//    }
//
//    @PostMapping("/searchMatchSettleScores")
//    public List<MatchSettleScoreDto> searchMatchSettleScores(@RequestBody MatchSettleScoreSearchDto settleScoreSearchDto){
//        return iFootballMatchScoresSettleApi.searchMatchSettleScores(settleScoreSearchDto);
//    }
//
//    @PostMapping("/updateMatchSettleScore")
//    public Response updateMatchSettleScore(@RequestBody UpdateMatchSettleScoreDto matchSettleScoreDto){
//        return iFootballMatchScoresSettleApi.updateMatchSettleScore(matchSettleScoreDto);
//    }
//
//    @PostMapping("/confirmMatchSettleScore")
//    public Response confirmMatchSettleScore(@RequestBody ConfirmMatchSettleScoreDto matchSettleScoreDto){
//        return iFootballMatchScoresSettleApi.confirmMatchSettleScore(matchSettleScoreDto);
//    }
//
//    @PostMapping("/settleMatchScore")
//    public Response settleMatchScore(@RequestBody SettleMatchScoreDto matchSettleScoreDto){
//        return iFootballMatchScoresSettleApi.settleMatchScore(matchSettleScoreDto);
//    }
//
//    @PostMapping("/template/editWeightTemplate")
//    public Response editWeightTemplate(@RequestBody SettleWeightTemplateUpdateDto settleTemplateUpdateDto){
//        return iSettleTemplateApi.editWeightTemplate(settleTemplateUpdateDto);
//    }
//
//    @PostMapping("/template/getDataSourceAllWeightByCode")
//    public Response getDataSourceAllWeightByCode(@RequestBody MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto){
//        return iSettleTemplateApi.getDataSourceAllWeightByCode(matchSettleDataSourceWeightAndSwitchDto);
//    }
//
//    @PostMapping("/template/editDataSourceAllWeight")
//    public Response editDataSourceAllWeight(@RequestBody DataSourceWeightUpdateDto DataSourceWeightUpdateDto){
//        return iSettleTemplateApi.editDataSourceAllWeight(DataSourceWeightUpdateDto);
//    }
//    @PostMapping("/template/addTemplate")
//    public Response addTemplate(@RequestBody MatchSettleTemplateDto matchSettleTemplateDto){
//        return iSettleTemplateApi.addTemplate(matchSettleTemplateDto);
//    }
//
//    @PostMapping("/template/list")
//    public Response templateList(@RequestBody TemplateListSearchDto templateListSearchDto){
//        return iSettleTemplateApi.list(templateListSearchDto);
//    }
//
//    @GetMapping("/settleCenterApi/goalPlayer")
//    public Response goalPlayer(){
//        return iSettleCenterApi.goalPlayer("linkId", 1l, 12321313l);
//    }
//}
