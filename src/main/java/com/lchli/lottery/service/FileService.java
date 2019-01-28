package com.lchli.lottery.service;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FileService {

    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private MongoDbFactory mongoDbFactory;


    public ObjectId saveFile(MultipartFile fileMetaData) {
        InputStream ins = null;

        try {
            String fname = fileMetaData.getOriginalFilename();

            ins = fileMetaData.getInputStream();
            ObjectId id = gridFsTemplate.store(ins, fname, fileMetaData.getContentType());
            ins.close();

            return id;

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;

    }

    public GridFsResource getFile(String fileId) {
        return find(fileId);
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
