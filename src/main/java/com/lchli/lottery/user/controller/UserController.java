package com.lchli.lottery.user.controller;

import com.lchli.lottery.BaseResponse;
import com.lchli.lottery.user.UserModelMapper;
import com.lchli.lottery.user.model.UserResponse;
import com.lchli.lottery.user.repo.UserRepo;
import com.lchli.lottery.user.repo.entity.User;
import com.lchli.lottery.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/sec/user")
public class UserController {

    @Autowired
    UserRepo userRepo;


    @PostMapping(path = "/register", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public UserResponse register(@RequestParam(value = "userName", required = false) String name,
                                 @RequestParam(value = "userPwd", required = false) String pwd,
                                 @RequestParam(value = "userHeadUrl", required = false) String userHeadUrl,
                                 @RequestParam(value = "userContact", required = false) String userContact,
                                 @RequestParam(value = "userNick", required = false) String userNick
    ) {

        UserResponse response = new UserResponse();
        response.status = BaseResponse.RESPCODE_FAIL;

        if (Utils.isEmpty(name) || Utils.isEmpty(pwd)) {
            response.message = "用户名或密码不能为空";
            return response;
        }

        User user = userRepo.findByName(name);
        if (user != null) {
            response.message = "用户名已经存在";
            return response;
        }

        user = new User();
        user.name = name;
        user.pwd = pwd;
        user.token = Utils.uuid();
        user.uid = Utils.uuid();
        user.headUrl = userHeadUrl;
        user.userContact = userContact;
        user.userNick = userNick;

        userRepo.save(user);

        response.status = BaseResponse.RESPCODE_SUCCESS;
        response.data = UserModelMapper.toUserModel(user);

        return response;

    }

    @PostMapping(path = "/update", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public UserResponse update(@RequestParam(value = "userPwd", required = false) String pwd,
                               @RequestParam(value = "sessionUserId", required = false) String sessionUserId,
                               @RequestParam(value = "sessionUserToken", required = false) String sessionUserToken,
                               @RequestParam(value = "userHeadUrl", required = false) String userHeadUrl,
                               @RequestParam(value = "userContact", required = false) String userContact,
                               @RequestParam(value = "userNick", required = false) String userNick
    ) {

        UserResponse response = new UserResponse();
        response.status = BaseResponse.RESPCODE_FAIL;

        if (Utils.isEmpty(sessionUserId) || Utils.isEmpty(sessionUserToken)) {
            response.message = "参数错误";
            return response;
        }

        User user = userRepo.findById(sessionUserId).orElse(null);
        if (user == null) {
            response.message = "用户名不存在";
            return response;
        }

        if (!sessionUserToken.equals(user.token)) {
            response.message = "token无效";
            return response;
        }

        if (!StringUtils.isEmpty(pwd)) {
            user.pwd = pwd;
        }

        if (!StringUtils.isEmpty(userHeadUrl)) {
            user.headUrl = userHeadUrl;
        }

        if (!StringUtils.isEmpty(userContact)) {
            user.userContact = userContact;
        }
        if (!StringUtils.isEmpty(userNick)) {
            user.userNick = userNick;
        }

        userRepo.save(user);

        response.status = BaseResponse.RESPCODE_SUCCESS;
        response.data = UserModelMapper.toUserModel(user);

        return response;

    }

    @GetMapping(path = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserResponse login(@RequestParam(value = "userName", required = false) String name,
                              @RequestParam(value = "userPwd", required = false) String pwd
    ) {
        UserResponse response = new UserResponse();
        response.status = BaseResponse.RESPCODE_FAIL;

        if (Utils.isEmpty(name) || Utils.isEmpty(pwd)) {
            response.message = "用户名或密码不能为空";
            return response;
        }

        User user = userRepo.findByNameAndPwd(name, pwd);
        if (user == null) {
            response.message = "用户名或密码错误";
            return response;
        }

        user.token = Utils.uuid();

        userRepo.save(user);

        response.status = BaseResponse.RESPCODE_SUCCESS;
        response.data = UserModelMapper.toUserModel(user);

        return response;

    }

    @GetMapping(path = "/findById", produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserResponse findById(@RequestParam(value = "userId", defaultValue = "") String userId,
                                 @RequestParam(value = "sessionUserToken", defaultValue = "") String token,
                                 @RequestParam(value = "sessionUserId", defaultValue = "") String currentUserId
    ) {

        UserResponse response = new UserResponse();
        response.status = BaseResponse.RESPCODE_FAIL;

        if (StringUtils.isEmpty(currentUserId)) {
            response.message = "currentUserId不能为空";
            return response;
        }

        if (StringUtils.isEmpty(token)) {
            response.message = "token不能为空";
            return response;
        }

        if (StringUtils.isEmpty(userId)) {
            response.message = "用户id不能为空";
            return response;
        }

        User currentUser = userRepo.findByUidAndToken(currentUserId, token);
        if (currentUser == null) {
            response.message = "token无效";
            return response;
        }

        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            response.message = "用户名不存在";
            return response;
        }

        response.status = BaseResponse.RESPCODE_SUCCESS;
        response.data = UserModelMapper.toUserModel(user);

        return response;

    }
}
