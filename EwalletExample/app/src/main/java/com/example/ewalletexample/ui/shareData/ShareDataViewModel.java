package com.example.ewalletexample.ui.shareData;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShareDataViewModel extends ViewModel {
    private MutableLiveData<String> balanceMutableLiveData;
    private MutableLiveData<Uri> imageUriMutableLiveData;

    public ShareDataViewModel(){
        balanceMutableLiveData = new MutableLiveData<>();
        imageUriMutableLiveData = new MutableLiveData<>();
    }

    public void setBalanceData(String balance){
        balanceMutableLiveData.setValue(balance);
    }

    public void setImageUriData(Uri imageUriData){
        imageUriMutableLiveData.setValue(imageUriData);
    }

    public LiveData<String> getBalance(){
        return balanceMutableLiveData;
    }

    public LiveData<Uri> getImageUri(){
        return imageUriMutableLiveData;
    }
}
