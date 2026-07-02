/* name_code生成函数：CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), */


/*
INSERT INTO i18n_market_category (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time)
SELECT standard_t.name_code,	i18n_t.flag,	i18n_t.data_source_code,	i18n_t.language_type,i18n_t.text,i18n_t.remark,unix_timestamp(now()) * 1000 ,unix_timestamp(now()) * 1000
FROM
  i18n_market_category i18n_t,
	standard_market_category standard_t,
	third_market_category third_t
WHERE
	third_t.reference_id = standard_t.id	and third_t.data_source_code = 'SR'	and i18n_t.name_code = third_t.name_code 		AND standard_t.id > 241;

SELECT  i18n_t.name_code,	i18n_t.flag,	i18n_t.data_source_code,	i18n_t.language_type,i18n_t.text,i18n_t.remark,unix_timestamp(now()) * 1000 ,unix_timestamp(now()) * 1000
from i18n_market_category i18n_t
INNER JOIN  standard_market_category category_t on i18n_t.name_code = category_t.name_code
where category_t.id > 241;	 	*/

/*
select category_t.third_source_id,category_t.reference_id,field_t.*
from third_market_category category_t, third_market_category_field field_t
where category_t.id = field_t.market_category_id
and category_t.third_source_id in ('SR:139','SR:152','SR:136','SR:149','SR:140','SR:141','SR:153','SR:154','SR:142','SR:155','SR:143','SR:144','SR:156','SR:157')
order by category_t.reference_id, field_t.order_no
 */

 /*
delete from i18n_market_category where name_code in (select name_code from standard_market_category_field where market_category_id in (307,309,310,311,314,315,316,317,318,319,320,321,322,323));

INSERT INTO i18n_market_category (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time)
select
	field_t.name_code,
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
from standard_market_category_field field_t
INNER JOIN third_market_category_field tt on tt.reference_id = field_t.id
INNER JOIN i18n_market_category i18t ON i18t.name_code = tt.name_code
where tt.reference_id in (SELECT id FROM standard_market_category_field WHERE market_category_id in (307,309,310,311,314,315,316,317,318,319,320,321,322,323)) and  tt.data_source_code = 'SR';


delete from i18n_market_category where name_code in (select name_code from standard_market_category_field where id in (964,965,966,967,968,969,970,971,972,973,974,975,976,977,978,979,980,981,1037,1038,1043,1044,1045,1046,1047) );

INSERT INTO i18n_market_category (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time)
select
	field_t.name_code,
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
from standard_market_category_field field_t
INNER JOIN third_market_category_field tt on tt.reference_id = field_t.id
INNER JOIN i18n_market_category i18t ON i18t.name_code = tt.name_code
where tt.reference_id in (964,965,966,967,968,969,970,971,972,973,974,975,976,977,978,979,980,981,1037,1038,1043,1044,1045,1046,1047)
and  tt.data_source_code = 'TX';

 */

update third_market_category set reference_id = 307 where third_source_id = 'SR:139';
update third_market_category set reference_id = 309 where third_source_id = 'SR:152';
update third_market_category set reference_id = 310 where third_source_id = 'SR:136';
update third_market_category set reference_id = 311 where third_source_id = 'SR:149';
update third_market_category set reference_id = 314 where third_source_id = 'SR:140';
update third_market_category set reference_id = 315 where third_source_id = 'SR:141';
update third_market_category set reference_id = 316 where third_source_id = 'SR:153';
update third_market_category set reference_id = 317 where third_source_id = 'SR:154';
update third_market_category set reference_id = 318 where third_source_id = 'SR:142';
update third_market_category set reference_id = 319 where third_source_id = 'SR:155';
update third_market_category set reference_id = 320 where third_source_id = 'SR:143';
update third_market_category set reference_id = 321 where third_source_id = 'SR:144';
update third_market_category set reference_id = 322 where third_source_id = 'SR:156';
update third_market_category set reference_id = 323 where third_source_id = 'SR:157';

