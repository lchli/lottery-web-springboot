package com.lchli.lottery.controller;

import com.lchli.lottery.model.BaseReponse;
import com.lchli.lottery.model.TopicModel;
import com.lchli.lottery.model.TopicResponse;
import com.lchli.lottery.model.entity.Topic;
import com.lchli.lottery.model.entity.User;
import com.lchli.lottery.repo.TopicRepo;
import com.lchli.lottery.repo.UserRepo;
import com.lchli.lottery.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("topic")
public class TopicController {

    @Autowired
    TopicRepo topicRepo;

    @Autowired
    UserRepo userRepo;


    @PostMapping(value = "/add", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public TopicResponse publish(@RequestParam(value = "title", required = false) String title,
                                 @RequestParam(value = "tag", required = false) String tag,
                                 @RequestParam(value = "content", required = false) String content,
                                 @RequestParam(value = "topicId", required = false) String topicID,
                                 @RequestParam(value = "userId", required = false) String userId) {

        TopicResponse response = new TopicResponse();

        if (Utils.isEmpty(userId) || !userRepo.findById(userId).isPresent()) {
            response.status = BaseReponse.RESPCODE_FAILE;
            response.message = "用户不存在";
            return response;
        }

        Topic topic = null;

        if (!Utils.isEmpty(topicID)) {
            topic = topicRepo.findByUidAndUserId(topicID, userId);
        }

        if (topic == null) {
            topic = new Topic();
            topic.uid = Utils.uuid();
            topic.userId = userId;
        }

        topic.title = title;
        topic.tag = tag;
        topic.content = content;
        topic.updateTime = System.currentTimeMillis();

        topicRepo.save(topic);

        response.status = BaseReponse.RESPCODE_SUCCESS;

        return response;
    }


    @GetMapping(path = "/get", produces = {MediaType.APPLICATION_JSON_VALUE})
    public TopicResponse get(@RequestParam(value = "sort", required = false) String sort,
                             @RequestParam(value = "sortDirect", required = false) String sortDirect,
                             @RequestParam(value = "tag", required = false) String tag,
                             @RequestParam(value = "title", required = false) String title,
                             @RequestParam(value = "topicId", required = false) String topicId,
                             @RequestParam(value = "userId", required = false) String userId,
                             @RequestParam(value = "page", required = false) int page,
                             @RequestParam(value = "pageSize", required = false) int size) {


        TopicResponse response = new TopicResponse();
        response.status = BaseReponse.RESPCODE_SUCCESS;

        List<Topic> res = getImpl(sort, sortDirect, tag, title, topicId, userId, page, size);
        if (res == null) {
            return response;
        }

        response.data = new ArrayList<>();

        for (Topic topic : res) {
            User u = userRepo.findById(topic.userId).orElse(null);
            if (u != null) {
                TopicModel model = new TopicModel();
                model.content = topic.content;
                model.tag = topic.tag;
                model.title = topic.title;
                model.uid = topic.uid;
                model.updateTime = topic.updateTime;
                model.userId = topic.userId;
                model.userName = u.name;

                response.data.add(model);
            }
        }

        return response;

    }

    private List<Topic> getImpl(String sort, String sortDirect, String tag, String title, String topicId, String userId,
                                int page, int size) {

//
//        Sort s = new Sort(Sort.Direction.ASC, "updateTime");
//
//        if (!Utils.isEmpty(sort)) {
//            if ("asc".equals(sortDirect)) {
//                s = new Sort(Sort.Direction.ASC, sort);
//            } else {
//                s = new Sort(Sort.Direction.DESC, sort);
//            }
//        }
//
//        Pageable pageable = new PageRequest(page, size, s);
//
//        List<Topic> topicList = null;
//
//        if (!Utils.isEmpty(topicId)) {
//            Topic t = topicRepo.findById(topicId).orElse(null);
//            if (t != null) {
//                topicList = new ArrayList<>();
//                topicList.add(t);
//            }
//
//            return topicList;
//        }
//
//        if (!Utils.isEmpty(userId)) {
//            if (!Utils.isEmpty(tag)) {
//                if (!Utils.isEmpty(title)) {
//                    return topicRepo.findComplexWithUserIdTagTitle(userId, title, tag, pageable);
//                } else {
//                    return topicRepo.findComplexWithUserIdTag(userId, tag, pageable);
//
//                }
//
//            } else {
//                if (!Utils.isEmpty(title)) {
//                    return topicRepo.findComplexWithUserIdTitle(userId, title, pageable);
//
//                } else {
//                    return topicRepo.findComplexWithUserId(userId, pageable);
//
//                }
//            }
//        } else {
//            if (!Utils.isEmpty(tag)) {
//                if (!Utils.isEmpty(title)) {
//                    return topicRepo.findComplexWithTitleTag(title, tag, pageable);
//
//                } else {
//                    return topicRepo.findComplexWithTag(tag, pageable);
//
//                }
//
//            } else {
//                if (!Utils.isEmpty(title)) {
//                    return topicRepo.findComplexWithTitle(title, pageable);
//
//                } else {
//                    return topicRepo.findComplex(pageable);
//
//                }
//            }
return null;
        }




}
