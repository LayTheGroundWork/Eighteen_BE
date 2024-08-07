//package com.st.eighteen_be.config.s3;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import software.amazon.awssdk.auth.credentials.AwsCredentials;
//import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.regions.providers.AwsRegionProvider;
//import software.amazon.awssdk.services.s3.S3Client;
//
//@Configuration
//public class S3Config {
//
//    @Value("${cloud.aws.credentials.accessKey}")
//    private String accessKey;
//
//    @Value("${cloud.aws.credentials.secretKey}")
//    private String secretKey;
//
//    @Value("${cloud.aws.region.static}")
//    private String region;
//
//    @Bean
//    @Primary
//    public AwsCredentialsProvider customAwsCredentialsProvider() {
//        return () -> new AwsCredentials() {
//            @Override
//            public String accessKeyId() {
//                return accessKey;
//            }
//
//            @Override
//            public String secretAccessKey() {
//                return secretKey;
//            }
//        };
//    }
//
//    @Bean
//    @Primary
//    public S3Client s3Client() {
//        return S3Client.builder()
//                .credentialsProvider(customAwsCredentialsProvider())
//                .region(customAwsRegionProvider().getRegion())
//                .build();
//    }
//
//    @Bean
//    public AwsRegionProvider customAwsRegionProvider() {
//        return () -> Region.of(region);
//    }
//}
