

ALTER TABLE config_outright_trade_market
    ADD INDEX idx_standard_match_id (standard_match_id),
ADD INDEX idx_standard_market_id (standard_market_id);


DELETE t1 FROM config_outright_trade_market t1
INNER JOIN config_outright_trade_market t2
  ON t1.standard_match_id = t2.standard_match_id
 AND t1.standard_market_id = t2.standard_market_id
 AND t1.id > t2.id;


ALTER TABLE config_outright_trade_market
    ADD UNIQUE KEY uk_match_market (standard_match_id, standard_market_id);