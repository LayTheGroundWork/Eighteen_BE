package com.st.eighteen_be.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    @Value("${cloud.aws.bucketName}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String awsRegion;  // 리전 정보를 가져옴

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;  // 액세스 키

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;  // 시크릿 키

    private final S3Client s3Client;

    public List<String> upload(List<MultipartFile> multipartFiles, String uniqueId) throws IOException {

        if (multipartFiles.isEmpty()) return Collections.emptyList();

        List<String> urls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String originalFilename = multipartFile.getOriginalFilename();
            String storeFilename = generateStoreFilename(originalFilename);

            // 회원 아이디로 폴더 생성
            String s3Key = uniqueId + "/" + storeFilename;

            // 메타데이터 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(multipartFile.getContentType())
                    .build();

            try {
                s3Client.putObject(putObjectRequest,
                        RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
            } catch (S3Exception e) {
                // S3 예외 처리
                throw new IOException("Failed to upload file to S3", e);
            }

            urls.add(s3Key);
        }

        // 업로드된 파일의 URL 반환
        return urls;
    }

    private String generateStoreFilename(String originalFilename) {
        String ext = extractExt(originalFilename);
        return UUID.randomUUID() + "." + ext;
    }

    private String extractExt(String filename) {
        int pos = filename.lastIndexOf(".");
        return (pos > 0) ? filename.substring(pos + 1) : "";
    }

    /* 2. 파일 삭제 */
    public void delete(String keyName) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
        }
    }

    /* 3. 파일의 preSigned URL 반환 */
    public String getPreSignedURL(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            S3Presigner preSigner = createS3Presigner();  // S3Presigner 생성

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(2))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest = preSigner.presignGetObject(getObjectPresignRequest);

            preSigner.close();  // 사용 후 S3Presigner 리소스 해제

            return presignedGetObjectRequest.url().toString();

        } catch (Exception e) {
            log.error(e.toString());
            return "Error generating preSigned URL";
        }
    }

    /* 4. 폴더의 preSigned URL들 반환 */
    public List<String> getPreSignedURLsForFolder(String folder) {

        List<String> preSignedUrls = new ArrayList<>();

        try {
            // S3에서 해당 폴더의 모든 객체 리스트 가져오기
            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(folder + "/")
                    .build();

            ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

            S3Presigner preSigner = createS3Presigner();  // S3Presigner 생성

            // 각 객체에 대해 preSigned URL 생성
            for (S3Object s3Object : listObjectsResponse.contents()) {
                String keyName = s3Object.key();

                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build();

                GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(2))
                        .getObjectRequest(getObjectRequest)
                        .build();

                PresignedGetObjectRequest presignedGetObjectRequest = preSigner.presignGetObject(getObjectPresignRequest);
                preSignedUrls.add(presignedGetObjectRequest.url().toString());
            }

            preSigner.close();  // 사용 후 S3Presigner 리소스 해제

        } catch (S3Exception e) {
            log.error("Failed to get preSigned URLs for folder: {}", folder, e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while getting preSigned URLs", e);
        }

        return preSignedUrls;
    }

    // S3Presigner를 생성하는 메서드
    private S3Presigner createS3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}
