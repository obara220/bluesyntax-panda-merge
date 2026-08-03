
-- 事件分表语句 目前支持['pd','sr','bc','bg','rb']数据源

DROP TABLE IF EXISTS `match_event_info_pd`;
CREATE TABLE `match_event_info_pd`  (
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = 'PA赛事盘中事件表' ROW_FORMAT = Dynamic;

-- 拷贝原始表中最近一天PD事件到新表中
INSERT INTO match_event_info_pd
SELECT * FROM match_event_info
WHERE  data_source_code='PD' AND create_time >= unix_timestamp(now()) * 1000 - 60 * 60 * 24 * 1000;


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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = 'SR赛事盘中事件表' ROW_FORMAT = Dynamic;

-- 拷贝原始表中最近一天SR事件到新表中
INSERT INTO match_event_info_sr
SELECT * FROM match_event_info
WHERE  data_source_code='SR' AND create_time >= unix_timestamp(now()) * 1000 - 60 * 60 * 24 * 1000;


DROP TABLE IF EXISTS `match_event_info_bc`;
CREATE TABLE `match_event_info_bc`  (
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = 'BC赛事盘中事件表' ROW_FORMAT = Dynamic;

-- 拷贝原始表中最近一天BC事件到新表中
INSERT INTO match_event_info_bc
SELECT * FROM match_event_info
WHERE  data_source_code='BC' AND create_time >= unix_timestamp(now()) * 1000 - 60 * 60 * 24 * 1000;


DROP TABLE IF EXISTS `match_event_info_bg`;
CREATE TABLE `match_event_info_bg`  (
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = 'BG赛事盘中事件表' ROW_FORMAT = Dynamic;

-- 拷贝原始表中最近一天BG事件到新表中
INSERT INTO match_event_info_bg
SELECT * FROM match_event_info
WHERE  data_source_code='BG' AND create_time >= unix_timestamp(now()) * 1000 - 60 * 60 * 24 * 1000;


DROP TABLE IF EXISTS `match_event_info_rb`;
CREATE TABLE `match_event_info_rb`  (
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = 'RB赛事盘中事件表' ROW_FORMAT = Dynamic;

-- 拷贝原始表中最近一天RB事件到新表中
INSERT INTO match_event_info_rb
SELECT * FROM match_event_info
WHERE  data_source_code='RB' AND create_time >= unix_timestamp(now()) * 1000 - 60 * 60 * 24 * 1000;






-- 2023-03-03 新增BT事件表
DROP TABLE IF EXISTS `match_event_info_bt`;
CREATE TABLE `match_event_info_bt`  (
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = 'BT赛事盘中事件表' ROW_FORMAT = Dynamic;