update third_market_category set reference_id = 306 where third_source_id = 'TX:107';
update third_market_category set reference_id = 307 where third_source_id = 'TX:108';
update third_market_category set reference_id = 308 where third_source_id = 'TX:109';
update third_market_category set reference_id = 309 where third_source_id = 'TX:110';
update third_market_category set reference_id = 310 where third_source_id = 'TX:111';
update third_market_category set reference_id = 311 where third_source_id = 'TX:112';
update third_market_category set reference_id = 312 where third_source_id = 'TX:466';
update third_market_category set reference_id = 313 where third_source_id = 'TX:479';
update third_market_category set reference_id = 324 where third_source_id = 'TX:471';
update third_market_category set reference_id = 325 where third_source_id = 'TX:472';
update third_market_category set reference_id = 326 where third_source_id = 'TX:473';
update third_market_category set reference_id = 327 where third_source_id = 'TX:474';
update third_market_category set reference_id = 328 where third_source_id = 'TX:475';
update third_market_category set reference_id = 329 where third_source_id = 'TX:476';
update third_market_category set reference_id = 330 where third_source_id = 'TX:411';
update third_market_category set reference_id = 331 where third_source_id = 'TX:412';
update third_market_category set reference_id = 332 where third_source_id = 'TX:256';
update third_market_category set reference_id = 333 where third_source_id = 'TX:263';
update third_market_category set reference_id = 334 where third_source_id = 'TX:264';
update third_market_category set reference_id = 335 where third_source_id = 'TX:265';
update third_market_category set reference_id = 336 where third_source_id = 'TX:283';
update third_market_category set reference_id = 135 where third_source_id = 'TX:274';
update third_market_category set reference_id = 126 where third_source_id = 'TX:257';




