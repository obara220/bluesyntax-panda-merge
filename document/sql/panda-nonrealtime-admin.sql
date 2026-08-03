-- 三方区域和标准区域关联
UPDATE third_sport_region SET reference_id=359 WHERE  third_region_id='1827' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=354 WHERE  third_region_id='1848' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=348 WHERE  third_region_id='1858' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=349 WHERE  third_region_id='1870' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=355 WHERE  third_region_id='1877' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=356 WHERE  third_region_id='1882' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=353 WHERE  third_region_id='1948' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=344 WHERE  third_region_id='2259' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=344 WHERE  third_region_id='2261' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=345 WHERE  third_region_id='2262' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=344 WHERE  third_region_id='2263' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=357 WHERE  third_region_id='2266' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=360 WHERE  third_region_id='2280' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=360 WHERE  third_region_id='2281' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=360 WHERE  third_region_id='2282' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=345 WHERE  third_region_id='2291' AND data_source_code = 'SR';
UPDATE third_sport_region SET reference_id=343 WHERE  third_region_id='SJM' AND data_source_code = 'SR';


-- H5赛事分析
ALTER TABLE third_sport_team_ranking ADD COLUMN `goal_diff_total` int(11) DEFAULT 0 COMMENT '净胜球数';

ALTER TABLE third_sport_team_ranking ADD COLUMN `group_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '组ID';
ALTER TABLE third_sport_team_ranking ADD COLUMN `group_cn_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '组名称';
ALTER TABLE third_sport_team_ranking ADD COLUMN `tournament_type` int(11) DEFAULT 0 COMMENT '联赛类别(0:其他,1联赛,2杯赛)';

