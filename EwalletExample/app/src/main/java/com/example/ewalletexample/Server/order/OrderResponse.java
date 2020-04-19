package com.example.ewalletexample.Server.order;

import java.util.List;

public interface OrderResponse {
    void response(boolean isSuccess, int code, List<String> objectList);
}
