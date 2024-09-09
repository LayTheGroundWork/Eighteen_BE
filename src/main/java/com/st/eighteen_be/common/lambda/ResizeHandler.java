package com.st.eighteen_be.common.lambda;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class ResizeHandler implements RequestHandler<S3Event, String> {

    private static final float MAX_HEIGHT = 60;
    private final String JPG_TYPE =  "jpg";
    private final String JPG_MIME =  "image/jpeg";
    private final String JPEG_TYPE = "jpeg";
    private final String JPEG_MIME = "image/jpeg";
    private final String PNG_TYPE =  "png";
    private final String PNG_MIME =  "image/png";
    private final String GIF_TYPE =  "gif";
    private final String GIF_MIME =  "image/gif";

    public String handleRequest(S3Event s3event, Context context) {
        LambdaLogger logger = context.getLogger();
        try {
            S3EventNotificationRecord record = s3event.getRecords().get(1);
            String srcBucket = record.getS3().getBucket().getName(); // 원본 버킷 이름
            String key = record.getS3().getObject().getUrlDecodedKey(); // 객체의 키 (파일 경로 및 이름)
            String dstBucket = "eighteen-resize-bucket"; // 수정된 저장될 버킷 이름


            // 파일 확장자 추출
            Matcher matcher = Pattern.compile(".*\\.([^\\.]*)").matcher(key);
            if (!matcher.matches()) {
                logger.log("Unable to infer image type for key " + key);
                return "";
            }
            String imageType = matcher.group(1);
            // 지원하지 않는 이미지 형식인 경우 로그를 남기고 리턴
            if (!(JPG_TYPE.equals(imageType)) && !(JPEG_TYPE.equals(imageType)) && !(PNG_TYPE.equals(imageType)) && !(GIF_TYPE.equals(imageType))) {
                logger.log("Skipping non-image " + key);
                return "";
            }

            // S3에서 이미지 가져오기
            AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(srcBucket, key));
            InputStream objectData = s3Object.getObjectContent();

            // 이미지 리사이징 처리
            BufferedImage srcImage = ImageIO.read(objectData);
            int srcHeight = srcImage.getHeight();
            int srcWidth = srcImage.getWidth();
            int width = (int) (srcWidth * (MAX_HEIGHT / srcHeight)); // 비율에 맞춰 너비 계산
            int height = (int) MAX_HEIGHT;

            // 새 이미지 버퍼 생성 및 그래픽 설정
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.setPaint(Color.white);
            g.fillRect(0, 0, width, height);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(srcImage, 0, 0, width, height, null);
            g.dispose();

            // 바이트 배열로 변환
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, imageType, os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(os.size()); // 메타데이터 설정
            meta.setContentType(getMimeType(imageType)); // MIME 타입 설정

            // 리사이즈된 이미지를 S3에 저장
            logger.log("Writing to: " + dstBucket + "/" + key);
            try {
                s3Client.putObject(new PutObjectRequest(dstBucket, key, is, meta).withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (AmazonServiceException e) {
                logger.log(e.getErrorMessage());
                System.exit(1);
            }
            logger.log("Successfully resized " + srcBucket + "/" + key + " and uploaded to " + dstBucket + "/" + key);
            return "Ok";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // MIME 타입을 반환하는 보조 메소드
    private String getMimeType(String imageType) {
        return switch (imageType) {
            case JPG_TYPE, JPEG_TYPE -> JPG_MIME;
            case PNG_TYPE -> PNG_MIME;
            case GIF_TYPE -> GIF_MIME;
            default -> "";
        };
    }
}
