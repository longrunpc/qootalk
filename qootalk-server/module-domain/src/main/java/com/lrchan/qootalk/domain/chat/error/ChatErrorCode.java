package com.lrchan.qootalk.domain.chat.error;

import com.lrchan.qootalk.common.error.ErrorCode;

public enum ChatErrorCode implements ErrorCode {
    
    CHAT_ROOM_NOT_FOUND("CHAT_001", "채팅방을 찾을 수 없습니다.", 404),
    CHAT_ROOM_DELETED("CHAT_002", "채팅방이 삭제되었습니다.", 404),
    CHAT_ROOM_ALREADY_EXISTS("CHAT_003", "이미 존재하는 채팅방입니다.", 400),
    CHAT_ROOM_INVALID_NAME("CHAT_004", "채팅방 이름이 올바르지 않습니다.", 400),
    CHAT_ROOM_INVALID_TYPE("CHAT_005", "채팅방 타입이 올바르지 않습니다.", 400),
    CHAT_ROOM_PARTICIPANT_INVALID_LAST_READ_MESSAGE_ID("CHAT_006", "마지막 읽은 메시지 ID가 올바르지 않습니다.", 404),
    CHAT_ROOM_PARTICIPANT_NOT_FOUND("CHAT_007", "채팅방 참여자를 찾을 수 없습니다.", 404),
    CHAT_ROOM_PARTICIPANT_DELETED("CHAT_008", "채팅방 참여자가 삭제되었습니다.", 404),
    CHAT_ROOM_PARTICIPANT_INVALID_ROLE("CHAT_009", "채팅방 참여자 권한이 올바르지 않습니다.", 400),

    CHAT_FILE_METADATA_INVALID_STORAGE_TYPE("CHAT_010", "파일 메타데이터 스토리지 타입이 올바르지 않습니다.", 400),
    CHAT_FILE_METADATA_INVALID_FILE_NAME("CHAT_011", "파일 이름이 올바르지 않습니다.", 400),
    CHAT_FILE_METADATA_INVALID_CONTENT_TYPE("CHAT_012", "파일 메타데이터 콘텐츠 타입이 올바르지 않습니다.", 400),
    CHAT_FILE_METADATA_INVALID_FILE_SIZE("CHAT_013", "파일 크기가 올바르지 않습니다.", 400),
    CHAT_FILE_SECURITY_INVALID_MALICIOUS_FILE_DOWNLOADABLE_OR_SHAREABLE("CHAT_014", "악성 파일은 다운로드 및 공유가 불가능합니다.", 400),
    CHAT_FILE_SECURITY_INVALID_PUBLIC_FILE_DOWNLOADABLE("CHAT_015", "공개 파일의 다운로드를 비활성화할 수 없습니다.", 400),
    CHAT_FILE_METADATA_INVALID_PATH("CHAT_016", "파일 경로가 올바르지 않습니다.", 400),
    CHAT_FILE_METADATA_INVALID_INPUT_STREAM("CHAT_017", "파일 입력 스트림이 올바르지 않습니다.", 400),
    CHAT_FILE_ATTACHMENT_NOT_FOUND("CHAT_018", "파일 첨부파일을 찾을 수 없습니다.", 404),
    CHAT_FILE_ATTACHMENT_DELETED("CHAT_019", "파일 첨부파일이 삭제되었습니다.", 404),
    CHAT_MESSAGE_NOT_FOUND("CHAT_020", "메시지를 찾을 수 없습니다.", 404),
    CHAT_MESSAGE_EMPTY_PAYLOAD("CHAT_021", "메시지 내용 또는 첨부파일이 필요합니다.", 400),
    CHAT_MESSAGE_INVALID_CONTENT("CHAT_022", "메시지 내용이 올바르지 않습니다.", 400),
    CHAT_MESSAGE_ATTACHMENT_REQUIRED("CHAT_023", "해당 메시지 타입에는 첨부파일이 필요합니다.", 400),
    CHAT_MESSAGE_INVALID_PARENT("CHAT_024", "부모 메시지가 현재 채팅방에 속하지 않습니다.", 400),
    CHAT_MESSAGE_ATTACHMENT_ROOM_MISMATCH("CHAT_025", "첨부파일이 현재 채팅방에 속하지 않습니다.", 400),
    CHAT_MESSAGE_ATTACHMENT_OWNER_MISMATCH("CHAT_026", "본인이 업로드한 첨부파일만 전송할 수 있습니다.", 400),
    CHAT_MESSAGE_TYPE_NOT_ALLOWED("CHAT_027", "사용자가 전송할 수 없는 메시지 타입입니다.", 400),
    CHAT_MESSAGE_MENTION_TARGET_NOT_FOUND("CHAT_028", "멘션 대상은 현재 채팅방 참여자여야 합니다.", 400),
    CHAT_MESSAGE_DUPLICATE_ATTACHMENT("CHAT_029", "중복된 첨부파일 ID는 전송할 수 없습니다.", 400),
    CHAT_MESSAGE_EDIT_FORBIDDEN("CHAT_030", "본인이 작성한 메시지만 수정할 수 있습니다.", 403),
    CHAT_MESSAGE_UPDATE_NOT_ALLOWED("CHAT_031", "해당 메시지 타입은 수정할 수 없습니다.", 400),
    CHAT_MESSAGE_DELETE_FORBIDDEN("CHAT_032", "본인이 작성한 메시지만 삭제할 수 있습니다.", 403),
    CHAT_MESSAGE_DELETE_NOT_ALLOWED("CHAT_033", "해당 메시지 타입은 삭제할 수 없습니다.", 400);

    private final String code;
    private final String message;
    private final int httpStatus;

    ChatErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
    
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }
}
