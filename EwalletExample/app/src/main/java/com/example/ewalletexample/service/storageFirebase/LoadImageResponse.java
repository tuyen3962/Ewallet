package com.example.ewalletexample.service.storageFirebase;

import android.net.Uri;

public interface LoadImageResponse {
    void LoadSuccess(Uri uri);

    void LoadFail();
}
