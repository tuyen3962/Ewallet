package com.example.ewalletexample.Server.api.login;

import com.example.ewalletexample.data.User;

public interface UserLoginResponse {
    void LoginSucess(User user, String customToken, String secretKey1, String secretKey2);

    void LoginFail(int code);
}
