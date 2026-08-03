package com.panda.merge.rocketmq.processor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.config.XxlJobConfig;
import com.panda.merge.dto.A99SystemConfigParam;
import com.panda.merge.dto.Request;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.job.LiveOddsJob;
import com.panda.merge.job.PreOddsJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.panda.merge.constant.XxlJobConstant.XXL_JOB_LOGIN_URL;
import static com.panda.merge.constant.XxlJobConstant.XXL_JOB_REDIS_COOKIE_KEY;

@Slf4j
@Validated
@Component
public class A99SystemParamProcessor extends BaseProcessor {

    @Resource
    private XxlJobConfig xxlJobConfig;

    @Resource
    private PreOddsJob preOddsJob;
    @Resource
    private LiveOddsJob liveOddsJob;



    @ExceptionHelper
    public void execute(Request<A99SystemConfigParam> request){
        if (request == null || request.getData() == null) {
            return;
        }

        A99SystemConfigParam a99SystemConfigParam = request.getData();
        if (ObjectUtil.isNotEmpty(a99SystemConfigParam)) {
            if (a99SystemConfigParam.getMatchType() != 0 && a99SystemConfigParam.getMatchType() != 1) {
                log.info("A99系统参数不合法：赛事类型{}", request);
                return;
            }
            if (a99SystemConfigParam.getInterval() > 60 || a99SystemConfigParam.getInterval() < 0) {
                log.info("A99系统参数不合法：间隔时间{}", request);
                return;
            }
            String cron = getCron(a99SystemConfigParam.getInterval());
            if (ObjectUtil.isEmpty(cron)) {
                log.info("A99系统参数不合法：cron表达式{}", request);
                return;
            }
            log.info("{}::准备修改定时任务,表达式:{},开关:{}", request.getLinkId(), cron, a99SystemConfigParam.getEnable());
            if (a99SystemConfigParam.getMatchType() == 0) {
//                preOddsJob.updateCronExpression(cron);
                return;
            } else if (a99SystemConfigParam.getMatchType() == 1) {
//                liveOddsJob.updateCronExpression(cron);
                return;
            }
//            int taskId = a99SystemConfigParam.getMatchType() == 0 ? xxlJobConfig.getPreTaskId() : xxlJobConfig.getLiveTaskId();
//            updateXxlJobCorn(request.getLinkId(), taskId, cron);
        }
    }


    /**
     *
     * @param taskId xxl-job定时任务的id，从xxl-job控制台查看
     * @param cron xxl-job表达式
     */
    public void updateXxlJobCorn(String linkId, int taskId, String cron){
        log.info("{}::修改xxl-job表达式,任务id:{}, 表达式:{}", linkId, taskId, cron);
        Map<String, Object> param = new HashMap<>();
        param.put("id", taskId);
        param.put("cron", cron);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String xxlJobCookie = getXxlJobCookie();
        if(ObjectUtil.isEmpty(xxlJobCookie)) {
            log.info("{}::获取xxl-job登录cookie失败,xxl-job修改未成功", linkId);
            return;
        }
        headers.add("Cookie", xxlJobCookie);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, Object>> requset = new HttpEntity<>(param, headers);
        String result = restTemplate.postForObject(xxlJobConfig.getAdminAddresses() + "/api/job/update", requset, String.class);
        log.info("{}::修改xxl-job表达式,返回结果:{}", linkId, result);
    }

    public String getCron(int interval){
        String cronTemplate = "0/{} * * * * ?";
        return StrUtil.format(cronTemplate, interval);
    }

    public String getXxlJobCookie(){
        Boolean flag = redisService.hasKey(XXL_JOB_REDIS_COOKIE_KEY);
        if(flag) {
            return (String) redisService.get(XXL_JOB_REDIS_COOKIE_KEY);
        } else {
            Map<String ,Object> param = new HashMap<>();
            param.put("userName", xxlJobConfig.getUserName());
            param.put("password", xxlJobConfig.getPassword());
            HttpResponse response = HttpRequest.post(xxlJobConfig.getAdminAddresses() + XXL_JOB_LOGIN_URL)
                    .form(param)
                    .execute();
            if(HttpStatus.HTTP_OK != response.getStatus()) {
                log.error("获取xxl-job登录cookie失败,{}", response);
                return null;
            }else {
                log.info("获取xxl-job登录cookie成功,{}", response);
                List<HttpCookie> cookies = response.getCookies();
                StringBuilder stringBuilder = new StringBuilder();
                for(HttpCookie httpCookie : cookies) {
                    stringBuilder.append(httpCookie.toString());
                }
                redisService.set(XXL_JOB_REDIS_COOKIE_KEY, stringBuilder.toString(), 7200);
                return stringBuilder.toString();
            }
        }
    }

}
