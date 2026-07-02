package com.panda.merge.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 球队： 包含标准球队和三方球队
 * id :  标准或三方球队ID_是否为标准球队
 * */
@Data
@Document(indexName = "match_team")
public class MatchTeam {
    /**三方或者标准球队ID_是否为标准球队*/
    @Id
    public String id;
    /**英语名称*/
    @Field(type = FieldType.Keyword, analyzer = "ik_max_word",fielddata=true)
    public String name_spell;
    /**中文名称*/
    @Field(type = FieldType.Keyword, analyzer = "ik_max_word",fielddata=true)
    public String name;
    /**数据源编码名称*/
    @Field(type = FieldType.Integer,fielddata=true)
    public Integer data_source_code;
    /**赛种*/
    @Field(type = FieldType.Long,fielddata=true)
    public Long sport_id;
    /**三方球队ID*/
    @Field(type = FieldType.Long,fielddata=true)
    public Long third_team_id;
    /**标准球队ID*/
    @Field(type = FieldType.Long,fielddata=true)
    public Long standard_team_id;
    /**区域ID*/
    @Field(type = FieldType.Long,fielddata=true)
    public Long region_id;
    /**国家ID*/
    @Field(type = FieldType.Long,fielddata=true)
    public Long country_id;
    /**关联球队数量*/
    @Field(type = FieldType.Integer_Range,fielddata=true)
    public Integer related_data_source_coder_num;
    /**管理ID*/
    @Field(type = FieldType.Long,fielddata=true)
    public String team_manage_id;
    /**类型*/
    @Field(type = FieldType.Integer,fielddata=true)
    public Integer type;
    /**是否为标准球队:  1 是  0  否*/
    @Field(type = FieldType.Integer,fielddata=true)
    public Integer is_standard;
    /**关联球队ID*/
    @Field(type = FieldType.Long ,fielddata=true)
    public Long reference_id;
    /**创建时间*/
    @Field(type = FieldType.Long_Range)
    public Long create_time;
    /**更新时间*/
    @Field(type = FieldType.Long_Range)
    public Long modify_time;
}
