
drop table if exists configuration_match_data_source;
CREATE TABLE `configuration_match_data_source` (
  `id` bigint(22) NOT NULL AUTO_INCREMENT,
  `standard_match_id` bigint(20) NOT NULL COMMENT '标准赛事id',
  `market_type` tinyint(4) DEFAULT NULL COMMENT '盘口类型 1早盘  0滚球',
  `sr_weight` tinyint(4) DEFAULT NULL COMMENT 'SR权重',
  `bc_weight` tinyint(4) DEFAULT NULL COMMENT 'BC权重',
  `bg_weight` tinyint(4) DEFAULT NULL COMMENT 'BG权重',
  `score_source` tinyint(4) DEFAULT NULL COMMENT '比分源1:SR(LiveData)  2:UOF, 注意：比分源还有为null的情况，需适配',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `modify_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_standard_match_id` (`standard_match_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1336 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='赛事开盘数据源配置表';


drop table if exists configuration_match_template_event;
CREATE TABLE `configuration_match_template_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `standard_match_id` bigint(20) NOT NULL COMMENT '标准赛事id',
  `event_code` varchar(64) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '事件编码',
  `event_audit_time` int(11) DEFAULT NULL COMMENT '自动审核时间',
  `event_settlement_time` int(11) DEFAULT NULL COMMENT '事件结算时间',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `modify_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  `canceled` tinyint(4) DEFAULT '0' COMMENT '是否被取消.1 被取消; 0:没有被取消',
  PRIMARY KEY (`id`),
  KEY `idx_event_standard_match_id` (`standard_match_id`,`event_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1698 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='赛事开盘事件审核明细表';




ALTER  TABLE market_category_sell ADD COLUMN `is_sell` tinyint(4) DEFAULT '0' COMMENT '是否开售 1：是  0：否';
ALTER  TABLE market_category_sell ADD COLUMN `market_count` int(11) DEFAULT '0' COMMENT '最大盘口数';
ALTER  TABLE market_category_sell ADD COLUMN `is_series` tinyint(4) DEFAULT '0' COMMENT '支持串关，1:是 0:否';
ALTER  TABLE market_category_sell ADD COLUMN `auto_close_Market` int(11) DEFAULT '0' COMMENT '足球自动关盘时间设置：6、上半场期间 41、加时赛上半场 7、下半场期间 42、加时赛下半场     篮球自动关盘时间设置：13、第1节 14、第2节 15、第3节 16、第4节 40、加时';
ALTER  TABLE market_category_sell ADD COLUMN `match_progress_time` int(11) DEFAULT '0' COMMENT '比赛进程时间';
ALTER  TABLE market_category_sell ADD COLUMN `injury_time` int(11) DEFAULT '0' COMMENT '补时时间';
ALTER  TABLE market_category_sell ADD COLUMN `market_near_diff`  decimal(20, 2) DEFAULT '0' COMMENT '相邻盘口差值';
ALTER  TABLE market_category_sell ADD COLUMN `market_near_odds_diff`  decimal(20, 2) DEFAULT '0' COMMENT '相邻盘口赔率差值';

ALTER  TABLE match_event_info modify column third_event_id varchar(80) NOT NULL COMMENT '第三方数据源提供的该事件id.';

--赛事开盘配置增加TXodds数据源设置
ALTER  TABLE configuration_match_data_source ADD COLUMN `tx_weight` tinyint(4) DEFAULT NULL COMMENT 'TXodds权重';





-- SR赛事盘中事件表
DROP TABLE IF EXISTS `match_event_info_sr`;
CREATE TABLE `match_event_info_sr`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = 'SR赛事盘中事件表' ROW_FORMAT = Dynamic;

-- 足球进球事件表
DROP TABLE IF EXISTS `match_event_info_scores`;
CREATE TABLE `match_event_info_scores`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '进球事件表' ROW_FORMAT = Dynamic;



-- 拷贝原始标准赛事多语言列表到分表后里面去
DELETE FROM language_internation_0 WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=0);
INSERT INTO language_internation_0(name_code,flag,data_source_code,language_type,text,remark,create_time,modify_time)
SELECT name_code,flag,data_source_code,language_type,text,remark,create_time,unix_timestamp(now()) * 1000 FROM `language_internation_his` WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=0);

DELETE FROM language_internation_1 WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=1);
INSERT INTO language_internation_1(name_code,flag,data_source_code,language_type,text,remark,create_time,modify_time)
SELECT name_code,flag,data_source_code,language_type,text,remark,create_time,unix_timestamp(now()) * 1000 FROM `language_internation_his` WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=1);

