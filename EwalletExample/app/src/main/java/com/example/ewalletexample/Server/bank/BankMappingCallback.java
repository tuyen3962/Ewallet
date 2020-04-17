package com.example.ewalletexample.Server.bank;

public interface BankMappingCallback<T> {
    void MappingResponse(boolean response, T callback);
}