-- 新建表
DROP TABLE IF EXISTS `third_match_lineup`;
CREATE TABLE `third_match_lineup` (
  `id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据来源ID:数据源赛事ID:数据源球队ID:数据源球员ID',
  `third_match_source_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源赛事id',
  `sport_id` bigint(11) NOT NULL COMMENT '运动类型',
  `data_source_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据来源',
  `third_team_source_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源球队id',
  `third_player_source_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源球员id',
  `third_player_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源球员名称',
  `third_player_pic_url` varchar(255) DEFAULT NULL COMMENT '球员头像',
  `position` int(11) DEFAULT NULL COMMENT '球员位置',
  `shirt_number` int(11) DEFAULT NULL COMMENT '球衣号码',
  `substitute` int(11) DEFAULT '0' COMMENT '是否替补(0:否,1:是)',
  `modify_time` bigint(20) DEFAULT NULL COMMENT '修改时间',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `home_away` int(11) DEFAULT NULL COMMENT '主客队标识(1主队,2客队)',
  `position_name` varchar(50) DEFAULT NULL COMMENT '球员位置名称(前锋，中场，后卫)',
  `invalid` int(2) DEFAULT '0' COMMENT '是否失效(0:否,1:是)',
  `overall_ratings` varchar(20) DEFAULT NULL COMMENT '综合评分',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='赛事首发阵容';

DROP TABLE IF EXISTS `third_match_history_statistics`;
CREATE TABLE `third_match_history_statistics` (
  `id` varchar(50) NOT NULL COMMENT '数据源编码ID+数据源赛事id',
  `third_match_source_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源赛事id',
  `third_tournament_source_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源联赛id',
  `third_season_source_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '数据源赛季id',
  `sport_id` bigint(11) NOT NULL COMMENT '运动类型',
  `data_source_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据来源',
  `begin_time` bigint(20) DEFAULT NULL COMMENT '开赛时间',
  `match_status` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '赛事状态',
  `home_team_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源主队ID',
  `away_team_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源客队ID',
  `home_team_name` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '主队名称',
  `away_team_name` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '客队名称',
  `home_team_score` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '主队得分',
  `away_team_score` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '主队得分',
  `handicap_val` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '初盘让球盘口值',
  `over_under_val` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '初盘大小盘口值',
  `winner_odds` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '初盘胜平负投注项值',
  `handicap_odds` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '初盘让球投注项值',
  `over_under_odds` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '初盘大小投注项值',
  `modify_time` bigint(20) DEFAULT NULL COMMENT '修改时间',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `match_group` int(2) DEFAULT '0' COMMENT '是否分组赛（0：否，1：是）',
  `tournament_type` int(2) DEFAULT '0' COMMENT '联赛类别(0:其他,1联赛,2杯赛)',
  `group_id` varchar(20) DEFAULT NULL COMMENT '分组id 对应分组信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='赛事历史统计信息';


----------------------------- 3月需求脚本，已发 -----------------------------

-- 初始化数据来源关系表
DROP TABLE IF EXISTS `data_source`;
CREATE TABLE `data_source`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '数据表id, 自增',
  `code` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL DEFAULT '' COMMENT '该数据源的编码.比如 SportRadar的编码是 SR',
  `full_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '数据源全称.比如 SportRadar',
  `short_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '数据源简称.比如 SR , 球探',
  `priority` int(11) NULL DEFAULT 0 COMMENT '数据的优先级. 值越大, 重要程度越高. ',
  `event_support` int(2) NULL DEFAULT 0 COMMENT '是否支持事件(0:否,1:是)',
  `commerce` tinyint(4) NULL DEFAULT 0 COMMENT '是否是商业来源的数据. 1: 商业来源;0:非商业',
  `standard` tinyint(4) NULL DEFAULT 0 COMMENT '是否为标准数据源. 1: 是; 0: 否',
  `type` int(11) NULL DEFAULT 0 COMMENT '数据源类型.0:竞品数据源;1:比分网',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '',
  `create_time` bigint(20) NULL DEFAULT 0,
  `modify_time` bigint(20) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_uq_code`(`code`) USING BTREE,
  UNIQUE INDEX `idx_uq_name`(`full_name`) USING BTREE,
  UNIQUE INDEX `idx_uq_short_name`(`short_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '该系统从哪些平台接收数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_source
-- ----------------------------
INSERT INTO `data_source` VALUES (1, 'SR', 'SportRadar', 'SR', 200, 1, 1, 1, 0, '以此为准,购买的第一数据源', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
INSERT INTO `data_source` VALUES (2, 'BC', 'BetConstruct', 'BC', 190, 1, 1, 0, 0, '作为参考,购买的第二数据源', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
INSERT INTO `data_source` VALUES (3, 'PA', 'Panda', '熊猫', 0, 1, 1, 0, 0, '熊猫体育自己的数据', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
INSERT INTO `data_source` VALUES (4, '188', '188', '188', 180, 0, 0, 0, 0, '金杯博', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
INSERT INTO `data_source` VALUES (5, 'QT', '球探', '球探', 170, 0, 0, 0, 1, '球探,用于补充logo', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
INSERT INTO `data_source` VALUES (6, 'SBA', '沙巴', '沙巴', 160, 0, 0, 0, 0, '沙巴体育', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
INSERT INTO `data_source` VALUES (7, 'SBO', 'SBO', 'SBO', 150, 0, 0, 0, 0, '盛帆娱乐', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
INSERT INTO `data_source` VALUES (8, 'BG', 'Betgenius', 'BG', 189, 1, 1, 0, 0, '作为参考,购买的第三数据源', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
INSERT INTO `data_source` VALUES (9, 'TS', 'tyson', 'TS', 185, 0, 0, 0, 0, 'tyson数据', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
INSERT INTO `data_source` VALUES (10, 'TX', 'TXODDS', 'TX', 0, 0, 1, 0, 0, 'TXODDS数据', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
INSERT INTO `data_source` VALUES (11, 'RB', 'RunningBall', 'RB', 0, 1, 1, 0, 0, 'RunningBall', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
INSERT INTO `data_source` VALUES (14, 'V2', '第二视频', 'V2', 186, 0, 0, 0, 0, '第二视频数据商', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);


-- 关联RB运动类型
delete from third_sport_type where name_code = 1 and third_sport_id = '1' and data_source_code = 'RB';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (1, '1', 'RB',1, '足球', '足球', 1618823027000, 1618823027000);

delete from third_sport_type where name_code = 2 and third_sport_id = '2' and data_source_code = 'RB';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (2, '2', 'RB',2, '篮球', '篮球', 1618823027000, 1618823027000);

delete from third_sport_type where name_code = 3 and third_sport_id = '17' and data_source_code = 'RB';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (3, '17', 'RB',3, '棒球', '棒球', 1618823027000, 1618823027000);

delete from third_sport_type where name_code = 4 and third_sport_id = '8' and data_source_code = 'RB';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (4, '8', 'RB',4, '冰球', '冰球', 1618823027000, 1618823027000);

delete from third_sport_type where name_code = 9 and third_sport_id = '9' and data_source_code = 'RB';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (9, '9', 'RB',9, '排球', '排球', 1618823027000, 1618823027000);



-- 4月需求脚本

DROP TABLE IF EXISTS `third_match_phrase`;
CREATE TABLE `third_match_phrase`  (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据来源ID:文字直播ID',
  `third_match_source_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源赛事id',
  `sport_id` bigint(11) NOT NULL COMMENT '运动类型',
  `data_source_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据来源',
  `time` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '发生时间',
  `cn_text` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '中文文字内容',
  `en_text` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '中文英字内容',
  `scores` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前比分',
  `team` int(11) DEFAULT 0 COMMENT '所属球队(0 公共，1 主队，2 客队)',
  `match_period` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '赛事阶段',
  `send_data` int(2) NULL DEFAULT 0 COMMENT '是否已经下发(0:否,1:是)',
  `link_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '线路ID',
  `modify_time` bigint(20) NULL DEFAULT NULL COMMENT '修改时间',
  `create_time` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_source_id_code`(`third_match_source_id`, `data_source_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '赛事文字直播' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `third_match_history_odds`;
CREATE TABLE `third_match_history_odds`  (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据来源ID+赛事源ID+供应商ID+玩法ID+盘口类型',
  `third_match_source_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '赛事源ID',
  `sport_id` bigint(11) NOT NULL COMMENT '运动类型',
  `data_source_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据来源',
  `book_id` int(11) NOT NULL COMMENT '供应商ID',
  `book_cn_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '供应商中文名称',
  `book_en_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '供应商英文名称',
  `type_id` int(11) NOT NULL COMMENT '玩法ID',
  `type_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '玩法名称',
  `market_type` int(11) NOT NULL COMMENT '盘口类型(1: 赛前盘; 0: 滚球盘)',
  `value0` varchar(20) DEFAULT NULL COMMENT '初始盘口值',
  `value` varchar(20) DEFAULT NULL COMMENT '即时盘口值',
  `odds_json` varchar(500) DEFAULT NULL COMMENT '投注项值',
  `create_time` bigint(20) NULL DEFAULT 0,
  `modify_time` bigint(20) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uq_third_match`(`third_match_source_id`, `data_source_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '三方赛事历史赔率信息' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `third_match_sidelined`;
CREATE TABLE `third_match_sidelined`  (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据来源ID+赛事源ID+球队源ID+球员源ID',
  `third_match_source_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源赛事id',
  `sport_id` bigint(11) NOT NULL COMMENT '运动类型',
  `data_source_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据来源',
  `third_team_source_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源球队id',
  `third_player_source_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源球员id',
  `third_player_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '球员名称',
  `third_player_en_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '球员英文名称',
  `third_player_pic_url` varchar(255) DEFAULT NULL COMMENT '球员头像',
  `position` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '球员位置',
  `shirt_number` int(11) DEFAULT NULL COMMENT '球衣号码',
  `home_away` int(11) DEFAULT NULL COMMENT '主客队标识(1主队,2客队)',
  `reason` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '缺阵原因',
  `description_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '原因描述id',
  `description` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '原因描述',
  `create_time` bigint(20) NULL DEFAULT 0,
  `modify_time` bigint(20) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uq_third_match`(`third_match_source_id`, `data_source_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '三方赛事球员伤停信息' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `third_sport_player_ranking`;
CREATE TABLE `third_sport_player_ranking`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT 'ID(赛季源ID+榜单类型+球员源ID)',
  `third_tournament_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源联赛ID',
  `sport_id` bigint(20) NOT NULL COMMENT '运动类型',
  `third_source_season_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源赛季ID',
  `third_source_season_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '三方数据源赛季名称',
  `third_source_season_begin_time` datetime(0) NULL DEFAULT NULL COMMENT '三方数据源赛季开始时间',
  `third_source_season_end_time` datetime(0) NULL DEFAULT NULL COMMENT '三方数据源赛季结束始时间',
  `match_count` int(11) NOT NULL COMMENT '参数场数',
  `ranking_value` int(11) NOT NULL COMMENT '榜单值',
  `ranking_sort` int(11) NULL DEFAULT NULL COMMENT '榜单序号',
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '联赛赛季球员排行榜' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `third_match_ex_infomation`;
CREATE TABLE `third_match_ex_infomation`  (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据来源ID:赛事源ID',
  `third_match_source_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '赛事源ID',
  `sport_id` bigint(11) NOT NULL COMMENT '运动类型',
  `data_source_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据来源',
  `home_coach` json NULL COMMENT '主队教练信息json',
  `away_coach` json NULL COMMENT '客队教练信息json',
  `informations` json NULL COMMENT '情报信息列表json',
  `winning_odds` json NULL COMMENT '赔率情况分析json',
  `modify_time` bigint(20) NULL DEFAULT NULL COMMENT '修改时间',
  `create_time` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_source_id_code`(`third_match_source_id`, `data_source_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '赛事情报综合资讯' ROW_FORMAT = Dynamic;


-- RB新增标准事件编码

UPDATE `match_event_type` SET create_time=unix_timestamp(now()) * 1000,modify_time=unix_timestamp(now()) * 1000;


-- 10月脚本
ALTER TABLE match_event_type ADD COLUMN `event_en_name` varchar(100) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '事件英文名称';

UPDATE `match_event_type` SET event_en_name='Temporary interruption' WHERE event_code='temporary_interruption'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Game on' WHERE event_code='game_on'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Suspension over' WHERE event_code='suspension_over'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Goal' WHERE event_code='goal'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Yellow card' WHERE event_code='yellow_card'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Suspension' WHERE event_code='suspension'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Yellow red card' WHERE event_code='yellow_red_card'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Red card' WHERE event_code='red_card'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Substitution' WHERE event_code='substitution'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Injury time' WHERE event_code='injury_time'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Possession' WHERE event_code='possession'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Free kick count' WHERE event_code='free_kick_count'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Goal kick count' WHERE event_code='goal_kick_count'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Throw-in count' WHERE event_code='throw_in_count'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Offside count' WHERE event_code='off_side_count'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Corner kick count' WHERE event_code='corner_kick_count'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Shot on target count' WHERE event_code='shot_on_target_count'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Shot off target count' WHERE event_code='shot_off_target_count'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Goalkeeper saved count' WHERE event_code='goal_keeper_save_count'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Free kick' WHERE event_code='free_kick'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Goal kick' WHERE event_code='goal_kick'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Throw-in' WHERE event_code='throw_in'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Offside' WHERE event_code='offside'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Corner' WHERE event_code='corner'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Shot on target' WHERE event_code='shot_on_target'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Shot off target' WHERE event_code='shot_off_target'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Goalkeeper saved' WHERE event_code='goal_keeper_save'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Player injury' WHERE event_code='injury'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Penalty awarded' WHERE event_code='penalty_awarded'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Weather Conditions' WHERE event_code='weather_conditions'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Attendance' WHERE event_code='attendance'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Player back from injury' WHERE event_code='player_back_from_injury'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Shots blocked count' WHERE event_code='shots_blocked_counts'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Shot blocked' WHERE event_code='shot_blocked'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Penalty missed' WHERE event_code='penalty_missed'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Penalty shoot-out' WHERE event_code='penalty_shootout_event'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Bet start' WHERE event_code='betstart'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Bet stop' WHERE event_code='betstop'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Kick-off team' WHERE event_code='kick_off_team'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Match status' WHERE event_code='match_status'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Pitch conditions' WHERE event_code='pitch_conditions'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Match comment' WHERE event_code='free_comment'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Possible corner' WHERE event_code='possible_corner'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Cancel corner' WHERE event_code='canceled_corner'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Possible goal' WHERE event_code='possible_goal'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Cancel goal' WHERE event_code='canceled_goal'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Match about to start' WHERE event_code='match_about_to_start'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Dangerous attack' WHERE event_code='dangerous_attack'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Safe ball' WHERE event_code='ball_safe'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Manual time adjustment' WHERE event_code='manual_time_adjustment'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Possible red card' WHERE event_code='possible_red_card'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Cancel red card' WHERE event_code='canceled_red_card'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Possible penalty' WHERE event_code='possible_penalty'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Cancel penalty' WHERE event_code='canceled_penalty'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Delete event alert' WHERE event_code='delete_event'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Play resumes after goal' WHERE event_code='play_resumes_after_goal'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Disable corner markets' WHERE event_code='disable_corner_markets'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Disable booking markets' WHERE event_code='disable_booking_markets'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Possible yellow card' WHERE event_code='possible_yellow_card'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Cancel yellow card' WHERE event_code='canceled_yellow_card'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Pre-match bet status' WHERE event_code='early_betstatus'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Coverage status' WHERE event_code='coverage_status'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Penalty shoot-out starting team' WHERE event_code='penalty_shootout_starting_team'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Attack' WHERE event_code='attack'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Take penalty' WHERE event_code='take_penalty'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='VAR' WHERE event_code='video_assistant_referee'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='VAR over' WHERE event_code='video_assistant_referee_over'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Possible VAR' WHERE event_code='possible_video_assistant_referee'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Cancel VAR' WHERE event_code='canceled_video_assistant_referee'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Cancel free kick' WHERE event_code='canceled_free_kick'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Penalty card' WHERE event_code='fa_card'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='2nd half ended' WHERE event_code='stop_rt2'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='2nd half overtime' WHERE event_code='stop_ot2'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Penalty shoot-out ended' WHERE event_code='stop_pen'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Danger ball' WHERE event_code='danger_ball'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Possible penalty' WHERE event_code='ppen'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Re-take penalty' WHERE event_code='retake_pen'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Next penalty scorer' WHERE event_code='next_penalty_scorer'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Goal under investigation' WHERE event_code='goal_under_investigation'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Possible free kick' WHERE event_code='possible_free_kick'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Possible card' WHERE event_code='possible_card'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Penalty about to be taken' WHERE event_code='penalty_about_to_be_taken'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Penalty shoot-out starts' WHERE event_code='start_pen_team'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Dangerous free kick' WHERE event_code='dfk'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Shot on woodwork' WHERE event_code='shw'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Foul' WHERE event_code='foul'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Danger' WHERE event_code='danger'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Re-take penalty' WHERE event_code='rpen'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Breakaway' WHERE event_code='breakaway'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Next penalty' WHERE event_code='next_pen'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Danger possession' WHERE event_code='dang_poss'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Possible goal update' WHERE event_code='exptected_goal_update'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='1st half starts' WHERE event_code='start_rt1'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='1st half ended' WHERE event_code='stop_rt1'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='2nd half starts' WHERE event_code='start_rt2'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='2nd half overtime starts' WHERE event_code='start_ot2'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='1st half overtime kick-off team' WHERE event_code='start_ot1'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Injury break' WHERE event_code='injury_break'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Players enter the field' WHERE event_code='plays_coming'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Player Lineup' WHERE event_code='plays_lineup'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Sing national anthem' WHERE event_code='national_sing'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Shake hands' WHERE event_code='shake_hands'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Coin flip' WHERE event_code='filp_cone'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Minute''s silence' WHERE event_code='silent'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Prize ceremony' WHERE event_code='price_ceremony'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Photo taking' WHERE event_code='photo_taking'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='No penalty' WHERE event_code='no_pen'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Referee ball' WHERE event_code='referee_ball'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='No card' WHERE event_code='no_card'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Possible throw-in' WHERE event_code='possible_throwin'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='No throw-in' WHERE event_code='no_throwin'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Substitution update' WHERE event_code='sub_update'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Assist details' WHERE event_code='assist'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Jersey changed' WHERE event_code='jersey_change'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Formation update' WHERE event_code='formation_update'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='VAR reason' WHERE event_code='var_reason'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Possible VAR' WHERE event_code='possible_var'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='VAR reviewing' WHERE event_code='var_reviewing'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='No corner' WHERE event_code='no_corner'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Yellow card confirmed' WHERE event_code='yellow_card_confirm'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Yellow red card confirmed' WHERE event_code='yellow_red_card_confirm'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Possible throw-in' WHERE event_code='possible_throw_in'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Safe ball' WHERE event_code='safe_ball'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Attack position' WHERE event_code='attack_position'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Kick-off' WHERE event_code='kick_off'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Goal confirmed' WHERE event_code='goal_confirm'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='No corner' WHERE event_code='no_cvorner'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Red card confirmed' WHERE event_code='red_card_confirm'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Away possible throw-in' WHERE event_code='posible_throw_in'  AND sport_id=1;
UPDATE `match_event_type` SET event_en_name='Breakaway' WHERE event_code='break_away'  AND sport_id=1;

-- 新增V2视频
ALTER TABLE third_video_board_cast_record ADD COLUMN `player_url` varchar(255) DEFAULT NULL COMMENT '播放器url(type  pc:电脑,mobile:手机, 默认pc)';
ALTER TABLE third_video_board_cast_record MODIFY ani_id varchar(50) DEFAULT NULL COMMENT 'TS:动画ID , SR:流ID';



-- 关联TX运动类型
delete from third_sport_type where name_code = 5 and third_sport_id = '5' and data_source_code = 'TX';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (5, '5', 'TX',5, '网球', '网球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

delete from third_sport_type where name_code = 3 and third_sport_id = '7' and data_source_code = 'TX';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (3, '7', 'TX',3, '棒球', '棒球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

delete from third_sport_type where name_code = 9 and third_sport_id = '13' and data_source_code = 'TX';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (9, '13', 'TX',9, '排球', '排球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

delete from third_sport_type where name_code = 8 and third_sport_id = '15' and data_source_code = 'TX';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (8, '15', 'TX',8, '乒乓球', '乒乓球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);


-- SR奥运会赛种对应政治娱乐玩法
UPDATE `third_sport_type` SET name_code=18,modify_time=unix_timestamp(now()) * 1000
WHERE data_source_code='SR' AND third_sport_id=30;

UPDATE `third_sport_tournament` SET sport_id=18,modify_time=unix_timestamp(now()) * 1000
WHERE data_source_code='SR' AND sport_id=50;

UPDATE `third_outright_match_info` SET sport_id=18,modify_time=unix_timestamp(now()) * 1000
WHERE data_source_code='SR' AND sport_id=50;

UPDATE `standard_sport_tournament` SET sport_id=18,modify_time=unix_timestamp(now()) * 1000
WHERE data_source_code='SR' AND sport_id=50;

UPDATE `standard_outright_match_info` SET sport_id=18,modify_time=unix_timestamp(now()) * 1000
WHERE data_source_code='SR' AND sport_id=50;





-- 新增Pinnacle数据源
INSERT INTO `data_source` VALUES (15, 'PI', 'Pinnacle', 'PI', 188, 0, 1, 0, 0, 'Pinnacle', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

-- 新增Pinnacle源赛种和标准赛种对应关系
delete from third_sport_type where name_code = 2 and third_sport_id = '4' and data_source_code = 'PI';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (2, '4', 'PI',2, '篮球', '篮球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

delete from third_sport_type where name_code = 1 and third_sport_id = '29' and data_source_code = 'PI';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (1, '29', 'PI',1, '足球', '足球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);




-- 新增LS数据源
INSERT INTO `data_source` VALUES (18, 'LS', 'LS', 'LS', 185, 0, 1, 0, 0, 'LS数据商', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

-- 关联LS数据源
delete from third_sport_type where name_code = 2 and third_sport_id = '48242' and data_source_code = 'LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (2, '48242', 'LS',2, '篮球', '篮球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);


-- 8月脚本
--LS新增足球
delete from third_sport_type where name_code = 1 and third_sport_id = '6046' and data_source_code = 'LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (1, '6046', 'LS',1, '足球', '足球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

-- 1003脚本
--泰森赛事表新增轮次字段
ALTER TABLE third_match_history_statistics ADD COLUMN `round` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮次中文名 示例：组A';
ALTER TABLE third_match_history_statistics ADD COLUMN `round_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮次类型中文名 示例：分组赛';

--泰森球队榜单新增字段
ALTER TABLE third_sport_team_ranking ADD COLUMN `record5` json  DEFAULT NULL COMMENT '球队最近5场战绩 JOSN字符串（[{“id”:"赛事ID"，“winner”:“胜平负（WDL）”},...]）';
ALTER TABLE third_sport_team_ranking ADD COLUMN `star_players` json  DEFAULT NULL COMMENT '榜单明星球员 JOSN字符串（[{"player_id":"数据源球员ID","player_logo":"数据源球员logo","zs":"中文名称","en":"英文名称","position":"位置"} ,...]）';
ALTER TABLE third_sport_team_ranking ADD COLUMN `team_names` json  DEFAULT NULL COMMENT '球队多语言 JOSN字符串（{"zs":"简体","zh":"繁体","en":"英文","team_badge":"队伍logo"}）';
ALTER TABLE third_sport_team_ranking ADD COLUMN `group_names` json  DEFAULT NULL COMMENT '分组多语言 JOSN字符串（{"zs":"简体","zh":"繁体","en":"英文"}）';
ALTER TABLE third_sport_team_ranking ADD COLUMN `coach_info` json  DEFAULT NULL COMMENT '教练信息 JOSN字符串（{"coach_logo":"教练logo","zs":"中文名称","en":"英文名称""}）';


-- 新增事件编码
INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2041, 2, 'period_score_change', 'BG比分修改', 'BG比分修改', '1', 'N', '1',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Period Score Change');



-- RB排球、冰球新增事件编码 2022-10-26
-- 冰球
INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2046, 4, 'next_pen_scorer', '下一个得分手', '下一个得分手', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Next Pen Scorer');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2047, 4, 'penalty_about_to_be_taken', '即将被罚点球', '即将被罚点球', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Penalty About To Be Taken');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2048, 4, 'pen_shot', '点球', '点球', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Pen Shot');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2049, 4, 'missed_pen_shot', '点球未进', '点球未进', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Missed Pen Shot');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2050, 4, 'retake_pen_shot', '重罚点球', '重罚点球', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Retake Pen shot');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2051, 4, '2mins_pen', '离场2分钟处罚', '离场2分钟处罚', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'2 Mins Pen');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2052, 4, '5mins_pen', '离场5分钟处罚', '离场5分钟处罚', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'5 Mins Pen');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2053, 4, '10mins_pen', '离场10分钟处罚', '离场10分钟处罚', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'10 Mins Pen');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2054, 4, 'shot', '射门', '射门', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Shot');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2055, 4, 'faceoff_win', '赢得争球', '赢得争球', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Faceoff Win');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2056, 4, 'offside', '越位', '越位', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Offside');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2057, 4, 'icing', '结冰', '结冰', '', 'Y', '球员射门、用手或棍子击球或将冰球按此顺序偏转越过中心红线和对方球队的红色球门线，而冰球保持不动而没有进球的违规行为。',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Icing');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2058, 4, 'attacking_half', '攻区半场', '攻区半场', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Attacking Half');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2059, 4, 'goal_confirmation', '进球确认', '进球确认', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Goal Confirmation');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2060, 4, 'next_pen_shot', '轮到某一方点球', '轮到某一方点球', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Next Pen Shot');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2061, 4, 'retake_pen', '重新判罚', '重新判罚', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Retake Pen');

-- 排球
INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2062, 9, 'kill', '扣杀', '扣杀', '主客队扣杀', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Kill');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2063, 9, 'block', '拦网', '拦网', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Block');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2064, 9, 'out', '出界', '出界', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Out');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2065, 9, 'error', '失误', '失误', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Error');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2066, 9, 'ace', '发球得分', '发球得分', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Ace');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2067, 9, 'service_error', '发球失误', '发球失误', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Service Error');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2068, 9, 'penalty', '处罚', '处罚', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Penalty');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2069, 9, 'expulsion', '驱逐出场', '驱逐出场', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Expulsion');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2070, 9, 'disqualification', '取消资格', '取消资格', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Disqualification');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2071, 9, 'cancel_point', '取消得分', '取消得分', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Cancel Point');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2072, 9, 'point_confirmation', '得分确认', '得分确认', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Point Confirmation');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2073, 9, 'start_service', '开始发球', '开始发球', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Start Service');

-- 手球
INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2074, 11, 'golkeeper_throw', '守门员掷球', '守门员掷球', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Golkeeper Throw');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2075, 11, 'throw_in', '掷界外球', '掷界外球', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Throw In');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2076, 11, '2mins_penalty', '离场2分钟处罚', '离场2分钟处罚', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'2 Mins Penalty');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2077, 11, 'next_scorer', '下一个得分手', '下一个得分手', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Next Scorer');




-- 2022-11-15新增BT数据源
INSERT INTO `data_source` VALUES (19, 'BT', 'BT', 'BT', 190, 0, 1, 0, 0, 'BT数据商', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

-- 关联BT数据源
delete from third_sport_type where name_code = 1 and third_sport_id = '1' and data_source_code = 'BT';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (1, '1', 'BT',1, '足球', '足球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

delete from third_sport_type where name_code = 2 and third_sport_id = '18' and data_source_code = 'BT';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (2, '18', 'BT',2, '篮球', '篮球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);







-- 新增BI数据源
INSERT INTO `data_source` VALUES (23, 'BI', 'betinvest', 'BI', 171, 1, 1, 0, 0, 'betinvest数据源', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

delete from third_sport_type where name_code = 1 and third_sport_id = '184' and data_source_code = 'BI';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (1, '184', 'BI',1, '电子足球', '电子足球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

delete from third_sport_type where name_code = 2 and third_sport_id = '185' and data_source_code = 'BI';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (2, '185', 'BI',2, '电子篮球', '电子篮球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);


DROP TABLE IF EXISTS `match_event_info_bi`;
CREATE TABLE `match_event_info_bi`  (
    `id` bigint(20) UNSIGNED NOT NULL COMMENT 'id',
    `sport_id` bigint(20) NULL DEFAULT 0 COMMENT '体育种类id.  对应  standard_sport_type.id',
    `canceled` tinyint(4) NULL DEFAULT 0 COMMENT '是否被取消.1 被取消; 0:没有被取消',
    `data_source_code` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '对应data_source.code',
    `event_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '事件编码. 对应 match_event_type.event_code',
    `event_time` bigint(20) NOT NULL DEFAULT 0 COMMENT '事件发生时间. UTC时间',
    `extra_info` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '扩展信息',
    `addition1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
    `home_away` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '主客场. 主场队:home; 客场队:away',
    `second_num` int(11) NULL DEFAULT 0 COMMENT '当前第几局',
    `first_t1` int(11) NULL DEFAULT 0 COMMENT '盘主队比分',
    `first_t2` int(11) NULL DEFAULT 0 COMMENT '盘客队比分',
    `second_t1` int(11) NULL DEFAULT 0 COMMENT '局主队比分',
    `second_t2` int(11) NULL DEFAULT 0 COMMENT '局客队比分',
    `first_num` int(11) NULL DEFAULT 0 COMMENT '当前盘数',
    `match_period_id` bigint(20) NULL DEFAULT 0 COMMENT '比赛阶段id.  system_item_dict.value',
    `player1_id` bigint(20) NULL DEFAULT 0 COMMENT '球员1的id',
    `player1_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '球员1的名称',
    `player2_id` bigint(20) NULL DEFAULT 0 COMMENT '球员2的id',
    `player2_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '球员2的名称',
    `seconds_from_start` bigint(11) NULL DEFAULT 0 COMMENT '距离比赛开始多少秒',
    `period_remaining_seconds` bigint(11) NULL DEFAULT 0 COMMENT '当前节阶段剩余时间',
    `standard_match_id` bigint(20) NULL DEFAULT 0 COMMENT '标准赛事的id. 对应 standard_match_info.id',
    `standard_team_id` bigint(20) NULL DEFAULT 0 COMMENT '标准球队 ID. 对应 standard_sport_team.id',
    `t1` int(11) NULL DEFAULT 0 COMMENT '主队数量',
    `t2` int(11) NULL DEFAULT 0 COMMENT '客队数量',
    `third_event_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '第三方数据源提供的该事件id.',
    `third_match_id` bigint(20) NULL DEFAULT 0 COMMENT '第三方赛事的id. 对应third_match_info.id',
    `third_match_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '比赛在数据源中的ID',
    `third_team_id` bigint(20) NULL DEFAULT 0 COMMENT '第三方球队id. 对应 third_sport_team.id',
    `source_type` tinyint(4) NULL DEFAULT 0 COMMENT '数据来源类型.0: UOF;1: Scoring Feed',
    `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '备注',
    `create_time` bigint(20) NULL DEFAULT 0 COMMENT '创建时间. UTC时间',
    `modify_time` bigint(20) NULL DEFAULT 0 COMMENT '修改时间. UTC时间',
    `addition3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
    `addition4` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
    `addition5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
    `addition6` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition7` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition8` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition9` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition10` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段2',
    `send_data` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '下发数据标识：Y:已下发, N:未下发',
    `link_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '事件最新一次下发的linkId',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_third_event`(`third_event_id`) USING BTREE,
    INDEX `idx_third_match`(`third_match_id`) USING BTREE,
    INDEX `idx_standard_match`(`standard_match_id`) USING BTREE,
    INDEX `idx_source_match`(`third_match_source_id`) USING BTREE,
    INDEX `idx_event_code`(`event_code`) USING BTREE,
    INDEX `create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = 'KO赛事盘中事件表' ROW_FORMAT = Dynamic;


-- TS集锦  topic:MATCH_EVENT_INFO_VIDEO
DROP TABLE IF EXISTS `match_event_info_ts`;
CREATE TABLE `match_event_info_ts`  (
    `id` bigint(20) UNSIGNED NOT NULL COMMENT 'id',
    `sport_id` bigint(20) NULL DEFAULT 0 COMMENT '体育种类id.  对应  standard_sport_type.id',
    `canceled` tinyint(4) NULL DEFAULT 0 COMMENT '是否被取消.1 被取消; 0:没有被取消',
    `data_source_code` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '对应data_source.code',
    `event_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '事件编码. 对应 match_event_type.event_code',
    `event_time` bigint(20) NOT NULL DEFAULT 0 COMMENT '事件发生时间. UTC时间',
    `extra_info` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '扩展信息',
    `addition1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
    `home_away` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '主客场. 主场队:home; 客场队:away',
    `second_num` int(11) NULL DEFAULT 0 COMMENT '当前第几局',
    `first_t1` int(11) NULL DEFAULT 0 COMMENT '盘主队比分',
    `first_t2` int(11) NULL DEFAULT 0 COMMENT '盘客队比分',
    `second_t1` int(11) NULL DEFAULT 0 COMMENT '局主队比分',
    `second_t2` int(11) NULL DEFAULT 0 COMMENT '局客队比分',
    `first_num` int(11) NULL DEFAULT 0 COMMENT '当前盘数',
    `match_period_id` bigint(20) NULL DEFAULT 0 COMMENT '比赛阶段id.  system_item_dict.value',
    `player1_id` bigint(20) NULL DEFAULT 0 COMMENT '球员1的id',
    `player1_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '球员1的名称',
    `player2_id` bigint(20) NULL DEFAULT 0 COMMENT '球员2的id',
    `player2_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '球员2的名称',
    `seconds_from_start` bigint(11) NULL DEFAULT 0 COMMENT '距离比赛开始多少秒',
    `period_remaining_seconds` bigint(11) NULL DEFAULT 0 COMMENT '当前节阶段剩余时间',
    `standard_match_id` bigint(20) NULL DEFAULT 0 COMMENT '标准赛事的id. 对应 standard_match_info.id',
    `standard_team_id` bigint(20) NULL DEFAULT 0 COMMENT '标准球队 ID. 对应 standard_sport_team.id',
    `t1` int(11) NULL DEFAULT 0 COMMENT '主队数量',
    `t2` int(11) NULL DEFAULT 0 COMMENT '客队数量',
    `third_event_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '第三方数据源提供的该事件id.',
    `third_match_id` bigint(20) NULL DEFAULT 0 COMMENT '第三方赛事的id. 对应third_match_info.id',
    `third_match_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '比赛在数据源中的ID',
    `third_team_id` bigint(20) NULL DEFAULT 0 COMMENT '第三方球队id. 对应 third_sport_team.id',
    `source_type` tinyint(4) NULL DEFAULT 0 COMMENT '数据来源类型.0: UOF;1: Scoring Feed',
    `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '备注',
    `create_time` bigint(20) NULL DEFAULT 0 COMMENT '创建时间. UTC时间',
    `modify_time` bigint(20) NULL DEFAULT 0 COMMENT '修改时间. UTC时间',
    `addition3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
    `addition4` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
    `addition5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
    `addition6` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition7` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition8` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition9` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition10` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段2',
    `send_data` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '下发数据标识：Y:已下发, N:未下发',
    `link_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '事件最新一次下发的linkId',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_third_event`(`third_event_id`) USING BTREE,
    INDEX `idx_third_match`(`third_match_id`) USING BTREE,
    INDEX `idx_standard_match`(`standard_match_id`) USING BTREE,
    INDEX `idx_source_match`(`third_match_source_id`) USING BTREE,
    INDEX `idx_event_code`(`event_code`) USING BTREE,
    INDEX `create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = 'TS赛事盘中事件表' ROW_FORMAT = Dynamic;


INSERT INTO match_event_type(id,sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (null, 1, 'corner_taken','当角球被确认时下发','授予角球',null,'Y',null,'corner taken',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);



-- 新增KO数据源
INSERT INTO `data_source` VALUES (22, 'KO', 'KOSPT', 'KO', 179, 1, 1, 0, 0, 'KOSPT事件源', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

delete from third_sport_type where name_code = 1 and third_sport_id = '1' and data_source_code = 'KO';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (1, '1', 'KO',1, '足球', '足球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

delete from third_sport_type where name_code = 2 and third_sport_id = '2' and data_source_code = 'KO';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (2, '2', 'KO',2, '篮球', '篮球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

DROP TABLE IF EXISTS `match_event_info_ko`;
CREATE TABLE `match_event_info_ko`  (
    `id` bigint(20) UNSIGNED NOT NULL COMMENT 'id',
    `sport_id` bigint(20) NULL DEFAULT 0 COMMENT '体育种类id.  对应  standard_sport_type.id',
    `canceled` tinyint(4) NULL DEFAULT 0 COMMENT '是否被取消.1 被取消; 0:没有被取消',
    `data_source_code` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '对应data_source.code',
    `event_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '事件编码. 对应 match_event_type.event_code',
    `event_time` bigint(20) NOT NULL DEFAULT 0 COMMENT '事件发生时间. UTC时间',
    `extra_info` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '扩展信息',
    `addition1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
    `home_away` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '主客场. 主场队:home; 客场队:away',
    `second_num` int(11) NULL DEFAULT 0 COMMENT '当前第几局',
    `first_t1` int(11) NULL DEFAULT 0 COMMENT '盘主队比分',
    `first_t2` int(11) NULL DEFAULT 0 COMMENT '盘客队比分',
    `second_t1` int(11) NULL DEFAULT 0 COMMENT '局主队比分',
    `second_t2` int(11) NULL DEFAULT 0 COMMENT '局客队比分',
    `first_num` int(11) NULL DEFAULT 0 COMMENT '当前盘数',
    `match_period_id` bigint(20) NULL DEFAULT 0 COMMENT '比赛阶段id.  system_item_dict.value',
    `player1_id` bigint(20) NULL DEFAULT 0 COMMENT '球员1的id',
    `player1_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '球员1的名称',
    `player2_id` bigint(20) NULL DEFAULT 0 COMMENT '球员2的id',
    `player2_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '球员2的名称',
    `seconds_from_start` bigint(11) NULL DEFAULT 0 COMMENT '距离比赛开始多少秒',
    `period_remaining_seconds` bigint(11) NULL DEFAULT 0 COMMENT '当前节阶段剩余时间',
    `standard_match_id` bigint(20) NULL DEFAULT 0 COMMENT '标准赛事的id. 对应 standard_match_info.id',
    `standard_team_id` bigint(20) NULL DEFAULT 0 COMMENT '标准球队 ID. 对应 standard_sport_team.id',
    `t1` int(11) NULL DEFAULT 0 COMMENT '主队数量',
    `t2` int(11) NULL DEFAULT 0 COMMENT '客队数量',
    `third_event_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '第三方数据源提供的该事件id.',
    `third_match_id` bigint(20) NULL DEFAULT 0 COMMENT '第三方赛事的id. 对应third_match_info.id',
    `third_match_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '比赛在数据源中的ID',
    `third_team_id` bigint(20) NULL DEFAULT 0 COMMENT '第三方球队id. 对应 third_sport_team.id',
    `source_type` tinyint(4) NULL DEFAULT 0 COMMENT '数据来源类型.0: UOF;1: Scoring Feed',
    `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '备注',
    `create_time` bigint(20) NULL DEFAULT 0 COMMENT '创建时间. UTC时间',
    `modify_time` bigint(20) NULL DEFAULT 0 COMMENT '修改时间. UTC时间',
    `addition3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
    `addition4` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
    `addition5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
    `addition6` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition7` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition8` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition9` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition10` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段2',
    `send_data` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '下发数据标识：Y:已下发, N:未下发',
    `link_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '事件最新一次下发的linkId',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_third_event`(`third_event_id`) USING BTREE,
    INDEX `idx_third_match`(`third_match_id`) USING BTREE,
    INDEX `idx_standard_match`(`standard_match_id`) USING BTREE,
    INDEX `idx_source_match`(`third_match_source_id`) USING BTREE,
    INDEX `idx_event_code`(`event_code`) USING BTREE,
    INDEX `create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = 'KO赛事盘中事件表' ROW_FORMAT = Dynamic;



--20230612发生产开始
delete from third_sport_type where name_code = 5 and third_sport_id = '54094' and data_source_code = 'LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (5, '54094', 'LS',5, '网球', '网球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

-- 新增OD数据源
INSERT INTO `data_source` VALUES (24, 'OD', 'odding', 'OD', 169, 1, 1, 0, 0, 'odding数据源', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

delete from third_sport_type where name_code = 1 and third_sport_id = '19' and data_source_code = 'OD';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (1, '19', 'OD',1, '电子足球', '电子足球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

-- 新增RC数据源
INSERT INTO `data_source` VALUES (25, 'RC', 'Red-Cat', 'RC', 150, 1, 1, 0, 0, '红猫数据源', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

-- Ls新增事件下发
DROP TABLE IF EXISTS `match_event_info_ls`;
CREATE TABLE `match_event_info_ls`  (
    `id` bigint(20) UNSIGNED NOT NULL COMMENT 'id',
    `sport_id` bigint(20) NULL DEFAULT 0 COMMENT '体育种类id.  对应  standard_sport_type.id',
    `canceled` tinyint(4) NULL DEFAULT 0 COMMENT '是否被取消.1 被取消; 0:没有被取消',
    `data_source_code` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '对应data_source.code',
    `event_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '事件编码. 对应 match_event_type.event_code',
    `event_time` bigint(20) NOT NULL DEFAULT 0 COMMENT '事件发生时间. UTC时间',
    `extra_info` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '扩展信息',
    `addition1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
    `home_away` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '主客场. 主场队:home; 客场队:away',
    `second_num` int(11) NULL DEFAULT 0 COMMENT '当前第几局',
    `first_t1` int(11) NULL DEFAULT 0 COMMENT '盘主队比分',
    `first_t2` int(11) NULL DEFAULT 0 COMMENT '盘客队比分',
    `second_t1` int(11) NULL DEFAULT 0 COMMENT '局主队比分',
    `second_t2` int(11) NULL DEFAULT 0 COMMENT '局客队比分',
    `first_num` int(11) NULL DEFAULT 0 COMMENT '当前盘数',
    `match_period_id` bigint(20) NULL DEFAULT 0 COMMENT '比赛阶段id.  system_item_dict.value',
    `player1_id` bigint(20) NULL DEFAULT 0 COMMENT '球员1的id',
    `player1_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '球员1的名称',
    `player2_id` bigint(20) NULL DEFAULT 0 COMMENT '球员2的id',
    `player2_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '球员2的名称',
    `seconds_from_start` bigint(11) NULL DEFAULT 0 COMMENT '距离比赛开始多少秒',
    `period_remaining_seconds` bigint(11) NULL DEFAULT 0 COMMENT '当前节阶段剩余时间',
    `standard_match_id` bigint(20) NULL DEFAULT 0 COMMENT '标准赛事的id. 对应 standard_match_info.id',
    `standard_team_id` bigint(20) NULL DEFAULT 0 COMMENT '标准球队 ID. 对应 standard_sport_team.id',
    `t1` int(11) NULL DEFAULT 0 COMMENT '主队数量',
    `t2` int(11) NULL DEFAULT 0 COMMENT '客队数量',
    `third_event_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '第三方数据源提供的该事件id.',
    `third_match_id` bigint(20) NULL DEFAULT 0 COMMENT '第三方赛事的id. 对应third_match_info.id',
    `third_match_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '比赛在数据源中的ID',
    `third_team_id` bigint(20) NULL DEFAULT 0 COMMENT '第三方球队id. 对应 third_sport_team.id',
    `source_type` tinyint(4) NULL DEFAULT 0 COMMENT '数据来源类型.0: UOF;1: Scoring Feed',
    `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '备注',
    `create_time` bigint(20) NULL DEFAULT 0 COMMENT '创建时间. UTC时间',
    `modify_time` bigint(20) NULL DEFAULT 0 COMMENT '修改时间. UTC时间',
    `addition3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
    `addition4` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
    `addition5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
    `addition6` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition7` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition8` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition9` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition10` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
    `addition2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段2',
    `send_data` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '下发数据标识：Y:已下发, N:未下发',
    `link_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '事件最新一次下发的linkId',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_third_event`(`third_event_id`) USING BTREE,
    INDEX `idx_third_match`(`third_match_id`) USING BTREE,
    INDEX `idx_standard_match`(`standard_match_id`) USING BTREE,
    INDEX `idx_source_match`(`third_match_source_id`) USING BTREE,
    INDEX `idx_event_code`(`event_code`) USING BTREE,
    INDEX `create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = 'LS赛事盘中事件表' ROW_FORMAT = Dynamic;
--20230612发生产完成



--新增OD三方区域和标准区域关联  0710版本  隔离已执行
DELETE from third_sport_region where id = 'OD:0:15';
INSERT INTO third_sport_region (id,sport_id,third_region_id,reference_id,name_code,data_source_code,introduction,remark,create_time,modify_time) values
    ('OD:0:15','0',15,15,0,'OD','电子联盟','',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000);

-- LS新增斯诺克
delete from third_sport_type where name_code = 7 and third_sport_id = '262622' and data_source_code = 'LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (7, '262622', 'LS',7, '斯诺克', '斯诺克', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

-- 新增事件类型
DELETE from match_event_type where event_code = 'lost_connection';
INSERT INTO match_event_type(id,sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (null, 1, 'lost_connection','赛事事件断连','连线中断',null,'N',null,'Lost Connection',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

DELETE from match_event_type where event_code = 'active_connection_checking_status';
INSERT INTO match_event_type(id,sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (null, 1, 'active_connection_checking_status','比赛连接，核查中','比赛连接，核查中',null,'N',null,'Active Connection Checking Status',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

-- 新增事件类型（2576足球VAR事件）
DELETE from match_event_type where event_code = 'water_break';
INSERT INTO match_event_type(id,sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,create_time,modify_time)
VALUES (null, 1, 'water_break','喝水事件','喝水',null,'N',null,'Water Break',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

DELETE from match_event_type where event_code = 'reject_event';
INSERT INTO match_event_type(id,sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,create_time,modify_time)
VALUES (null, 1, 'reject_event','拒单事件','拒单事件',null,'N',null,'Reject Event',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

delete from third_sport_type where name_code = 2 and third_sport_id = '34' and data_source_code = 'OD';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (2, '34', 'OD',2, '电子篮球', '电子篮球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);


-- 2023.10.23版本
-- 新增电子赛事选手字段
ALTER TABLE third_match_info ADD COLUMN `home_away_player_name` varchar(100) DEFAULT NULL COMMENT '主客队球员名称(主名称,客名称)';

-- 优化单43014新增赛事级别赛事对阵类型&事件加速系数
ALTER TABLE third_match_info ADD COLUMN `competitor_type` tinyint(4) NULL DEFAULT 0 COMMENT '赛事对阵类型(0:人类，1:机器人)';
ALTER TABLE third_match_info ADD COLUMN `acceleration_factor` varchar(20) NULL DEFAULT 1 COMMENT '赛事事件加速系数';


-------------需求 2958	【操盘后台】新增4个标准事件(G01)开始  0108版本--------------------
ALTER TABLE third_match_history_statistics MODIFY `round` varchar(100) DEFAULT NULL COMMENT '轮次中文名 示例：组A';
ALTER TABLE third_match_history_statistics MODIFY `round_type` varchar(100) DEFAULT NULL COMMENT '轮次类型中文名 示例：分组赛';

--B04数据源调整为事件源
UPDATE data_source SET event_support = 1 WHERE code = 'BT';

--原始统计表remark字段长度调整为2000
ALTER TABLE match_statistics_info MODIFY `remark` varchar(2000) DEFAULT '' COMMENT '备注';

-- 需求 2958	【操盘后台】新增4个标准事件(G01)
INSERT INTO `match_event_type` (`id`, `sport_id`, `event_code`, `event_describe`, `event_name`, `extra_info`,`required_team`, `remark`, `create_time`, `modify_time`, `event_en_name`)
VALUES (2087, 1, 'goal_modified', '进球或取消进球后的比分修改事件', '修改进球', '', 'Y', '', unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000, 'goal modified');

INSERT INTO `match_event_type` (`id`, `sport_id`, `event_code`, `event_describe`, `event_name`, `extra_info`,`required_team`, `remark`, `create_time`, `modify_time`, `event_en_name`)
VALUES (2089, 1, 'redcard_modified', '缺少红牌后补回红牌以及取消之前红牌的修改红牌事', '修改红牌', '', 'Y', '', unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000, 'redcard modified');

INSERT INTO `match_event_type` (`id`, `sport_id`, `event_code`, `event_describe`, `event_name`, `extra_info`,
                                `required_team`, `remark`, `create_time`, `modify_time`, `event_en_name`)
VALUES (2090, 1, 'yellowcard_modified', '缺少黄牌后补回黄牌以及取消之前黄牌的修改黄牌事件', '修改黄牌', '', 'Y', '', unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000, 'yellowcard modified');

INSERT INTO `match_event_type` (`id`, `sport_id`, `event_code`, `event_describe`, `event_name`, `extra_info`,`required_team`, `remark`, `create_time`, `modify_time`, `event_en_name`)
VALUES (2091, 1, 'corner_modified', '补回之前角球以及取消之前角球的修改角球事件', '修改角球', '', 'Y', '', unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000, 'corner modified');

INSERT INTO `match_event_type` (`id`, `sport_id`, `event_code`, `event_describe`, `event_name`, `extra_info`,`required_team`, `remark`, `create_time`, `modify_time`, `event_en_name`)
VALUES (2092, 1, 'stoppage_time_confirmed', '上半场与全场的伤停补时时间', '补时确认', '', 'Y', '', unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000, 'stoppage time confirmed');

INSERT INTO `match_event_type` (`id`, `sport_id`, `event_code`, `event_describe`, `event_name`, `extra_info`,`required_team`, `remark`, `create_time`, `modify_time`, `event_en_name`)
VALUES (2093, 1, 'system_message', '系统发送的备注消息', '系统消息', '', 'N', '', unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000, 'system message');

-------------需求 2958	【操盘后台】新增4个标准事件(G01)结束 ------------------
--新增SK数据源
delete from `data_source` where `short_name`='SK';
INSERT INTO `data_source` VALUES ('30','SK', 'SK','SK', 0,0,0,0,0,'SK', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);



-------------需求2915 【客户端】接入L01动画源--------------------
-- 回滚脚本
delete from `third_sport_type` where `third_sport_id`='154830' and `data_source_code`='LS';
delete from `third_sport_type` where `third_sport_id`='35709' and `data_source_code`='LS';
delete from `third_sport_type` where `third_sport_id`='274791' and `data_source_code`='LS';
delete from `third_sport_type` where `third_sport_id`='274792' and `data_source_code`='LS';
delete from `third_sport_type` where `third_sport_id`='35232' and `data_source_code`='LS';
delete from `third_sport_type` where `third_sport_id`='1149093' and `data_source_code`='LS';
delete from `third_sport_type` where `third_sport_id`='265917' and `data_source_code`='LS';
delete from `third_sport_type` where `third_sport_id`='154914' and `data_source_code`='LS';
delete from `third_sport_type` where `third_sport_id`='452674' and `data_source_code`='LS';
delete from `third_sport_type` where `third_sport_id`='621569' and `data_source_code`='LS';
delete from `third_sport_type` where `third_sport_id`='154923' and `data_source_code`='LS';
delete from `third_sport_type` where `third_sport_id`='131506' and `data_source_code`='LS';

-- LS增加赛种
delete from `third_sport_type` where `third_sport_id`='154830' and `data_source_code`='LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (9, '154830', 'LS', 9, '排球', '排球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

delete from `third_sport_type` where `third_sport_id`='35709' and `data_source_code`='LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (11, '35709', 'LS', 11, '手球', '手球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

delete from `third_sport_type` where `third_sport_id`='274791' and `data_source_code`='LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (14, '274791', 'LS', 14, '联合式橄榄球', 'Rugby Union', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

delete from `third_sport_type` where `third_sport_id`='274792' and `data_source_code`='LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (41, '274792', 'LS', 41, '联盟式橄榄球', 'Rugby League', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

delete from `third_sport_type` where `third_sport_id`='35232' and `data_source_code`='LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (4, '35232', 'LS', 4, '冰球', '冰球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

delete from `third_sport_type` where `third_sport_id`='1149093' and `data_source_code`='LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (10, '1149093', 'LS', 10, '羽毛球', '羽毛球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

delete from `third_sport_type` where `third_sport_id`='265917' and `data_source_code`='LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (8, '265917', 'LS', 8, '乒乓球', '乒乓球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

delete from `third_sport_type` where `third_sport_id`='154914' and `data_source_code`='LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (3, '154914', 'LS', 3, '棒球', '棒球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

delete from `third_sport_type` where `third_sport_id`='452674' and `data_source_code`='LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (37, '452674', 'LS', 37, '板球', '板球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

delete from `third_sport_type` where `third_sport_id`='621569' and `data_source_code`='LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (13, '621569', 'LS', 13, '沙滩排球', '沙滩排球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

delete from `third_sport_type` where `third_sport_id`='154923' and `data_source_code`='LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (38, '154923', 'LS', 38, '飞镖', '飞镖', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

delete from `third_sport_type` where `third_sport_id`='131506' and `data_source_code`='LS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (6, '131506', 'LS', 6, '美式足球', '美式足球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);


-------------需求2843 【操盘风控】爬虫数据接入开始 0205版本--------------------
-- 新增台湾N01数据源回滚
delete from data_source where id = 28;
delete from data_source where id = 29;

-- 新增台湾N01数据源
INSERT INTO `data_source` VALUES (28, 'N01', 'N01', 'N01', 149, 1, 1, 0, 0, 'IM與FB数据抓取', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
INSERT INTO `data_source` VALUES (29, 'N02', 'N02', 'N02', 148, 1, 1, 0, 0, 'IM與FB数据抓取', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

DROP TABLE IF EXISTS `match_event_info_n01`;
CREATE TABLE `match_event_info_n01`  (
     `id` bigint(20) UNSIGNED NOT NULL COMMENT 'id',
     `sport_id` bigint(20) NULL DEFAULT 0 COMMENT '体育种类id.  对应  standard_sport_type.id',
     `canceled` tinyint(4) NULL DEFAULT 0 COMMENT '是否被取消.1 被取消; 0:没有被取消',
     `data_source_code` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '对应data_source.code',
     `event_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '事件编码. 对应 match_event_type.event_code',
     `event_time` bigint(20) NOT NULL DEFAULT 0 COMMENT '事件发生时间. UTC时间',
     `extra_info` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '扩展信息',
     `addition1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
     `home_away` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '主客场. 主场队:home; 客场队:away',
     `second_num` int(11) NULL DEFAULT 0 COMMENT '当前第几局',
     `first_t1` int(11) NULL DEFAULT 0 COMMENT '盘主队比分',
     `first_t2` int(11) NULL DEFAULT 0 COMMENT '盘客队比分',
     `second_t1` int(11) NULL DEFAULT 0 COMMENT '局主队比分',
     `second_t2` int(11) NULL DEFAULT 0 COMMENT '局客队比分',
     `first_num` int(11) NULL DEFAULT 0 COMMENT '当前盘数',
     `match_period_id` bigint(20) NULL DEFAULT 0 COMMENT '比赛阶段id.  system_item_dict.value',
     `player1_id` bigint(20) NULL DEFAULT 0 COMMENT '球员1的id',
     `player1_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '球员1的名称',
     `player2_id` bigint(20) NULL DEFAULT 0 COMMENT '球员2的id',
     `player2_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '球员2的名称',
     `seconds_from_start` bigint(11) NULL DEFAULT 0 COMMENT '距离比赛开始多少秒',
     `period_remaining_seconds` bigint(11) NULL DEFAULT 0 COMMENT '当前节阶段剩余时间',
     `standard_match_id` bigint(20) NULL DEFAULT 0 COMMENT '标准赛事的id. 对应 standard_match_info.id',
     `standard_team_id` bigint(20) NULL DEFAULT 0 COMMENT '标准球队 ID. 对应 standard_sport_team.id',
     `t1` int(11) NULL DEFAULT 0 COMMENT '主队数量',
     `t2` int(11) NULL DEFAULT 0 COMMENT '客队数量',
     `third_event_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '第三方数据源提供的该事件id.',
     `third_match_id` bigint(20) NULL DEFAULT 0 COMMENT '第三方赛事的id. 对应third_match_info.id',
     `third_match_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '比赛在数据源中的ID',
     `third_team_id` bigint(20) NULL DEFAULT 0 COMMENT '第三方球队id. 对应 third_sport_team.id',
     `source_type` tinyint(4) NULL DEFAULT 0 COMMENT '数据来源类型.0: UOF;1: Scoring Feed',
     `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '备注',
     `create_time` bigint(20) NULL DEFAULT 0 COMMENT '创建时间. UTC时间',
     `modify_time` bigint(20) NULL DEFAULT 0 COMMENT '修改时间. UTC时间',
     `addition3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
     `addition4` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
     `addition5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
     `addition6` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
     `addition7` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
     `addition8` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
     `addition9` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
     `addition10` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
     `addition2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段2',
     `send_data` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '下发数据标识：Y:已下发, N:未下发',
     `link_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '事件最新一次下发的linkId',
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `idx_third_event`(`third_event_id`) USING BTREE,
     INDEX `idx_third_match`(`third_match_id`) USING BTREE,
     INDEX `idx_standard_match`(`standard_match_id`) USING BTREE,
     INDEX `idx_source_match`(`third_match_source_id`) USING BTREE,
     INDEX `idx_event_code`(`event_code`) USING BTREE,
     INDEX `create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = 'N01赛事盘中事件表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `match_event_info_n02`;
CREATE TABLE `match_event_info_n02`  (
     `id` bigint(20) UNSIGNED NOT NULL COMMENT 'id',
     `sport_id` bigint(20) NULL DEFAULT 0 COMMENT '体育种类id.  对应  standard_sport_type.id',
     `canceled` tinyint(4) NULL DEFAULT 0 COMMENT '是否被取消.1 被取消; 0:没有被取消',
     `data_source_code` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '对应data_source.code',
     `event_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '事件编码. 对应 match_event_type.event_code',
     `event_time` bigint(20) NOT NULL DEFAULT 0 COMMENT '事件发生时间. UTC时间',
     `extra_info` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '扩展信息',
     `addition1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
     `home_away` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '主客场. 主场队:home; 客场队:away',
     `second_num` int(11) NULL DEFAULT 0 COMMENT '当前第几局',
     `first_t1` int(11) NULL DEFAULT 0 COMMENT '盘主队比分',
     `first_t2` int(11) NULL DEFAULT 0 COMMENT '盘客队比分',
     `second_t1` int(11) NULL DEFAULT 0 COMMENT '局主队比分',
     `second_t2` int(11) NULL DEFAULT 0 COMMENT '局客队比分',
     `first_num` int(11) NULL DEFAULT 0 COMMENT '当前盘数',
     `match_period_id` bigint(20) NULL DEFAULT 0 COMMENT '比赛阶段id.  system_item_dict.value',
     `player1_id` bigint(20) NULL DEFAULT 0 COMMENT '球员1的id',
     `player1_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '球员1的名称',
     `player2_id` bigint(20) NULL DEFAULT 0 COMMENT '球员2的id',
     `player2_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '球员2的名称',
     `seconds_from_start` bigint(11) NULL DEFAULT 0 COMMENT '距离比赛开始多少秒',
     `period_remaining_seconds` bigint(11) NULL DEFAULT 0 COMMENT '当前节阶段剩余时间',
     `standard_match_id` bigint(20) NULL DEFAULT 0 COMMENT '标准赛事的id. 对应 standard_match_info.id',
     `standard_team_id` bigint(20) NULL DEFAULT 0 COMMENT '标准球队 ID. 对应 standard_sport_team.id',
     `t1` int(11) NULL DEFAULT 0 COMMENT '主队数量',
     `t2` int(11) NULL DEFAULT 0 COMMENT '客队数量',
     `third_event_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '第三方数据源提供的该事件id.',
     `third_match_id` bigint(20) NULL DEFAULT 0 COMMENT '第三方赛事的id. 对应third_match_info.id',
     `third_match_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '比赛在数据源中的ID',
     `third_team_id` bigint(20) NULL DEFAULT 0 COMMENT '第三方球队id. 对应 third_sport_team.id',
     `source_type` tinyint(4) NULL DEFAULT 0 COMMENT '数据来源类型.0: UOF;1: Scoring Feed',
     `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '备注',
     `create_time` bigint(20) NULL DEFAULT 0 COMMENT '创建时间. UTC时间',
     `modify_time` bigint(20) NULL DEFAULT 0 COMMENT '修改时间. UTC时间',
     `addition3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
     `addition4` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
     `addition5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段',
     `addition6` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
     `addition7` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
     `addition8` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
     `addition9` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
     `addition10` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '附加字段',
     `addition2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '扩展字段2',
     `send_data` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '下发数据标识：Y:已下发, N:未下发',
     `link_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '事件最新一次下发的linkId',
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `idx_third_event`(`third_event_id`) USING BTREE,
     INDEX `idx_third_match`(`third_match_id`) USING BTREE,
     INDEX `idx_standard_match`(`standard_match_id`) USING BTREE,
     INDEX `idx_source_match`(`third_match_source_id`) USING BTREE,
     INDEX `idx_event_code`(`event_code`) USING BTREE,
     INDEX `create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = 'N02赛事盘中事件表' ROW_FORMAT = Dynamic;

-------------需求2843 【操盘风控】爬虫数据接入完成 --------------------

-- 需求 2550 【A01】【操盘风控】足球-范特西联赛
DROP TABLE IF EXISTS `match_event_info_fts`;
CREATE TABLE `match_event_info_fts`
(
    `id`                       bigint(20) UNSIGNED NOT NULL COMMENT 'id',
    `sport_id`                 bigint(20) NULL DEFAULT 0 COMMENT '体育种类id.  对应  standard_sport_type.id',
    `canceled`                 tinyint(4) NULL DEFAULT 0 COMMENT '是否被取消.1 被取消; 0:没有被取消',
    `data_source_code`         varchar(4)  NULL DEFAULT '' COMMENT '对应data_source.code',
    `event_code`               varchar(64)  NULL DEFAULT '' COMMENT '事件编码. 对应 match_event_type.event_code',
    `event_time`               bigint(20) NOT NULL DEFAULT 0 COMMENT '事件发生时间. UTC时间',
    `extra_info`               varchar(256)  NULL DEFAULT '' COMMENT '扩展信息',
    `addition1`                varchar(255)  NULL DEFAULT NULL,
    `home_away`                varchar(6)  NULL DEFAULT '' COMMENT '主客场. 主场队:home; 客场队:away',
    `second_num`               int(11) NULL DEFAULT 0 COMMENT '当前第几局',
    `first_t1`                 int(11) NULL DEFAULT 0 COMMENT '盘主队比分',
    `first_t2`                 int(11) NULL DEFAULT 0 COMMENT '盘客队比分',
    `second_t1`                int(11) NULL DEFAULT 0 COMMENT '局主队比分',
    `second_t2`                int(11) NULL DEFAULT 0 COMMENT '局客队比分',
    `first_num`                int(11) NULL DEFAULT 0 COMMENT '当前盘数',
    `match_period_id`          bigint(20) NULL DEFAULT 0 COMMENT '比赛阶段id.  system_item_dict.value',
    `player1_id`               bigint(20) NULL DEFAULT 0 COMMENT '球员1的id',
    `player1_name`             varchar(64)  NULL DEFAULT '' COMMENT '球员1的名称',
    `player2_id`               bigint(20) NULL DEFAULT 0 COMMENT '球员2的id',
    `player2_name`             varchar(64)  NULL DEFAULT '' COMMENT '球员2的名称',
    `seconds_from_start`       bigint(11) NULL DEFAULT 0 COMMENT '距离比赛开始多少秒',
    `period_remaining_seconds` bigint(11) NULL DEFAULT 0 COMMENT '当前节阶段剩余时间',
    `standard_match_id`        bigint(20) NULL DEFAULT 0 COMMENT '标准赛事的id. 对应 standard_match_info.id',
    `standard_team_id`         bigint(20) NULL DEFAULT 0 COMMENT '标准球队 ID. 对应 standard_sport_team.id',
    `t1`                       int(11) NULL DEFAULT 0 COMMENT '主队数量',
    `t2`                       int(11) NULL DEFAULT 0 COMMENT '客队数量',
    `third_event_id`           varchar(80)  NULL DEFAULT '' COMMENT '第三方数据源提供的该事件id.',
    `third_match_id`           bigint(20) NULL DEFAULT 0 COMMENT '第三方赛事的id. 对应third_match_info.id',
    `third_match_source_id`    varchar(50)  NULL DEFAULT '' COMMENT '比赛在数据源中的ID',
    `third_team_id`            bigint(20) NULL DEFAULT 0 COMMENT '第三方球队id. 对应 third_sport_team.id',
    `source_type`              tinyint(4) NULL DEFAULT 0 COMMENT '数据来源类型.0: UOF;1: Scoring Feed',
    `remark`                   varchar(500)  NULL DEFAULT '' COMMENT '备注',
    `create_time`              bigint(20) NULL DEFAULT 0 COMMENT '创建时间. UTC时间',
    `modify_time`              bigint(20) NULL DEFAULT 0 COMMENT '修改时间. UTC时间',
    `addition3`                varchar(255)  NULL DEFAULT NULL COMMENT '扩展字段',
    `addition4`                varchar(255)  NULL DEFAULT NULL COMMENT '扩展字段',
    `addition5`                varchar(255)  NULL DEFAULT NULL COMMENT '扩展字段',
    `addition6`                varchar(255)  NULL DEFAULT '' COMMENT '附加字段',
    `addition7`                varchar(255)  NULL DEFAULT '' COMMENT '附加字段',
    `addition8`                varchar(255)  NULL DEFAULT '' COMMENT '附加字段',
    `addition9`                varchar(255)  NULL DEFAULT '' COMMENT '附加字段',
    `addition10`               varchar(255)  NULL DEFAULT '' COMMENT '附加字段',
    `addition2`                varchar(255)  NULL DEFAULT NULL COMMENT '扩展字段2',
    `send_data`                char(1)  NULL DEFAULT '' COMMENT '下发数据标识：Y:已下发, N:未下发',
    `link_id`                  varchar(60)  NULL DEFAULT '' COMMENT '事件最新一次下发的linkId',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX                      `idx_third_event`(`third_event_id`) USING BTREE,
    INDEX                      `idx_third_match`(`third_match_id`) USING BTREE,
    INDEX                      `idx_standard_match`(`standard_match_id`) USING BTREE,
    INDEX                      `idx_source_match`(`third_match_source_id`) USING BTREE,
    INDEX                      `idx_event_code`(`event_code`) USING BTREE,
    INDEX                      `create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 COMMENT = '范特西赛事盘中事件表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `match_event_info_fts1`;
CREATE TABLE `match_event_info_fts1`
(
    `id`                       bigint(20) UNSIGNED NOT NULL COMMENT 'id',
    `sport_id`                 bigint(20) NULL DEFAULT 0 COMMENT '体育种类id.  对应  standard_sport_type.id',
    `canceled`                 tinyint(4) NULL DEFAULT 0 COMMENT '是否被取消.1 被取消; 0:没有被取消',
    `data_source_code`         varchar(4)  NULL DEFAULT '' COMMENT '对应data_source.code',
    `event_code`               varchar(64)  NULL DEFAULT '' COMMENT '事件编码. 对应 match_event_type.event_code',
    `event_time`               bigint(20) NOT NULL DEFAULT 0 COMMENT '事件发生时间. UTC时间',
    `extra_info`               varchar(256)  NULL DEFAULT '' COMMENT '扩展信息',
    `addition1`                varchar(255)  NULL DEFAULT NULL,
    `home_away`                varchar(6)  NULL DEFAULT '' COMMENT '主客场. 主场队:home; 客场队:away',
    `second_num`               int(11) NULL DEFAULT 0 COMMENT '当前第几局',
    `first_t1`                 int(11) NULL DEFAULT 0 COMMENT '盘主队比分',
    `first_t2`                 int(11) NULL DEFAULT 0 COMMENT '盘客队比分',
    `second_t1`                int(11) NULL DEFAULT 0 COMMENT '局主队比分',
    `second_t2`                int(11) NULL DEFAULT 0 COMMENT '局客队比分',
    `first_num`                int(11) NULL DEFAULT 0 COMMENT '当前盘数',
    `match_period_id`          bigint(20) NULL DEFAULT 0 COMMENT '比赛阶段id.  system_item_dict.value',
    `player1_id`               bigint(20) NULL DEFAULT 0 COMMENT '球员1的id',
    `player1_name`             varchar(64)  NULL DEFAULT '' COMMENT '球员1的名称',
    `player2_id`               bigint(20) NULL DEFAULT 0 COMMENT '球员2的id',
    `player2_name`             varchar(64)  NULL DEFAULT '' COMMENT '球员2的名称',
    `seconds_from_start`       bigint(11) NULL DEFAULT 0 COMMENT '距离比赛开始多少秒',
    `period_remaining_seconds` bigint(11) NULL DEFAULT 0 COMMENT '当前节阶段剩余时间',
    `standard_match_id`        bigint(20) NULL DEFAULT 0 COMMENT '标准赛事的id. 对应 standard_match_info.id',
    `standard_team_id`         bigint(20) NULL DEFAULT 0 COMMENT '标准球队 ID. 对应 standard_sport_team.id',
    `t1`                       int(11) NULL DEFAULT 0 COMMENT '主队数量',
    `t2`                       int(11) NULL DEFAULT 0 COMMENT '客队数量',
    `third_event_id`           varchar(80)  NULL DEFAULT '' COMMENT '第三方数据源提供的该事件id.',
    `third_match_id`           bigint(20) NULL DEFAULT 0 COMMENT '第三方赛事的id. 对应third_match_info.id',
    `third_match_source_id`    varchar(50)  NULL DEFAULT '' COMMENT '比赛在数据源中的ID',
    `third_team_id`            bigint(20) NULL DEFAULT 0 COMMENT '第三方球队id. 对应 third_sport_team.id',
    `source_type`              tinyint(4) NULL DEFAULT 0 COMMENT '数据来源类型.0: UOF;1: Scoring Feed',
    `remark`                   varchar(500)  NULL DEFAULT '' COMMENT '备注',
    `create_time`              bigint(20) NULL DEFAULT 0 COMMENT '创建时间. UTC时间',
    `modify_time`              bigint(20) NULL DEFAULT 0 COMMENT '修改时间. UTC时间',
    `addition3`                varchar(255)  NULL DEFAULT NULL COMMENT '扩展字段',
    `addition4`                varchar(255)  NULL DEFAULT NULL COMMENT '扩展字段',
    `addition5`                varchar(255)  NULL DEFAULT NULL COMMENT '扩展字段',
    `addition6`                varchar(255)  NULL DEFAULT '' COMMENT '附加字段',
    `addition7`                varchar(255)  NULL DEFAULT '' COMMENT '附加字段',
    `addition8`                varchar(255)  NULL DEFAULT '' COMMENT '附加字段',
    `addition9`                varchar(255)  NULL DEFAULT '' COMMENT '附加字段',
    `addition10`               varchar(255)  NULL DEFAULT '' COMMENT '附加字段',
    `addition2`                varchar(255)  NULL DEFAULT NULL COMMENT '扩展字段2',
    `send_data`                char(1)  NULL DEFAULT '' COMMENT '下发数据标识：Y:已下发, N:未下发',
    `link_id`                  varchar(60)  NULL DEFAULT '' COMMENT '事件最新一次下发的linkId',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX                      `idx_third_event`(`third_event_id`) USING BTREE,
    INDEX                      `idx_third_match`(`third_match_id`) USING BTREE,
    INDEX                      `idx_standard_match`(`standard_match_id`) USING BTREE,
    INDEX                      `idx_source_match`(`third_match_source_id`) USING BTREE,
    INDEX                      `idx_event_code`(`event_code`) USING BTREE,
    INDEX                      `create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 COMMENT = '范特西赛事盘中事件表' ROW_FORMAT = Dynamic;

-------------需求2927,标准赛事、三方数据商赛事联赛名变更需要额外标识 --------------------
ALTER TABLE `standard_match_info` ADD COLUMN `tournament_change_status` int(4) DEFAULT '0' COMMENT '标准赛事联赛名是否变更,0:否,1:是',ALGORITHM=INPLACE,LOCK=NONE;
ALTER TABLE `third_match_info` ADD COLUMN `tournament_change_status` int(4) DEFAULT '0' COMMENT '三方赛事联赛名是否变更,0:否,1:是',ALGORITHM=INPLACE,LOCK=NONE;

-------------生产bug49152,风控滚球操盘赛事开赛时间超过4小时限制优化 --------------------
ALTER TABLE `standard_match_info` ADD COLUMN `interruption_cancellation_status` int(4) DEFAULT '0' COMMENT '标准赛事是否出现过中断或取消状态,0:否,1:是',ALGORITHM=INPLACE,LOCK=NONE;
ALTER TABLE `third_match_info` ADD COLUMN `interruption_cancellation_status` int(4) DEFAULT '0' COMMENT '三方赛事是否出现过中断或取消状态,0:否,1:是',ALGORITHM=INPLACE,LOCK=NONE;

INSERT INTO match_event_type(id,sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,create_time,modify_time)
VALUES (2094, 1, 'no_red_card','没有掏红牌','没有掏红牌',null,'N',null,'No red card',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);



-------------3070 【视频动画商】G-Live数据商视频接入  V03，3281 【数据源】V04数据源接入-------------
INSERT INTO `data_source` VALUES (33, 'V03', 'V03', 'V03', 164, 0, 0, 0, 0, 'V03视频源', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
INSERT INTO `data_source` VALUES (34, 'V04', 'V04', 'V04', 163, 0, 0, 0, 0, 'V04视频源', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);



-- 需求：2798【客户端】2024欧洲杯专题页
-- 创建索引
CREATE INDEX idx_source_tournament_id ON third_match_history_statistics (third_tournament_source_id);
CREATE INDEX idx_source_season_id ON third_match_history_statistics (third_season_source_id);

ALTER TABLE third_match_history_statistics MODIFY `home_team_score` varchar(20) DEFAULT NULL COMMENT '主队得分(7:5表示 全场:点球大战)';
ALTER TABLE third_match_history_statistics MODIFY `away_team_score` varchar(20) DEFAULT NULL COMMENT '客队得分(7:5表示 全场:点球大战)';

--3031篮球增加事件
INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2095, 2, 'rebound_attack', '进攻', '进攻', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Rebound Attack');
INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2096, 2, 'rebound_defense', '防守', '防守', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Rebound Defense');
INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2097, 2, 'assist', '助攻', '助攻', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Assist');
INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2098, 2, 'ball_possession', '控球权', '控球权', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Ball Possession');


-- 65841优化单，足球新增连接恢复事件
INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (2099, 1, 'recovery_connection', '连线恢复', '连线恢复', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Recovery Connection');


-- F01数据源（3277 【数据源】F01数据源接入）09.23
INSERT INTO `data_source` VALUES (37, 'F01', 'F01', 'F01', 191, 1, 1, 0, 0, 'F01数据源', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

-- O01新增赛种关系（需求 3576，3577）09.23
delete from third_sport_type where name_code = 1 and third_sport_id = '32' and data_source_code = 'OD';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (1, '32', 'OD',1, '电子足球', '电子足球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

delete from third_sport_type where name_code = 2 and third_sport_id = '33' and data_source_code = 'OD';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (2, '33', 'OD',2, '电子篮球', '电子篮球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

-- 新增D01数据源（需求 3333）09.23
delete from data_source where code = 'D01';
INSERT INTO `data_source` VALUES (38, 'D01', 'D01', 'D01', 165, 0, 0, 0, 0, 'D01数据源', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

--新增事件类型
INSERT INTO match_event_type(sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (1, 'penalty','点球确认','点球确认',null,'Y',null,'penalty',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

INSERT INTO match_event_type(sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (1, 'penalty_goal','确认点球进球','确认点球进球',null,'Y',null,'penalty goal',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

INSERT INTO match_event_type(sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (1, 'penalty_canceled','点球状态: 取消点球','取消点球',null,'Y',null,'penalty canceled',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

INSERT INTO match_event_type(sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (1, 'possible_var_red_card','可能VAR: 罚牌','可能VAR罚牌',null,'Y',null,'possible var red card',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

INSERT INTO match_event_type(sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (1, 'possible_var_goal','可能VAR: 进球','可能VAR进球',null,'Y',null,'possible var goal',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

INSERT INTO match_event_type(sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (1, 'possible_var_penalty','可能VAR: 点球','可能VAR点球',null,'Y',null,'possible var penalty',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

INSERT INTO match_event_type(sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (1, 'var_red_card','VAR确认罚牌','VAR确认罚牌',null,'Y',null,'var red card',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

INSERT INTO match_event_type(sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (1, 'var_goal','VAR确认进球','VAR确认进球',null,'Y',null,'var goal',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

INSERT INTO match_event_type(sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (1, 'var_penalty','VAR确认点球','VAR确认点球',null,'Y',null,'var penalty',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

INSERT INTO match_event_type(sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (1, 'var_yellow_card','VAR确认黄牌','VAR确认黄牌',null,'Y',null,'var yellow card',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

INSERT INTO match_event_type(sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (1, 'canceled_var_red_card','VAR取消罚牌','VAR取消罚牌',null,'Y',null,'canceled var red card',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

INSERT INTO match_event_type(sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (1, 'canceled_var_goal','VAR取消进球','VAR取消进球',null,'Y',null,'canceled var goal',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

INSERT INTO match_event_type(sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (1, 'canceled_var_penalty','VAR取消点球','VAR取消点球',null,'Y',null,'canceled var penalty',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

INSERT INTO match_event_type(sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (1, 'penalty_first','先罚','先罚',null,'Y',null,'penalty first',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

INSERT INTO match_event_type(sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,standard_code,create_time,modify_time)
VALUES (1, 'kick_off_team_none','开球球队(不区分主客队)','开球球队(不区分主客队)',null,'N',null,'kick off team none',null,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

--P01视频源
INSERT INTO `data_source` VALUES (39, 'P01', 'P01', 'P01', 144, 0, 0, 0, 0, 'P01视频源', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
update `data_source` set `remark`='测试数据源' where `code`='PAtest';


-- 66013 【产品】【生产】操盘后台新增比赛相关信息（11.11）
ALTER TABLE `third_match_info` ADD COLUMN `live_event_source` int(4) DEFAULT '0' COMMENT '事件来源类型(0:其他，1:现场（VENUE）,2电视（TV）)',ALGORITHM=INPLACE,LOCK=NONE;


-- S02赛种关系调整(S02 9:橄榄球 为匹配)
delete from `third_sport_type` where `data_source_code`='SK';
INSERT INTO `third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (1, '1', 'SK', 1, '足球', '足球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO `third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (2, '2', 'SK', 2, '篮球', '篮球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO `third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (3, '18', 'SK', 3, '棒球', '棒球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO `third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (5, '3', 'SK', 5, '网球', '网球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO `third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (7, '5', 'SK', 7, '斯诺克/台球', '斯诺克/台球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO `third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (9, '6', 'SK', 9, '排球', '排球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO `third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (11, '11', 'SK', 11, '手球', '手球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO `third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (4, '12', 'SK', 4, '冰球', '冰球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO `third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (37, '13', 'SK', 37, '板球', '板球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO `third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (6, '16', 'SK', 6, '美式足球', '美式足球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO `third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (10, '19', 'SK', 10, '羽毛球', '羽毛球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO `third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (8, '23', 'SK', 8, '乒乓球', '乒乓球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO `third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (15, '28', 'SK', 15, '曲棍球', '曲棍球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);


--------------------------------------------------------1230版本---------------------------------------------------------

--3648 G01/B02增加板球
delete from third_sport_type where name_code = 37 and third_sport_id = '6' and data_source_code = 'BG';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (37, '6', 'BG',37, '板球', '板球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
delete from third_sport_type where name_code = 37 and third_sport_id = '19' and data_source_code = 'BC';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (37, '19', 'BC',37, '板球', '板球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

--板球标准事件编码
delete from match_event_type where sport_id = 37;

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 37, 'valid', '指的是投球手合法的投球。有效的球是指不是“无球”或“宽球”的，并且按照规定投出的球。', '有效', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'valid');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 37, 'delivery', '每次投球手投出的球都称为一次投球。一个标准的“一轮”（Over）由六次投球组成。', '投球', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'delivery');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 37, 'over', '由一个投球手连续投出的六个合法球称为“一轮”（Over）。当一个“一轮”完成后，另一位投球手会从对面的球场端接手继续投球。', '轮次', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'over');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 37, 'point', '指的是得分系统（例如，赢得比赛或平局的积分）。', '分数', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'point');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 37, 'period_Count', 'periodCount	指已经进行的“一轮”或“时段”的数量。', '时段计数', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'period_Count');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 37, 'total_Runs', '指的是一个队伍或球员在比赛期间或特定时间段内的总得分。', '总分', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'total_Runs');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 37, 'wicet', '指击球手出局（失去一个“门柱”），或指由三个柱子和两个小横木组成的“门柱”装置，也可以指比赛的球场。', '三柱门', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'wicet');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 37, 'match_status', '赛事阶段发生改变时发送', '比赛阶段', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'match status');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 37, 'match_status', '赛事阶段发生改变时发送', '比赛阶段', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'match status');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 37, 'to_win_the_toss', '赢得掷币', '赢得掷币', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'To Win The Toss');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 37, 'delete_event', '删除事件', '删除事件', '', 'Y', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Delete Event');



--板球标准赛事阶段
delete from system_item_dict where parent_type_id = 8 and addition1 = '37';

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES ('830','8','Not Started','0','1','未开赛','37','Not started yet',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'0','0','0','0');

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES ('831','8','Set1','8','1','第一局','37','1st set',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'8','1','2','0');

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES ('832','8','Set2','9','1','第二局','37','2st set',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'9','2','2','0');

-- INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
-- VALUES ('833','8','Set3','10','1','第三局','37','3st set',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'10','3','2','0');
--
-- INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
-- VALUES ('834','8','Set4','11','1','第四局','37','4st set',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'11','4','2','0');

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES ('835','8','1st set end','301','1','第一局结束','37','1st set end',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'301','1','1','0');

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES ('836','8','2st set end','302','1','第二局结束','37','2st set end',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'302','2','1','0');

-- INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
-- VALUES ('837','8','3st set end','303','1','第三局结束','37','3st set end',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'303','3','1','0');
--
-- INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
-- VALUES ('838','8','4st set end','304','1','第四局结束','37','4st set end',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'304','4','1','0');

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES ('839','8','Postponed','61','1','比赛推迟','37','The match start is delayed',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'0','0','0','0');

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES ('840','8','Interrupted','80','1','比赛中断','37','The match has been interrupted',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'0','0','0','0');

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES ('841','8','Canceled','90','1','比赛取消','37','The match has been abandoned',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'0','0','0','0');

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES ('842','8','Full Time Ended','100','1','全场结束','37','The match has ended',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'100','0','1','0');

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES ('843','8','Ended','999','1','比赛结束','37','The match has ended',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'999','-1','1','0');


--3729增加没有点球重踢标准事件
INSERT INTO match_event_type(id,sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,create_time,modify_time)
VALUES (null, 1, 'no_retake_pen','没有点球重踢 区分主客队','没有点球重踢 区分主客队',null,'Y',null,'no retake pen',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);


--80403增加黄牌结束,红牌结束事件
INSERT INTO match_event_type(id,sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,create_time,modify_time)
VALUES (null, 1, 'Yellow_Card_Ended','此次黄牌事件结束','黄牌结束',null,'Y',null,'Yellow Card Risk Ended',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
INSERT INTO match_event_type(id,sport_id,event_code,event_describe,event_name,extra_info,required_team,remark,event_en_name,create_time,modify_time)
VALUES (null, 1, 'Red_Card_Ended ','此次红牌事件结束','红牌结束',null,'Y',null,'Red Card Risk Ended',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);



--需求：3574（足球新增12个事件编码）
INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 1, 'safe_event_goal', '手动进球安全事件', '进球类安全事件', '手动点击进球，加时进球的安全事件对应触发此事件', 'Y', '常规进球和加时进球玩法集',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'manual safe goal');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 1, 'safe_event_corner', '手动角球安全事件', '角球类安全事件', '手动点击角球，加时角球的安全事件对应触发此事件', 'Y', '常规角球和加时角球玩法集',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'manual safe corner');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 1, 'safe_event_booking', '手动罚牌安全事件', '罚牌类安全事件', '手动点击罚牌，加时罚牌的安全事件对应触发此事件', 'Y', '常规罚牌和加时罚牌玩法集',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'manual safe booking');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 1, 'danger_event_goal', '手动进球危险事件', '进球类危险事件', '手动点击进球，加时进球的危险事件对应触发此事件', 'Y', '常规进球和加时进球玩法集',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'manual danger goal');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 1, 'danger_event_corner', '手动角球危险事件', '角球类危险事件', '手动点击角球，加时角球的危险事件对应触发此事件', 'Y', '常规角球和加时角球玩法集',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'manual danger corner');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 1, 'danger_event_booking', '手动罚牌危险事件', '罚牌类危险事件', '手动点击罚牌，加时罚牌的危险事件对应触发此事件', 'Y', '常规罚牌和加时罚牌玩法集',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'manual danger booking');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 1, 'Tmax_event_goal', '手动进球Tmax事件', '进球类Tmax事件', '手动点击进球，加时进球的Tmax事件对应触发此事件', 'Y', '常规进球和加时进球玩法集',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'manual Tmax goal');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 1, 'Tmax_event_corner', '手动角球Tmax事件', '角球类Tmax事件', '手动点击角球，加时角球的Tmax事件对应触发此事件', 'Y', '常规角球和加时角球玩法集',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'manual Tmax corner');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 1, 'Tmax_event_booking', '手动罚牌Tmax事件', '罚牌类Tmax事件', '手动点击罚牌，加时罚牌的Tmax事件对应触发此事件', 'Y', '常规罚牌和加时罚牌玩法集',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'manual Tmax booking');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 1, 'reject_event_goal', '手动进球拒单事件', '进球类拒单事件', '手动点击进球，加时进球的拒单事件对应触发此事件', 'Y', '常规进球和加时进球玩法集',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'manual reject goal');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 1, 'reject_event_corner', '手动角球拒单事件', '角球类拒单事件', '手动点击角球，加时角球的拒单事件对应触发此事件', 'Y', '常规角球和加时角球玩法集',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'manual reject corner');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 1, 'reject_event_booking', '手动罚牌拒单事件', '罚牌类拒单事件', '手动点击罚牌，加时罚牌的拒单事件对应触发此事件', 'Y', '常规罚牌和加时罚牌玩法集',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'manual reject booking');

delete from `third_sport_type` where `third_sport_id`='37' and `data_source_code`='TS';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (37, '37', 'TS', 37, '板球', '板球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);


--需求：3861 【操盘风控】N03数据源足球接入
delete from data_source where code = 'N03';
INSERT INTO `data_source` VALUES (40, 'N03', 'N03', 'N03', 150, 1, 1, 0, 0, 'N03数据源', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

DROP TABLE IF EXISTS `match_event_info_n03`;
CREATE TABLE `match_event_info_n03`  (
     `id` bigint(20) UNSIGNED NOT NULL COMMENT 'id',
     `sport_id` bigint(20) NULL DEFAULT 0 COMMENT '体育种类id.  对应  standard_sport_type.id',
     `canceled` tinyint(4) NULL DEFAULT 0 COMMENT '是否被取消.1 被取消; 0:没有被取消',
     `data_source_code` varchar(4)  NULL DEFAULT '' COMMENT '对应data_source.code',
     `event_code` varchar(64)  NULL DEFAULT '' COMMENT '事件编码. 对应 match_event_type.event_code',
     `event_time` bigint(20) NOT NULL DEFAULT 0 COMMENT '事件发生时间. UTC时间',
     `extra_info` varchar(256)  NULL DEFAULT '' COMMENT '扩展信息',
     `addition1` varchar(255)  NULL DEFAULT NULL,
     `home_away` varchar(6)  NULL DEFAULT '' COMMENT '主客场. 主场队:home; 客场队:away',
     `second_num` int(11) NULL DEFAULT 0 COMMENT '当前第几局',
     `first_t1` int(11) NULL DEFAULT 0 COMMENT '盘主队比分',
     `first_t2` int(11) NULL DEFAULT 0 COMMENT '盘客队比分',
     `second_t1` int(11) NULL DEFAULT 0 COMMENT '局主队比分',
     `second_t2` int(11) NULL DEFAULT 0 COMMENT '局客队比分',
     `first_num` int(11) NULL DEFAULT 0 COMMENT '当前盘数',
     `match_period_id` bigint(20) NULL DEFAULT 0 COMMENT '比赛阶段id.  system_item_dict.value',
     `player1_id` bigint(20) NULL DEFAULT 0 COMMENT '球员1的id',
     `player1_name` varchar(64)  NULL DEFAULT '' COMMENT '球员1的名称',
     `player2_id` bigint(20) NULL DEFAULT 0 COMMENT '球员2的id',
     `player2_name` varchar(64)  NULL DEFAULT '' COMMENT '球员2的名称',
     `seconds_from_start` bigint(11) NULL DEFAULT 0 COMMENT '距离比赛开始多少秒',
     `period_remaining_seconds` bigint(11) NULL DEFAULT 0 COMMENT '当前节阶段剩余时间',
     `standard_match_id` bigint(20) NULL DEFAULT 0 COMMENT '标准赛事的id. 对应 standard_match_info.id',
     `standard_team_id` bigint(20) NULL DEFAULT 0 COMMENT '标准球队 ID. 对应 standard_sport_team.id',
     `t1` int(11) NULL DEFAULT 0 COMMENT '主队数量',
     `t2` int(11) NULL DEFAULT 0 COMMENT '客队数量',
     `third_event_id` varchar(80)  NULL DEFAULT '' COMMENT '第三方数据源提供的该事件id.',
     `third_match_id` bigint(20) NULL DEFAULT 0 COMMENT '第三方赛事的id. 对应third_match_info.id',
     `third_match_source_id` varchar(50)  NULL DEFAULT '' COMMENT '比赛在数据源中的ID',
     `third_team_id` bigint(20) NULL DEFAULT 0 COMMENT '第三方球队id. 对应 third_sport_team.id',
     `source_type` tinyint(4) NULL DEFAULT 0 COMMENT '数据来源类型.0: UOF;1: Scoring Feed',
     `remark` varchar(500)  NULL DEFAULT '' COMMENT '备注',
     `create_time` bigint(20) NULL DEFAULT 0 COMMENT '创建时间. UTC时间',
     `modify_time` bigint(20) NULL DEFAULT 0 COMMENT '修改时间. UTC时间',
     `addition3` varchar(255)  NULL DEFAULT NULL COMMENT '扩展字段',
     `addition4` varchar(255)  NULL DEFAULT NULL COMMENT '扩展字段',
     `addition5` varchar(255)  NULL DEFAULT NULL COMMENT '扩展字段',
     `addition6` varchar(255)  NULL DEFAULT '' COMMENT '附加字段',
     `addition7` varchar(255)  NULL DEFAULT '' COMMENT '附加字段',
     `addition8` varchar(255)  NULL DEFAULT '' COMMENT '附加字段',
     `addition9` varchar(255)  NULL DEFAULT '' COMMENT '附加字段',
     `addition10` varchar(255)  NULL DEFAULT '' COMMENT '附加字段',
     `addition2` varchar(255)  NULL DEFAULT NULL COMMENT '扩展字段2',
     `send_data` char(1)  NULL DEFAULT '' COMMENT '下发数据标识：Y:已下发, N:未下发',
     `link_id` varchar(60)  NULL DEFAULT '' COMMENT '事件最新一次下发的linkId',
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `idx_third_event`(`third_event_id`) USING BTREE,
     INDEX `idx_third_match`(`third_match_id`) USING BTREE,
     INDEX `idx_standard_match`(`standard_match_id`) USING BTREE,
     INDEX `idx_source_match`(`third_match_source_id`) USING BTREE,
     INDEX `idx_event_code`(`event_code`) USING BTREE,
     INDEX `create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 COMMENT = 'N03赛事盘中事件表' ROW_FORMAT = Dynamic;


-- 84441 优化单，篮球新增连线中断和连接恢复事件
INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 2, 'lost_connection', '连线中断', '连线中断', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Lost Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 2, 'recovery_connection', '连线恢复', '连线恢复', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Recovery Connection');


-- 84790 单，伤停新增失效字段
ALTER TABLE third_match_sidelined ADD COLUMN `invalid` tinyint(4) NULL DEFAULT 0 COMMENT '是否失效(0:否,1:是)';

-- 83679 单，阵容新增阵型字段
ALTER TABLE third_match_lineup ADD COLUMN `home_formation` varchar(255) DEFAULT NULL COMMENT '主队阵型';
ALTER TABLE third_match_lineup ADD COLUMN `away_formation` varchar(255) DEFAULT NULL COMMENT '客队阵型';
-- 添加索引
CREATE INDEX idx_source_match_id ON third_match_info (third_match_source_id);




--需求： 3653 拳击（MMA）标准赛事阶段
delete from system_item_dict where parent_type_id = 8 and addition1 = '12';

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES (null,'8','Not Started','0','1','未开赛','12','Not started yet',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'0','0','0','0');

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES (null,'8','Postponed','61','1','比赛推迟','12','The match start is delayed',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'0','0','0','0');

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES (null,'8','Interrupted','80','1','比赛中断','12','The match has been interrupted',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'0','0','0','0');

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES (null,'8','Canceled','90','1','比赛取消','12','The match has been abandoned',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'0','0','0','0');

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES (null,'8','Full Time Ended','100','1','全场结束','12','The match has ended',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'100','0','1','0');

INSERT INTO `system_item_dict` (`id`,`parent_type_id`,`code`,`value`,`active`,`description`,`addition1`,`remark`,`create_time`,`modify_time`,`sort`,`addition2`,`addition3`,`name_code`)
VALUES (null,'8','Ended','999','1','比赛结束','12','The match has ended',unix_timestamp(now()) * 1000,unix_timestamp(now()) * 1000,'999','-1','1','0');



-- 90188 【产品】【生产】综合球种新增连线中断/连线恢复的标准事件
-- delete FROM match_event_type where id=2124; (隔离生产需要执行)

-- ALTER TABLE match_event_type ADD UNIQUE INDEX `idx_sport_event_code_unique` (`sport_id`,`event_code`);


INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 3, 'lost_connection', '连线中断', '连线中断', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Lost Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 3, 'recovery_connection', '连线恢复', '连线恢复', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Recovery Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 4, 'lost_connection', '连线中断', '连线中断', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Lost Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 4, 'recovery_connection', '连线恢复', '连线恢复', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Recovery Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 5, 'lost_connection', '连线中断', '连线中断', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Lost Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 5, 'recovery_connection', '连线恢复', '连线恢复', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Recovery Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 6, 'lost_connection', '连线中断', '连线中断', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Lost Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 6, 'recovery_connection', '连线恢复', '连线恢复', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Recovery Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 7, 'lost_connection', '连线中断', '连线中断', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Lost Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 7, 'recovery_connection', '连线恢复', '连线恢复', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Recovery Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 8, 'lost_connection', '连线中断', '连线中断', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Lost Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 8, 'recovery_connection', '连线恢复', '连线恢复', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Recovery Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 9, 'lost_connection', '连线中断', '连线中断', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Lost Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 9, 'recovery_connection', '连线恢复', '连线恢复', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Recovery Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 10, 'lost_connection', '连线中断', '连线中断', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Lost Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 10, 'recovery_connection', '连线恢复', '连线恢复', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Recovery Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 37, 'lost_connection', '连线中断', '连线中断', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Lost Connection');

INSERT INTO `match_event_type` (`id`,`sport_id`, `event_code`,`event_describe`,`event_name`,`extra_info`,`required_team`,`remark`,`create_time`,`modify_time`,`event_en_name`)
VALUES (null, 37, 'recovery_connection', '连线恢复', '连线恢复', '', 'N', '',unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000,'Recovery Connection');


-- 需求4024 【赛程管理】足篮综合开赛时间变动提示
ALTER TABLE third_sport_team_ranking ADD COLUMN `promotion_cn_name` varchar(100)  DEFAULT NULL COMMENT '晋级中文名';
ALTER TABLE third_sport_team_ranking ADD COLUMN `promotion_en_name` varchar(100)  DEFAULT NULL COMMENT '晋级中英名';
ALTER TABLE third_sport_team_ranking ADD COLUMN `promotion_id` varchar(100)  DEFAULT NULL COMMENT '晋级id';


-- 需求 4072 【客户端】【PC&H5】篮球_赛事分析改版 及足球_赛事分析迭代
ALTER TABLE third_match_lineup ADD COLUMN `play_time` varchar(255) DEFAULT NULL COMMENT '上场时间（分钟）';
ALTER TABLE third_match_lineup ADD COLUMN `assist` varchar(255) DEFAULT NULL COMMENT '助攻';
ALTER TABLE third_match_lineup ADD COLUMN `rebound` varchar(255) DEFAULT NULL COMMENT '篮板';
ALTER TABLE third_match_lineup ADD COLUMN `point` varchar(255) DEFAULT NULL COMMENT '得分';

DROP TABLE IF EXISTS `third_match_team_skill_statistics`;
CREATE TABLE third_match_team_skill_statistics (
    id varchar(100) COMMENT '数据来源ID:源赛事ID:源球队ID',
    match_id VARCHAR(50) NOT NULL COMMENT '源赛事ID',
    data_source_code varchar(20) NOT NULL COMMENT '数据来源',
    sport_id BIGINT(20) NOT NULL COMMENT '运动类型',
    team_id VARCHAR(50) NOT NULL COMMENT '源球队ID',
    home_away VARCHAR(11) NOT NULL COMMENT '主客队（1:主,2:客）',
    rebound INT(11) COMMENT '篮板总数，包括进攻篮板和防守篮板',
    offensive_rebound INT(11) COMMENT '进攻篮板数量',
    defensive_rebound INT(11) COMMENT '防守篮板数量',
    assist INT(11) COMMENT '助攻数量',
    block INT(11) COMMENT '盖帽数量',
    steal INT(11) COMMENT '抢断数量',
    turnover INT(11) COMMENT '失误数量',
    score INT(11) COMMENT '得分',
    fouls INT(11) COMMENT '犯规数量',
    modify_time bigint(20) DEFAULT NULL COMMENT '修改时间',
    create_time bigint(20) DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_match_id (match_id),
    INDEX idx_source_code (data_source_code),
    INDEX index_sport_id (sport_id),
    INDEX index_team_id (team_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT = '赛事球队技术统计表';

DROP TABLE IF EXISTS `third_match_promotion_chart`;
CREATE TABLE third_match_promotion_chart (
    id varchar(100) COMMENT '数据来源ID:源赛季ID:系列赛ID',
    tournament_id VARCHAR(50) NOT NULL COMMENT '源联赛ID',
    season_id VARCHAR(50) NOT NULL COMMENT '源赛季ID',
    data_source_code varchar(20) NOT NULL COMMENT '数据来源',
    sport_id BIGINT NOT NULL COMMENT '运动类型',
    cn_name VARCHAR(255) COMMENT '中文榜单名称',
    en_name VARCHAR(255) COMMENT '英文榜单名称',
    group_id BIGINT COMMENT '组 ID',
    series_id VARCHAR(50) COMMENT '系列赛ID',
    begin_time DATETIME COMMENT '系列赛开始时间',
    team1_id VARCHAR(50) NOT NULL COMMENT '队伍1的ID(主队)',
    team2_id VARCHAR(50) NOT NULL COMMENT '队伍2的ID(客队)',
    team1_name VARCHAR(255) COMMENT '主队名称，通常在没有队伍1ID的时候，请显示该名称，那时候该名称将表示资格名单编号',
    team2_name VARCHAR(255) COMMENT '客队名称，通常在没有队伍2ID的时候，请显示该名称，那时候该名称将表示资格名单编号',
    team1_score INT(11) COMMENT '队伍1得分',
    team2_score INT(11) COMMENT '队伍2得分',
    team1_come_from INT(11) COMMENT '队伍1从哪个系列赛来,系列赛ID（仅对双败淘汰赛）',
    team2_come_from INT(11) COMMENT '队伍2从哪个系列赛来,系列赛ID（仅对双败淘汰赛）',
    match_ids VARCHAR(255) COMMENT '该系列赛包含的比赛ID列表,多个比赛ID用逗号隔开',
    round_order INT(11) COMMENT '轮次序号,从右边数,1开始',
    line_order INT(11) COMMENT '纵向序号,从上至下,1开始',
    double_elimination_group INT(11) COMMENT '双败淘汰赛组别(1.胜者组,2.败者组,3.决赛)',
    status INT(11) COMMENT '系列赛状态(0.占位,1.未开始,2.进行中,3.完成)',
    winner INT(11) COMMENT '胜利者(1:主,2:客)',
    round_description VARCHAR(255) COMMENT '轮次文字描述',
    parent_id INT(11) COMMENT '上一级系列赛的ID',
    modify_time bigint(20) DEFAULT NULL COMMENT '修改时间',
    create_time bigint(20) DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_tournament_id (tournament_id),
    INDEX idx_season_id (season_id),
    INDEX idx_series_id (series_id),
    INDEX idx_source_code (data_source_code),
    INDEX index_sport_id (sport_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT = '赛事淘汰晋级图表';

-- 需求 3803 【比分网】比分网后台-联赛管理
alter table third_sport_tournament
    add match_type int(4) default 1 null comment '赛事类型（默认1）1：普通赛事、2：电竞赛事';


-- 优化单:94733 0910已经发上生产
INSERT INTO `third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (14, '9', 'SK', 14, '联合式橄榄球', '联合式橄榄球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);


-- 4193  【S01】【数据源】S01数据源重启-第二期 0918已经发上生产
delete from third_sport_type where data_source_code = 'SR';


-- 4185需求杯赛淘汰赛新增失效字段
ALTER TABLE third_match_promotion_chart ADD COLUMN `match_id` VARCHAR(50) NOT NULL COMMENT '源赛事ID';
ALTER TABLE third_match_promotion_chart ADD COLUMN `invalid` tinyint(4) NULL DEFAULT 0 COMMENT '是否失效(0:否,1:是)';

ALTER TABLE third_match_promotion_chart MODIFY `team1_score` varchar(20) DEFAULT NULL COMMENT '队伍1得分1(5)表示 全场:点球大战)';
ALTER TABLE third_match_promotion_chart MODIFY `team2_score` varchar(20) DEFAULT NULL COMMENT '队伍2得分1(5)表示 全场:点球大战)';

