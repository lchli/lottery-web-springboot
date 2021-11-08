package com.lchli.lottery.priv;

import com.lchli.lottery.user.repo.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProtoRepo extends MongoRepository<ProtoModel,String> {

    ProtoModel findByType(String type);
}
