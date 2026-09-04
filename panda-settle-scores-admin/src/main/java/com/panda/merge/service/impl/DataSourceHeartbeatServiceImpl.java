package com.panda.merge.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.SettleTemplateTypeEnum;
import com.panda.merge.dto.DataSourceSettleWeightDto;
import com.panda.merge.model.*;
import com.panda.merge.dto.settle.DataSourceConnectionStatusDto;
import com.panda.merge.service.*;
import com.panda.merge.utils.SettleTemplateJsonUtils;
import com.panda.merge.v2.repository.MatchSettleDataSourceSwitchRepository;
import com.panda.merge.v2.repository.MatchSettleTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据商心跳服务实现类
 * @author system
 */
@Slf4j
@Service
public class DataSourceHeartbeatServiceImpl implements IDataSourceHeartbeatService {
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    private MatchSettleDataSourceSwitchRepository matchSettleDataSourceSwitchRepository;
    
    @Autowired
    private MatchSettleTemplateRepository matchSettleTemplateRepository;
    
    @Autowired
    private IWsPushService wsPushService;
    
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    
    @Autowired
    private StandardSportTournamentService standardSportTournamentService;
    
    @Autowired(required = false)
    private com.panda.merge.mq.producer.DataSourceConnectionStatusProducer dataSourceConnectionStatusProducer;

    @Autowired
    ISettleTemplateService settleTemplateService;
    
    // 8小时的毫秒数
    private static final long EIGHT_HOURS_MILLIS = 8 * 60 * 60 * 1000L;
    private static final List<String> datasourceSettleFilter = Arrays.asList("N01", "N02", "N03", "L01");
    
    @Override
    public void updateDataSourceTimestamp(MatchEventInfo matchEventInfo) {
        try {
            if(datasourceSettleFilter.contains(matchEventInfo.getDataSourceCode())) {
                return;
            }
            String dataSourceCode = matchEventInfo.getDataSourceCode();
            Long standardMatchId = matchEventInfo.getStandardMatchId();
            
            // 构建Hash field: 比赛id+数据源格式
            String field = buildTimestampField(standardMatchId, dataSourceCode);
            long currentTime = System.currentTimeMillis();
            
            // 使用Hash结构存储，key为DATASOURCE_HEARTBEAT_TIMESTAMP，field为比赛id+数据源，value为时间戳
            // 设置过期时间为30天
            redisService.hSet(CommonConstant.DATASOURCE_HEARTBEAT_TIMESTAMP, field, String.valueOf(currentTime), RedisConfig.REDIS_WEEK_TIME);
            
            log.debug("linkId::{}::更新数据商时间戳成功, key:{}, field:{}, timestamp:{}", 
                    matchEventInfo.getLinkId(), CommonConstant.DATASOURCE_HEARTBEAT_TIMESTAMP, field, currentTime);
        } catch (Exception e) {
            log.error("linkId::{}::更新数据商时间戳失败, matchEventInfo:{}", 
                    matchEventInfo.getLinkId(), JSON.toJSONString(matchEventInfo), e);
        }
    }
    
