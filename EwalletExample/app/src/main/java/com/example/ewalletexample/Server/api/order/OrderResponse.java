package com.example.ewalletexample.Server.api.order;

import java.util.List;

public interface OrderResponse {
    void response(boolean isSuccess, int code, String transactionId);
}
