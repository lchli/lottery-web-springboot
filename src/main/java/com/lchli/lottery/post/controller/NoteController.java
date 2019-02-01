package com.lchli.lottery.post.controller;


import com.lchli.lottery.BaseResponse;
import com.lchli.lottery.post.model.NoteModel;
import com.lchli.lottery.post.model.QueryNoteResponse;
import com.lchli.lottery.post.model.SingleNoteResponse;
import com.lchli.lottery.post.repo.entity.Note;
import com.lchli.lottery.user.repo.entity.User;
import com.lchli.lottery.util.Constants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("api/sec/note")
public class NoteController {

    @Autowired
    private MongoTemplate mongoTemplate;


    @ResponseBody
    @PostMapping(value = "/saveOrUpdate", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public BaseResponse uploadNote(@RequestParam(value = "content", required = false) String content,
                                   @RequestParam(value = "title", required = false) String title,
                                   @RequestParam(value = "type", required = false) String type,
                                   @RequestParam(value = "sessionUserId", required = false) String userId,
                                   @RequestParam(value = "sessionUserToken", required = false) String userToken,
                                   @RequestParam(value = "uid", required = false) String uid,
                                   @RequestParam(value = "isPublic", defaultValue = Note.PUBLIC_FALSE) String isPublic

    ) {

        BaseResponse res = new BaseResponse();
        res.status = BaseResponse.RESPCODE_FAIL;

        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(title) || StringUtils.isEmpty(type) ||
                StringUtils.isEmpty(userId) || StringUtils.isEmpty(userToken)) {
            res.message = "参数不合法";
            return res;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("uid").is(userId));
        query.addCriteria(Criteria.where("token").is(userToken));

        User u = mongoTemplate.findOne(query, User.class);
        if (u == null) {
            res.message = "用户token无效";
            return res;
        }

        Note note = new Note();
        note.content = content;
        note.title = title;
        note.type = type;
        note.uid = uid;//if null,mongo generate it.
        note.updateTime = System.currentTimeMillis();
        note.userId = userId;

        if (!isPublic.equals(Note.PUBLIC_TRUE)) {
            isPublic = Note.PUBLIC_FALSE;
        }
        note.isPublic = isPublic;

        mongoTemplate.save(note);

        res.status = BaseResponse.RESPCODE_SUCCESS;

        return res;


    }


    @ResponseBody
    @PostMapping(value = "/likeOrUnLike", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public SingleNoteResponse likeNote(@RequestParam(value = "sessionUserToken", required = false) String userToken,
                                       @RequestParam(value = "noteId", required = false) String noteId,
                                       @RequestParam(value = "sessionUserId", required = false) String userId
    ) {

        SingleNoteResponse res = new SingleNoteResponse();
        res.status = BaseResponse.RESPCODE_FAIL;

        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(userToken)) {
            res.message = "参数不合法";
            return res;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("uid").is(userId));
        query.addCriteria(Criteria.where("token").is(userToken));

        User u = mongoTemplate.findOne(query, User.class);
        if (u == null) {
            res.message = "用户token无效";
            return res;
        }

        Note note = mongoTemplate.findById(noteId, Note.class);
        if (note == null) {
            res.message = "笔记不存在";
            return res;
        }

        List<String> stars = note.star;
        if (stars == null) {
            stars = new ArrayList<>();
        }

        if (stars.contains(userId)) {
            stars.remove(userId);
        } else {
            stars.add(userId);
        }

        note.star = stars;

        mongoTemplate.save(note);

        res.status = BaseResponse.RESPCODE_SUCCESS;
        res.data = NoteModelMapper.toModel(note, mongoTemplate);

        return res;

    }


    @ResponseBody
    @PostMapping(value = "/publicOrUnPublic", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public BaseResponse publicNote(@RequestParam(value = "sessionUserToken", required = false) String userToken,
                                   @RequestParam(value = "noteId", required = false) String noteId,
                                   @RequestParam(value = "sessionUserId", required = false) String userId
    ) {

        SingleNoteResponse res = new SingleNoteResponse();
        res.status = BaseResponse.RESPCODE_FAIL;

        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(userToken)) {
            res.message = "参数不合法";
            return res;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("uid").is(userId));
        query.addCriteria(Criteria.where("token").is(userToken));

        User u = mongoTemplate.findOne(query, User.class);
        if (u == null) {
            res.message = "用户验证失败";
            return res;
        }

        Note note = mongoTemplate.findById(noteId, Note.class);
        if (note == null) {
            res.message = "笔记不存在";
            return res;
        }

        if (!userId.equals(note.userId)) {
            res.message = "无权限";
            return res;
        }

        if (Note.PUBLIC_TRUE.equals(note.isPublic)) {
            note.isPublic = Note.PUBLIC_FALSE;
        } else {
            note.isPublic = Note.PUBLIC_TRUE;
        }

        mongoTemplate.save(note);

        res.data = NoteModelMapper.toModel(note, mongoTemplate);
        res.status = BaseResponse.RESPCODE_SUCCESS;

        return res;


    }