    @Override
    public Integer getHeartbeatConfigSeconds(String dataSourceCode, Long sportId, Long standardMatchId) {
        try {
            // 从模板中查询心跳配置时间
            MatchSettleTemplate template = settleTemplateService.getTemplateByStandardMatchId(standardMatchId, SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
            if (template == null || template.getTemplateJson() == null) {
                return null;
            }
            
            // 使用工具类解析templateJson
            String templateJsonStr = template.getTemplateJson();
            try {
                // 使用工具类将JSON转换为DataSourceSettleWeightDto列表
                java.util.List<DataSourceSettleWeightDto> dataSourceSettleWeightDtos = 
                        SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(templateJsonStr);
                
                if (dataSourceSettleWeightDtos != null && !dataSourceSettleWeightDtos.isEmpty()) {
                    // 查找匹配的数据商配置
                    for (DataSourceSettleWeightDto dto : dataSourceSettleWeightDtos) {
                        if (dataSourceCode.equals(dto.getDataSourceCode())) {
                            Integer heartbeatSecond = dto.getHeartbeatSecond();
                            if (heartbeatSecond != null) {
                                return heartbeatSecond;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("解析templateJson失败: {}", templateJsonStr, e);
            }
            
            return null;
        } catch (Exception e) {
            log.error("查询数据商心跳配置时间失败, dataSourceCode:{}, sportId:{}, standardMatchId:{}",
                    dataSourceCode, sportId, standardMatchId, e);
            return null;
        }
    }
    
    @Override
    public boolean checkAndUpdateConnectionStatus(Long standardMatchId, String dataSourceCode, Long sportId, Integer tournamentLevel) {
        try {
            // 首先检查数据商心跳开关是否开启
            List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchRepository.getMatchSettleDataSourceSwitchByRedis(sportId, dataSourceCode);
            boolean heartbeatSwitchEnabled = false;
            if (switches != null && !switches.isEmpty()) {
                MatchSettleDataSourceSwitch switchConfig = switches.get(0);
                // dataSourceHeartbeat 为 1 表示开启，0 表示关闭
                heartbeatSwitchEnabled = switchConfig.getDataSourceHeartbeat() != null && switchConfig.getDataSourceHeartbeat() == SettleTemplateTypeEnum.ON_CODE.code;
            }
            
            // Hash field使用比赛维度
            String field = buildTimestampField(standardMatchId, dataSourceCode);
            // 连接状态key使用比赛维度
            String matchStatusKey = buildMatchConnectionStatusKey(standardMatchId, dataSourceCode);
            
            // 如果心跳开关未开启，设置状态为0（开关未开启）
            if (!heartbeatSwitchEnabled) {
                boolean statusChanged = updateMatchConnectionStatus(matchStatusKey, 0, standardMatchId, dataSourceCode);
                // 如果状态变更，推送到topic
                if (statusChanged && dataSourceConnectionStatusProducer != null) {
                    pushConnectionStatusChange(standardMatchId, dataSourceCode, 0);
                }
                return false;
            }
            
            // 从Hash中获取当前时间戳
            Object timestampObj = redisService.hGet(CommonConstant.DATASOURCE_HEARTBEAT_TIMESTAMP, field);
            long currentTime = System.currentTimeMillis();
            if (timestampObj == null) {
                // 没有时间戳记录，但此时有新事件到来，更新为当前时间戳，状态设为1（连接状态）
                redisService.hSet(CommonConstant.DATASOURCE_HEARTBEAT_TIMESTAMP, field, String.valueOf(currentTime), RedisConfig.REDIS_WEEK_TIME);
                boolean statusChanged = updateMatchConnectionStatus(matchStatusKey, 1, standardMatchId, dataSourceCode);
                // 如果状态变更，推送到topic
                if (statusChanged && dataSourceConnectionStatusProducer != null) {
                    pushConnectionStatusChange(standardMatchId, dataSourceCode, 1);
                }
                return true;
            }
            
            long lastTimestamp = Long.parseLong(timestampObj.toString());
            long timeDiff = currentTime - lastTimestamp;
            
            // 获取心跳配置时间（秒），转换为毫秒
            Integer heartbeatSeconds = getHeartbeatConfigSeconds(dataSourceCode, sportId, standardMatchId);
            if (heartbeatSeconds == null) {
                // 未配置心跳时间，默认认为断连
                boolean statusChanged = updateMatchConnectionStatus(matchStatusKey, 2, standardMatchId, dataSourceCode);
                // 如果状态变更，推送到topic
                if (statusChanged && dataSourceConnectionStatusProducer != null) {
                    pushConnectionStatusChange(standardMatchId, dataSourceCode, 2);
                }
                return false;
            }
            
            long heartbeatMillis = heartbeatSeconds * 1000L;
            
            // 检查是否在维护时间内
            DataSourceMaintenanceTime maintenanceTime = getMaintenanceTime(dataSourceCode);
            if (maintenanceTime != null && maintenanceTime.isInMaintenanceTime(currentTime)) {
                // 在维护时间内，视为断连，设置状态为2（断连）
                boolean statusChanged = updateMatchConnectionStatus(matchStatusKey, 2, standardMatchId, dataSourceCode);
                // 如果状态变更，推送到topic
                if (statusChanged && dataSourceConnectionStatusProducer != null) {
                    pushConnectionStatusChange(standardMatchId, dataSourceCode, 2);
                }
                return false;
            }
            
//            // 判断是否断连：如果时间差超过心跳配置时间，认为断连
//            // 状态：1=开关开启且连接，2=开关开启且断连
//            Integer connectionStatus = (timeDiff <= heartbeatMillis) ? 1 : 2;
            Integer connectionStatus = 1;

            // 更新比赛维度的连接状态
            boolean statusChanged = updateMatchConnectionStatus(matchStatusKey, connectionStatus, standardMatchId, dataSourceCode);

            // 如果连接状态改变，发送WS通知前端和推送到topic
            if (statusChanged) {
                sendMatchConnectionStatusChangeNotification(standardMatchId, dataSourceCode, true);
                // 推送到topic
                if (dataSourceConnectionStatusProducer != null) {
                    pushConnectionStatusChange(standardMatchId, dataSourceCode, connectionStatus);
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("判断数据商连接状态失败, standardMatchId:{}, dataSourceCode:{}, sportId:{}, tournamentLevel:{}", 
                    standardMatchId, dataSourceCode, sportId, tournamentLevel, e);
            return false;
        }
    }
    
    @Override
    public Boolean getMatchConnectionStatus(Long standardMatchId, String dataSourceCode) {
        try {
            String matchStatusKey = buildMatchConnectionStatusKey(standardMatchId, dataSourceCode);
            Object statusObj = redisService.get(matchStatusKey);
            if (statusObj == null) {
                return null; // 未找到状态记录
            }
            
            // 兼容新旧格式：新格式是Integer（0/1/2），旧格式是Boolean
            try {
                // 尝试解析为Integer
                Integer status = Integer.parseInt(statusObj.toString());
                // 状态：0=开关未开启或维护状态，1=开关开启且连接，2=开关开启且断连
                // 只返回true（连接）或false（断连/维护/开关未开启）
                // 对于5/15分钟阶段，需要检查连接状态，所以返回 status == 1
                return status == 1;
            } catch (NumberFormatException e) {
                // 兼容旧格式（boolean），转换为Boolean
                return Boolean.parseBoolean(statusObj.toString());
            }
        } catch (Exception e) {
            log.error("获取比赛连接状态失败, standardMatchId:{}, dataSourceCode:{}", standardMatchId, dataSourceCode, e);
            return null;
        }
    }
    
    @Override
    public Integer getTournamentLevel(Long standardMatchId) {
        return getTournamentLevelInternal(standardMatchId);
    }

    @Override
    public java.util.List<DataSourceConnectionStatusDto> scanAllMatchesConnectionStatus() {
        log.info("scanAllMatchesConnectionStatus start...");
        java.util.List<DataSourceConnectionStatusDto> resultList = new java.util.ArrayList<>();
        
        try {
            long startTime = System.currentTimeMillis();
            
            // 1. 从Redis获取所有有事件记录的赛事ID列表
            java.util.Set<Long> matchIds = getActiveMatchIdsFromRedis();
            
            if (matchIds == null || matchIds.isEmpty()) {
                return resultList;
            }
            
            log.info("scanAllMatchesConnectionStatus 开始扫描数据商连接状态，赛事数量: {}", matchIds.size());
            
            int checkedCount = 0;
            int statusChangedMatchCount = 0;
            
            // 2. 遍历每个赛事，检查所有数据商的连接状态
            for (Long standardMatchId : matchIds) {
                try {
                    log.info("scanAllMatchesConnectionStatus inside start:{}", standardMatchId);
                    StandardMatchInfo match = standardMatchInfoService.getItem(standardMatchId);
                    if (match == null) {
                        continue;
                    }
                    
                    // 只处理未结束的赛事：matchStatus in (0, 1, 2)
//                    Integer matchStatus = match.getMatchStatus();
//                    if (matchStatus == null || (matchStatus != 0 && matchStatus != 1 && matchStatus != 2)) {
//                        continue;
//                    }
//
//                    // 完赛的赛事跳过
//                    if (match.getMatchOver() != null && match.getMatchOver() == 1) {
//                        continue;
//                    }
                    log.info("scanAllMatchesConnectionStatus inside 2");
                    Long sportId = match.getSportId();
                    
                    // 获取联赛等级
//                    Integer tournamentLevel = getTournamentLevelInternal(standardMatchId);
//                    if (tournamentLevel == null) {
//                        continue;
//                    }
                    
                    // 获取该赛事关联的所有数据商列表
                    java.util.Set<String> dataSourceCodes = getDataSourceCodesForMatch(standardMatchId);
                    log.info("scanAllMatchesConnectionStatus inside 3 dataSourceCodes:{}", dataSourceCodes);
                    if (dataSourceCodes.isEmpty()) {
                        continue;
                    }
                    
                    // 3. 收集该赛事所有数据商的状态（包括状态改变和未改变的）
                    java.util.Map<String, Integer> datasourceStatusMap = new java.util.HashMap<>();
                    boolean hasStatusChanged = false;
                    
                    for (String dataSourceCode : dataSourceCodes) {
                        try {
                            // 计算连接状态码
                            Integer newStatus = calculateConnectionStatus(standardMatchId, dataSourceCode, sportId);
                            log.info("scanAllMatchesConnectionStatus inside 3 dataSourceCode:{} newStatus:{}", dataSourceCode, newStatus);
                            // 获取旧的状态码
                            String matchStatusKey = buildMatchConnectionStatusKey(standardMatchId, dataSourceCode);
                            Object oldStatusObj = redisService.get(matchStatusKey);
                            log.info("scanAllMatchesConnectionStatus inside 3 dataSourceCode:{} oldStatusObj:{}", dataSourceCode, oldStatusObj);
                            Integer oldStatus = null;
                            if (oldStatusObj != null) {
                                try {
                                    oldStatus = Integer.parseInt(oldStatusObj.toString());
                                } catch (Exception e) {
                                    // 兼容旧数据（可能是boolean类型）
                                    Boolean oldBooleanStatus = Boolean.parseBoolean(oldStatusObj.toString());
                                    oldStatus = oldBooleanStatus ? 1 : 2;
                                }
                            }
                            
                            // 将当前状态添加到映射中（无论是否改变）
                            datasourceStatusMap.put(dataSourceCode, newStatus);
                            
                            // 检查状态是否改变
                            if (oldStatus == null || !oldStatus.equals(newStatus)) {
                                // 状态改变，更新Redis
                                redisService.set(matchStatusKey, String.valueOf(newStatus), RedisConfig.REDIS_MONTH_TIME);
                                
                                log.info("比赛数据商连接状态更新, standardMatchId:{}, dataSourceCode:{}, oldStatus:{}, newStatus:{}", 
                                        standardMatchId, dataSourceCode, oldStatus, newStatus);
                                
                                hasStatusChanged = true;
                            }
                            log.info("scanAllMatchesConnectionStatus inside 3 dataSourceCode:{} hasStatusChanged:{}", dataSourceCode, hasStatusChanged);
                            checkedCount++;
                            
                        } catch (Exception e) {
                            log.error("检查数据商连接状态失败, standardMatchId:{}, dataSourceCode:{}", 
                                    standardMatchId, dataSourceCode, e);
                        }
                    }
                    log.info("scanAllMatchesConnectionStatus inside 4 datasourceStatusMap:{}", datasourceStatusMap);
                    // 4. 如果该赛事有状态改变，或者首次扫描（需要推送所有状态），创建DTO并推送
                    // TODO
//                    if (hasStatusChanged || !datasourceStatusMap.isEmpty()) {
                    if (!datasourceStatusMap.isEmpty()) {
                        DataSourceConnectionStatusDto dto = new DataSourceConnectionStatusDto();
                        dto.setStandardMatchId(standardMatchId);
                        dto.setDatasourceStatusMap(datasourceStatusMap);
                        dto.setTimestamp(System.currentTimeMillis());
                        resultList.add(dto);
                        
                        if (hasStatusChanged) {
                            statusChangedMatchCount++;
                            
                            // 状态改变时立即推送（复用XXL-Job的推送逻辑）
                            if (dataSourceConnectionStatusProducer != null) {
                                try {
                                    dataSourceConnectionStatusProducer.pushDataSourceConnectionStatus(dto);
                                    log.info("推送数据商连接状态成功, standardMatchId:{}, datasourceCount:{}",
                                            standardMatchId, datasourceStatusMap.size());
                                } catch (Exception e) {
                                    log.error("推送数据商连接状态失败, standardMatchId:{}", standardMatchId, e);
                                }
                            }
                        }
                    }
                    log.info("scanAllMatchesConnectionStatus inside 5");
                } catch (Exception e) {
                    log.error("scanAllMatchesConnectionStatus 处理赛事连接状态检查失败, matchId:{}", standardMatchId, e);
                }
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            if (duration > 1000) {
                log.warn("scanAllMatchesConnectionStatus 数据商连接状态扫描耗时较长: {}ms, 赛事数量: {}, 检查次数: {}, 推送赛事数量: {}, 状态改变赛事数量: {}",
                        duration, matchIds.size(), checkedCount, resultList.size(), statusChangedMatchCount);
            } else {
                log.info("scanAllMatchesConnectionStatus 数据商连接状态扫描完成, 耗时: {}ms, 赛事数量: {}, 检查次数: {}, 推送赛事数量: {}, 状态改变赛事数量: {}",
                        duration, matchIds.size(), checkedCount, resultList.size(), statusChangedMatchCount);
            }
            
        } catch (Exception e) {
            log.error("scanAllMatchesConnectionStatus 扫描所有赛事连接状态失败", e);
        }
        
        return resultList;
    }

    @Override
    public DataSourceConnectionStatusDto scanMatchConnectionStatus(Long standardMatchId) {
        if (standardMatchId == null) {
            log.warn("scanMatchConnectionStatus: standardMatchId is null");
            return null;
        }
        
        try {
            // 获取该赛事关联的所有数据商列表
            java.util.Set<String> dataSourceCodes = getDataSourceCodesForMatch(standardMatchId);
            
            if (dataSourceCodes == null || dataSourceCodes.isEmpty()) {
                log.debug("scanMatchConnectionStatus: 未找到赛事关联的数据商, standardMatchId:{}", standardMatchId);
                return null;
            }
            
            // 从Redis直接读取状态，不重新计算
            java.util.Map<String, Integer> datasourceStatusMap = new java.util.HashMap<>();
            
            for (String dataSourceCode : dataSourceCodes) {
                if ("N01".equals(dataSourceCode) || "N02".equals(dataSourceCode) || "N03".equals(dataSourceCode)){
                    continue;
                }
                try {
                    String matchStatusKey = buildMatchConnectionStatusKey(standardMatchId, dataSourceCode);
                    Object statusObj = redisService.get(matchStatusKey);
                    
                    if (statusObj != null) {
                        try {
                            // 尝试解析为Integer状态码（0,1,2）
                            Integer status = Integer.parseInt(statusObj.toString());
                            datasourceStatusMap.put(dataSourceCode, status);
                        } catch (NumberFormatException e) {
                            // 兼容旧数据（可能是boolean类型）
                            try {
                                Boolean booleanStatus = Boolean.parseBoolean(statusObj.toString());
                                Integer status = booleanStatus ? 1 : 2;
                                datasourceStatusMap.put(dataSourceCode, status);
                            } catch (Exception ex) {
                                log.warn("无法解析连接状态, standardMatchId:{}, dataSourceCode:{}, statusObj:{}", 
                                        standardMatchId, dataSourceCode, statusObj, ex);
                            }
                        }
                    }
                    // 如果Redis中没有状态记录，则不添加到map中（表示该数据商暂无状态记录）
                    
                } catch (Exception e) {
                    log.error("读取数据商连接状态失败, standardMatchId:{}, dataSourceCode:{}", 
                            standardMatchId, dataSourceCode, e);
                }
            }
            
            // 如果没有读取到任何状态，返回null
            if (datasourceStatusMap.isEmpty()) {
                log.debug("scanMatchConnectionStatus: 未找到任何状态记录, standardMatchId:{}", standardMatchId);
                return null;
            }
            
            // 构建并返回DTO
            DataSourceConnectionStatusDto dto = new DataSourceConnectionStatusDto();
            dto.setStandardMatchId(standardMatchId);
            dto.setDatasourceStatusMap(datasourceStatusMap);
            dto.setTimestamp(System.currentTimeMillis());
            
            log.debug("scanMatchConnectionStatus: 成功读取状态, standardMatchId:{}, dataSourceCount:{}", 
                    standardMatchId, datasourceStatusMap.size());
            
            return dto;
            
        } catch (Exception e) {
            log.error("扫描赛事连接状态失败, standardMatchId:{}", standardMatchId, e);
            return null;
        }
    }

    /**
     * 从Redis Hash获取有事件记录的赛事ID列表，并清理超过8小时的时间戳
     */
    private java.util.Set<Long> getActiveMatchIdsFromRedis() {
        try {
            // 从Hash中获取所有field-value对
            java.util.Map<String, Object> hashMap = redisService.hGetAll(CommonConstant.DATASOURCE_HEARTBEAT_TIMESTAMP);
            log.info("scanAllMatchesConnectionStatus getActiveMatchIdsFromRedis hashMap: {}", hashMap);
            if (hashMap == null || hashMap.isEmpty()) {
                return new java.util.HashSet<>();
            }
            
            long currentTime = System.currentTimeMillis();
            java.util.Set<Long> matchIds = new java.util.HashSet<>();
            java.util.List<String> expiredFields = new java.util.ArrayList<>();
            
            // 遍历所有field-value对
            for (java.util.Map.Entry<String, Object> entry : hashMap.entrySet()) {
                try {
                    String field = entry.getKey();
                    Object valueObj = entry.getValue();
                    
                    if (valueObj == null) {
                        continue;
                    }
                    
                    // 解析时间戳
                    long timestamp = Long.parseLong(valueObj.toString());
                    long timeDiff = currentTime - timestamp;
                    
                    // 检查时间戳是否超过8小时
                    if (timeDiff > EIGHT_HOURS_MILLIS) {
                        // 超过8小时，标记为过期，需要移除
                        expiredFields.add(field);
                        log.info("发现过期时间戳，field:{}, timestamp:{}, 距离当前时间:{}ms",
                                field, timestamp, timeDiff);
                        continue;
                    }
                    
                    // 解析field格式: {standardMatchId}:{dataSourceCode}
                    String[] parts = field.split(":");
                    if (parts.length >= 1) {
                        Long matchId = Long.parseLong(parts[0]);
                        matchIds.add(matchId);
                    }
                    
                } catch (Exception e) {
                    log.warn("解析时间戳field失败: {}", entry.getKey(), e);
                }
            }
            log.info("scanAllMatchesConnectionStatus getActiveMatchIdsFromRedis expiredFields:{}", expiredFields);
            // 移除超过8小时的时间戳
            if (!expiredFields.isEmpty()) {
                try {
                    redisService.hDel(CommonConstant.DATASOURCE_HEARTBEAT_TIMESTAMP, expiredFields.toArray());
                    log.info("清理过期时间戳完成, 清理数量: {}", expiredFields.size());
                } catch (Exception e) {
                    log.error("清理过期时间戳失败", e);
                }
            }
            log.info("scanAllMatchesConnectionStatus getActiveMatchIdsFromRedis finish matchIds: {}", matchIds);
            return matchIds;
            
        } catch (Exception e) {
            log.error("scanAllMatchesConnectionStatus 从Redis获取赛事列表失败", e);
            return new java.util.HashSet<>();
        }
    }
    
    /**
     * 获取赛事关联的所有数据商列表
     */
    private java.util.Set<String> getDataSourceCodesForMatch(Long standardMatchId) {
        java.util.Set<String> dataSourceCodes = new java.util.HashSet<>();
        
        try {
            // 从Hash中获取所有field-value对
            java.util.Map<String, Object> hashMap = redisService.hGetAll(CommonConstant.DATASOURCE_HEARTBEAT_TIMESTAMP);
            
            if (hashMap == null || hashMap.isEmpty()) {
                return dataSourceCodes;
            }
            
            // 遍历所有field，查找匹配的赛事ID
            for (String field : hashMap.keySet()) {
                try {
                    // field格式: {standardMatchId}:{dataSourceCode}
                    String[] parts = field.split(":");
                    if (parts.length >= 2) {
                        Long matchId = Long.parseLong(parts[0]);
                        if (matchId.equals(standardMatchId)) {
                            String dataSourceCode = parts[1];
                            dataSourceCodes.add(dataSourceCode);
                        }
                    }
                } catch (Exception e) {
                    log.warn("解析数据商编码失败, field:{}", field, e);
                }
            }
            
        } catch (Exception e) {
            log.error("获取赛事数据商列表失败, standardMatchId:{}", standardMatchId, e);
        }
        
        return dataSourceCodes;
    }
    
    /**
     * 计算数据商连接状态码
     * @param standardMatchId 标准赛事ID
     * @param dataSourceCode 数据商编码
     * @param sportId 球种ID
     * @param standardMatchId 联赛等级
     * @return 0=开关未开启（前端不展示），1=开关开启且连接，2=开关开启且断连或维护
     */
    private Integer calculateConnectionStatus(Long standardMatchId, String dataSourceCode, Long sportId) {
        try {
            long currentTime = System.currentTimeMillis();
            
            // 1. 检查数据商心跳开关是否开启
            boolean switchEnabled = isDataSourceHeartbeatSwitchEnabled(dataSourceCode, sportId);
            if (!switchEnabled) {
                // 开关未开启，返回0（前端不展示）
                return 0;
            }
            
            // 2. 检查是否在维护时间内
            DataSourceMaintenanceTime maintenanceTime = getMaintenanceTime(dataSourceCode);
            if (maintenanceTime != null && maintenanceTime.isInMaintenanceTime(currentTime)) {
                // 在维护时间内，视为断连，返回2
                return 2;
            }
            
            // 3. 检查连接状态 - 从Hash中获取时间戳
            String field = buildTimestampField(standardMatchId, dataSourceCode);
            Object timestampObj = redisService.hGet(CommonConstant.DATASOURCE_HEARTBEAT_TIMESTAMP, field);
            if (timestampObj == null) {
                // 没有时间戳记录，认为断连，返回2
                return 2;
            }
            
            long lastTimestamp = Long.parseLong(timestampObj.toString());
            long timeDiff = currentTime - lastTimestamp;
            
            // 获取心跳配置时间（秒），转换为毫秒
            Integer heartbeatSeconds = getHeartbeatConfigSeconds(dataSourceCode, sportId, standardMatchId);
            if (heartbeatSeconds == null) {
                // 未配置心跳时间，认为断连，返回2
                return 2;
            }
            
            long heartbeatMillis = heartbeatSeconds * 1000L;
            
            // 判断是否断连：如果时间差超过心跳配置时间，认为断连
            boolean isConnected = timeDiff <= heartbeatMillis;
            
            // 返回状态：1=连接，2=断连
            return isConnected ? 1 : 2;
            
        } catch (Exception e) {
            log.error("计算数据商连接状态失败, standardMatchId:{}, dataSourceCode:{}, sportId:{}, standardMatchId:{}",
                    standardMatchId, dataSourceCode, sportId, standardMatchId, e);
            return 0; // 异常情况返回0，前端不展示
        }
    }
    
    /**
     * 检查数据商心跳开关是否开启
     */
    private boolean isDataSourceHeartbeatSwitchEnabled(String dataSourceCode, Long sportId) {
        try {
            java.util.List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchRepository
                    .getMatchSettleDataSourceSwitchByRedis(sportId, dataSourceCode);
            
            if (switches == null || switches.isEmpty()) {
                // 未找到开关配置，默认认为关闭
                return false;
            }
            
            // 取第一个开关配置（通常只有一个）
            MatchSettleDataSourceSwitch switchConfig = switches.get(0);
            Integer dataSourceHeartbeat = switchConfig.getDataSourceHeartbeat();
            
            // 1表示开启，0表示关闭
            return dataSourceHeartbeat != null && dataSourceHeartbeat.equals(SettleTemplateTypeEnum.ON_CODE.code);
            
        } catch (Exception e) {
            log.error("检查数据商心跳开关状态失败, dataSourceCode:{}, sportId:{}", dataSourceCode, sportId, e);
            return false; // 异常情况认为关闭
        }
    }
    
    @Override
    public DataSourceMaintenanceTime getMaintenanceTime(String dataSourceCode) {
        try {
            String maintenanceKey = CommonConstant.DATASOURCE_MAINTENANCE_TIME + dataSourceCode;
            Object value = redisService.get(maintenanceKey);
            if (value == null) {
                return null;
            }
            
            // Redis中存储格式为: "startTime,endTime"
            String[] times = value.toString().split(",");
            if (times.length == 2) {
                Long startTime = Long.parseLong(times[0]);
                Long endTime = Long.parseLong(times[1]);
                return new DataSourceMaintenanceTime(startTime, endTime);
            }
            
            return null;
        } catch (Exception e) {
            log.error("获取数据源维护时间失败, dataSourceCode:{}", dataSourceCode, e);
            return null;
        }
    }
    
    /**
     * 更新比赛维度的连接状态到Redis
     * 状态：0=开关未开启或维护状态（前端不展示），1=开关开启且连接，2=开关开启且断连
     * @param connectionStatus 连接状态：0, 1, 或 2
     * @return true表示状态发生变化，false表示状态未变化
     */
    private boolean updateMatchConnectionStatus(String matchStatusKey, Integer connectionStatus, 
                                               Long standardMatchId, String dataSourceCode) {
        try {
            Object oldStatusObj = redisService.get(matchStatusKey);
            Integer oldStatus = null;
            if (oldStatusObj != null) {
                try {
                    // 尝试解析为Integer
                    oldStatus = Integer.parseInt(oldStatusObj.toString());
                } catch (NumberFormatException e) {
                    // 兼容旧格式（boolean），转换为Integer
                    Boolean oldBooleanStatus = Boolean.parseBoolean(oldStatusObj.toString());
                    oldStatus = oldBooleanStatus ? 1 : 2;
                }
            }
            
            // 如果状态未变化，直接返回
            if (oldStatus != null && oldStatus.equals(connectionStatus)) {
                return false;
            }
            
            // 更新状态到Redis，设置过期时间为30天
            redisService.set(matchStatusKey, String.valueOf(connectionStatus), RedisConfig.REDIS_MONTH_TIME);
            
            log.info("比赛数据商连接状态更新, standardMatchId:{}, dataSourceCode:{}, oldStatus:{}, newStatus:{}", 
                    standardMatchId, dataSourceCode, oldStatus, connectionStatus);
            
            return true;
        } catch (Exception e) {
            log.error("更新比赛连接状态到Redis失败, matchStatusKey:{}", matchStatusKey, e);
            return false;
        }
    }
    
    /**
     * 发送比赛连接状态改变通知到前端（通过WS服务）
     */
    private void sendMatchConnectionStatusChangeNotification(Long standardMatchId, String dataSourceCode, boolean isConnected) {
        try {
            log.info("比赛数据商连接状态改变，发送WS通知, standardMatchId:{}, dataSourceCode:{}, isConnected:{}", 
                    standardMatchId, dataSourceCode, isConnected);
            
            // 通过WS服务推送连接状态改变通知
            // 由于现有WS推送服务没有专门的连接状态推送接口，这里通过推送比分来触发前端更新
            // 前端可以根据连接状态来更新UI显示
            // 推送一个空事件来触发前端刷新，或者推送当前比分以更新显示
            if (!isConnected) {
                // 断连时推送通知，让前端知道数据商断连
                // 这里推送标准比分，前端可以根据数据商状态来显示断连提示
                wsPushService.pushStandardSettleScores(standardMatchId, "goal");
                log.info("比赛数据商断连，已发送WS通知, standardMatchId:{}, dataSourceCode:{}", standardMatchId, dataSourceCode);
            } else {
                // 重连时也推送通知
                wsPushService.pushStandardSettleScores(standardMatchId, "goal");
                log.info("比赛数据商重连，已发送WS通知, standardMatchId:{}, dataSourceCode:{}", standardMatchId, dataSourceCode);
            }
        } catch (Exception e) {
            log.error("发送比赛连接状态改变通知失败, standardMatchId:{}, dataSourceCode:{}", standardMatchId, dataSourceCode, e);
        }
    }
    
    /**
     * 推送数据商连接状态变更到topic（DATASOURCE_CONNECTION_STATUS_PUSH）
     * @param standardMatchId 标准赛事ID
     * @param dataSourceCode 数据商编码
     * @param connectionStatus 连接状态：0=开关未开启或维护状态，1=开关开启且连接，2=开关开启且断连
     */
    private void pushConnectionStatusChange(Long standardMatchId, String dataSourceCode, Integer connectionStatus) {
        try {
            java.util.Map<String, Integer> datasourceStatusMap = new java.util.HashMap<>();
            datasourceStatusMap.put(dataSourceCode, connectionStatus);
            
            DataSourceConnectionStatusDto dto = new DataSourceConnectionStatusDto();
            dto.setStandardMatchId(standardMatchId);
            dto.setDatasourceStatusMap(datasourceStatusMap);
            dto.setTimestamp(System.currentTimeMillis());
            
            dataSourceConnectionStatusProducer.pushDataSourceConnectionStatus(dto);
            log.info("推送数据商连接状态变更成功, standardMatchId:{}, dataSourceCode:{}, connectionStatus:{}",
                    standardMatchId, dataSourceCode, connectionStatus);
        } catch (Exception e) {
            log.error("推送数据商连接状态变更失败, standardMatchId:{}, dataSourceCode:{}, connectionStatus:{}",
                    standardMatchId, dataSourceCode, connectionStatus, e);
        }
    }
    
    /**
     * 构建时间戳Hash field（比赛id+数据源格式）
     */
    private String buildTimestampField(Long standardMatchId, String dataSourceCode) {
        return standardMatchId + ":" + dataSourceCode;
    }
    
    /**
     * 构建比赛维度的连接状态Redis key
     */
    private String buildMatchConnectionStatusKey(Long standardMatchId, String dataSourceCode) {
        return CommonConstant.DATASOURCE_HEARTBEAT_CONNECTION_STATUS + standardMatchId + ":" + dataSourceCode;
    }
    
    /**
     * 获取联赛等级（内部方法）
     */
    private Integer getTournamentLevelInternal(Long standardMatchId) {
        try {
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
            if (standardMatchInfo == null || standardMatchInfo.getStandardTournamentId() == null) {
                return null;
            }
            
            StandardSportTournament tournament = standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());
            if (tournament == null) {
                return null;
            }
            
            return tournament.getTournamentLevel();
        } catch (Exception e) {
            log.error("获取联赛等级失败, standardMatchId:{}", standardMatchId, e);
            return null;
        }
    }
}






