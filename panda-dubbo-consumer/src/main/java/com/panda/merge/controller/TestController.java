package com.panda.merge.controller;

import com.panda.merge.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/12 <br>
 * @see com.panda.merge.controller <br>
 */
@RestController
public class TestController {

    @Autowired
    TestService testService;

    @GetMapping("/test")
    public void test(){
        testService.test();
    }

    @GetMapping("/testQuerySportTournamentPage")
    public void testQuerySportTournamentPage(){
        testService.testQuerySportTournamentPage();
    }

}
