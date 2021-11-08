package com.lchli.lottery.policy;

import com.lchli.lottery.priv.ProtoModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepo extends MongoRepository<PolicyModel,String> {

    PolicyModel findByType(String type);
}
