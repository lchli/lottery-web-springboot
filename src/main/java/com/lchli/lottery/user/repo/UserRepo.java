package com.lchli.lottery.user.repo;

import com.lchli.lottery.user.repo.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends MongoRepository<User,String> {

    User findByName(String name);
    User findByNameAndPwd(String name,String pwd);
    User findByUidAndToken(String uid,String token);
}
