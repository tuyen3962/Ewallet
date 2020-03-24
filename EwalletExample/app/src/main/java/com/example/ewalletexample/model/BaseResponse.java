package com.example.ewalletexample.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class BaseResponse implements Serializable {
    @JsonProperty("returncode")
    public int returnCode;
}
