package com.lchli.lottery.controller;

import com.lchli.lottery.model.BaseReponse;
import com.lchli.lottery.model.UploadFileResponse;
import com.lchli.lottery.util.Constants;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("file")
public class FileRes {


    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private MongoDbFactory mongoDbFactory;


    @GetMapping(path = "/download/{filename}")
    public ResponseEntity<Resource> downloadApkFile(@PathVariable("filename") final String filename) {

        GridFsResource gridFsResource = find(filename);
        if (gridFsResource == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(gridFsResource.getContentType())).header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + gridFsResource.getFilename() + "\"").body(gridFsResource);

    }


    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile fileMetaData) {

        UploadFileResponse response = new UploadFileResponse();

        try {

            String fname = fileMetaData.getOriginalFilename();

            InputStream ins = fileMetaData.getInputStream();
            ObjectId id = gridFsTemplate.store(ins, fname, fileMetaData.getContentType());
            ins.close();

            response.status = BaseReponse.RESPCODE_SUCCESS;

            response.data = String.format("http://%s/file/download/%s", Constants.HOST, id.toString());

        } catch (Throwable e) {
            e.printStackTrace();
            response.status = BaseReponse.RESPCODE_FAILE;
            response.message = e.getMessage();
        }

        return response;
    }

    private GridFsResource find(String fileId) {
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        if (file == null) {
            return null;
        }

        return new GridFsResource(file, getGridFs().openDownloadStream(file.getObjectId()));
    }

    private GridFSBucket getGridFs() {

        MongoDatabase db = mongoDbFactory.getDb("fileDB");
        return GridFSBuckets.create(db);
    }

}
