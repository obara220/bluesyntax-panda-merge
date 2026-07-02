######################2020年10月20日开发环境执行start######################
-- 最大最小值增加盘口位置字段
ALTER table  config_market_trade_item add place_num  int DEFAULT 0 COMMENT '盘口位置' after
market_category_id;
ALTER table  config_market_trade_item_log add place_num  int DEFAULT 0 COMMENT '盘口位置' after
market_category_id;
#####################2020年10月20日开发环境执行end

#####################2020年10月27日开发环境执行start#####################
-- 给手自动操盘表创建索引
ALTER TABLE config_trade_type ADD INDEX index_standard_match_id (standard_match_id);
ALTER TABLE config_trade_type ADD INDEX index_standard_category_id (standard_category_id);
-- 给最大最小值设置表创建索引
ALTER TABLE config_market_trade_item ADD INDEX index_match_id (match_id);
ALTER TABLE config_market_trade_item ADD INDEX index_market_category_id (market_category_id);
-- 给margin配置表创建索引
ALTER TABLE config_market_category_margin ADD INDEX index_standard_match_info_id (standard_match_info_id);
ALTER TABLE config_market_category_margin ADD INDEX index_standard_category_id (standard_category_id);
-- 给水差表创建索引
ALTER TABLE config_market_auto_diff_trade ADD INDEX index_standard_match_id (standard_match_id);



-- 泰森排行榜单相关脚本开始
-- 三方联赛表表添加三方数据源赛季ID字段
alter table third_sport_tournament add column `third_season_source_id` varchar(50) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '三方数据源当前赛季ID';

