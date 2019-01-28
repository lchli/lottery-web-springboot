package com.lchli.lottery.post.controller;


import com.lchli.lottery.BaseResponse;
import com.lchli.lottery.post.model.NoteModel;
import com.lchli.lottery.post.model.QueryNoteResponse;
import com.lchli.lottery.post.repo.entity.ExpertNote;
import com.lchli.lottery.user.repo.entity.User;
import com.lchli.lottery.util.RoleUtils;
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
import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping("api/sec/expertNote")
public class ExpertNoteController {

    @Autowired
    private MongoTemplate mongoTemplate;


    @ResponseBody
    @PostMapping(value = "/saveOrUpdate", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public BaseResponse uploadNote(@RequestParam(value = "content", required = false) String content,
                                   @RequestParam(value = "title", required = false) String title,
                                   @RequestParam(value = "type", required = false) String type,
                                   @RequestParam(value = "userId", required = false) String userId,
                                   @RequestParam(value = "userToken", required = false) String userToken,
                                   @RequestParam(value = "uid", required = false) String uid

    ) {
        BaseResponse res = new BaseResponse();
        res.status = BaseResponse.RESPCODE_FAIL;

        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(title) || StringUtils.isEmpty(type)) {
            res.message = "参数不合法";
            return res;
        }

        Calendar car = Calendar.getInstance();
        car.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE), 20, 20, 0);
        long start = car.getTimeInMillis();
        car.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE), 20, 40, 0);
        long end = car.getTimeInMillis();
        long now = System.currentTimeMillis();
        if (now > start && now < end) {
            res.message = "该时间段不允许修改专家帖子";
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

        if (!RoleUtils.hasRole(User.ROLE_EXPERT, u.roles)) {
            res.message = "用户无权限";
            return res;
        }

        ExpertNote note = new ExpertNote();
        note.content = content;
        note.title = title;
        note.type = type;
        note.uid = uid;//if null,mongo generate it.
        note.updateTime = System.currentTimeMillis();
        note.userId = userId;

        mongoTemplate.save(note);

        res.status = BaseResponse.RESPCODE_SUCCESS;

        return res;


    }


    @ResponseBody
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public BaseResponse deleteNote(@RequestParam(value = "userToken", required = false) String userToken,
                                   @RequestParam(value = "noteId", required = false) String noteId,
                                   @RequestParam(value = "userId", required = false) String userId
    ) {

        BaseResponse res = new BaseResponse();
        res.status = BaseResponse.RESPCODE_FAIL;

        Query query = new Query();
        query.addCriteria(Criteria.where("uid").is(userId));
        query.addCriteria(Criteria.where("token").is(userToken));

        User u = mongoTemplate.findOne(query, User.class);
        if (u == null) {
            res.message = "用户验证失败";
            return res;
        }

        ExpertNote note = mongoTemplate.findById(noteId, ExpertNote.class);
        if (note == null) {
            res.message = "笔记不存在";
            return res;
        }

        if (!RoleUtils.hasRole(User.ROLE_ADMIN, u.roles)) {
            res.message = "用户无权限";
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
                                   @RequestParam(value = "userId", required = false) String userId,
                                   @RequestParam(value = "uid", required = false) String uid,
                                   @RequestParam(value = "page", required = false) int page,
                                   @RequestParam(value = "pageSize", required = false) int pageSize,
                                   @RequestParam(value = "sort", required = false) String sortArray,
                                   @RequestParam(value = "authUserId", required = false) String authUserId,
                                   @RequestParam(value = "authUserToken", required = false) String authUserToken
    ) {

        QueryNoteResponse res = new QueryNoteResponse();
        res.status = BaseResponse.RESPCODE_SUCCESS;
        res.data = new ArrayList<>();

        if (page < 0 || pageSize <= 0) {
            res.status = BaseResponse.RESPCODE_FAIL;
            res.message = "参数不合法";
            return res;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("uid").is(authUserId));
        query.addCriteria(Criteria.where("token").is(authUserToken));

        User u = mongoTemplate.findOne(query, User.class);
        if (u == null) {
            res.message = "用户验证失败";
            return res;
        }
        boolean isaccept = false;

        if (authUserId.equals(userId) && RoleUtils.hasRole(User.ROLE_EXPERT, u.roles)) {
            isaccept = true;
        } else if (RoleUtils.hasRole(User.ROLE_VIP, u.roles)) {
            isaccept = true;
        }

        if (!isaccept) {
            res.message = "用户无权限";
            return res;
        }

        query = new Query();

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

        List<ExpertNote> notes = mongoTemplate.find(query, ExpertNote.class);

        if (notes != null && !notes.isEmpty()) {
            for (ExpertNote note : notes) {
                NoteModel noteModel = new NoteModel();
                noteModel.content = note.content;
                noteModel.title = note.title;
                noteModel.type = note.type;
                noteModel.uid = note.uid;
                noteModel.updateTime = note.updateTime;
                noteModel.userId = note.userId;

                User user = mongoTemplate.findById(note.userId + "", User.class);
                if (user != null) {
                    noteModel.userHeadUrl = user.headUrl;
                    noteModel.userName = user.name;
                }

                res.data.add(noteModel);
            }
        }


        return res;

    }


}
