package com.lrchan.qootalk.presentation.api.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lrchan.qootalk.application.chat.dto.result.FileAttachmentQueryResult;
import com.lrchan.qootalk.application.chat.dto.result.DeleteFileAttachmentQueryResult;
import com.lrchan.qootalk.application.chat.dto.command.UploadFileAttachmentCommand;
import com.lrchan.qootalk.application.chat.port.in.DeleteFileAttachmentUsecase;
import com.lrchan.qootalk.application.chat.port.in.LoadFileAttachmentsUsecase;
import com.lrchan.qootalk.application.chat.port.in.UploadFileAttachmentUsecase;
import com.lrchan.qootalk.common.response.ApiResponse;
import com.lrchan.qootalk.common.response.PagedResponse;
import com.lrchan.qootalk.presentation.api.chat.dto.response.DeleteFileAttachmentResponse;
import com.lrchan.qootalk.presentation.api.chat.dto.response.FileAttachmentResponse;
import com.lrchan.qootalk.presentation.global.auth.AuthenticatedUserProvider;
import com.lrchan.qootalk.domain.chat.attachment.FileType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "파일 API")
public class FileController {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final DeleteFileAttachmentUsecase deleteFileAttachmentUsecase;
    private final LoadFileAttachmentsUsecase loadFileAttachmentsUsecase;
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

    @GetMapping
    @Operation(
        summary = "파일 목록 조회",
        description = "현재 로그인한 사용자가 접근 가능한 채팅방 파일 목록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<PagedResponse<FileAttachmentResponse>>> getFiles(
        @RequestParam Long roomId,
        @RequestParam(required = false) Long uploaderId,
        @RequestParam(required = false) FileType fileType,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Long requesterId = authenticatedUserProvider.getCurrentUserId();
        PagedResponse<FileAttachmentQueryResult> result = loadFileAttachmentsUsecase.load(
            new com.lrchan.qootalk.application.chat.dto.command.LoadFileAttachmentsCommand(
                requesterId,
                roomId,
                uploaderId,
                fileType,
                page,
                size
            )
        );
        PagedResponse<FileAttachmentResponse> response = PagedResponse.of(
            result.content().stream().map(FileAttachmentResponse::of).toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages()
        );
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @DeleteMapping("/{fileId}")
    @Operation(
        summary = "파일 삭제",
        description = "현재 로그인한 사용자가 채팅방 파일을 삭제합니다."
    )
    public ResponseEntity<ApiResponse<DeleteFileAttachmentResponse>> deleteFile(
        @PathVariable Long fileId,
        @RequestParam Long roomId
    ) {
        Long requesterId = authenticatedUserProvider.getCurrentUserId();
        DeleteFileAttachmentQueryResult result = deleteFileAttachmentUsecase.delete(
            new com.lrchan.qootalk.application.chat.dto.command.DeleteFileAttachmentCommand(requesterId, roomId, fileId)
        );
        return ResponseEntity.ok(ApiResponse.of(DeleteFileAttachmentResponse.of(result)));
    }
}
