package com.example.demo.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.demo.config.MinIoClientConfig;
import com.example.demo.file.FileModel;
import com.example.demo.file.FileService;
import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import io.minio.messages.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.*;

@CrossOrigin(value = "*", maxAge = 3600)
@RestController
@RequestMapping("api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private MinIoClientConfig minIoClientConfig;

    @GetMapping("/buckets/list")
    public ResponseEntity<List<Bucket>> getListBucket() {
        return ResponseEntity.ok().body(fileService.listBuckets());
    }

    @GetMapping("/bucket/objects/{bucketName}")
    public ResponseEntity<Iterable<Result<Item>>> getListObject(@PathVariable("bucketName") String bucketName) {
        return ResponseEntity.ok().body(fileService.listObjects(bucketName));
    }

    @GetMapping("view/{bucketName}/{dateDir}/{fileName}")
    public ResponseEntity<Object> getResource(@PathVariable String bucketName,
                                              @PathVariable String dateDir,
                                              @PathVariable String fileName) {
        if (bucketName == null || fileName == null || dateDir == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please fill correctly all parameters");
        }
        String objectName = dateDir + "/" + fileName;
        Tags tags = fileService.getObjectTags(bucketName, objectName);
        if (tags != null && tags.get() != null) {
            try {
                Map<String, String> maps = tags.get();
                return ResponseEntity.ok()
                        .header(CONTENT_DISPOSITION, "filename=" + maps.get(FileModel.name))
                        .header(CONTENT_TYPE, maps.get(FileModel.contentType))
                        .header(CONTENT_LENGTH, maps.get(FileModel.size))
                        .header("Connection", "close")
                        .body(IoUtil.readBytes(fileService.getObject(bucketName, objectName)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File Not Found");
    }

    @PostMapping("/upload")
    public JSONObject uploadResource(@RequestParam("file") MultipartFile file) {
        JSONObject result = JSONUtil.createObj();
        result.set("code", "fail");

        try {
            if (file != null && !file.isEmpty()) {
                Map<String, String> map = fileService.putObject(minIoClientConfig.getBucketName(), file);
                if (map != null) {
                    result.set("code", "success");
                    result.set("message", "upload successfully");
                    result.set(FileModel.url, map.get(FileModel.url));
                    result.set(FileModel.name, map.get(FileModel.name));
                    result.set(FileModel.oldName, Base64.decodeStr(map.get(FileModel.oldName), "UTF-8"));
                    result.set(FileModel.size, map.get(FileModel.size));
                    result.set(FileModel.uploadDate, map.get(FileModel.uploadDate));
                    result.set(FileModel.suffix, map.get(FileModel.suffix));
                    map = null;
                } else {
                    result.set("message", "upload unsuccessfully, please try again");
                }
            } else {
                result.set("message", "the file is empty");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result.set("message", ex.getMessage());
        }
        return result;
    }

    @GetMapping("download/{bucketName}/{dateDir}/{fileName}")
    public void downloadResource(@PathVariable String bucketName,
                                 @PathVariable String dateDir,
                                 @PathVariable String fileName,
                                 HttpServletResponse response) {
        if (bucketName == null || fileName == null || dateDir == null) {
            return;
        }
        String objectName = dateDir + "/" + fileName;
        fileService.downloadObject(bucketName, objectName, response);
    }

    @DeleteMapping("delete/{bucketName}/{dateDir}/{fileName}")
    public JSONObject deleteResource(@PathVariable String bucketName,
                                     @PathVariable String dateDir,
                                     @PathVariable String fileName) {
        JSONObject result = JSONUtil.createObj();
        result.set("code", "fail");
        if (bucketName == null || fileName == null || dateDir == null) {
            result.set("message", "Please fill correctly all parameters");
            return result;
        }
        String objectName = dateDir + "/" + fileName;
        boolean isDeleteSuccess = fileService.removeObject(bucketName, objectName);
        result.set("code", isDeleteSuccess ? "success" : "fail");
        result.set("message", isDeleteSuccess ? "delete successfully" : "delete failed");

        return result;
    }

}
