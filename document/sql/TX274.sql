delete from third_market_category where third_source_id = 'TX:274';
INSERT INTO third_market_category (id,name_code, fields_num, third_source_id, reference_id, data_source_code, active, create_time, modify_time) VALUES
(1380845907337146378,1392039705052311559, 2, 'TX:274', 135, 'TX', 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
delete from third_sport_market_category where market_category_id in (select id from third_market_category where third_source_id = 'TX:274') and sport_id = 1;
INSERT INTO third_sport_market_category (sport_id, market_category_id, create_time, modify_time)
SELECT 1 ,id,UNIX_TIMESTAMP()*100,UNIX_TIMESTAMP()*1000 from third_market_category where third_source_id = 'TX:274';
delete from third_market_category_field where third_source_id in ('TX:274:1','TX:274:2');
INSERT INTO third_market_category_field (market_category_id, name_code, third_source_id, reference_id, order_no, data_source_code, create_time, modify_time) VALUES
(1380845907337146378, 1392121435070263344, 'TX:274:1', 442, 1, 'TX', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1380845907337146378, 1392121435070263347, 'TX:274:2', 443, 2, 'TX', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
delete from i18n_market_category where name_code in (1392121435070263344,1392121435070263347,1392039705052311559);
INSERT INTO i18n_market_category (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time) VALUES
(1392121435070263344, 2, 'TX', 'en', 'Home', NULL, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1392121435070263347, 2, 'TX', 'en', 'Away', NULL, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1392121435070263344, 2, 'TX', 'zs', 'Home', NULL, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1392039705052311559, 2, 'TX', 'en', 'To Qualify', NULL, UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(1392121435070263347, 2, 'TX', 'zs', 'Away', NULL, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);


#update third_market_category_field set market_category_id = 1380845907337146376 where third_source_id in ('TX:263:1','TX:263:2','TX:263:3');