-- ----------------------------
-- Table structure for third_sport_player_ranking
-- ----------------------------
DROP TABLE IF EXISTS `third_sport_player_ranking`;
CREATE TABLE `third_sport_player_ranking`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT 'ID(三方数据源赛季ID+榜单类型+榜单序号)',
  `third_tournament_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源联赛ID',
  `sport_id` bigint(20) NOT NULL COMMENT '运动类型',
  `third_source_season_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源赛季ID',
  `third_source_season_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '三方数据源赛季名称',
  `third_source_season_begin_time` datetime(0) NULL DEFAULT NULL COMMENT '三方数据源赛季开始时间',
  `third_source_season_end_time` datetime(0) NULL DEFAULT NULL COMMENT '三方数据源赛季结束始时间',
  `match_count` int(11) NOT NULL COMMENT '参数场数',
  `ranking_value` int(11) NOT NULL COMMENT '榜单值',
  `ranking_sort` int(11) NOT NULL COMMENT '榜单序号',
  `ranking_type` int(11) NOT NULL COMMENT '榜单类型（足球类：1 射手榜,24 助攻榜 |   篮球类：24助攻榜,59盖帽榜,60得分榜,61篮板榜,62抢断榜,63技术犯规榜,64失误榜,65投篮次数榜,66进球次数榜,67效率榜,69二分命中数榜,70二分投球次数榜,71三分命中数榜,72三分投中次数榜,73罚中次数榜,74罚球次数榜）',
  `third_team_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源球队ID',
  `team_cn_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球队中文名称',
  `team_en_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球队英文名称',
  `team_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球队logo',
  `third_player_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源球员ID',
  `player_cn_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球员中文名称',
  `player_en_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球员英文名称',
  `player_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球员logo',
  `modify_time` bigint(20) NOT NULL COMMENT '修改时间',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uq_tour`(`third_tournament_source_id`, `sport_id`) USING BTREE,
  INDEX `idx_season_id`(`third_source_season_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '联赛下球员排行榜（泰森独有）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for third_sport_team_ranking
-- ----------------------------
DROP TABLE IF EXISTS `third_sport_team_ranking`;
CREATE TABLE `third_sport_team_ranking`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源赛季ID+榜单ID+球队ID',
  `third_tournament_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源联赛ID',
  `sport_id` bigint(20) NOT NULL COMMENT '运动类型',
  `third_source_season_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源赛季ID',
  `third_source_season_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '三方数据源赛季名称',
  `third_source_season_begin_time` datetime(0) NULL DEFAULT NULL COMMENT '三方数据源赛季开始时间',
  `third_source_season_end_time` datetime(0) NULL DEFAULT NULL COMMENT '三方数据源赛季结束始时间',
  `ranking_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '榜单ID',
  `ranking_cn_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '榜单中文名称',
  `ranking_en_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '榜单英文名称',
  `match_count` int(11) NULL DEFAULT NULL COMMENT '参数场数',
  `third_team_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源球队ID',
  `team_cn_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球队中文名称',
  `team_en_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球队英文名称',
  `team_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球队logo',
  `position_total` int(11) NOT NULL COMMENT '排名值',
  `win_total` int(11) NOT NULL COMMENT '胜场数',
  `draw_total` int(11) NOT NULL COMMENT '平局数',
  `loss_total` int(11) NOT NULL COMMENT '负场数',
  `points_total` int(11) NOT NULL COMMENT '积分数',
  `goals_for_total` int(11) NOT NULL COMMENT '进球数',
  `goals_against_total` int(11) NOT NULL COMMENT '失球数',
  `modify_time` bigint(20) NOT NULL COMMENT '修改时间',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uq_tour`(`third_tournament_source_id`, `sport_id`) USING BTREE,
  INDEX `idx_season_id`(`third_source_season_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '联赛下球队积分排行榜（泰森独有）' ROW_FORMAT = Dynamic;
-- 泰森排行榜单相关脚本结束


####################2020年10月27日11开发环境执行end#############

-- noah增加脚本
UPDATE market_category_template_relation
SET dynamic_flag = 2
WHERE market_category_id = 180 and template_id = 47 and sport_id = 7 LIMIT 1;

UPDATE match_event_template SET template_no = 1 WHERE id = 30;

UPDATE match_event_template SET event_code = 'ball_pot' WHERE id = 41;


UPDATE system_item_dict
SET description = 'SET1'
WHERE parent_type_id = 8 and value = '800' and addition1 = '5' LIMIT 1;

UPDATE system_item_dict
SET description = 'SET2'
WHERE parent_type_id = 8 and value = '900' and addition1 = '5' LIMIT 1;

UPDATE system_item_dict
SET description = 'SET3'
WHERE parent_type_id = 8 and value = '1000' and addition1 = '5' LIMIT 1;

UPDATE system_item_dict
SET description = 'SET4'
WHERE parent_type_id = 8 and value = '1100' and addition1 = '5' LIMIT 1;

UPDATE system_item_dict
SET description = 'SET5'
WHERE parent_type_id = 8 and value = '1200' and addition1 = '5' LIMIT 1;


#####################2020年11月20日开发环境执行START#####################
-- bevan增加操盘配置记录操作人ID脚本
ALTER TABLE config_market_category_place ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';

ALTER TABLE config_market_trade_item ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';
ALTER TABLE config_market_trade_item_log ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';

ALTER TABLE config_market_category_margin ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';
ALTER TABLE config_market_category_margin_log ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';

ALTER TABLE config_market_auto_diff_trade ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';
ALTER TABLE config_market_auto_diff_trade_log ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';

ALTER TABLE config_market_category_head ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';
ALTER TABLE config_market_category_head_log ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';

ALTER TABLE config_market_status_trade ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';

ALTER TABLE config_trade_type ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';

ALTER TABLE match_data_source_weight ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';
#####################END#####################


#####################2020年12月08日开发环境执行START#####################
-- 标准赛事新增收到滚球赔率标识
ALTER TABLE standard_match_info ADD COLUMN `odds_live` TINYINT ( 4 ) DEFAULT '0' COMMENT '是否接受到滚球赔率：0(否),1(是)' AFTER live_odd_business;
ALTER TABLE standard_match_info_his ADD COLUMN `odds_live` TINYINT ( 4 ) DEFAULT '0' COMMENT '是否接受到滚球赔率：0(否),1(是)';
#####################END#####################

#####################2020年12月09日开发环境执行START#####################
ALTER TABLE config_market_display_trade DROP INDEX idx_standard_match_id;
CREATE UNIQUE INDEX idx_standard_match_id ON config_market_display_trade ( standard_match_id );
#####################END#####################

#####################2021年08月15日开发环境执行START#####################
ALTER TABLE  `market_category_sell`
ADD COLUMN `is_special_pumping` int DEFAULT '0' COMMENT '是否特殊抽水1:是 0:否' ,
ADD COLUMN `special_odds_interval` VARCHAR(200) NULL COMMENT '特殊抽水赔率区间' ;
#####################END#####################
