package com.example.ewalletexample.Server.api.login;

import com.example.ewalletexample.data.User;

public interface UserLoginResponse {
    void LoginSucess(User user);

    void LoginFail(int code);
}