    @ResponseBody
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public BaseResponse deleteNote(@RequestParam(value = "sessionUserToken", required = false) String userToken,
                                   @RequestParam(value = "noteId", required = false) String noteId,
                                   @RequestParam(value = "sessionUserId", required = false) String userId
    ) {

        BaseResponse res = new BaseResponse();
        res.status = BaseResponse.RESPCODE_FAIL;

        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(userToken)) {
            res.message = "参数不合法";
            return res;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("uid").is(userId));
        query.addCriteria(Criteria.where("token").is(userToken));

        User u = mongoTemplate.findOne(query, User.class);
        if (u == null) {
            res.message = "用户验证失败";
            return res;
        }

        Note note = mongoTemplate.findById(noteId, Note.class);
        if (note == null) {
            res.message = "笔记不存在";
            return res;
        }

        if (!userId.equals(note.userId)) {
            res.message = "无权限";
            return res;
        }

        mongoTemplate.remove(note);

        res.status = BaseResponse.RESPCODE_SUCCESS;

        return res;


    }


    @ResponseBody
    @PostMapping(value = "/get", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public BaseResponse queryNotes(@RequestParam(value = "title", required = false) String title,
                                   @RequestParam(value = "type", required = false) String type,
                                   @RequestParam(value = "uid", required = false) String uid,
                                   @RequestParam(value = "page", required = false) int page,
                                   @RequestParam(value = "pageSize", required = false) int pageSize,
                                   @RequestParam(value = "sort", required = false) String sortArray,
                                   @RequestParam(value = "sessionUserToken", required = false) String sessionUserToken,
                                   @RequestParam(value = "sessionUserId", required = false) String sessionUserId,
                                   @RequestParam(value = "ownerUserId", required = false) String ownerUserId
    ) {

        QueryNoteResponse res = new QueryNoteResponse();
        res.status = BaseResponse.RESPCODE_FAIL;
        res.data = new ArrayList<>();

        if (page < 0 || pageSize <= 0) {
            res.message = "参数不合法";
            return res;
        }

        Query query = new Query();

        if (!StringUtils.isEmpty(ownerUserId)) {

            if (ownerUserId.equals(sessionUserId)) {
                Query userQ = new Query();
                userQ.addCriteria(Criteria.where("uid").is(sessionUserId));
                userQ.addCriteria(Criteria.where("token").is(sessionUserToken));

                User u = mongoTemplate.findOne(userQ, User.class);
                if (u == null) {
                    res.message = "用户token无效";
                    return res;
                }

            } else {
                query.addCriteria(Criteria.where("isPublic").is(Note.PUBLIC_TRUE));
            }

        } else {
            query.addCriteria(Criteria.where("isPublic").is(Note.PUBLIC_TRUE));
        }


        if (!StringUtils.isEmpty(uid)) {
            query.addCriteria(Criteria.where("uid").is(uid));
        }

        if (!StringUtils.isEmpty(ownerUserId)) {
            query.addCriteria(Criteria.where("userId").is(ownerUserId));
        }

        if (!StringUtils.isEmpty(type)) {
            query.addCriteria(Criteria.where("type").is(type));
        }

        if (!StringUtils.isEmpty(title)) {
            query.addCriteria(Criteria.where("title").regex(title));
        }

        List<Sort.Order> orders = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(sortArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jobj = jsonArray.optJSONObject(i);
                if (jobj == null) {
                    continue;
                }
                String key = jobj.optString("key");
                String val = jobj.optString("direction");
                if (StringUtils.isEmpty(key) || StringUtils.isEmpty(val)) {
                    continue;
                }

                Sort.Order order = Sort.Order.by(key);
                if (val.equals("asc")) {
                    order.with(Sort.Direction.ASC);
                } else {
                    order.with(Sort.Direction.DESC);
                }

                orders.add(order);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by(orders));

        query.with(pageable);

        List<Note> notes = mongoTemplate.find(query, Note.class);

        if (notes != null && !notes.isEmpty()) {
            for (Note note : notes) {
                res.data.add(NoteModelMapper.toModel(note, mongoTemplate));

            }
        }

        res.status = BaseResponse.RESPCODE_SUCCESS;

        return res;

    }


}
