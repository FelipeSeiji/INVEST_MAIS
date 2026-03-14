package com.repositorio.mvp.service;

import org.springframework.stereotype.Service;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

@Service
public class TotpService {

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public String generateSecret() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public boolean verifyCode(String secret, int code) {
        return gAuth.authorize(secret, code);
    }

    public String getQrCodeUrl(String email, String secret) {
        String issuer = "totp-auth";
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", 
                issuer, email, secret, issuer);
    }
}