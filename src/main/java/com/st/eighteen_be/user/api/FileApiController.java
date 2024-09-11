package com.st.eighteen_be.user.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.user.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@Tag(name = "미디어파일 API", description = "미디어파일 API")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class FileApiController {

    private final S3Service s3Service;

    // TODO: 썸네일용 이미지 조회 및 원본 사진 삭제 시 리사이징 사진도 같이 지워져야함
    @Operation(summary = "preSignedUrl & 접근 key 생성", description = "s3에 이미지 업로드를 위한 url 및 접근 key 생성")
    @PostMapping("/v1/api/file/upload")
    public ApiResp<List<String[]>> fileUpload(@RequestParam("fileNames") List<String> fileNames,
                                            @RequestParam("uniqueId") String uniqueId) throws IOException {

        return ApiResp.success(HttpStatus.OK, s3Service.generateUploadPreSignedUrls(fileNames,uniqueId));
    }

    @Operation(summary = "접근 key로 미디어 파일 삭제", description = "접근 key로 미디어 파일 삭제")
    @DeleteMapping("/v1/api/file/delete")
    public ApiResp<String> fileDelete(@RequestParam("key") String key, @RequestParam("uniqueId") String uniqueId){
        s3Service.delete(key,uniqueId);
        return ApiResp.success(HttpStatus.OK, "Delete Complete");
    }

    @Operation(summary = "접근 key로 미디어 파일 조회", description = "접근 key로 미디어 파일 조회")
    @GetMapping("/v1/api/file/view")
    public ApiResp<String> fileView(@RequestParam("key") String key, @RequestParam("uniqueId") String uniqueId) {
        return ApiResp.success(HttpStatus.OK,s3Service.getPreSignedURL(key,uniqueId));
    }

    @Operation(summary = "폴더명으로 미디어 파일 전체 조회", description = "폴더명으로 미디어 파일 전체 조회")
    @GetMapping("/v1/api/file/view-all")
    public ApiResp<List<String>> fileTotalView(@RequestParam("uniqueId") String uniqueId) {
        return ApiResp.success(HttpStatus.OK,s3Service.getPreSignedURLsForFolder(uniqueId));
    }
}
