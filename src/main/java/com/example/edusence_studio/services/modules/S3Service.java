package com.example.edusence_studio.services.modules;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    public String uploadPdf(MultipartFile file, String folder) {

        String key = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("application/pdf")
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload PDF to S3", e);
        }

        return getPublicUrl(key);
    }

    public String uploadPdfBytes(byte[] pdfBytes, String folder, String filename) {
        String key = folder + "/" + UUID.randomUUID() + "-" + filename;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("application/pdf")
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(pdfBytes)
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload PDF to S3", e);
        }

        return getPublicUrl(key);
    }

    public byte[] downloadPdf(String s3Url) throws IOException {
        // Extract key from S3 URL (handle both regional and global endpoints)
        String baseUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/";
        String globalUrl = "https://" + bucket + ".s3.amazonaws.com/";
        String key = s3Url.replace(baseUrl, "").replace(globalUrl, "");
        if (key.contains("?")) {
            key = key.substring(0, key.indexOf("?"));
        }

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request)) {
            return response.readAllBytes();
        }
    }

    private String getPublicUrl(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }
}
