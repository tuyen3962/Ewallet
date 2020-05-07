package com.example.ewalletexample.Server.api.checklist;

import com.example.ewalletexample.model.UserSearchModel;

import java.util.List;

public interface CheckListResponse {
    void Response(List<UserSearchModel> models);
}
