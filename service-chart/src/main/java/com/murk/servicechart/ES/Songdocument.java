package com.murk.servicechart.ES;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "song")
@Data
public class Songdocument {
    @Id//主键,ES用这个去重，必须有
    private String songId;
    @Field(type= FieldType.Text)//可以分词
    private String songName;
    @Field(type= FieldType.Text)
    private String singerName;
    @Field(type= FieldType.Text)
    private String rankName;
    @Field(type= FieldType.Integer)//搜索完成后按这个来排序
    private Integer voteCount;


}
