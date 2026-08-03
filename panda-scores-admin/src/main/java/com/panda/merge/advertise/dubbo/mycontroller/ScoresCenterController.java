package com.panda.merge.advertise.dubbo.mycontroller;

import com.panda.merge.api.IScoresCenterApi;
import com.panda.merge.dto.Response;
//import com.panda.merge.dto.scores.ScoresCenterDTO;
//import com.panda.merge.model.SportScoreShowStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author warren
 * @since 2024/02/20 16:57:23
 */
@Slf4j
@RestController
@RequestMapping("/matchScoreCentre")
@Api(value = "赛事事件控制器", tags = {"赛事事件控制器"})
public class ScoresCenterController {
    @Autowired
    private IScoresCenterApi scoresCenterApi;


//    @PostMapping("/setSportResuleShowStatus")
//    @ApiOperation(value = "全部显示/隐藏", notes = "全部显示/隐藏")
//    public Response setSportResuleShowStatus(@RequestBody ScoresCenterDTO scoresCenter, HttpServletRequest request) {
//        return scoresCenterApi.setSportResuleShowStatus(scoresCenter);
//    }
//    @PostMapping("/editScoreCenterSettle")
//    @ApiOperation(value = "修改赛种赛果显示状态", notes = "修改赛种赛果显示状态")
//    public Response editScoreCenterSettle(@RequestBody SportScoreShowStatus sportScoreShowStatus, HttpServletRequest request) {
//        return scoresCenterApi.editScoreCenterSettle(sportScoreShowStatus);
//    }
}
