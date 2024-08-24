package com.st.eighteen_be.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.ArrayList;
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

    public List<String[]> generateUploadPreSignedUrls(List<String> originalFilenames, String uniqueId) {

        List<String[]> preSignedUrlAndKeys = new ArrayList<>();

        for (String originalFilename : originalFilenames) {
            String[] response = generateUploadPreSignedUrl(originalFilename, uniqueId);

            preSignedUrlAndKeys.add(response);
        }

        return preSignedUrlAndKeys;
    }

    public String[] generateUploadPreSignedUrl(String originalFilename, String uniqueId) {
        try {
            // Generate a unique filename using the original filename and a UUID
            String storeFilename = generateStoreFilename(originalFilename);

            // Construct the S3 key using the user's unique ID and the generated filename
            String s3Key = uniqueId + "/" + storeFilename;

            // Create a PUT object request with metadata such as content type
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            // Create an S3Presigner instance
            S3Presigner preSigner = createS3Presigner();

            // Create a pre-signed URL for the PUT request
            PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))  // Set the duration for which the URL is valid
                    .putObjectRequest(putObjectRequest)
                    .build();

            // Generate the pre-signed URL
            PresignedPutObjectRequest presignedPutObjectRequest = preSigner.presignPutObject(putObjectPresignRequest);
            preSigner.close();

            return new String[]{presignedPutObjectRequest.url().toString(), storeFilename};

        } catch (Exception e) {
            log.error("Error generating preSigned URL for upload", e);
            return new String[]{"Error generating preSigned URL",e.toString()};
        }
    }

    // 기존 S3Presigner 생성 메서드
    private S3Presigner createS3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    // UUID를 이용해 저장 파일명을 생성하는 메서드
    private String generateStoreFilename(String originalFilename) {
        String ext = extractExt(originalFilename);
        return UUID.randomUUID() + "." + ext;
    }

    // 확장자를 추출하는 메서드
    private String extractExt(String filename) {
        int pos = filename.lastIndexOf(".");
        return (pos > 0) ? filename.substring(pos + 1) : "";
    }

    /* 2. 파일 삭제 */
    public void delete(String storeFilename, String uniqueId) {
        try {
            String s3Key = uniqueId + "/" + storeFilename;
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
        }
    }

    /* 3. 파일의 preSigned URL 반환 */
    public String getPreSignedURL(String storeFilename, String uniqueId) {
        try {
            String s3Key = uniqueId + "/" + storeFilename;
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
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
}
