package com.lrchan.qootalk.application.user.port.out;

import com.lrchan.qootalk.domain.user.User;

public interface SaveUserPort {
    User save(User user);
}
