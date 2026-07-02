CREATE TABLE `sport_market_relation`
(
    `id`                  bigint(20) NOT NULL,
    `market_relation_key` varchar(100) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
    `relation_market_id`  bigint(20)                              DEFAULT NULL,
    `create_time`         bigint(20)                              DEFAULT '0',
    `modify_time`         bigint(20)                              DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `market_relation_key` (`market_relation_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_as_cs COMMENT ='记录表relationId';