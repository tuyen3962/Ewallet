package com.example.ewalletexample.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponse extends BaseResponse {
    @JsonProperty("userid")
    public String userid;
}
