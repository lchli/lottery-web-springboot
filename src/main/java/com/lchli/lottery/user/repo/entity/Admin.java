package com.lchli.lottery.user.repo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "admin")
public class Admin {
    @Id
    public String uid;
    public String name;
    public String pwd;

}
