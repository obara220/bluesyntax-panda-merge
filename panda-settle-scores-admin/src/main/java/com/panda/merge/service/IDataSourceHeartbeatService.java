package com.panda.merge.service;

import com.panda.merge.model.MatchEventInfo;

/**
 * 数据商心跳服务接口
 * @author system
 */
public interface IDataSourceHeartbeatService {
    
    /**
     * 更新数据商和联赛等级的时间戳到Redis
     * @param matchEventInfo 事件信息
     */
    void updateDataSourceTimestamp(MatchEventInfo matchEventInfo);
    
    /**
     * 查询数据商心跳配置时间（秒）
     * @param dataSourceCode 数据商编码
     * @param sportId 球种ID
     * @param tournamentLevel 联赛等级
     * @return 心跳配置时间（秒），如果未配置返回null
     */
    Integer getHeartbeatConfigSeconds(String dataSourceCode, Long sportId, Integer tournamentLevel);
    
    /**
     * 判断数据商是否断连并更新连接状态（比赛维度）
     * @param standardMatchId 标准赛事ID
     * @param dataSourceCode 数据商编码
     * @param sportId 球种ID
     * @param tournamentLevel 联赛等级
     * @return true表示连接，false表示断连
     */
    boolean checkAndUpdateConnectionStatus(Long standardMatchId, String dataSourceCode, Long sportId, Integer tournamentLevel);
    
    /**
     * 获取数据源维护时间
     * @param dataSourceCode 数据商编码
     * @return 维护时间对象，包含开始时间和结束时间，如果不存在返回null
     */
    DataSourceMaintenanceTime getMaintenanceTime(String dataSourceCode);
    
    /**
     * 获取联赛等级（辅助方法）
     */
    Integer getTournamentLevel(Long standardMatchId);
    
    /**
     * 获取比赛的数据商连接状态（比赛维度）
     * @param standardMatchId 标准赛事ID
     * @param dataSourceCode 数据商编码
     * @return true表示连接，false表示断连，如果未找到返回null
     */
    Boolean getMatchConnectionStatus(Long standardMatchId, String dataSourceCode);

    /**
     * 扫描所有正在进行的赛事，检查数据商连接状态并返回需要推送的状态列表
     * @return 需要推送的连接状态列表（只返回状态改变的数据）
     */
    java.util.List<com.panda.merge.dto.settle.DataSourceConnectionStatusDto> scanAllMatchesConnectionStatus();

    /**
     * 根据标准赛事ID从Redis读取数据商连接状态（不重新计算）
     * @param standardMatchId 标准赛事ID
     * @return 数据商连接状态列表
     */
    com.panda.merge.dto.settle.DataSourceConnectionStatusDto scanMatchConnectionStatus(Long standardMatchId);
    
    /**
     * 数据源维护时间内部类
     */
    class DataSourceMaintenanceTime {
        private Long startTime;
        private Long endTime;
        
        public DataSourceMaintenanceTime(Long startTime, Long endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        public Long getStartTime() {
            return startTime;
        }
        
        public Long getEndTime() {
            return endTime;
        }
        
        /**
         * 判断当前时间是否在维护时间内
         */
        public boolean isInMaintenanceTime(long currentTime) {
            return startTime != null && endTime != null && currentTime >= startTime && currentTime <= endTime;
        }
    }
}






