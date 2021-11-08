package com.lchli.lottery.policy;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "policy")
public class PolicyModel {
    public static String AD_POLI="AD_POLI";
    @Id
    public String id;
    public String type;
    public int asw=0;
}
