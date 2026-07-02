ALTER TABLE  `market_category_sell`
ADD COLUMN `is_special_pumping` int DEFAULT '0' COMMENT '是否特殊抽水1:是 0:否' ,
ADD COLUMN `special_odds_interval` VARCHAR(200) NULL COMMENT '特殊抽水赔率区间' ;
