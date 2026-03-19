package com.lrchan.qootalk.presentation.api.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lrchan.qootalk.application.chat.dto.command.UploadFileAttachmentCommand;
import com.lrchan.qootalk.application.chat.dto.result.FileAttachmentQueryResult;
import com.lrchan.qootalk.application.chat.port.in.UploadFileAttachmentUsecase;
import com.lrchan.qootalk.common.response.ApiResponse;
import com.lrchan.qootalk.presentation.api.chat.dto.response.FileAttachmentResponse;
import com.lrchan.qootalk.presentation.global.auth.AuthenticatedUserProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "파일 API")
public class FileController {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final UploadFileAttachmentUsecase uploadFileAttachmentUsecase;

    @PostMapping(consumes = "multipart/form-data")
    @Operation(
        summary = "파일 업로드",
        description = "현재 로그인한 사용자가 채팅방에 파일을 업로드합니다."
    )
    public ResponseEntity<ApiResponse<FileAttachmentResponse>> uploadFile(
        @RequestParam Long roomId,
        @RequestParam(required = false) Long messageId,
        @RequestPart("file") MultipartFile file
    ) throws Exception {
        Long requesterId = authenticatedUserProvider.getCurrentUserId();
        FileAttachmentQueryResult result = uploadFileAttachmentUsecase.upload(
            new UploadFileAttachmentCommand(
                requesterId,
                roomId,
                messageId,
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
            )
        );
        return ResponseEntity.ok(ApiResponse.of(FileAttachmentResponse.of(result)));
    }
}
