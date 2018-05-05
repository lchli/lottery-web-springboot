package com.lchli.lottery.repo;

import com.lchli.lottery.model.entity.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepo extends MongoRepository<Topic, String> {


    Topic findByUidAndUserId(String uid, String userId);

//    @Query("select u from Topic u where u.userId = :userId and u.tag=:tag and u.title like %:title%")
//    List<Topic> findComplexWithUserIdTagTitle(@Param("userId") String userId, @Param("title") String title, @Param("tag") String tag, Pageable pageable);
//
//    @Query("select u from Topic u where u.userId = :userId and u.tag=:tag")
//    List<Topic> findComplexWithUserIdTag(@Param("userId") String userId, @Param("tag") String tag, Pageable pageable);
//
//    @Query("select u from Topic u where u.userId = :userId and u.title like %:title%")
//    List<Topic> findComplexWithUserIdTitle(@Param("userId") String userId, @Param("title") String title, Pageable pageable);
//
//    @Query("select u from Topic u where u.userId = :userId")
//    List<Topic> findComplexWithUserId(@Param("userId") String userId, Pageable pageable);
//
//    @Query("select u from Topic u where u.tag=:tag and u.title like %:title%")
//    List<Topic> findComplexWithTitleTag(@Param("title") String title, @Param("tag") String tag, Pageable pageable);
//
//    @Query("select u from Topic u where u.tag=:tag")
//    List<Topic> findComplexWithTag(@Param("tag") String tag, Pageable pageable);
//
//    @Query("select u from Topic u where u.title like %:title%")
//    List<Topic> findComplexWithTitle(@Param("title") String title, Pageable pageable);
//
//    @Query("select u from Topic u")
//    List<Topic> findComplex(Pageable pageable);
}
