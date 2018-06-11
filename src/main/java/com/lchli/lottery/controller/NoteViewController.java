package com.lchli.lottery.controller;


import com.lchli.lottery.model.NoteModel;
import com.lchli.lottery.model.entity.Apk;
import com.lchli.lottery.model.entity.Note;
import com.lchli.lottery.util.Constants;
import com.lchli.lottery.util.NoteConverter;
import com.lchli.lottery.util.QrcodeUtil;
import com.lchli.lottery.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/public/note")
public class NoteViewController {

    @Autowired
    private MongoTemplate mongoTemplate;


    @GetMapping("/view/{uid}")
    public String view(@PathVariable("uid") String uid, Model model) {

        Note note = mongoTemplate.findById(uid, Note.class);
        if (note == null) {
            model.addAttribute("error", "笔记不存在");
            return "error";
        }

        NoteModel noteModel = new NoteModel();
        noteModel.content = NoteConverter.convertNoteContentToHtml(note.content);
        noteModel.ShareUrl = buildShareUrl(uid);
        noteModel.thumbNail = note.thumbNail;
        noteModel.title = note.title;
        noteModel.type = note.type;
        noteModel.uid = note.uid;
        noteModel.updateTime = note.updateTime;
        noteModel.userId = note.userId;

        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "version"));

        Apk apk = mongoTemplate.findOne(query, Apk.class);

        if (apk != null && apk.fileId != null) {
            String apkUrl = Utils.buildFileDownloadUrl(apk.fileId);

            String base64 = QrcodeUtil.getBase64QRCode(apkUrl, 400, 400);

            model.addAttribute("apkBase64", "data:image/jpg;base64," + base64);
        } else {
            model.addAttribute("apkBase64", "");
        }

        model.addAttribute("note", noteModel);

        return "noteDetail";
    }


    private static String buildShareUrl(String noteId) {
        return String.format("%s/api/public/note/view/%s", Constants.HOST, noteId);
    }

}
