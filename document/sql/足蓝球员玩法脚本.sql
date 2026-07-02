#足球
#select * from third_market_category where third_source_id in ('BG:12','SR:38');
update third_market_category set reference_id = 35, modify_time = UNIX_TIMESTAMP() * 1000 where third_source_id in ('BG:12','SR:38');

delete from third_sport_market_category where market_category_id = 7033 and sport_id = 1;
INSERT INTO third_sport_market_category(sport_id, market_category_id, create_time, modify_time) VALUES (1, 7033, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

#篮球
#select * from third_market_category where third_source_id in ('SR:921','SR:922','SR:923','SR:924');
update third_market_category set reference_id = 220, modify_time = UNIX_TIMESTAMP() * 1000 where third_source_id = 'SR:921';
update third_market_category set reference_id = 221, modify_time = UNIX_TIMESTAMP() * 1000 where third_source_id = 'SR:924';
update third_market_category set reference_id = 271, modify_time = UNIX_TIMESTAMP() * 1000 where third_source_id = 'SR:922';
update third_market_category set reference_id = 272, modify_time = UNIX_TIMESTAMP() * 1000 where third_source_id = 'SR:923';

delete from third_sport_market_category where sport_id = 2 and market_category_id in (6669,7281,7029,7098);
INSERT INTO third_sport_market_category(sport_id, market_category_id, create_time, modify_time) VALUE  
(2, 6669, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(2, 7281, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(2, 7029, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(2, 7098, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- -----------------------------------------------------标准-------------------------------------------------------------
#新增标准玩法35,271,272 更新220,221
#select * from standard_market_category where id in (35,220,221,271,272);
delete from standard_market_category where id in (35,271,272);
INSERT INTO standard_market_category  (id, name_code, fields_num, multi_market, support_odds, template_pc, template_h5, `status`, order_no, create_time, modify_time) VALUE
(35, CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 6, 6, 1, 54, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(271, CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '1', 12, 12, 1, 74, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(272, CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '1', 12, 12, 1, 75, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000); 
update standard_market_category set template_h5 = 12,template_pc=12,order_no=62, modify_time = unix_timestamp(now()) * 1000 where id = 220;
update standard_market_category set template_h5 = 12,template_pc=12,order_no=63, modify_time = unix_timestamp(now()) * 1000 where id = 221;

#select * from standard_sport_market_category where market_category_id in (35,220,221,271,272);
delete from standard_sport_market_category where sport_id =2 and market_category_id in (271,272);
INSERT INTO standard_sport_market_category (sport_id, market_category_id, name_code, desc_name_code, is_collapse, scope_id, `status`, order_no, create_time, modify_time ) VALUES 
(2, 271, CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '3', 1, 74, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(2, 272, CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '3', 1, 75, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

UPDATE standard_sport_market_category 
SET scope_id = '3',order_no = 54,
name_code = CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),
desc_name_code = CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),
modify_time = unix_timestamp(now()) * 1000
WHERE
	sport_id = 1 
	AND market_category_id = 35;
select * from i18n_market_category where name_code in (select name_code from standard_market_category where id in (35,271,272));

delete from i18n_market_category where name_code in (select name_code from standard_market_category where id in (35,271,272));
INSERT INTO i18n_market_category (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time) 
SELECT
	st.name_code,
	i18t.flag,
	i18t.data_source_code,
	CASE		
		WHEN i18t.language_type = 'pb' THEN
		'pt' 
		WHEN i18t.language_type = 'it' THEN
		'it_IT' 
		WHEN i18t.language_type = 'de' THEN
		'de_DE' 
		WHEN i18t.language_type = 'fr' THEN
		'fr_FR' ELSE i18t.language_type 
	END,
	TRIM(REPLACE(i18t.text,'{%player}','')),
	i18t.remark,
	unix_timestamp(now()) * 1000,	
	unix_timestamp(	now()) * 1000 
FROM
	standard_market_category st
	INNER JOIN third_market_category tt ON tt.reference_id = st.id
	INNER JOIN i18n_market_category i18t ON i18t.name_code = tt.name_code 
WHERE
	tt.data_source_code = 'SR' 
	AND tt.reference_id IN ( 35, 271, 272 );

delete from i18n_market_category where name_code in (select name_code from standard_sport_market_category where market_category_id in (35,271,272));
INSERT INTO i18n_market_category (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time) 
select 
	sport_t.name_code,
	i18t.flag,
	i18t.data_source_code,
	CASE		
		WHEN i18t.language_type = 'pb' THEN
		'pt' 
		WHEN i18t.language_type = 'it' THEN
		'it_IT' 
		WHEN i18t.language_type = 'de' THEN
		'de_DE' 
		WHEN i18t.language_type = 'fr' THEN
		'fr_FR' ELSE i18t.language_type 
	END,
	 TRIM(REPLACE(i18t.text,'{%player}','')),
	i18t.remark,
	unix_timestamp(now()) * 1000,	
	unix_timestamp(	now()) * 1000
from standard_sport_market_category sport_t
INNER JOIN standard_market_category st on st.id = sport_t.market_category_id
INNER JOIN i18n_market_category i18t ON i18t.name_code = st.name_code 
where st.id in (35,271,272);

delete from i18n_market_category where name_code in (select desc_name_code from standard_sport_market_category where market_category_id in (35,271,272));
INSERT INTO i18n_market_category (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time) 
select 
	sport_t.desc_name_code,
	i18t.flag,
	i18t.data_source_code,
	CASE		
		WHEN i18t.language_type = 'pb' THEN
		'pt' 
		WHEN i18t.language_type = 'it' THEN
		'it_IT' 
		WHEN i18t.language_type = 'de' THEN
		'de_DE' 
		WHEN i18t.language_type = 'fr' THEN
		'fr_FR' ELSE i18t.language_type 
	END,
	TRIM(REPLACE(i18t.text,'{%player}','')),
	i18t.remark,
	unix_timestamp(now()) * 1000,	
	unix_timestamp(	now()) * 1000
from standard_sport_market_category sport_t
INNER JOIN standard_market_category st on st.id = sport_t.market_category_id
INNER JOIN i18n_market_category i18t ON i18t.name_code = st.name_code 
where st.id in (35,271,272);
COMMIT;

DELETE FROM	i18n_market_category WHERE
	name_code IN ( SELECT name_code FROM standard_market_category_field WHERE id IN ( 903, 904, 905, 906, 907, 908, 909, 910 ) );

DELETE FROM standard_market_category_field WHERE id IN (903,904,905,906,907,908,909,910);

INSERT INTO standard_market_category_field VALUES (903, 220, CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);
INSERT INTO standard_market_category_field VALUES (904, 220, CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 2, UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);
INSERT INTO standard_market_category_field VALUES (905, 221, CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);
INSERT INTO standard_market_category_field VALUES (906, 221, CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 2, UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);
INSERT INTO standard_market_category_field VALUES (907, 271, CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);
INSERT INTO standard_market_category_field VALUES (908, 271, CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 2, UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);
INSERT INTO standard_market_category_field VALUES (909, 272, CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);
INSERT INTO standard_market_category_field VALUES (910, 272, CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 2, UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);
update third_market_category_field set reference_id = 903 where third_source_id = 'SR:921:12';
update third_market_category_field set reference_id = 904 where third_source_id = 'SR:921:13';
update third_market_category_field set reference_id = 907 where third_source_id = 'SR:922:12';
update third_market_category_field set reference_id = 908 where third_source_id = 'SR:922:13';
update third_market_category_field set reference_id = 909 where third_source_id = 'SR:923:12';
update third_market_category_field set reference_id = 910 where third_source_id = 'SR:923:13';
update third_market_category_field set reference_id = 905 where third_source_id = 'SR:924:12';
update third_market_category_field set reference_id = 906 where third_source_id = 'SR:924:13';

INSERT INTO i18n_market_category (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time)
select
	standard_t.name_code ,
	i18t.flag,
	i18t.data_source_code,
	CASE
		WHEN i18t.language_type = 'pb' THEN
		'pt'
		WHEN i18t.language_type = 'it' THEN
		'it_IT'
		WHEN i18t.language_type = 'de' THEN
		'de_DE'
		WHEN i18t.language_type = 'fr' THEN
		'fr_FR' ELSE i18t.language_type
	END,
	i18t.text,
	i18t.remark,
	unix_timestamp(now()) * 1000,
	unix_timestamp(	now()) * 1000
from
standard_market_category_field standard_t
INNER JOIN third_market_category_field third_t on standard_t.id  = third_t.reference_id
INNER JOIN i18n_market_category i18t ON i18t.name_code = third_t.name_code
where third_t.data_source_code = 'SR' and  standard_t.id in (903,904,905,906,907,908,909,910);

update standard_market_category set modify_time = unix_timestamp(now()) * 1000 where id in (35,220,221,271,272);

delete from third_sport_market_category where market_category_id = 6566 and sport_id = 4;
INSERT INTO third_sport_market_category(sport_id, market_category_id, create_time, modify_time) VALUES ( '4', '6566', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);

delete from third_sport_market_category where market_category_id in(select id from third_market_category where  third_source_id = 'BG:10467') and sport_id = 4;
   INSERT INTO third_sport_market_category(sport_id, market_category_id, create_time, modify_time)
         select  '4', id, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000 from third_market_category where third_source_id = 'BG:10467';

        update third_market_category  set reference_id  = 15  where third_source_id = 'BG:10467';

        update third_market_category_field set reference_id = 852 where third_source_id = 'BG:7642:4';
        update third_market_category_field set reference_id = 851 where third_source_id = 'BG:7642:5';