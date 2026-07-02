######################2020年10月18日测试环境执行start#########################
-- 政治娱乐赛种配置
delete from third_sport_type where name_code = 105 and third_sport_id = '18' and data_source_code = 'SR';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`) VALUES (105, '18', 'SR',18, '政治娱乐', '政治娱乐', 1564997479754, 1564997479754);
delete from third_sport_type where name_code = 105 and third_sport_id = '89' and data_source_code = 'BC';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`) VALUES (105, '89', 'BC',18, '政治选举', '政治选举', 1564997479754, 1564997479754);
delete from third_sport_type where name_code = 105 and third_sport_id = '91' and data_source_code = 'BC';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`) VALUES (105, '91', 'BC',18, '娱乐', '娱乐', 1564997479754, 1564997479754);
delete from third_sport_type where name_code = 105 and third_sport_id = '92' and data_source_code = 'BC';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`) VALUES (105, '92', 'BC',18, '奥斯卡', '奥斯卡', 1564997479754, 1564997479754);
delete from standard_sport_type where id = 18;
INSERT INTO `panda`.`standard_sport_type`(`id`, `name_code`, `introduction`, `remark`, `create_time`, `modify_time`) VALUES (18, 105, '政治娱乐', '政治娱乐', 1564997479754, 1564997479754);

-- 删除之前的表
drop table if exists i18n_outright_market;
drop table if exists i18nnames_outright_category_name;
drop table if exists i18nnames_outright_match_name;
drop table if exists standard_outright_match_category;
drop table if exists standard_outright_match_info;
drop table if exists third_outright_match_info;
drop table if exists i18n_market_odds;

