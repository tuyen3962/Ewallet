package com.example.ewalletexample.Server.order;

public interface StatusOrder {
    void TransactionSuccess();

    void TransactionExcuting();

    void TransactionFail();
}
