package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonalDetailActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnUploadImage, btnVerifyUploadImage, btnCancelUploadImage,
            btnEditUserDetail, btnFinishEditUserDetail, btnCancelEditUserDetail;

    TextView tvPhone, tvFullName, tvDateOfBirth, tvAddress, tvCMND;
    EditText etPhone, etFullName, etDateOfBirth, etAddress, etCMND;
    ImageView imgAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_detail);

        Initialize();

        ButtonRegisterOnClickEvent();
    }

    void Initialize(){
        btnCancelEditUserDetail = findViewById(R.id.btnCancelEditUserDetail);
        btnVerifyUploadImage = findViewById(R.id.btnVerifyUploadImage);
        btnCancelUploadImage = findViewById(R.id.btnCancelUploadImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnEditUserDetail = findViewById(R.id.btnEditUserDetail);
        btnFinishEditUserDetail = findViewById(R.id.btnFinishEditUserDetail);

        tvPhone = findViewById(R.id.tvPhone);
        tvFullName = findViewById(R.id.tvFullName);
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        tvAddress = findViewById(R.id.tvAddress);
        tvCMND = findViewById(R.id.tvCMND);

        etPhone = findViewById(R.id.etPhone);
        etFullName = findViewById(R.id.etFullName);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etAddress = findViewById(R.id.etAddress);
        etCMND = findViewById(R.id.etCMND);

        imgAccount = findViewById(R.id.imgAccount);
    }

    void ButtonRegisterOnClickEvent(){
        btnCancelUploadImage.setOnClickListener(this);
        btnVerifyUploadImage.setOnClickListener(this);
        btnUploadImage.setOnClickListener(this);
        btnEditUserDetail.setOnClickListener(this);
        btnFinishEditUserDetail.setOnClickListener(this);
        btnCancelEditUserDetail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        if(view.getId() == btnCancelUploadImage.getId()){
            CancelUploadImageEvent();
        }
        else if(view.getId() == btnVerifyUploadImage.getId()){
            VerifyUploadImage();
        }
        else if(view.getId() == btnUploadImage.getId()){
            UploadImage();
        }
        else if(view.getId() == btnEditUserDetail.getId()){
            EditUserDetail();
        }
        else if(view.getId() == btnFinishEditUserDetail.getId()){
            FinishEditUserDetail();
        }
        else if(view.getId() == btnCancelEditUserDetail.getId()){
            CancelEditUserDetail();
        }
    }

    private void CancelUploadImageEvent(){
        ShowButtonCancelEditImageAccount();
    }

    //unfinish
    private void VerifyUploadImage(){
        ShowButtonCancelEditImageAccount();
    }

    private void UploadImage(){
        ShowButtonEditImageAccount();
    }

    //Finish
    private void EditUserDetail(){
        ShowEditTextUserDetail();
        HideTextViewUserDetail();
        ShowButtonEditUserDetail();
    }

    //Unfinish
    private void FinishEditUserDetail(){
        ShowUserDetail();
    }

    private void ShowUserDetail(){
        CancelEditUserDetail();
    }

    //Finish
    private void CancelEditUserDetail(){
        HideEditTextUserDetail();
        ShowTextViewUserDetail();
        ShowButtonCancelEditUserDetail();
    }

    private void ShowEditTextUserDetail(){
        etPhone.setVisibility(View.VISIBLE);
        etFullName.setVisibility(View.VISIBLE);
        etDateOfBirth.setVisibility(View.VISIBLE);
        etAddress.setVisibility(View.VISIBLE);
        etCMND.setVisibility(View.VISIBLE);
    }

    private void HideEditTextUserDetail(){
        etPhone.setVisibility(View.GONE);
        etFullName.setVisibility(View.GONE);
        etDateOfBirth.setVisibility(View.GONE);
        etAddress.setVisibility(View.GONE);
        etCMND.setVisibility(View.GONE);
    }

    private void ShowTextViewUserDetail(){
        tvPhone.setVisibility(View.VISIBLE);
        tvFullName.setVisibility(View.VISIBLE);
        tvDateOfBirth.setVisibility(View.VISIBLE);
        tvAddress.setVisibility(View.VISIBLE);
        tvCMND.setVisibility(View.VISIBLE);
    }

    private void HideTextViewUserDetail(){
        tvPhone.setVisibility(View.GONE);
        tvFullName.setVisibility(View.GONE);
        tvDateOfBirth.setVisibility(View.GONE);
        tvAddress.setVisibility(View.GONE);
        tvCMND.setVisibility(View.GONE);
    }

    private void ShowButtonEditUserDetail(){
        btnEditUserDetail.setVisibility(View.GONE);
        btnFinishEditUserDetail.setVisibility(View.VISIBLE);
        btnCancelEditUserDetail.setVisibility(View.VISIBLE);
    }

    private void ShowButtonCancelEditUserDetail(){
        btnEditUserDetail.setVisibility(View.VISIBLE);
        btnFinishEditUserDetail.setVisibility(View.GONE);
        btnCancelEditUserDetail.setVisibility(View.GONE);
    }

    private void ShowButtonEditImageAccount(){
        btnUploadImage.setVisibility(View.GONE);
        btnCancelUploadImage.setVisibility(View.VISIBLE);
        btnVerifyUploadImage.setVisibility(View.VISIBLE);
    }

    private void ShowButtonCancelEditImageAccount(){
        btnUploadImage.setVisibility(View.VISIBLE);
        btnCancelUploadImage.setVisibility(View.GONE);
        btnVerifyUploadImage.setVisibility(View.GONE);
    }
}
