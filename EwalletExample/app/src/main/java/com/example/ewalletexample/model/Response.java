package com.example.ewalletexample.model;

import com.example.ewalletexample.Symbol.ErrorCode;

public class Response {
    private ErrorCode code;

    public Response(ErrorCode code){
        this.code = code;
    }

    public String GetMessage() {
        return code.GetMessage();
    }

    public boolean GetStatus() {
        return code.GetValue() == ErrorCode.SUCCESS.GetValue();
    }
}
