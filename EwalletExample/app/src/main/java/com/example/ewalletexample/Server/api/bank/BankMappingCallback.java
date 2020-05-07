package com.example.ewalletexample.Server.api.bank;

public interface BankMappingCallback<T> {
    void MappingResponse(boolean response, T callback);
}
