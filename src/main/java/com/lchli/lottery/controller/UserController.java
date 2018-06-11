package com.lchli.lottery.controller;

import com.lchli.lottery.model.BaseReponse;
import com.lchli.lottery.model.UserResponse;
import com.lchli.lottery.model.entity.User;
import com.lchli.lottery.repo.UserRepo;
import com.lchli.lottery.util.EncryptUtils;
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


    @PostMapping(value = "/register", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public UserResponse register(@RequestParam(value = "userName", required = false) String name,
                                 @RequestParam(value = "userPwd", required = false) String pwd,
                                 @RequestParam(value = "userHeadUrl", required = false) String userHeadUrl,
                                 @RequestParam(value = "userContact", required = false) String userContact
    ) {

        UserResponse response = new UserResponse();

        try {

            if (Utils.isEmpty(name) || Utils.isEmpty(pwd)) {
                response.status = BaseReponse.RESPCODE_FAILE;
                response.message = "用户名或密码不能为空";
                return response;
            }
            User user = userRepo.findByName(name);
            if (user != null) {
                response.status = BaseReponse.RESPCODE_FAILE;
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

            userRepo.save(user);

            user.pwd = null;
            response.status = BaseReponse.RESPCODE_SUCCESS;
            response.data = user;

            return response;

        } catch (Throwable e) {
            e.printStackTrace();
            response.status = BaseReponse.RESPCODE_FAILE;
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(path = "/update", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public UserResponse update(@RequestParam(value = "userPwd", required = false) String pwd,
                               @RequestParam(value = "userId", required = false) String userId,
                               @RequestParam(value = "token", required = false) String token,
                               @RequestParam(value = "userHeadUrl", required = false) String userHeadUrl,
                               @RequestParam(value = "userContact", required = false) String userContact) {

        UserResponse response = new UserResponse();

        try {

            if (Utils.isEmpty(userId) || Utils.isEmpty(token)) {
                response.status = BaseReponse.RESPCODE_FAILE;
                response.message = "参数错误";
                return response;
            }

            User user = userRepo.findById(userId).orElse(null);
            if (user == null) {
                response.status = BaseReponse.RESPCODE_FAILE;
                response.message = "用户名不存在";
                return response;
            }

            if (!token.equals(user.token)) {
                response.status = BaseReponse.RESPCODE_FAILE;
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

            userRepo.save(user);

            user.pwd = null;//do not save pwd to app.
            response.status = BaseReponse.RESPCODE_SUCCESS;
            response.data = user;

            return response;

        } catch (Throwable e) {
            e.printStackTrace();
            response.status = BaseReponse.RESPCODE_FAILE;
            response.message = e.getMessage();
            return response;
        }
    }

    @GetMapping(path = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserResponse login(@RequestParam(value = "userName", required = false) String name,
                              @RequestParam(value = "userPwd", required = false) String pwd
    ) {
        UserResponse response = new UserResponse();

        try {

            if (Utils.isEmpty(name) || Utils.isEmpty(pwd)) {
                response.status = BaseReponse.RESPCODE_FAILE;
                response.message = "用户名或密码不能为空";
                return response;
            }

            User user = userRepo.findByNameAndPwd(name, pwd);
            if (user == null) {
                response.status = BaseReponse.RESPCODE_FAILE;
                response.message = "用户名或密码错误";
                return response;
            }

            user.token = Utils.uuid();

            userRepo.save(user);

            user.pwd = null;

            response.status = BaseReponse.RESPCODE_SUCCESS;
            response.data = user;

            return response;

        } catch (Throwable e) {
            e.printStackTrace();
            response.status = BaseReponse.RESPCODE_FAILE;
            response.message = e.getMessage();
            return response;
        }
    }

    @GetMapping(path = "/findById", produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserResponse findById(@RequestParam(value = "userId", required = false) String userId
    ) {

        UserResponse response = new UserResponse();

        try {

            if (StringUtils.isEmpty(userId)) {
                response.status = BaseReponse.RESPCODE_FAILE;
                response.message = "用户id不能为空";
                return response;
            }

            User user = userRepo.findById(userId).orElse(null);
            if (user == null) {
                response.status = BaseReponse.RESPCODE_FAILE;
                response.message = "用户名不存在";
                return response;
            }

            user.pwd = null;

            response.status = BaseReponse.RESPCODE_SUCCESS;
            response.data = user;

            return response;

        } catch (Throwable e) {
            e.printStackTrace();
            response.status = BaseReponse.RESPCODE_FAILE;
            response.message = e.getMessage();
            return response;
        }
    }
}
