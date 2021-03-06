package com.lchli.lottery.post.repo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "note")
public class Note {
    public static final String PUBLIC_TRUE = "1";
    public static final String PUBLIC_FALSE = "0";
    @Id
    public String uid;
    public String title;
    public String type;
    public String content;
    public String userId;
    public String isPublic=PUBLIC_FALSE;
    public long updateTime;
    public List<String> star;

}
