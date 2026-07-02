#隔离
#update third_market_category_field set market_category_id = 1380845907337146376 where third_source_id in ('TX:263:1','TX:263:2','TX:263:3');
#生产
#update third_market_category_field set market_category_id = 1380845907337146377 where third_source_id in ('TX:263:1','TX:263:2','TX:263:3');

DELETE FROM third_sport_market_category where sport_id =4 and market_category_id = 6566;
INSERT INTO third_sport_market_category (sport_id, market_category_id, create_time, modify_time) VALUES
(4, 6566, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);