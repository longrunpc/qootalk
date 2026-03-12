package com.lrchan.qootalk.application.chat.port.out;

import com.lrchan.qootalk.domain.chat.message.Message;

public interface SaveMessagePort {
    Message save(Message message);
}
