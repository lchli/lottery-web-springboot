package com.lchli.lottery.controller;

import com.lchli.lottery.model.ApkResponse;
import com.lchli.lottery.model.entity.Admin;
import com.lchli.lottery.model.entity.Apk;
import com.lchli.lottery.service.FileService;
import com.lchli.lottery.util.Utils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.lchli.lottery.model.BaseReponse.RESPCODE_FAILE;
import static com.lchli.lottery.model.BaseReponse.RESPCODE_SUCCESS;

@Controller
@RequestMapping("api/public/apk")
public class ApkController {

    @Autowired
    private FileService fileService;

    @Autowired
    private MongoTemplate mongoTemplate;


    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadFile(@RequestParam("file") MultipartFile fileMetaData,
                             @RequestParam(value = "name", required = false) String name,
                             @RequestParam(value = "version", required = false) int version,
                             @RequestParam(value = "userName", required = false) String userName,
                             @RequestParam(value = "userPwd", required = false) String userPwd,
                             Model model) {

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(userPwd)) {
            model.addAttribute("result", 0);
            model.addAttribute("msg", "用户验证失败！");
            return "uploadApkResult";
        }
        if (StringUtils.isEmpty(name)) {
            model.addAttribute("result", 0);
            model.addAttribute("msg", "app name不能为空！");
            return "uploadApkResult";
        }
        if (StringUtils.isEmpty(version)) {
            model.addAttribute("result", 0);
            model.addAttribute("msg", "app version不能为空！");
            return "uploadApkResult";
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(userName));
        query.addCriteria(Criteria.where("pwd").is(userPwd));

        Admin result = mongoTemplate.findOne(query, Admin.class);
        if (result == null) {
            model.addAttribute("result", 0);
            model.addAttribute("msg", "用户验证失败！");
            return "uploadApkResult";
        }

        ObjectId id = fileService.saveFile(fileMetaData);

        if (id == null || fileMetaData.isEmpty()) {
            model.addAttribute("result", 0);
            model.addAttribute("msg", "文件保存失败！");
            return "uploadApkResult";
        }

        Apk apk = new Apk();
        apk.name = name;
        apk.version = version;
        apk.uid = name + "-" + version;
        apk.fileId = id.toString();

        mongoTemplate.save(apk);

        String fileUrl = Utils.buildFileDownloadUrl(id.toString());

        model.addAttribute("fileUrl", fileUrl);
        model.addAttribute("result", 1);

        return "uploadApkResult";
    }

    @ResponseBody
    @GetMapping(path = "/update")
    public ApkResponse uploadFile(@RequestParam(value = "currentVersionCode", required = false) int currentVersionCode
    ) {
        ApkResponse apkResponse = new ApkResponse();
        apkResponse.status = RESPCODE_SUCCESS;

        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "version"));

        Apk apk = mongoTemplate.findOne(query, Apk.class);

        if (apk != null) {

            if (apk.version > currentVersionCode) {
                return apkResponse;
            }
        }

        apkResponse.status = RESPCODE_FAILE;
        apkResponse.message = "暂无更新";

        return apkResponse;
    }

}
