update i18n_market_category set text = 'under' where language_type = 'en' and name_code in (select name_code from third_market_category_field where third_source_id in ('SR:19:13','SR:20:13'));
update i18n_market_category set text = 'over' where language_type = 'en' and name_code in (select name_code from third_market_category_field where third_source_id in ('SR:19:12','SR:20:12'));
