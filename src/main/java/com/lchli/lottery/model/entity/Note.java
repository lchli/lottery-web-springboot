package com.lchli.lottery.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

    public boolean isPublic=false;

}
