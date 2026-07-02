
ALTER TABLE `panda`.`standard_sport_market` DROP INDEX `idx_datasource_marketsourceId_unique`, DROP INDEX `idx_relation_market_id_datasource_unique`, ADD UNIQUE `idx_datasource_marketsourceId_unique` ( `standard_match_info_id`, `third_market_source_id`, `data_source_code` ) USING BTREE;
ALTER TABLE `panda`.`standard_sport_market` ADD INDEX index_relation_market_id_data_source_code ( `relation_market_id`, `data_source_code` );



