package com.lchli.lottery.file.controller;

import com.lchli.lottery.BaseResponse;
import com.lchli.lottery.file.model.UploadFileResponse;
import com.lchli.lottery.file.service.FileService;
import com.lchli.lottery.user.repo.entity.User;
import com.lchli.lottery.util.Utils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/sec/file")
public class FileUploadController {

    @Autowired
    private FileService fileService;
    @Autowired
    private MongoTemplate mongoTemplate;


    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile fileMetaData,
                                         @RequestParam(value = "sessionUserId", required = false) String userId,
                                         @RequestParam(value = "sessionUserToken", required = false) String userToken) {

        UploadFileResponse response = new UploadFileResponse();

        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(userToken)) {
            response.message = "参数不合法";
            return response;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("uid").is(userId));
        query.addCriteria(Criteria.where("token").is(userToken));

        User u = mongoTemplate.findOne(query, User.class);
        if (u == null) {
            response.message = "用户token无效";
            return response;
        }


        ObjectId id = fileService.saveFile(fileMetaData);

        if (id == null) {
            response.status = BaseResponse.RESPCODE_FAIL;
            return response;
        }

        response.status = BaseResponse.RESPCODE_SUCCESS;
        response.data = Utils.buildFileDownloadUrl(id.toString());

        return response;
    }


}
