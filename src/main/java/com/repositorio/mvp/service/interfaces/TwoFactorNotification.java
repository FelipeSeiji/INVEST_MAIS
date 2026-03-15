package com.repositorio.mvp.service.interfaces;

import com.repositorio.mvp.model.User;

public interface TwoFactorNotification {
    void sendTwoFactorCode(User user, String code);
}
