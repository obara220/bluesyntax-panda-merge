package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMatchPhrase;
import com.panda.merge.model.ThirdMatchPhraseExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ThirdMatchPhraseMapper {
    long countByExample(ThirdMatchPhraseExample example);

    int deleteByExample(ThirdMatchPhraseExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdMatchPhrase record);

    int insertSelective(ThirdMatchPhrase record);

    List<ThirdMatchPhrase> selectByExample(ThirdMatchPhraseExample example);

    ThirdMatchPhrase selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdMatchPhrase record, @Param("example") ThirdMatchPhraseExample example);

    int updateByExample(@Param("record") ThirdMatchPhrase record, @Param("example") ThirdMatchPhraseExample example);

    int updateByPrimaryKeySelective(ThirdMatchPhrase record);

    int updateByPrimaryKey(ThirdMatchPhrase record);
}