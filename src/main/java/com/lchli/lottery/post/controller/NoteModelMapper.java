package com.lchli.lottery.post.controller;

import com.lchli.lottery.post.model.NoteModel;
import com.lchli.lottery.post.repo.entity.Note;
import com.lchli.lottery.user.repo.entity.User;
import com.lchli.lottery.util.Constants;
import org.springframework.data.mongodb.core.MongoTemplate;

public class NoteModelMapper {

    public static NoteModel toModel(Note note, MongoTemplate mongoTemplate) {
        NoteModel noteModel = new NoteModel();
        noteModel.content = note.content;
        noteModel.title = note.title;
        noteModel.type = note.type;
        noteModel.uid = note.uid;
        noteModel.updateTime = note.updateTime;
        noteModel.userId = note.userId;
        noteModel.star = note.star;
        noteModel.isPublic = note.isPublic;
        noteModel.shareUrl = buildShareUrl(note.uid);

        User user = mongoTemplate.findById(note.userId, User.class);
        if (user != null) {
            noteModel.userHeadUrl = user.headUrl;
            noteModel.userName = user.name;
        }

        return noteModel;
    }


    private static String buildShareUrl(String noteId) {
        return String.format("%s/api/public/note/view/%s", Constants.HOST, noteId);
    }
}
