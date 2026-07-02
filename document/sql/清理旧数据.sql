DELETE FROM third_sport_market_odds_bc WHERE  `third_match_id` = 0;
DELETE FROM third_sport_market_odds_bg WHERE  `third_match_id` = 0;
DELETE FROM third_sport_market_odds_pa WHERE  `third_match_id` = 0;
DELETE FROM third_sport_market_odds_sr WHERE  `third_match_id` = 0;
DELETE FROM third_sport_market_odds_tx WHERE  `third_match_id` = 0;

DELETE FROM standard_sport_market_odds_0 WHERE `standard_match_id` = 0;
DELETE FROM standard_sport_market_odds_1 WHERE `standard_match_id` = 0;
DELETE FROM standard_sport_market_odds_2 WHERE `standard_match_id` = 0;
DELETE FROM standard_sport_market_odds_3 WHERE `standard_match_id` = 0;
DELETE FROM standard_sport_market_odds_4 WHERE `standard_match_id` = 0;
DELETE FROM standard_sport_market_odds_5 WHERE `standard_match_id` = 0;
DELETE FROM standard_sport_market_odds_6 WHERE `standard_match_id` = 0;
DELETE FROM standard_sport_market_odds_7 WHERE `standard_match_id` = 0;
DELETE FROM standard_sport_market_odds_8 WHERE `standard_match_id` = 0;
DELETE FROM standard_sport_market_odds_9 WHERE `standard_match_id` = 0;


-- 日志索引
ALTER TABLE config_trade_market_log ADD INDEX `index_target_id` ( `TARGET_ID` );
ALTER TABLE standard_sport_market_sell_log ADD INDEX `index_standard_match_id` ( `standard_match_id` );
ALTER TABLE config_market_display_trade_log ADD INDEX `index_standard_match_id` ( `standard_match_id` );
ALTER TABLE config_market_trade_item_log ADD INDEX `index_match_id` ( `match_id` );