DELETE FROM language_internation_2 WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=2);
INSERT INTO language_internation_2(name_code,flag,data_source_code,language_type,text,remark,create_time,modify_time)
SELECT name_code,flag,data_source_code,language_type,text,remark,create_time,unix_timestamp(now()) * 1000 FROM `language_internation_his` WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=2);

DELETE FROM language_internation_3 WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=3);
INSERT INTO language_internation_3(name_code,flag,data_source_code,language_type,text,remark,create_time,modify_time)
SELECT name_code,flag,data_source_code,language_type,text,remark,create_time,unix_timestamp(now()) * 1000 FROM `language_internation_his` WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=3);

DELETE FROM language_internation_4 WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=4);
INSERT INTO language_internation_4(name_code,flag,data_source_code,language_type,text,remark,create_time,modify_time)
SELECT name_code,flag,data_source_code,language_type,text,remark,create_time,unix_timestamp(now()) * 1000 FROM `language_internation_his` WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=4);

DELETE FROM language_internation_5 WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=5);
INSERT INTO language_internation_5(name_code,flag,data_source_code,language_type,text,remark,create_time,modify_time)
SELECT name_code,flag,data_source_code,language_type,text,remark,create_time,unix_timestamp(now()) * 1000 FROM `language_internation_his` WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=5);

DELETE FROM language_internation_6 WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=6);
INSERT INTO language_internation_6(name_code,flag,data_source_code,language_type,text,remark,create_time,modify_time)
SELECT name_code,flag,data_source_code,language_type,text,remark,create_time,unix_timestamp(now()) * 1000 FROM `language_internation_his` WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=6);

DELETE FROM language_internation_7 WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=7);
INSERT INTO language_internation_7(name_code,flag,data_source_code,language_type,text,remark,create_time,modify_time)
SELECT name_code,flag,data_source_code,language_type,text,remark,create_time,unix_timestamp(now()) * 1000 FROM `language_internation_his` WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=7);

DELETE FROM language_internation_8 WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=8);
INSERT INTO language_internation_8(name_code,flag,data_source_code,language_type,text,remark,create_time,modify_time)
SELECT name_code,flag,data_source_code,language_type,text,remark,create_time,unix_timestamp(now()) * 1000 FROM `language_internation_his` WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=8);

DELETE FROM language_internation_9 WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=9);
INSERT INTO language_internation_9(name_code,flag,data_source_code,language_type,text,remark,create_time,modify_time)
SELECT name_code,flag,data_source_code,language_type,text,remark,create_time,unix_timestamp(now()) * 1000 FROM `language_internation_his` WHERE name_code IN (SELECT name_code FROM `standard_sport_type` WHERE name_code%10=9);






-- 2023-02-24 BUG37906脚本
UPDATE `match_event_type`
SET event_en_name='1st half overtime starts'
,event_name='加时赛上半场开始'
,modify_time=unix_timestamp(now()) * 1000
WHERE event_code='start_ot1' AND sport_id=1;

UPDATE `match_event_type`
SET event_en_name='2nd half overtime ended'
,event_name='加时赛下半场结束'
,modify_time=unix_timestamp(now()) * 1000
WHERE event_code='stop_ot2' AND sport_id=1;

UPDATE `match_event_type`
SET event_en_name='Possible throw-in'
,event_name='可能掷界外球'
,modify_time=unix_timestamp(now()) * 1000
WHERE event_code='posible_throw_in' AND sport_id=1;

UPDATE `match_event_type`
SET event_en_name='Bookings count status'
,event_name='红黄牌数据'
,modify_time=unix_timestamp(now()) * 1000
WHERE event_code='fa_card' AND sport_id=1;

UPDATE `match_event_type`
SET event_en_name='Possible VAR'
,event_name='可能需要视频辅助裁判'
,modify_time=unix_timestamp(now()) * 1000
WHERE event_code='possible_var' AND sport_id=1;

UPDATE `match_event_type`
SET event_en_name='Substitution update'
,event_name='换人更新'
,modify_time=unix_timestamp(now()) * 1000
WHERE event_code='sub_update' AND sport_id=1;

UPDATE `match_event_type`
SET event_en_name='Second yellow red card'
,event_name='第二次黄牌'
,modify_time=unix_timestamp(now()) * 1000
WHERE event_code='yellow_red_card' AND sport_id=1;

UPDATE `match_event_type`
SET event_en_name='Second yellow red card confirmed'
,event_name='红黄牌确认'
,modify_time=unix_timestamp(now()) * 1000
WHERE event_code='yellow_red_card_confirm' AND sport_id=1;






