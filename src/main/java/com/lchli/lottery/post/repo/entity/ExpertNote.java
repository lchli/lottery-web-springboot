package com.lchli.lottery.post.repo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "expert_note")
public class ExpertNote {
    @Id
    public String uid;
    public String title;
    public String type;
    public String content;
    public String userId;
    public long updateTime;
}
