
-- 隔离2分半;生产需要5分钟

ALTER TABLE `match_time_info` ADD COLUMN `first_num` int(8) DEFAULT NULL COMMENT '盘切换展示:1,2,3,4,5' AFTER `event_time`;
ALTER TABLE `match_time_info` ADD COLUMN `period_length_json` varchar(255)  DEFAULT NULL COMMENT '每盘的长度: {"1":15,"2":13,"3":13}' AFTER `event_time`;
ALTER TABLE `match_time_info` ADD COLUMN `match_length_json` varchar(255)  DEFAULT NULL COMMENT '每盘的局制: {"1":2,"2":2,"3":2} 1长盘制, 2抢七制,3单人抢十,4双人抢十,5特' AFTER `event_time`;
ALTER TABLE `match_time_info` ADD COLUMN `round_type` int(8) DEFAULT NULL COMMENT '3: 3盘 5:5盘' AFTER `event_time`;
ALTER TABLE `match_time_info` ADD COLUMN `current_set` int(8) DEFAULT '0' COMMENT '当前盘数' AFTER `event_time`;
ALTER TABLE `match_time_info` ADD COLUMN `current_round` varchar(8) DEFAULT '0' COMMENT '当前局数' AFTER `event_time`;


DROP TABLE IF EXISTS `match_gray_interval`;
CREATE TABLE `match_gray_interval` (
     `id` int(11) NOT NULL AUTO_INCREMENT,
     `data_source_code` varchar(25) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '数据商编码',
     `tournament_level` int(11) NOT NULL,
     `min15_goal` int(11) DEFAULT NULL,
     `min15_corner` int(11) DEFAULT NULL,
     `min15_bookings` int(11) DEFAULT NULL,
     `min5_goal` int(11) DEFAULT NULL,
     `create_time` bigint(20) DEFAULT NULL,
     `modify_time` bigint(20) DEFAULT NULL,
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

