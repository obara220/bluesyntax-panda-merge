CREATE TABLE `config_tournament_trade_item` (
`id` bigint(20) NOT NULL,
`sport_id` bigint(20) DEFAULT NULL COMMENT '运动ID',
`tournament_id` bigint(20) DEFAULT NULL COMMENT '联赛ID',
`match_type` int(11) DEFAULT NULL COMMENT '1：早盘；0：滚球',
`spread_max_odds` decimal(22,2) DEFAULT NULL COMMENT '马来 最大赔率',
`spread_min_odds` decimal(22,2) DEFAULT NULL COMMENT '马来 最小赔率',
`margin_max_odds` decimal(22,2) DEFAULT NULL COMMENT '欧赔 最大赔率',
`margin_min_odds` decimal(22,2) DEFAULT NULL COMMENT '欧赔 最小赔率',
`create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
`modify_time` bigint(20) DEFAULT NULL COMMENT '修改时间',
`operater_id` bigint(20) DEFAULT '0' COMMENT '操作人ID',
PRIMARY KEY (`id`),
UNIQUE KEY `idx_sportId_tournamentId_matchType_unique` (`sport_id`,`tournament_id`,`match_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='联赛级别最大最小赔率配置';

