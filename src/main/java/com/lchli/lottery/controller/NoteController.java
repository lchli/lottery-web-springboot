package com.lchli.lottery.controller;


import com.lchli.lottery.model.BaseReponse;
import com.lchli.lottery.model.NoteModel;
import com.lchli.lottery.model.QueryNoteResponse;
import com.lchli.lottery.model.entity.Note;
import com.lchli.lottery.model.entity.User;
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
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public BaseReponse uploadNote(@RequestParam(value = "content", required = false) String content,
                                  @RequestParam(value = "title", required = false) String title,
                                  @RequestParam(value = "type", required = false) String type,
                                  @RequestParam(value = "thumbNail", required = false) String thumbNail,
                                  @RequestParam(value = "userId", required = false) String userId,
                                  @RequestParam(value = "userToken", required = false) String userToken,
                                  @RequestParam(value = "uid", required = false) String uid,
                                  @RequestParam(value = "isPublic", required = false) String isPublic
    ) {
        BaseReponse res = new BaseReponse();

        try {

            Query query = new Query();
            query.addCriteria(Criteria.where("uid").is(userId));
            query.addCriteria(Criteria.where("token").is(userToken));

            User u = mongoTemplate.findOne(query, User.class);
            if (u == null) {
                res.status = BaseReponse.RESPCODE_FAILE;
                res.message = "用户token无效";
                return res;
            }

            if (StringUtils.isEmpty(content) || StringUtils.isEmpty(title) || StringUtils.isEmpty(type)) {
                res.status = BaseReponse.RESPCODE_FAILE;
                res.message = "参数不合法";
                return res;
            }

            Note note = new Note();
            note.content = content;
            note.title = title;
            note.thumbNail = thumbNail;
            note.type = type;
            note.uid = uid;//if null,mongo generate it.
            note.updateTime = System.currentTimeMillis();
            note.userId = userId;
            note.isPublic = isPublic;

            mongoTemplate.save(note);

            res.status = BaseReponse.RESPCODE_SUCCESS;

            return res;

        } catch (Throwable e) {
            e.printStackTrace();
            res.status = BaseReponse.RESPCODE_FAILE;
            res.message = e.getMessage();
            return res;
        }
    }


    @ResponseBody
    @PostMapping(value = "/like", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public BaseReponse likeNote(@RequestParam(value = "userToken", required = false) String userToken,
                                @RequestParam(value = "noteId", required = false) String noteId,
                                @RequestParam(value = "userId", required = false) String userId
    ) {

        BaseReponse res = new BaseReponse();

        try {

            Query query = new Query();
            query.addCriteria(Criteria.where("uid").is(userId));
            query.addCriteria(Criteria.where("token").is(userToken));

            User u = mongoTemplate.findOne(query, User.class);
            if (u == null) {
                res.status = BaseReponse.RESPCODE_FAILE;
                res.message = "用户验证失败";
                return res;
            }

            Note note = mongoTemplate.findById(noteId, Note.class);
            if (note == null) {
                res.status = BaseReponse.RESPCODE_FAILE;
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

            res.status = BaseReponse.RESPCODE_SUCCESS;

            return res;

        } catch (Throwable e) {
            e.printStackTrace();
            res.status = BaseReponse.RESPCODE_FAILE;
            res.message = e.getMessage();
            return res;
        }
    }


    @ResponseBody
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public BaseReponse deleteNote(@RequestParam(value = "userToken", required = false) String userToken,
                                  @RequestParam(value = "noteId", required = false) String noteId,
                                  @RequestParam(value = "userId", required = false) String userId
    ) {

        BaseReponse res = new BaseReponse();

        try {

            Query query = new Query();
            query.addCriteria(Criteria.where("uid").is(userId));
            query.addCriteria(Criteria.where("token").is(userToken));

            User u = mongoTemplate.findOne(query, User.class);
            if (u == null) {
                res.status = BaseReponse.RESPCODE_FAILE;
                res.message = "用户验证失败";
                return res;
            }


            Note note = mongoTemplate.findById(noteId, Note.class);
            if (note == null) {
                res.status = BaseReponse.RESPCODE_FAILE;
                res.message = "笔记不存在";
                return res;
            }

            if (!userId.equals(note.userId)) {
                res.status = BaseReponse.RESPCODE_FAILE;
                res.message = "无权限";
                return res;
            }

            mongoTemplate.remove(note);

            res.status = BaseReponse.RESPCODE_SUCCESS;

            return res;

        } catch (Throwable e) {
            e.printStackTrace();
            res.status = BaseReponse.RESPCODE_FAILE;
            res.message = e.getMessage();
            return res;
        }
    }


    @ResponseBody
    @PostMapping(value = "/get", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public BaseReponse queryNote(@RequestParam(value = "title", required = false) String title,
                                 @RequestParam(value = "type", required = false) String type,
                                 @RequestParam(value = "userId", required = false) String userId,
                                 @RequestParam(value = "uid", required = false) String uid,
                                 @RequestParam(value = "page", required = false) int page,
                                 @RequestParam(value = "pageSize", required = false) int pageSize,
                                 @RequestParam(value = "sort", required = false) String sortArray,
                                 @RequestParam(value = "isPublic", required = false) String isPublic) {

        QueryNoteResponse res = new QueryNoteResponse();
        res.status = BaseReponse.RESPCODE_SUCCESS;
        res.data = new ArrayList<>();

        try {

            if (page < 0 || pageSize <= 0) {
                res.status = BaseReponse.RESPCODE_FAILE;
                res.message = "参数不合法";
                return res;
            }

            Query query = new Query();

            if (!StringUtils.isEmpty(uid)) {
                query.addCriteria(Criteria.where("uid").is(uid));
            }

            if (!StringUtils.isEmpty(userId)) {
                query.addCriteria(Criteria.where("userId").is(userId));
            }

            if (!StringUtils.isEmpty(type)) {
                query.addCriteria(Criteria.where("type").is(type));
            }

            if (!StringUtils.isEmpty(title)) {
                query.addCriteria(Criteria.where("title").regex(title));
            }
            if (!StringUtils.isEmpty(isPublic)) {
                query.addCriteria(Criteria.where("isPublic").is(isPublic));
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
                    NoteModel noteModel = new NoteModel();
                    noteModel.content = note.content;
                    noteModel.ShareUrl = buildShareUrl(note.uid);
                    noteModel.thumbNail = note.thumbNail;
                    noteModel.title = note.title;
                    noteModel.type = note.type;
                    noteModel.uid = note.uid;
                    noteModel.updateTime = note.updateTime;
                    noteModel.userId = note.userId;
                    noteModel.isPublic = note.isPublic;
                    noteModel.star = note.star;

                    User user = mongoTemplate.findById(note.userId + "", User.class);
                    if (user != null) {
                        noteModel.userHeadUrl = user.headUrl;
                        noteModel.userName = user.name;
                    }

                    res.data.add(noteModel);
                }
            }


            return res;

        } catch (Throwable e) {
            e.printStackTrace();
            res.status = BaseReponse.RESPCODE_FAILE;
            res.message = e.getMessage();
            return res;
        }
    }


    private static String buildShareUrl(String noteId) {
        return String.format("%s/api/public/note/view/%s", Constants.HOST, noteId);
    }

}
