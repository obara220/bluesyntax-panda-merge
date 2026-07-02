CREATE TABLE `config_cash_out_trade_item` (
  `id` bigint(20) NOT NULL,
  `match_id` bigint(20) DEFAULT NULL COMMENT '赛事ID',
  `market_type` int(1) DEFAULT NULL COMMENT '1:赛前盘;0:滚球盘',
  `market_category_id` bigint(20) DEFAULT NULL COMMENT '玩法ID',
  `match_pre_status` int(1) DEFAULT NULL COMMENT '赛事级别提前结算开关,0:关 1:开',
  `category_pre_status` int(1) DEFAULT NULL COMMENT '玩法级别提前结算开关,0:关 1:开',
  `cash_out_margin` bigint(5) DEFAULT NULL COMMENT 'cashOutMargin',
  `leve` int(1) DEFAULT NULL COMMENT '等级：1：赛事、2：玩法 ',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `modify_time` bigint(20) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_matchId_marketType_marketCategoryId` (`match_id`,`market_type`,`market_category_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='提前结算配置表';








	
ALTER TABLE  `market_category_sell`
ADD COLUMN  `min_ball_head` decimal(20,2) DEFAULT NULL COMMENT '最小球头',
ADD COLUMN  `max_ball_head` decimal(20,2) DEFAULT NULL COMMENT '最大球头';


	 
 
	