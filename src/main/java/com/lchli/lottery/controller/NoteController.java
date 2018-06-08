package com.lchli.lottery.controller;


import com.lchli.lottery.model.BaseReponse;
import com.lchli.lottery.model.NoteModel;
import com.lchli.lottery.model.QueryNoteResponse;
import com.lchli.lottery.model.entity.Apk;
import com.lchli.lottery.model.entity.Note;
import com.lchli.lottery.model.entity.User;
import com.lchli.lottery.util.Constants;
import com.lchli.lottery.util.NoteConverter;
import com.lchli.lottery.util.QrcodeUtil;
import com.lchli.lottery.util.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("note")
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
                                  @RequestParam(value = "isPublic", required = false) boolean isPublic) {
        Note note = new Note();
        note.content = content;
        note.title = title;
        note.thumbNail = thumbNail;
        note.type = type;
        note.uid = uid;
        note.updateTime = System.currentTimeMillis();
        note.userId = userId;
        note.isPublic = isPublic;

        mongoTemplate.save(note);

        BaseReponse res = new BaseReponse();
        res.status = BaseReponse.RESPCODE_SUCCESS;

        return res;
    }


    @ResponseBody
    @PostMapping(value = "/get", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public BaseReponse queryNote(@RequestParam(value = "title", required = false) String title,
                                 @RequestParam(value = "type", required = false) String type,
                                 @RequestParam(value = "userId", required = false) String userId,
                                 @RequestParam(value = "userToken", required = false) String userToken,
                                 @RequestParam(value = "uid", required = false) String uid,
                                 @RequestParam(value = "page", required = false) int page,
                                 @RequestParam(value = "pageSize", required = false) int pageSize,
                                 @RequestParam(value = "sort", required = false) String sortArray,
                                 @RequestParam(value = "isPublic", required = false) boolean isPublic) {

        QueryNoteResponse res = new QueryNoteResponse();
        res.status = BaseReponse.RESPCODE_SUCCESS;
        res.data = new ArrayList<>();

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

                User user = mongoTemplate.findById(note.userId + "", User.class);
                if (user != null) {
                    noteModel.userHeadUrl = user.headUrl;
                }

                res.data.add(noteModel);
            }
        }


        return res;
    }


    @GetMapping("/view/{uid}")
    public String view(@PathVariable("uid") String uid, Model model) {

        Note note = mongoTemplate.findById(uid, Note.class);
        NoteModel noteModel = null;

        if (note != null) {
            noteModel = new NoteModel();
            noteModel.content = NoteConverter.convertNoteContentToHtml(note.content);
            noteModel.ShareUrl = buildShareUrl(uid);
            noteModel.thumbNail = note.thumbNail;
            noteModel.title = note.title;
            noteModel.type = note.type;
            noteModel.uid = note.uid;
            noteModel.updateTime = note.updateTime;
            noteModel.userId = note.userId;
        }

        Query query = new Query().with(Sort.by(Sort.Direction.ASC, "version"));

        Apk apk = mongoTemplate.findOne(query, Apk.class);

        if (apk != null && apk.fileId != null) {
            String apkUrl = Utils.buildFileDownloadUrl(apk.fileId);

            String base64 = QrcodeUtil.getBase64QRCode(apkUrl, 400, 400);

            model.addAttribute("apkBase64", "data:image/jpg;base64," + base64);
        } else {
            model.addAttribute("apkBase64", "");
        }

        model.addAttribute("note", noteModel);

        return "index";
    }


    private static String buildShareUrl(String noteId) {
        return String.format("%s/note/view/%s", Constants.HOST, noteId);
    }

}
