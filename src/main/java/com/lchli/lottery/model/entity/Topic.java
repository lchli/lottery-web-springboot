package com.lchli.lottery.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "topic")
public class Topic {
    @Id
    public String uid;
    public String title;
    public String tag;
    public String content;
    public String userId;
    public long updateTime;

}