CREATE TABLE `i18n_market_odds` (
  `odds_source_id` varchar(225) COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方投注项源id',
  `data_source_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '数据源编码',
  `language_type` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '语言类型. zh jp en 等',
  `text` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '文字内容.  ',
  `create_time` bigint(20) DEFAULT '0',
  `modify_time` bigint(20) DEFAULT '0',
  UNIQUE KEY `idx_odds_id_source_type` (`odds_source_id`,`language_type`,`data_source_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='投注项多语言';

-- 冠军赛事创建表
CREATE TABLE `i18n_outright_market` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name_code` bigint(20) DEFAULT '0' COMMENT '文字对应的编码',
  `flag` tinyint(4) DEFAULT '2' COMMENT '1 人工  2 系统',
  `data_source_code` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '数据来源编码. SR BC等',
  `language_type` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '语言类型. zh jp en 等',
  `text` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '文字内容.  ',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '',
  `create_time` bigint(20) DEFAULT '0',
  `modify_time` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_uq_source_namecode` (`data_source_code`,`language_type`,`name_code`) USING BTREE,
  KEY `idx_namecodeand` (`name_code`,`data_source_code`) USING BTREE,
  KEY `idx_namecode` (`name_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1317448333729760071 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='冠军盘口多语言';

CREATE TABLE `i18nnames_outright_match_name` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `match_category_filed` bigint(255) DEFAULT NULL COMMENT '玩法 赛事  投注项',
  `type` tinyint(4) DEFAULT NULL COMMENT '1  三方赛事  2 标准赛事  3 投注项',
  `data_source_code` varchar(12) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '数据源  PA  SR BG',
  `language_type` varchar(12) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '语言类型',
  `text` varchar(128) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '值',
  `flag` tinyint(4) DEFAULT NULL COMMENT ' 1 人工  2 系统',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `modfiy_time` bigint(20) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `index_match_category_filed` (`match_category_filed`) USING BTREE,
  KEY `index_data_source_code` (`data_source_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=258690 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='冠军赛事及投注项国际化表';

CREATE TABLE `outright_match_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `operate_target_id` bigint(20) DEFAULT NULL COMMENT '操作目标id，赛事id,盘口id...',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人id',
  `operator_name` varchar(64) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '操作人名称',
  `operator_modle` varchar(32) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '操作模块',
  `operator_number` varchar(125) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '操作批次编号(uuid)',
  `operator_text` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '操作内容',
  `operator_time` bigint(20) DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='冠军玩法操作日志表';

CREATE TABLE `standard_outright_market` (
  `id` bigint(20) NOT NULL COMMENT '标准盘口id',
  `standard_match_id` bigint(20) DEFAULT NULL COMMENT '标准赛事id',
  `market_category_id` bigint(20) DEFAULT NULL COMMENT '标准玩法id',
  `market_sell_status` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT 'Unsold' COMMENT ' 盘口开售状态 Sold 开售 Unsold 未售',
  `name_code` bigint(20) DEFAULT NULL COMMENT '盘口国际信息',
  `market_status` tinyint(4) DEFAULT NULL COMMENT '标准盘口状态',
  `next_closing_time` bigint(20) DEFAULT '0' COMMENT '下次封盘时间',
  `link_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `modfiy_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  UNIQUE KEY `standard_match_category_uq_index` (`id`,`standard_match_id`),
  KEY `index_standard_match_id` (`standard_match_id`) USING BTREE COMMENT '标准冠军赛事id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='标准冠军赛事盘口表';

CREATE TABLE `standard_outright_match_info` (
  `id` bigint(20) NOT NULL COMMENT '主键id',
  `sport_id` bigint(20) DEFAULT '0' COMMENT '赛种id',
  `region_id` bigint(20) DEFAULT NULL COMMENT '区域id',
  `standard_tournament_id` bigint(20) DEFAULT NULL COMMENT '标准联赛id',
  `standard_outright_name_en` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '冠军赛事中文名称',
  `standard_outright_name_cn` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '冠军赛事英文名称',
  `data_source_code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '数据源',
  `match_market_status` tinyint(4) DEFAULT '-1' COMMENT '赛事开关封锁 -1 未开 0 :开、2:关、1:封、11',
  `standard_outright_manager_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '冠军赛事管理id',
  `third_outright_match_id` bigint(20) DEFAULT '0' COMMENT '三方冠军赛事id',
  `third_outright_match_source_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '三方冠军赛事源id',
  `standrd_outright_match_begion_time` bigint(20) DEFAULT '0' COMMENT '标准冠军赛事开始时间',
  `standrd_outright_match_end_time` bigint(20) DEFAULT '0' COMMENT '标准冠军赛事结束时间',
  `sell_status` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT 'Unsold' COMMENT '冠军赛事开售状态 Sold 开售 Unsold 未售',
  `auto_sell_status` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '是否自动开售新盘口 Yes  是 No 否',
  `season_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '赛季id',
  `standard_outright_year` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '标准冠军赛事赛季名称',
  `booked` tinyint(4) DEFAULT NULL COMMENT '是否订阅  0 未订阅  1已订阅',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '备注',
  `create_time` bigint(20) DEFAULT '0' COMMENT '新增时间',
  `modify_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_uinque_standard_champion_manager_id` (`standard_outright_manager_id`) USING BTREE COMMENT '管理id',
  UNIQUE KEY `index_uinque_third_champion_match_source_id` (`third_outright_match_source_id`) USING BTREE COMMENT '三方源id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='标准冠军赛事表';

CREATE TABLE `third_outright_match_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `sport_id` bigint(20) DEFAULT '0' COMMENT '赛种',
  `region_id` bigint(20) DEFAULT '0' COMMENT '区域id',
  `tournament_id` bigint(20) DEFAULT '0' COMMENT '三方联赛id',
  `third_match_name_en` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '三方冠军赛事英文',
  `third_match_name_cn` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '三方冠军赛事中文',
  `data_source_code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '数据源  sr, bc ,bg',
  `third_outright_begin_time` bigint(20) DEFAULT '0' COMMENT '三方冠军赛事开始时间',
  `third_outright_end_time` bigint(20) DEFAULT '0' COMMENT '三方冠军赛事结束时间',
  `third_outright_source_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '三方冠军赛事源id',
  `standard_outright_manager_id` varchar(64) COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '标准管理id',
  `reference_id` bigint(20) DEFAULT '0' COMMENT '标准冠军赛id',
  `season_id` varchar(64) COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '赛季id',
  `third_outright_year` varchar(55) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '三方冠军赛事赛季名称',
  `booked` int(11) DEFAULT NULL COMMENT '是否订阅 0 未订阅  1 已订阅',
  `remark` varchar(128) COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '备注',
  `modify_time` bigint(20) DEFAULT NULL COMMENT '修改时间',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_uinque_third_champion_source_id` (`third_outright_source_id`) USING BTREE COMMENT '三方冠军赛事源id',
  KEY `index_standard_champion_id` (`reference_id`) USING BTREE COMMENT '标准冠军赛事id',
  KEY `index_third_champion_source_id` (`tournament_id`) USING BTREE COMMENT '三方联赛id'
) ENGINE=InnoDB AUTO_INCREMENT=1317537052537999362 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='三方冠军赛事表';

-- 篮球相关
CREATE TABLE `config_market_category_head` (
  `id` bigint(22) NOT NULL AUTO_INCREMENT,
  `standard_match_info_id` bigint(20) DEFAULT NULL COMMENT '标准比赛ID   standard_match_info.id',
  `standard_category_id` bigint(22) DEFAULT NULL COMMENT '标准玩法 ID',
  `market_type` tinyint(4) DEFAULT NULL COMMENT '盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.',
  `market_head_gap` double(22,2) DEFAULT NULL COMMENT '盘口差',
  `link_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '日志id',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `modify_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `config_market_head_matchid` (`standard_match_info_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='盘口玩法盘口差配置表';

CREATE TABLE `config_market_category_head_log` (
  `id` bigint(22) NOT NULL AUTO_INCREMENT,
  `standard_match_info_id` bigint(20) DEFAULT NULL COMMENT '标准比赛ID   standard_match_info.id',
  `standard_category_id` bigint(22) DEFAULT NULL COMMENT '标准玩法 ID',
  `market_type` tinyint(4) DEFAULT NULL COMMENT '盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.',
  `market_head_gap` double(22,2) DEFAULT NULL COMMENT '盘口差',
  `link_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '日志id',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `modify_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `config_market_head_log_matchid` (`standard_match_info_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='盘口玩法盘口差配置日志表';

######################2020年10月18日测试环境执行end#########################

######################2020年10月20日测试环境执行start######################
-- 最大最小值增加盘口位置字段
ALTER table  config_market_trade_item add place_num  int DEFAULT 0 COMMENT '盘口位置' after
market_category_id;
ALTER table  config_market_trade_item_log add place_num  int DEFAULT 0 COMMENT '盘口位置' after
market_category_id;
#####################2020年10月20日测试环境执行end

######################2020年10月21日测试环境执行start######################
update third_market_category_field set reference_id  = 480, modify_time = UNIX_TIMESTAMP() * 1000 where third_source_id  = 'BG:6750:25';
update third_market_category_field set reference_id  = 481, modify_time = UNIX_TIMESTAMP() * 1000 where third_source_id  = 'BG:6750:26';
#####################2020年10月21日测试环境执行end


######################2020年10月24日测试环境执行start######################
ALTER TABLE config_trade_market_log
ADD COLUMN MATCH_TYPE varchar(32) NULL AFTER TARGET_ID;
#####################2020年10月24日测试环境执行end

######################2020年10月25日测试环境执行start######################
ALTER TABLE sport_market_relation
MODIFY COLUMN market_relation_key varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL AFTER `id`;
#####################2020年10月25日测试环境执行end

#####################2020年10月27日测试环境执行start#####################
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

####################2020年10月27日11测试环境执行end#############

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

#####################2020年12月08日测试环境执行START#####################
-- 标准赛事新增收到滚球赔率标识
ALTER TABLE standard_match_info ADD COLUMN `odds_live` TINYINT ( 4 ) DEFAULT '0' COMMENT '是否接受到滚球赔率：0(否),1(是)' AFTER live_odd_business;
ALTER TABLE standard_match_info_his ADD COLUMN `odds_live` TINYINT ( 4 ) DEFAULT '0' COMMENT '是否接受到滚球赔率：0(否),1(是)';
#####################END#####################

#####################2020年12月09日开发环境执行START#####################
ALTER TABLE config_market_display_trade DROP INDEX idx_standard_match_id;
CREATE UNIQUE INDEX idx_standard_match_id ON config_market_display_trade ( standard_match_id );
#####################END#####################

#####################2021年08月19日测试环境执行START#####################
ALTER TABLE  `market_category_sell`
ADD COLUMN `is_special_pumping` int DEFAULT '0' COMMENT '是否特殊抽水1:是 0:否' ,
ADD COLUMN `special_odds_interval` VARCHAR(200) NULL COMMENT '特殊抽水赔率区间' ;
#####################END#####################

