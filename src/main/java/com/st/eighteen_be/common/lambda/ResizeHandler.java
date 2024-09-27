package com.st.eighteen_be.common.lambda;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResizeHandler {

    private static final float MAX_HEIGHT = 60;
    private final String JPG_TYPE =  "jpg";
    private final String JPEG_TYPE = "jpeg";
    private final String PNG_TYPE =  "png";
    private final String GIF_TYPE =  "gif";
    private final String MP4_TYPE = "mp4";
    private final String AVI_TYPE = "avi";
    private final String MKV_TYPE = "mkv";
    private final String MOV_TYPE = "mov";

    public String handleRequest(Map<String, Object> event) {

        try (S3Client s3Client = S3Client.builder().build()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode records = mapper.convertValue(event.get("Records"), JsonNode.class);
            JsonNode record = records.get(0);
            JsonNode s3 = record.get("s3");
            JsonNode bucket = s3.get("bucket");
            JsonNode objectNode = s3.get("object");
            String srcBucket = bucket.get("name").asText();
            String key = objectNode.get("key").asText();
            String dstBucket = "eighteen-resize-bucket";

            System.out.println("Processing object with key: " + key);
            System.out.println("Source bucket: " + srcBucket);
            System.out.println("Destination bucket: " + dstBucket);


            Matcher matcher = Pattern.compile(".*\\.([^.]*)").matcher(key);
            if (!matcher.matches()) {
                System.err.println("Unable to infer media type for key " + key);
                return "Failed";
            }
            String mediaType = matcher.group(1).toLowerCase();

            if (!isSupportedMediaType(mediaType)) {
                System.err.println("Skipping non-image " + key);
                return "Failed";
            }

            InputStream objectData = s3Client.getObject(GetObjectRequest.builder().bucket(srcBucket).key(key).build());

            BufferedImage srcImage = ImageIO.read(objectData);
            if (srcImage == null) {
                System.err.println("Unable to read media for key " + key);
                return "Failed";
            }

            int srcHeight = srcImage.getHeight();
            int srcWidth = srcImage.getWidth();
            int width = (int) (srcWidth * (MAX_HEIGHT / srcHeight));
            int height = (int) MAX_HEIGHT;

            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.setPaint(Color.WHITE);
            g.fillRect(0, 0, width, height);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(srcImage, 0, 0, width, height, null);
            g.dispose();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, mediaType, os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());

            System.out.println("Writing to: " + dstBucket + "/" + key);

            putResizedImageToS3(s3Client, dstBucket, key, is, os.size(), mediaType);

            System.out.println("Successfully resized " + srcBucket + "/" + key + " and uploaded to " + dstBucket + "/" + key);
            return "Ok";
        } catch (IOException | SdkException e) {
            System.err.println("Error processing media: " + e.getMessage());
            return "Failed";
        }
    }

    private boolean isSupportedMediaType(String mediaType) {
        System.out.println("Checking media type: " + mediaType);  // 디버깅 로그 추가

        return JPG_TYPE.equals(mediaType) || JPEG_TYPE.equals(mediaType) ||
                PNG_TYPE.equals(mediaType) || GIF_TYPE.equals(mediaType) ||
                MP4_TYPE.equals(mediaType) || AVI_TYPE.equals(mediaType) ||
                MKV_TYPE.equals(mediaType) || MOV_TYPE.equals(mediaType);
    }

    private void putResizedImageToS3(S3Client s3Client, String bucket, String key, InputStream is, long contentLength, String imageType) {
        try {
            String contentType = getContentType(imageType);

            PutObjectResponse putObjectResponse = s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(contentType)  // Content-Type 추가
                            .build(),
                    RequestBody.fromInputStream(is, contentLength)
            );
            System.out.println("S3 putObject response: " + putObjectResponse);
        } catch (SdkException e) {
            System.err.println("Failed to upload resized media to S3: " + e.getMessage());
            throw new RuntimeException("Failed to put object in S3", e);
        }
    }

    private String getContentType(String imageType) {
        return switch (imageType) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            default -> "application/octet-stream"; // 기본값
        };
    }
}
