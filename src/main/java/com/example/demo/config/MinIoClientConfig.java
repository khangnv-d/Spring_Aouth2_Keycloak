package com.example.demo.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@Component
public class MinIoClientConfig {
    @Value("${spring.minio.url}")
    public String endPoint;
    @Value("${spring.minio.access-key}")
    public String accessKey;
    @Value("${spring.minio.secret-key}")
    public String secretKey;
    @Value("${spring.minio.secure}")
    public Boolean secure;
    @Value("${spring.minio.bucket}")
    public String bucketName;

    @Bean
    public MinioClient getMinioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endPoint)
                .credentials(accessKey, secretKey)
                .build();
        return minioClient;
    }
}
