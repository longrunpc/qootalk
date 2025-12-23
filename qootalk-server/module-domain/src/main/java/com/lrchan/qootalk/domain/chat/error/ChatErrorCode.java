package com.lrchan.qootalk.domain.chat.error;

import com.lrchan.qootalk.common.error.ErrorCode;

public enum ChatErrorCode implements ErrorCode {
    
    CHAT_ROOM_NOT_FOUND("CHAT_001", "채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_ALREADY_EXISTS("CHAT_002", "이미 존재하는 채팅방입니다."),
    CHAT_ROOM_INVALID_NAME("CHAT_003", "채팅방 이름이 올바르지 않습니다."),
    CHAT_ROOM_INVALID_TYPE("CHAT_004", "채팅방 타입이 올바르지 않습니다."),
    CHAT_ROOM_PARTICIPANT_INVALID_LAST_READ_MESSAGE_ID("CHAT_005", "마지막 읽은 메시지 ID가 올바르지 않습니다."),

    CHAT_FILE_METADATA_INVALID_STORAGE_TYPE("CHAT_006", "파일 메타데이터 스토리지 타입이 올바르지 않습니다."),
    CHAT_FILE_METADATA_INVALID_FILE_NAME("CHAT_007", "파일 이름이 올바르지 않습니다."),
    CHAT_FILE_METADATA_INVALID_CONTENT_TYPE("CHAT_008", "파일 메타데이터 콘텐츠 타입이 올바르지 않습니다."),
    CHAT_FILE_METADATA_INVALID_FILE_SIZE("CHAT_009", "파일 크기가 올바르지 않습니다."),
    CHAT_FILE_SECURITY_INVALID_MALICIOUS_FILE_DOWNLOADABLE_OR_SHAREABLE("CHAT_010", "악성 파일은 다운로드 및 공유가 불가능합니다."),
    CHAT_FILE_SECURITY_INVALID_PUBLIC_FILE_DOWNLOADABLE("CHAT_011", "공유 가능한 파일은 다운로드가 가능합니다."),
    CHAT_FILE_METADATA_INVALID_PATH("CHAT_012", "파일 경로가 올바르지 않습니다.");

    private final String code;
    private final String message;

    ChatErrorCode(String code, String message) {;
        this.code = code;
        this.message = message;
    }
    
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
