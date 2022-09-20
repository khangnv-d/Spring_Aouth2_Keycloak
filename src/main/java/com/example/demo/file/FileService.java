package com.example.demo.file;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.example.demo.apiexception.RequestException;
import com.example.demo.config.MinIoClientConfig;
import io.minio.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import io.minio.messages.Tags;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class FileService {

    private final MinioClient minioClient;
    private final MinIoClientConfig minIoConfig;
    private static final int DEFAULT_EXPIRY_TIME = 7 * 24 * 3600;

    public FileService(MinIoClientConfig minIoConfig) {
        this.minIoConfig = minIoConfig;
        this.minioClient = minIoConfig.getMinioClient();
    }

    @SneakyThrows
    public boolean isBucketExists(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    public boolean makeBucket(String bucketName) {
        try {
            boolean flag = isBucketExists(bucketName);
            // If the bucket does not exist, create a bucket
            if (!flag) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SneakyThrows
    public List<Bucket> listBuckets() {
        return minioClient.listBuckets();
    }

    public Iterable<Result<Item>> listObjects(String bucketName) {
        return minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
    }

//    public String uploadFile(MultipartFile file) {
//        // Create buckets
//        boolean isCreateBucketSuccess = makeBucket(minIoConfig.getBucketName());
//        if (!isCreateBucketSuccess) {
//            return "";
//        }
//
//        try {
//            String originalFilename = file.getOriginalFilename();
//            int pointIndex = originalFilename.lastIndexOf(".");
//
//            // Get the file stream
//            InputStream inputStream = file.getInputStream();
//            // Ensure that the file name does not duplicate
//            String objectName = minIoConfig.getBucketName() + DateUtil.format(new Date(), "_yyyyMMddHHmmss")
//                    + (pointIndex > -1 ? originalFilename.substring(pointIndex) : "");
//
//            minioClient.uploadObject(new UploadObjectArgs());
//            minioClient.putObject(PutObjectArgs.builder()
//                    .bucket(minIoConfig.getBucketName())
//                    .contentType(file.getContentType())
//                    .object(objectName)
//                    .stream(inputStream, inputStream.available(), -1)
//                    .build());
//
//        } catch (Exception e) {
//
//        }
//        return null;
//    }

    public Map<String, String> putObject(String bucketName, MultipartFile multipartFile) {
        return multipartFileUpload(bucketName, multipartFile, IdUtil.simpleUUID());
    }

    @SneakyThrows
    private Map<String, String> multipartFileUpload(String bucketName, MultipartFile multipartFile, String objectName) {

        long size = multipartFile.getSize();
        String contentType = multipartFile.getContentType();
        String oldName = multipartFile.getOriginalFilename();
        String suffix = oldName.substring(oldName.lastIndexOf("."));
        oldName = Base64.encode(oldName, "UTF-8");

        // Ensure that the file name does not duplicate
        String name = (objectName != null && objectName.length() > 0) ? objectName : oldName;
        String timePrefix = DateUtil.format(LocalDateTime.now(), "yyyyMM");
        name = timePrefix + "/" + name + suffix;

        Map<String, String> headers = new ConcurrentHashMap<>();
        headers.put(FileModel.size, size + "");
        headers.put(FileModel.name, name);
        headers.put(FileModel.oldName, oldName);
        headers.put(FileModel.contentType, contentType);
        headers.put(FileModel.suffix, suffix);
        headers.put(FileModel.uploadDate, DateUtil.now());

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(name)
                .contentType(contentType)
                .headers(headers)
                .tags(headers)
                .stream(multipartFile.getInputStream(), size, PutObjectOptions.MAX_PART_SIZE)
                .build());

        headers.put(FileModel.url, bucketName + "/" + name);

        return headers;
    }

    public Tags getObjectTags(String bucketName, String objectName) {
        Tags tags = null;
        try {
            tags = minioClient.getObjectTags(
                    GetObjectTagsArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            throw new RequestException(e.getMessage());
        }
        return tags;
    }

    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName) {
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
        return stream;
    }
}