-- 1X赛事盘中事件表
DROP TABLE IF EXISTS `match_event_info_1x`;
CREATE TABLE `match_event_info_1x`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '1X赛事盘中事件表' ROW_FORMAT = Dynamic;

-- 2022-03-14新增BE数据源
INSERT INTO `data_source` VALUES (21, 'BE', 'Beter', 'BE', 180, 1, 1, 0, 0, 'Beter数据商', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

delete from third_sport_type where name_code = 1 and third_sport_id = '1' and data_source_code = 'BE';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (1, '1', 'BE',1, '足球', '足球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

delete from third_sport_type where name_code = 2 and third_sport_id = '2' and data_source_code = 'BE';
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`)
VALUES (2, '2', 'BE',2, '篮球', '篮球', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);


-- 修改视频表赛事ID长度
ALTER TABLE `third_video_board_cast_record` CHANGE `id` `id` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '主键ID（数据源ID+数据源赛事ID）';
ALTER TABLE `third_video_board_cast_record` CHANGE `match_id` `match_id` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '数据源赛事ID';


-- BE赛事盘中事件表
DROP TABLE IF EXISTS `match_event_info_be`;
CREATE TABLE `match_event_info_be`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = 'BE赛事盘中事件表' ROW_FORMAT = Dynamic;

--3333数据源接入
alter table `third_sport_team_ranking` add column `data_source_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci default 'TS' NOT NULL COMMENT '数据来源';
alter table `third_match_history_statistics` add column `home_team_score_d01` varchar(20) DEFAULT NULL COMMENT '常规赛事主队得分';
alter table `third_match_history_statistics` add column `away_team_score_d01` varchar(20) DEFAULT NULL COMMENT '常规赛事客队得分';
alter table `third_match_history_statistics` add column `edit_status` int(2) DEFAULT '0' COMMENT '0:自动 1:手动';
alter table `third_match_lineup` add column `edit_status` int(2) DEFAULT '0' COMMENT '0:自动 1:手动';
alter table `third_match_info` add column `home_expectation_xg` decimal(5,2) DEFAULT NULL COMMENT '主队预期进球xG';
alter table `third_match_info` add column `home_expectation_loss` decimal(5,2) DEFAULT NULL COMMENT '主队预期失球';
alter table `third_match_info` add column `away_expectation_xg` decimal(5,2) DEFAULT NULL COMMENT '客队预期进球xG';
alter table `third_match_info` add column `away_expectation_loss` decimal(5,2) DEFAULT NULL COMMENT '客队预期失球';
alter table `third_sport_team_ranking` add column `invalid` int(2) DEFAULT '0' COMMENT '是否失效(0:否,1:是)';
alter table `third_sport_team_ranking` add column `total_matches` bigint(20) DEFAULT null COMMENT '赛事总数';
alter table `third_sport_team_ranking` add column `matches_completed` bigint(20) DEFAULT null COMMENT '已完成赛事数';

DROP TABLE IF EXISTS `third_match_history_expression`;
create table `third_match_history_expression`(
         `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源联赛ID+三方数据源球队ID+数据来源+数据类型+运动类型',
         `third_tournament_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源联赛ID',
         `third_team_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源球队ID',
         `data_source_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'D01' COMMENT '数据来源',
         `team_cn_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '球队中文名称',
         `team_en_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '球队英文名称',
         `edit_status` int(2) NOT NULL COMMENT '0:自动 1:手动',
         `sport_id` bigint(20) NOT NULL COMMENT '运动类型',
         `expression_ranking` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '联赛表现排名:如10/20',
         `expressing_type` int(2) NOT NULL COMMENT '数据类型,0:总体 1:主队 2:客队',
         `first_status` int(2) NOT NULL COMMENT '最近第1场赛事状态,0:赢 1:平 2:输',
         `second_status` int(2) NOT NULL COMMENT '最近第2场赛事状态,0:赢 1:平 2:输',
         `third_status` int(2) NOT NULL COMMENT '最近第3场赛事状态,0:赢 1:平 2:输',
         `fourth_status` int(2) NOT NULL COMMENT '最近第4场赛事状态,0:赢 1:平 2:输',
         `fifth_status` int(2) NOT NULL COMMENT '最近第5场赛事状态,0:赢 1:平 2:输',
         `goals_for_total` int(11) NOT NULL COMMENT '最近5场进球数',
         `average_goal` decimal(5,2) NOT NULL COMMENT '最近5场均进球数',
         `win_percent` decimal(5,2) NOT NULL COMMENT '最近5场赢球占比',
         `both_goal_percent` decimal(5,2) NOT NULL COMMENT '两队都得分占比',
         `not_lost_percent` decimal(5,2) NOT NULL COMMENT '没有失球占比',
         `first_goal_percent` decimal(5,2) NOT NULL COMMENT '第一队入球占比',
         `average_goal_percent` decimal(5,2) NOT NULL COMMENT '平均进球占比',
         `goal_percent` decimal(5,2) NOT NULL COMMENT '得分占比',
         `lost_goal_percent` decimal(5,2) NOT NULL COMMENT '失球占比',
         `goal_xg` decimal(5,2) NOT NULL COMMENT 'xG',
         `goal_xga` decimal(5,2) NOT NULL COMMENT 'xGA',
         `modify_time` bigint(20) NOT NULL COMMENT '修改时间',
         `create_time` bigint(20) NOT NULL COMMENT '创建时间',
         PRIMARY KEY (`id`) USING BTREE,
         KEY `idx_uq_tour_team` (`third_tournament_source_id`, `third_team_source_id`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_as_cs ROW_FORMAT = DYNAMIC COMMENT = '联赛球队历史表现';

DROP TABLE IF EXISTS `third_match_season_statistics`;
create table `third_match_season_statistics`(
        `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源赛季ID+数据来源+运动类型+数据类型',
        `data_source_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'D01' COMMENT '数据来源',
        `third_source_season_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源赛季ID',
        `third_source_season_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '三方数据源赛季名称',
        `third_tournament_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源联赛ID',
        `tournament_type` int(2) DEFAULT '0' COMMENT '数据类型(0:其他,1联赛,2杯赛,3汇总)',
        `edit_status` int(2) NOT NULL COMMENT '0:自动 1:手动',
        `sport_id` bigint(20) NOT NULL COMMENT '运动类型',
        `percent_than_one` decimal(5,2) NOT NULL COMMENT '高于1.5占比',
        `percent_than_two` decimal(5,2) NOT NULL COMMENT '高于2.5占比',
        `percent_than_three` decimal(5,2) NOT NULL COMMENT '两队都得分',
        `average_goal` decimal(5,2) NOT NULL COMMENT '均场入球',
        `average_card` decimal(5,2) NOT NULL COMMENT '罚牌',
        `average_corner` decimal(5,2) NOT NULL COMMENT '角球',
        `modify_time` bigint(20) NOT NULL COMMENT '修改时间',
        `create_time` bigint(20) NOT NULL COMMENT '创建时间',
        PRIMARY KEY (`id`) USING BTREE,
        KEY `idx_uq_sea` (`third_source_season_id`,`data_source_code`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_as_cs ROW_FORMAT = DYNAMIC COMMENT = '当前赛季统计信息';

DROP TABLE IF EXISTS `third_match_front_statistics`;
create table `third_match_front_statistics`(
       `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '数据源赛事id+数据来源+运动类型',
       `data_source_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'D01' COMMENT '数据来源',
       `third_match_source_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源赛事id',
       `home_team_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源主队ID',
       `away_team_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源客队ID',
       `home_team_name` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '主队名称',
       `away_team_name` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '客队名称',
       `sport_id` bigint(20) NOT NULL COMMENT '运动类型',
       `edit_status` int(2) NOT NULL COMMENT '0:自动 1:手动',
       `count_total` int(11) NOT NULL COMMENT '总场数',
       `home_win` int(11) NOT NULL COMMENT '主队赢场数',
       `away_win` int(11) NOT NULL COMMENT '客队赢场数',
       `dogfall_total` int(11) NOT NULL COMMENT '和局场数',
       `more_than_one` int(11) NOT NULL COMMENT '高于1.5场数',
       `more_than_two` int(11) NOT NULL COMMENT '高于2.5场数',
       `more_than_three` int(11) NOT NULL COMMENT '高于3.5场数',
       `all_scores` int(11) NOT NULL COMMENT '两队都得分场数',
       `home_not_lost` int(11) NOT NULL COMMENT '主队没有失球场数',
       `away_not_lost` int(11) NOT NULL COMMENT '客队没有失球场数',
       `modify_time` bigint(20) NOT NULL COMMENT '修改时间',
       `create_time` bigint(20) NOT NULL COMMENT '创建时间',
       PRIMARY KEY (`id`) USING BTREE,
       KEY `idx_uq_thirdsour` (`third_match_source_id`,`data_source_code`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_as_cs ROW_FORMAT = DYNAMIC COMMENT = '正面交手统计信息';













