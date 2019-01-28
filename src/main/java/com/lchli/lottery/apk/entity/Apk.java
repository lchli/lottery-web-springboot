package com.lchli.lottery.apk.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "apks")
public class Apk {
    @Id
    public String uid;
    public String name;
    public int version;
    public String fileId;

}
