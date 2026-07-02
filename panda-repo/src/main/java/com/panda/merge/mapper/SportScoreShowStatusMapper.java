package com.panda.merge.mapper;

import com.panda.merge.dto.scores.B02ScoresSourceDTO;
import com.panda.merge.model.B02ScoresSource;
import com.panda.merge.model.SportScoreShowStatus;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportScoreShowStatusMapper {

    /**
     * 查询
     * @author Fymen
     * @date 2024/02/17
     **/
    List<SportScoreShowStatus> query();


    /**
     * 更新
     * @author Fymen
     * @date 2024/02/17
     **/
    int update(SportScoreShowStatus sportScoreShowStatus);
}