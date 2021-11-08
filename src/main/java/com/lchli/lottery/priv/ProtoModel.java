package com.lchli.lottery.priv;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "proto")
public class ProtoModel {
    public static final String USER="user";
    public static final String PRI="privacy";

    @Id
    public String id;
    public String content;
    public String type;
}
