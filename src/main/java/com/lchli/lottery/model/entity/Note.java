package com.lchli.lottery.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "note")
public class Note {
    @Id
    public String uid;
    public String title;
    public String type;
    public String content;
    public String userId;
    public String thumbNail;
    public long updateTime;
    public List<String> star;

    public String isPublic="0";

}