#标准玩法
DELETE FROM	standard_market_category WHERE id IN (306,307,308,309,310,311,312,313,314,315,316,317,318,319,320,321,322,323,324,325,326,327,328,329,330,331,332,333,334,335,336);
INSERT INTO standard_market_category(id, name_code, fields_num, multi_market, support_odds, template_pc, template_h5, status, order_no, create_time, modify_time) VALUES
(306,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,306,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(307,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,307,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(308,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,308,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(309,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,309,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(310,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),3,0,'2',1,1,1,310,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(311,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),3,0,'2',1,1,1,311,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(312,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,0,'1',3,3,1,312,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(313,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,0,'1',3,3,1,313,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(314,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,314,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(315,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,315,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(316,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,316,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(317,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,317,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(318,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),10,0,'2',0,0,1,318,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(319,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),7,0,'2',0,0,1,319,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(320,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),4,0,'2',0,0,1,320,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(321,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),4,0,'2',0,0,1,321,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(322,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),4,0,'2',0,0,1,322,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(323,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),4,0,'2',0,0,1,323,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(324,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,324,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(325,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,325,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(326,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),3,0,'2',1,1,1,326,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(327,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,327,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(328,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,328,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(329,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),3,0,'2',1,1,1,329,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(330,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,0,'1',3,3,1,330,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(331,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,331,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(332,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,332,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(333,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),3,0,'2',1,1,1,333,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(334,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,334,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(335,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,1,'1',2,13,1,335,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(336,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),2,0,'2',3,3,1,336,UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

delete from i18n_market_category where name_code in (select name_code from standard_market_category
where id in (307,309,310,311,314,315,316,317,318,319,320,321,322,323));
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
	i18t.text,
	i18t.remark,
	unix_timestamp(now()) * 1000,
	unix_timestamp(	now()) * 1000
FROM
	standard_market_category st
	INNER JOIN third_market_category tt ON tt.reference_id = st.id
	INNER JOIN i18n_market_category i18t ON i18t.name_code = tt.name_code
WHERE
	tt.data_source_code = 'SR'
	AND tt.reference_id IN (307,309,310,311,314,315,316,317,318,319,320,321,322,323);

delete from i18n_market_category where name_code in (select name_code from standard_market_category
where id in (306,308,312,313,330,333,334));/*,324,325,326,327,328,329,331,332,335,336*/
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
	i18t.text,
	i18t.remark,
	unix_timestamp(now()) * 1000,
	unix_timestamp(	now()) * 1000
FROM
	standard_market_category st
	INNER JOIN third_market_category tt ON tt.reference_id = st.id
	INNER JOIN i18n_market_category i18t ON i18t.name_code = tt.name_code
WHERE
	tt.data_source_code = 'TX'
	AND tt.reference_id IN (306,308,312,313,330,333,334);/*,324,325,326,327,328,329,331,332,335,336*/

#赛种
delete from standard_sport_market_category
where sport_id = 1 and market_category_id in (306,307,308,309,310,311,312,313,314,315,316,317,318,319,320,321,322,323,324,325,326,327,328,329,330,331,332,333,334,335,336);
INSERT INTO standard_sport_market_category (sport_id, market_category_id, name_code, desc_name_code, is_collapse, scope_id, `status`, order_no, create_time, modify_time ) VALUES
(1,306,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,307,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,308,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'1',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,309,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'1',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,310,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,311,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'1',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,312,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,313,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'1',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,314,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,315,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,316,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'1',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,317,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'1',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,318,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,319,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'1',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,320,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,321,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,322,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'1',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,323,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'1',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
/*
(1,324,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,325,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,326,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,327,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'1',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,328,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'1',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,329,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'1',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
*/
(1,330,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
 /*
(1,331,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,332,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
*/
(1,333,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,334,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
/*(1,335,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1,336,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)),1,'3',1,74,unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000)
*/
delete from i18n_market_category where name_code in (select name_code from standard_sport_market_category
where market_category_id in (306,307,308,309,310,311,312,313,314,315,316,317,318,319,320,321,322,323,324,325,326,327,328,329,330,331,332,333,334,335,336));

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
	i18t.text,
	i18t.remark,
	unix_timestamp(now()) * 1000,
	unix_timestamp(	now()) * 1000
from standard_sport_market_category sport_t
INNER JOIN standard_market_category st on st.id = sport_t.market_category_id
INNER JOIN i18n_market_category i18t ON i18t.name_code = st.name_code
where st.id in (306,307,308,309,310,311,312,313,314,315,316,317,318,319,320,321,322,323,330,333,334);/*,324,325,326,327,328,329,331,332,335,336*/

delete from i18n_market_category where name_code in (select desc_name_code from standard_sport_market_category
where market_category_id in (306,307,308,309,310,311,312,313,314,315,316,317,318,319,320,321,322,323,324,325,326,327,328,329,330,331,332,333,334,335,336));

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
	i18t.text,
	i18t.remark,
	unix_timestamp(now()) * 1000,
	unix_timestamp(	now()) * 1000
from standard_sport_market_category sport_t
INNER JOIN standard_market_category st on st.id = sport_t.market_category_id
INNER JOIN i18n_market_category i18t ON i18t.name_code = st.name_code
where st.id in (306,307,308,309,310,311,312,313,314,315,316,317,318,319,320,321,322,323,330,333,334);/*,324,325,326,327,328,329,331,332,335,336*/

#标准投注项
DELETE FROM standard_market_category_field
WHERE id in (964,965,966,967,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,
991,992,993,994,995,996,997,998,999,1000,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,
1011,1012,1013,1014,1015,1016,1017,1018,1019,1020,1021,1022,1023,1024,1025,1026,1027,1028,1029,1030,
1031,1032,1033,1034,1035,1036,1037,1038,1039,1040,1041,1042,1043,1044,1045,1046,1047,1048,1049,1050,1051);

INSERT INTO standard_market_category_field(id, market_category_id, name_code, order_no, create_time, modify_time) VALUES
(964, 306, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(965, 306, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 2, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(966, 307, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 3, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(967, 307, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 12, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(968, 308, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 13, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(969, 308, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(970, 309, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 2, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(971, 309, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 3, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(972, 310, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 12, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(973, 310, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 13, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(974, 310, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(975, 311, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 2, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(976, 311, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 3, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(977, 311, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 1714, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(978, 312, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 1715, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(979, 312, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 70, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(980, 313, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 72, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(981, 313, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 1714, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(982, 314, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 1715, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(983, 314, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 12, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(984, 315, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 13, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(985, 315, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 12, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(986, 316, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 13, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(987, 316, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(988, 317, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(989, 317, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(990, 318, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(991, 318, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(992, 318, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(993, 318, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(994, 318, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(995, 318, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(996, 318, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(997, 318, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(998, 318, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(999, 318, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1000, 319, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1001, 319, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1002, 319, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1003, 319, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1004, 319, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1005, 319, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1006, 319, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1007, 320, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1008, 320, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1009, 320, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1010, 320, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1011, 321, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1012, 321, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1013, 321, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1014, 321, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1015, 322, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1016, 322, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1017, 322, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1018, 322, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1019, 323, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1020, 323, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1021, 323, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1022, 323, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1023, 324, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1024, 324, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1025, 325, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1026, 325, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1027, 326, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1028, 326, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1029, 326, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1030, 327, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1031, 327, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1032, 328, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1033, 328, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1034, 329, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1035, 329, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1036, 329, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1037, 330, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1038, 330, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1039, 331, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1040, 331, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1041, 332, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1042, 332, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1043, 333, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1044, 333, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1045, 333, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1046, 334, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1047, 334, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1048, 335, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1049, 335, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1050, 336, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1051, 336, CONCAT(UNIX_TIMESTAMP(),CEILING(RAND()*900000000+100000000)), 4, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);

update third_market_category_field set reference_id =966 ,order_no=12 where third_source_id = 'SR:139:12';
update third_market_category_field set reference_id =967 ,order_no=13 where third_source_id = 'SR:139:13';
update third_market_category_field set reference_id =970 ,order_no=12 where third_source_id = 'SR:152:12';
update third_market_category_field set reference_id =971 ,order_no=13 where third_source_id = 'SR:152:13';
update third_market_category_field set reference_id =972 ,order_no=1  where third_source_id = 'SR:136:1';
update third_market_category_field set reference_id =973 ,order_no=2  where third_source_id = 'SR:136:2';
update third_market_category_field set reference_id =974 ,order_no=3  where third_source_id = 'SR:136:3';
update third_market_category_field set reference_id =975 ,order_no=1  where third_source_id = 'SR:149:1';
update third_market_category_field set reference_id =976 ,order_no=2  where third_source_id = 'SR:149:2';
update third_market_category_field set reference_id =977 ,order_no=3  where third_source_id = 'SR:149:3';
update third_market_category_field set reference_id =982 ,order_no=12 where third_source_id = 'SR:140:12';
update third_market_category_field set reference_id =983 ,order_no=13 where third_source_id = 'SR:140:13';
update third_market_category_field set reference_id =984 ,order_no=12 where third_source_id = 'SR:141:12';
update third_market_category_field set reference_id =985 ,order_no=13 where third_source_id = 'SR:141:13';
update third_market_category_field set reference_id =986 ,order_no=12 where third_source_id = 'SR:153:12';
update third_market_category_field set reference_id =987 ,order_no=13 where third_source_id = 'SR:153:13';
update third_market_category_field set reference_id =988 ,order_no=12 where third_source_id = 'SR:154:12';
update third_market_category_field set reference_id =989 ,order_no=13 where third_source_id = 'SR:154:13';
update third_market_category_field set reference_id =990 ,order_no=710  where third_source_id = 'SR:142:710';
update third_market_category_field set reference_id =991 ,order_no=712  where third_source_id = 'SR:142:712';
update third_market_category_field set reference_id =992 ,order_no=714  where third_source_id = 'SR:142:714';
update third_market_category_field set reference_id =993 ,order_no=716  where third_source_id = 'SR:142:716';
update third_market_category_field set reference_id =994 ,order_no=718  where third_source_id = 'SR:142:718';
update third_market_category_field set reference_id =995 ,order_no=720  where third_source_id = 'SR:142:720';
update third_market_category_field set reference_id =996 ,order_no=722  where third_source_id = 'SR:142:722';
update third_market_category_field set reference_id =997 ,order_no=724  where third_source_id = 'SR:142:724';
update third_market_category_field set reference_id =998 ,order_no=726  where third_source_id = 'SR:142:726';
update third_market_category_field set reference_id =999 ,order_no=728  where third_source_id = 'SR:142:728';
update third_market_category_field set reference_id =1000,order_no= 1760 where third_source_id='SR:155:1760';
update third_market_category_field set reference_id =1001,order_no= 1761 where third_source_id='SR:155:1761';
update third_market_category_field set reference_id =1002,order_no= 1762 where third_source_id='SR:155:1762';
update third_market_category_field set reference_id =1003,order_no= 1763 where third_source_id='SR:155:1763';
update third_market_category_field set reference_id =1004,order_no= 1764 where third_source_id='SR:155:1764';
update third_market_category_field set reference_id =1005,order_no= 1765 where third_source_id='SR:155:1765';
update third_market_category_field set reference_id =1006,order_no= 1766 where third_source_id='SR:155:1766';
update third_market_category_field set reference_id =1007,order_no= 730  where third_source_id='SR:143:730';
update third_market_category_field set reference_id =1008,order_no= 732  where third_source_id='SR:143:732';
update third_market_category_field set reference_id =1009,order_no= 734  where third_source_id='SR:143:734';
update third_market_category_field set reference_id =1010,order_no= 736  where third_source_id='SR:143:736';
update third_market_category_field set reference_id =1011,order_no= 730  where third_source_id='SR:144:730';
update third_market_category_field set reference_id =1012,order_no= 732  where third_source_id='SR:144:732';
update third_market_category_field set reference_id =1013,order_no= 734  where third_source_id='SR:144:734';
update third_market_category_field set reference_id =1014,order_no= 736  where third_source_id='SR:144:736';
update third_market_category_field set reference_id =1015,order_no= 54 where third_source_id='SR:156:54';
update third_market_category_field set reference_id =1016,order_no= 56 where third_source_id='SR:156:56';
update third_market_category_field set reference_id =1017,order_no= 58 where third_source_id='SR:156:58';
update third_market_category_field set reference_id =1018,order_no= 60 where third_source_id='SR:156:60';
update third_market_category_field set reference_id =1019,order_no= 54 where third_source_id='SR:157:54';
update third_market_category_field set reference_id =1020,order_no= 56 where third_source_id='SR:157:56';
update third_market_category_field set reference_id =1021,order_no= 58 where third_source_id='SR:157:58';
update third_market_category_field set reference_id =1022,order_no= 60 where third_source_id='SR:157:60';


update third_market_category_field set reference_id = 964 ,order_no =1 where third_source_id ='TX:107:47';
update third_market_category_field set reference_id = 965 ,order_no =2 where third_source_id ='TX:107:48';
update third_market_category_field set reference_id = 966 ,order_no =1 where third_source_id ='TX:108:106';
update third_market_category_field set reference_id = 967 ,order_no =2 where third_source_id ='TX:108:107';
update third_market_category_field set reference_id = 968 ,order_no =1 where third_source_id ='TX:109:6';
update third_market_category_field set reference_id = 969 ,order_no =2 where third_source_id ='TX:109:7';
update third_market_category_field set reference_id = 970 ,order_no =1 where third_source_id ='TX:110:8';
update third_market_category_field set reference_id = 971 ,order_no =2 where third_source_id ='TX:110:9';
update third_market_category_field set reference_id = 972 ,order_no =1 where third_source_id ='TX:111:44';
update third_market_category_field set reference_id = 974 ,order_no =2 where third_source_id ='TX:111:45';
update third_market_category_field set reference_id = 973 ,order_no =3 where third_source_id ='TX:111:46';
update third_market_category_field set reference_id = 975 ,order_no =3 where third_source_id ='TX:112:3';
update third_market_category_field set reference_id = 977 ,order_no =2 where third_source_id ='TX:112:4';
update third_market_category_field set reference_id = 976 ,order_no =3 where third_source_id ='TX:112:5';
update third_market_category_field set reference_id = 978 ,order_no =1 where third_source_id ='TX:466:38';
update third_market_category_field set reference_id = 979 ,order_no =2 where third_source_id ='TX:466:39';
update third_market_category_field set reference_id = 980 ,order_no =1 where third_source_id ='TX:479:40';
update third_market_category_field set reference_id = 981 ,order_no =2 where third_source_id ='TX:479:41';
update third_market_category_field set reference_id = 1037,order_no = 1 where third_source_id ='TX:411:1';
update third_market_category_field set reference_id = 1038,order_no = 2 where third_source_id ='TX:411:2';
update third_market_category_field set reference_id = 1043,order_no = 1 where third_source_id ='TX:263:1';
update third_market_category_field set reference_id = 1044,order_no = 2 where third_source_id ='TX:263:2';
update third_market_category_field set reference_id = 1045,order_no = 3 where third_source_id ='TX:263:3';
update third_market_category_field set reference_id = 1046,order_no = 1 where third_source_id ='TX:264:1';
update third_market_category_field set reference_id = 1047,order_no = 2 where third_source_id ='TX:264:2';

update third_market_category_field set reference_id = 440,order_no = 1 where third_source_id ='TX:265:119';
update third_market_category_field set reference_id = 441,order_no = 1 where third_source_id ='TX:265:120';

update third_market_category_field set reference_id = 1019 where third_source_id ='SR:157:54';
update third_market_category_field set reference_id = 1020 where third_source_id ='SR:157:56';
update third_market_category_field set reference_id = 1022 where third_source_id ='SR:157:60';
update third_market_category_field set reference_id = 1021 where third_source_id ='SR:157:58';

delete from third_sport_market_category
where sport_id = 2 and market_category_id in (SELECT id FROM third_market_category WHERE third_source_id in('TX:466','TX:107','TX:111','TX:112','TX:109','TX:479','TX:110','TX:108','TX:264','TX:411','TX:257','TX:274','TX:263'))

delete from third_sport_market_category
where market_category_id in (SELECT id from third_market_category
where third_source_id in('SR:139','SR:152','SR:136','SR:149','SR:140','SR:141','SR:153','SR:154','SR:142','SR:155','SR:143','SR:144','SR:156','SR:157'));

INSERT INTO third_sport_market_category (sport_id, market_category_id, create_time, modify_time)
SELECT 1, id, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000
from third_market_category
where third_source_id in('SR:139','SR:152','SR:136','SR:149','SR:140','SR:141','SR:153','SR:154','SR:142','SR:155','SR:143','SR:144','SR:156','SR:157');


 update standard_sport_market_category set order_no = 140 where market_category_id = 306 and sport_id = 1;
 update standard_sport_market_category set order_no = 141 where market_category_id = 307 and sport_id = 1;
 update standard_sport_market_category set order_no = 142 where market_category_id = 308 and sport_id = 1;
 update standard_sport_market_category set order_no = 143 where market_category_id = 309 and sport_id = 1;
 update standard_sport_market_category set order_no = 144 where market_category_id = 310 and sport_id = 1;
 update standard_sport_market_category set order_no = 145 where market_category_id = 311 and sport_id = 1;
 update standard_sport_market_category set order_no = 146 where market_category_id = 312 and sport_id = 1;
 update standard_sport_market_category set order_no = 147 where market_category_id = 313 and sport_id = 1;
 update standard_sport_market_category set order_no = 148 where market_category_id = 314 and sport_id = 1;
 update standard_sport_market_category set order_no = 149 where market_category_id = 315 and sport_id = 1;
 update standard_sport_market_category set order_no = 150 where market_category_id = 316 and sport_id = 1;
 update standard_sport_market_category set order_no = 151 where market_category_id = 317 and sport_id = 1;
 update standard_sport_market_category set order_no = 152 where market_category_id = 318 and sport_id = 1;
 update standard_sport_market_category set order_no = 153 where market_category_id = 319 and sport_id = 1;
 update standard_sport_market_category set order_no = 154 where market_category_id = 320 and sport_id = 1;
 update standard_sport_market_category set order_no = 155 where market_category_id = 321 and sport_id = 1;
 update standard_sport_market_category set order_no = 156 where market_category_id = 322 and sport_id = 1;
 update standard_sport_market_category set order_no = 157 where market_category_id = 323 and sport_id = 1;
 update standard_sport_market_category set order_no = 158 where market_category_id = 324 and sport_id = 1;
 update standard_sport_market_category set order_no = 159 where market_category_id = 325 and sport_id = 1;
 update standard_sport_market_category set order_no = 160 where market_category_id = 326 and sport_id = 1;
 update standard_sport_market_category set order_no = 161 where market_category_id = 327 and sport_id = 1;
 update standard_sport_market_category set order_no = 162 where market_category_id = 328 and sport_id = 1;
 update standard_sport_market_category set order_no = 163 where market_category_id = 329 and sport_id = 1;
 update standard_sport_market_category set order_no = 164 where market_category_id = 330 and sport_id = 1;
 update standard_sport_market_category set order_no = 165 where market_category_id = 331 and sport_id = 1;
 update standard_sport_market_category set order_no = 166 where market_category_id = 332 and sport_id = 1;
 update standard_sport_market_category set order_no = 167 where market_category_id = 333 and sport_id = 1;
 update standard_sport_market_category set order_no = 168 where market_category_id = 334 and sport_id = 1;
 update standard_sport_market_category set order_no = 169 where market_category_id = 335 and sport_id = 1;
 update standard_sport_market_category set order_no = 170 where market_category_id = 336 and sport_id = 1;

update standard_sport_market_category set order_no =1000 where market_category_id= 10001 and sport_id = 1;
update standard_sport_market_category set order_no =1007 where market_category_id= 10002 and sport_id = 1;
update standard_sport_market_category set order_no =1002 where market_category_id= 10003 and sport_id = 1;
update standard_sport_market_category set order_no =1003 where market_category_id= 10004 and sport_id = 1;
update standard_sport_market_category set order_no =1004 where market_category_id= 10005 and sport_id = 1;
update standard_sport_market_category set order_no =1005 where market_category_id= 10006 and sport_id = 1;
update standard_sport_market_category set order_no =1006 where market_category_id= 10007 and sport_id = 1;
update standard_sport_market_category set order_no =1008 where market_category_id= 10008 and sport_id = 1;
update standard_sport_market_category set order_no =1009 where market_category_id= 10009 and sport_id = 1;
update standard_sport_market_category set order_no =1010 where market_category_id= 10010 and sport_id = 1;
update standard_sport_market_category set order_no =1011 where market_category_id= 10011 and sport_id = 1;
update standard_sport_market_category set order_no =1001 where market_category_id= 10012 and sport_id = 1;


update third_market_category_field set reference_id = 0 where third_source_id in
('SR:156:56', 'SR:156:54', 'SR:156:58','SR:156:60', 'SR:157:54', 'SR:157:56','SR:157:60', 'SR:157:58',
'SR:155:1760', 'SR:155:1766','SR:155:1762','SR:155:1761', 'SR:155:1763', 'SR:144:736', 'SR:144:730',
'SR:144:732', 'SR:144:734', 'SR:155:1764', 'SR:155:1765', 'SR:142:718', 'SR:142:712', 'SR:142:726',
'SR:142:720', 'SR:142:728', 'SR:142:722', 'SR:142:716', 'SR:142:724', 'SR:142:710', 'SR:142:714',
'SR:143:734','SR:143:730','SR:143:732','SR:143:736');

update third_market_category_field set reference_id = 442 where third_source_id = 'TX:274:1';
update third_market_category_field set reference_id = 443 where third_source_id = 'TX:274:2';

#del panda-merge::ThirdMarketCategoryField:6717-SR:157:54
#del panda-merge::ThirdMarketCategoryField:6717-SR:157:56
#del panda-merge::ThirdMarketCategoryField:6717-SR:157:60
#del panda-merge::ThirdMarketCategoryField:6717-SR:157:58


