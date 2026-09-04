package com.panda.merge.component;

import com.panda.merge.common.enums.SystemTypeDictEnum;
import com.panda.merge.common.utils.BigDecimalUtils;
import com.panda.merge.model.EuropeConvertMalay;
import com.panda.merge.model.MalayConvertEurope;
import com.panda.merge.model.MatchEventType;
import com.panda.merge.model.SystemItemDict;
import com.panda.merge.service.EuropeConvertMalayService;
import com.panda.merge.service.MalayConvertEuropeService;
import com.panda.merge.service.MatchEventTypeService;
import com.panda.merge.service.SystemItemDictService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.component.AutoDiffCountMarketMalay.subDoubleTwo;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/10 <br>
 * @see com.panda.merge.component <br>
 */
@Component
public class InitializeComponent {

    @Autowired
    SystemItemDictService systemItemDictService;

    @Autowired
    MatchEventTypeService matchEventTypeService;

    @Autowired
    EuropeConvertMalayService europeConvertMalayService;

    @Autowired
    MalayConvertEuropeService malayConvertEuropeService;

    /**
     * 赛事阶段数据缓存
     * 描述：system_type_dict表 parent_type_id = 8, Map<addition1,Map<value,SystemItemDict>>
     */
    @Getter
    private volatile Map<Long,Map<Long,SystemItemDict>> matchPeriodData = new HashMap<>();

    /**
     * 事件类型数据缓存
     * 描述：match_event_type, Map<sport_id,Map<event_code,event_name>>
     */
    @Getter
    private volatile Map<Long,Map<String,MatchEventType>> matchEventTypeData = new HashMap<>();

    /**
     * 欧赔转马来赔缓存
     */
    @Getter
    private volatile  Map<Double,Double> europeConvertMalayMap = new HashMap<>();

    /**
     * 马来转欧赔缓存
     */
    @Getter
    private volatile  Map<Double,Double> malayConvertEuropeMap = new HashMap<>();


    @PostConstruct
    private void init() {
        //------------初始化查询字段表------------
        List<SystemItemDict> systemItemDictAll = systemItemDictService.getItemAll();
        Map<Long,List<SystemItemDict>> systemItemDictMap = systemItemDictAll.stream().collect(Collectors.groupingBy(SystemItemDict::getParentTypeId));
        //初始化赛事阶段
        List<SystemItemDict> sportTeamTypes = systemItemDictMap.get(SystemTypeDictEnum.MATCH_PERIOD.getCode());
        for (SystemItemDict systemItemDict : sportTeamTypes) {
            Map<Long,SystemItemDict> matchPeriodMap = matchPeriodData.get(Long.valueOf(systemItemDict.getAddition1()));
            if (null == matchPeriodMap) {
                matchPeriodMap = new HashMap<>(16);
                matchPeriodData.put(Long.valueOf(systemItemDict.getAddition1()),matchPeriodMap);
            }
            matchPeriodMap.put(Long.valueOf(systemItemDict.getValue()),systemItemDict);
        }

        //-----------初始化查询事件类型表-----------
        List<MatchEventType> matchEventTypeAll = matchEventTypeService.getItemAll();
        for (MatchEventType matchEventType : matchEventTypeAll) {
            Map<String,MatchEventType> matchEventTypeMap = matchEventTypeData.get(matchEventType.getSportId());
            if (null == matchEventTypeMap) {
                matchEventTypeMap = new HashMap<>(16);
                matchEventTypeData.put(matchEventType.getSportId(),matchEventTypeMap);
            }
            matchEventTypeMap.put(matchEventType.getEventCode(),matchEventType);
        }
        //-----------初始化欧赔转马来的对应关系-----------
        europeConvertMalayMap = europeConvertMalayService.listAll().stream()
                .collect(Collectors.toMap(EuropeConvertMalay::getEuropeValue, EuropeConvertMalay::getMalayValue));
        //-----------初始化马来转欧赔的对应关系-----------
        malayConvertEuropeMap = malayConvertEuropeService.listAll().stream()
                .collect(Collectors.toMap(MalayConvertEurope::getMalayValue, MalayConvertEurope::getEuropeValue));
    }

    /**
     * 马来转换欧赔
     * @param malayOddsValue
     * @return
     */
    public Double getConvertMalayToEurope(Double malayOddsValue){
        Double europeValue = this.getMalayConvertEuropeMap().get(malayOddsValue);
        if(null == europeValue){
            europeValue = 0D;
        }
        return  europeValue;
    }
    /**
     * 欧赔转换马来
     * @param paOddsValue
     * @return
     */
    public Double getConvertEuropeToMalay(Integer paOddsValue) {
        if (null == paOddsValue || 0 == paOddsValue) {
            return 0D;
        }
        Double malayValue = this.getEuropeConvertMalayMap().get(subDoubleTwo(BigDecimal.valueOf(paOddsValue).divide(new BigDecimal(Double.toString(100000))).doubleValue()));
        if (null == malayValue) {
            malayValue = 0D;
        }
        return malayValue;
    }

}
