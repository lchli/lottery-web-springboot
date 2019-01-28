package com.lchli.lottery.controller;

import com.lchli.lottery.model.BaseResponse;
import com.lchli.lottery.model.UploadFileResponse;
import com.lchli.lottery.service.FileService;
import com.lchli.lottery.util.Utils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/sec/file")
public class FileUploadController {

    @Autowired
    private FileService fileService;

    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile fileMetaData) {

        UploadFileResponse response = new UploadFileResponse();

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
