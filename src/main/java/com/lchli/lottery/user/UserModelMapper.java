package com.lchli.lottery.user;

import com.lchli.lottery.user.model.UserModel;
import com.lchli.lottery.user.repo.entity.User;

public class UserModelMapper {

    public static UserModel toUserModel(User user) {
        UserModel model = new UserModel();
        model.name = user.name;
        model.headUrl = user.headUrl;
        model.roles = user.roles;
        model.token = user.token;
        model.uid = user.uid;
        model.userContact = user.userContact;
        return model;
    }
}
