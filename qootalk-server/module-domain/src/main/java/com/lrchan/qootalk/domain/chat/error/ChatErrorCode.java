package com.lrchan.qootalk.domain.chat.error;

import com.lrchan.qootalk.common.error.ErrorCode;

public enum ChatErrorCode implements ErrorCode {
    
    CHAT_ROOM_NOT_FOUND("CHAT_001", "채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_ALREADY_EXISTS("CHAT_002", "이미 존재하는 채팅방입니다."),
    CHAT_ROOM_INVALID_NAME("CHAT_003", "채팅방 이름이 올바르지 않습니다."),
    CHAT_ROOM_INVALID_TYPE("CHAT_004", "채팅방 타입이 올바르지 않습니다."),
    CHAT_ROOM_INVALID_CREATED_BY("CHAT_005", "채팅방 생성자가 올바르지 않습니다."),
    CHAT_ROOM_INVALID_UPDATED_BY("CHAT_006", "채팅방 수정자가 올바르지 않습니다."),
    CHAT_ROOM_INVALID_DELETED_BY("CHAT_007", "채팅방 삭제자가 올바르지 않습니다."),
    CHAT_ROOM_INVALID_DELETED_AT("CHAT_008", "채팅방 삭제 시간이 올바르지 않습니다.");

    private final String code;
    private final String message;

    ChatErrorCode(String code, String message) {
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
