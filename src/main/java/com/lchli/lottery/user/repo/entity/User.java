package com.lchli.lottery.user.repo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Document(collection = "user")
public class User {
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_VIP = "vip";
    public static final String ROLE_EXPERT = "expert";


    @Id
    public String uid;
    public String name;
    public String pwd;
    public String token;
    public String headUrl;
    public String userContact;
    public String userNick;
    public Set<String> roles;
}
