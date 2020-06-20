package com.example.ewalletexample.Server.api.register;

import javax.crypto.SecretKey;

public interface RegisterCallback {
    void RegisterSuccessful(String userid, String customToken, String secretKeyString1, String secretKeyString2);

    void RegisterTemp(String userid, String fullName, String phone);
}
