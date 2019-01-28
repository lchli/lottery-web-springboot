package com.lchli.lottery.file.controller;

import com.lchli.lottery.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/public/file")
public class FileDownloadController {

    @Autowired
    private FileService fileService;


    @GetMapping(path = "/download/{fileId}")
    public ResponseEntity<Resource> downloadApkFile(@PathVariable("fileId") final String fileId) {

        GridFsResource gridFsResource = fileService.getFile(fileId);
        if (gridFsResource == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(gridFsResource.getContentType())).header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + gridFsResource.getFilename() + "\"").body(gridFsResource);

    }

}
