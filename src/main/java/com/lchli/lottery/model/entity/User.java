package com.lchli.lottery.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public class User {
    @Id
    public String uid;
    public String name;
    public String pwd;
    public String token;
}
