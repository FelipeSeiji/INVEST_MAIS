package com.repositorio.mvp.domain.auth.service.interfaces;

import com.repositorio.mvp.domain.user.model.User;

public interface TwoFactorNotification {
    void sendTwoFactorCode(User user, String code);
}
