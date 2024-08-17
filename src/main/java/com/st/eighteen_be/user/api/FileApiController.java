package com.st.eighteen_be.user.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.user.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Tag(name = "미디어파일 API", description = "미디어파일 API")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class FileApiController {

    private final S3Service s3Service;

    @Operation(summary = "미디어 파일 업로드", description = "미디어 파일 업로드")
    @PostMapping("/v1/api/file/upload")
    public ApiResp<List<String>> fileUpload(@RequestPart("files") List<MultipartFile> files,
                                                   @RequestParam("uniqueId") String uniqueId) throws IOException {

        return ApiResp.success(HttpStatus.OK, s3Service.upload(files,uniqueId));
    }

    @Operation(summary = "미디어 파일 삭제", description = "미디어 파일 삭제")
    @DeleteMapping("/v1/api/file/delete")
    public ApiResp<String> fileDelete(@RequestParam("key") String key){
        s3Service.delete(key);
        return ApiResp.success(HttpStatus.OK, "Delete Complete");
    }

    @Operation(summary = "미디어 파일 조회", description = "미디어 파일 조회")
    @GetMapping("/v1/api/file/view")
    public ApiResp<String> fileView(@RequestParam("key") String key) {
        return ApiResp.success(HttpStatus.OK,s3Service.getPreSignedURL(key));
    }

    @Operation(summary = "미디어 파일 전체 조회", description = "미디어 파일 전체 조회")
    @GetMapping("/v1/api/file/view-all")
    public ApiResp<List<String>> fileTotalView(@RequestParam("uniqueId") String uniqueId) {
        return ApiResp.success(HttpStatus.OK,s3Service.getPreSignedURLsForFolder(uniqueId));
    }
}
