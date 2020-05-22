package com.example.ewalletexample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import jp.wasabeef.glide.transformations.BlurTransformation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ewalletexample.Server.api.update.UpdateUserAPI;
import com.example.ewalletexample.Server.api.update.UpdateUserResponse;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.service.MemoryPreference.SharedPreferenceLocal;
import com.example.ewalletexample.service.ResponseMethod;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.storageFirebase.LoadImageResponse;
import com.example.ewalletexample.utilies.Utilies;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

public class VerifyAccountActivity extends AppCompatActivity implements ResponseMethod, UpdateUserResponse {
    private final static int TAKE_PHOTO_FRONT_SIDE_REQUEST = 100;
    private final static int TAKE_PHOTO_BACK_SIDE_REQUEST = 101;
    private final static int CHOOSE_PICTURE_FRONT_SIDE_REQUEST = 102;
    private final static int CHOOSE_PICTURE_BACK_SIDE_REQUEST = 102;

    FirebaseStorageHandler firebaseStorageHandler;
    ProgressBarManager progressBarManager;
    User user;
    TextView tvFullName, tvBack;
    EditText etCMND;
    View btnVerify;
    ImageView imgFrontIdentifierCard, imgBackIdentifierCard;
    String frontPhotoPath, backPhotoPath;
    File frontPhotoFile, backPhotoFile;
    Uri frontPhotoUri, backPhotoUri;
    UpdateUserAPI updateAPI;
    boolean hasUploadTwoImages;
    SharedPreferenceLocal local;
    String update;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        GetValueFromIntent();
        Initialize();
        FillUserProfile();
    }

    void Initialize(){
        frontPhotoUri = null;
        backPhotoUri = null;
        local = new SharedPreferenceLocal(this, Symbol.NAME_PREFERENCES.GetValue());
        tvFullName = findViewById(R.id.tvFullName);
        tvBack = findViewById(R.id.tvBack);
        etCMND = findViewById(R.id.etCMND);
        imgFrontIdentifierCard = findViewById(R.id.imgFrontIdentifierCard);
        imgBackIdentifierCard = findViewById(R.id.imgBackIdentifierCard);
        btnVerify = findViewById(R.id.btnVerify);
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar),
                btnVerify, imgFrontIdentifierCard, imgBackIdentifierCard);
        updateAPI = new UpdateUserAPI(user.getUserId(), this);
        hasUploadTwoImages = false;
        firebaseStorageHandler = new FirebaseStorageHandler(FirebaseStorage.getInstance(), this);
        if(update.equalsIgnoreCase(Symbol.UPDATE_FOR_REGISTER.GetValue())){
            tvBack.setText("Skip");
            tvFullName.setCompoundDrawables(null,null,null,null);
        } else {
            tvBack.setVisibility(View.GONE);
        }
    }

    void GetValueFromIntent(){
        gson = new Gson();
        user = new User();
        Intent intent = getIntent();
        update = intent.getStringExtra(Symbol.UPDATE_SYMBOL.GetValue());
        if(update.equalsIgnoreCase(Symbol.UPDATE_FOR_REGISTER.GetValue())){
            user = gson.fromJson(intent.getStringExtra(Symbol.USER.GetValue()), User.class);
        } else {
            user.setUserId(intent.getStringExtra(Symbol.USER_ID.GetValue()));
            user.setFullName(intent.getStringExtra(Symbol.FULLNAME.GetValue()));
            user.setCmnd(intent.getStringExtra(Symbol.CMND.GetValue()));
            user.setCmndFrontImage(intent.getStringExtra(Symbol.IMAGE_CMND_FRONT.GetValue()));
            user.setCmndBackImage(intent.getStringExtra(Symbol.IMAGE_CMND_BACK.GetValue()));
        }
    }

    void FillUserProfile(){
        tvFullName.setText(user.getFullName());
        if(!user.getCmnd().isEmpty()){
            etCMND.setHint("********" + user.getCmnd().substring(8));
        }
        LoadImage();
        if (user.getStatus() == 1){
            btnVerify.setEnabled(false);
            etCMND.setEnabled(false);
        }
    }

    public void BackPreviousActivity(View view){
        if(update.equalsIgnoreCase(Symbol.UPDATE_FOR_REGISTER.GetValue())){
            SwitchToMain();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    void LoadImage(){
        if(!user.getCmndFrontImage().isEmpty()){
            String[] images = user.getCmndFrontImage().split(",");
            BlurImage frontImage = new BlurImage(firebaseStorageHandler, this, findViewById(R.id.frontImage),
                    images[0], imgFrontIdentifierCard);

            BlurImage backImage = new BlurImage(firebaseStorageHandler, this, findViewById(R.id.frontImage),
                    images[1], imgBackIdentifierCard);
        }
    }

    public void TakeFrontPhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        CheckAndRequestPermissionForTakingPhoto();
        if (intent.resolveActivity(getPackageManager()) != null && user.getStatus() != 1) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = firebaseStorageHandler.createImageFile();
                frontPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("TAG", "TakePhoto: " + ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.ewalletexample.fileprovider",
                        photoFile);

                this.frontPhotoFile = photoFile;

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, TAKE_PHOTO_FRONT_SIDE_REQUEST);
            }
        }
    }

    public void ChooseFrontPicture(View view){
        CheckAndRequestPermissionForPickingPhoto();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_PICTURE_FRONT_SIDE_REQUEST);
        }
    }

    public void TakeBackPhoto(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        CheckAndRequestPermissionForTakingPhoto();
        if (intent.resolveActivity(getPackageManager()) != null && user.getStatus() != 1) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = firebaseStorageHandler.createImageFile();
                backPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("TAG", "TakePhoto: " + ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.ewalletexample.fileprovider",
                        photoFile);

                this.backPhotoFile = photoFile;

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, TAKE_PHOTO_BACK_SIDE_REQUEST);
            }
        }
    }

    public void ChooseBackPicture(View view){
        CheckAndRequestPermissionForPickingPhoto();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_PICTURE_BACK_SIDE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TAKE_PHOTO_FRONT_SIDE_REQUEST && resultCode == RESULT_OK){
            try {
                frontPhotoFile = firebaseStorageHandler.compressToFile(frontPhotoFile);

                Glide.with(getApplicationContext())
                        .load(frontPhotoFile)
                        .into(imgFrontIdentifierCard);

                firebaseStorageHandler.SetPictureInImageView(imgFrontIdentifierCard, frontPhotoPath);
                frontPhotoUri = firebaseStorageHandler.AddImageToGallery(frontPhotoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(requestCode == TAKE_PHOTO_BACK_SIDE_REQUEST && resultCode == RESULT_OK){
            try {
                backPhotoFile = firebaseStorageHandler.compressToFile(backPhotoFile);

                Glide.with(getApplicationContext())
                        .load(backPhotoFile)
                        .into(imgBackIdentifierCard);

                firebaseStorageHandler.SetPictureInImageView(imgBackIdentifierCard, backPhotoPath);
                backPhotoUri = firebaseStorageHandler.AddImageToGallery(backPhotoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(requestCode == CHOOSE_PICTURE_FRONT_SIDE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            frontPhotoUri = data.getData();
        } else if(requestCode == CHOOSE_PICTURE_BACK_SIDE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            backPhotoUri = data.getData();
        }
    }

    public void VerifyInformation(View view){
        if(!user.getCmndFrontImage().isEmpty()){
            firebaseStorageHandler.DeleteFileInStorage(user.getCmndFrontImage());
        }
        if (!user.getCmndBackImage().isEmpty()){
            firebaseStorageHandler.DeleteFileInStorage(user.getCmndBackImage());
        }
        firebaseStorageHandler.UploadImage(frontPhotoUri, this);
    }

    @Override
    public void SetMessageProgressBar(String message) {
        progressBarManager.ShowProgressBar(message);
    }

    @Override
    public void HideProgressBar() {
        progressBarManager.HideProgressBar();
    }

    @Override
    public void GetImageServerFile(String serverFile) {
        if(!hasUploadTwoImages){
            firebaseStorageHandler.UploadImage(backPhotoUri, this);
            frontPhotoPath = serverFile;
            hasUploadTwoImages = true;
        }
        else{
            backPhotoPath = serverFile;
            progressBarManager.ShowProgressBar("Loading");
            user.setStatus(0);
            user.setCmnd(etCMND.getText().toString());
            user.setCmndFrontImage(frontPhotoPath);
            user.setCmndBackImage(backPhotoPath);
            updateAPI.setCmnd(etCMND.getText().toString());
            updateAPI.setCmndFrontImage(frontPhotoPath);
            updateAPI.setCmndBackImage(backPhotoPath);
            updateAPI.UpdateUser();
        }
    }

    @Override
    public void UpdateSuccess() {
        if(update.equalsIgnoreCase(Symbol.UPDATE_FOR_REGISTER.GetValue())){
            SwitchToMain();
        } else {
            Intent intent = new Intent();
            intent.putExtra(Symbol.IMAGE_CMND_FRONT.GetValue(), user.getCmndFrontImage());
            intent.putExtra(Symbol.IMAGE_CMND_BACK.GetValue(), user.getCmndBackImage());
            intent.putExtra(Symbol.CMND.GetValue(), user.getCmnd());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void UpdateFail() {

    }

    void SwitchToMain(){
        Intent intent = new Intent(VerifyAccountActivity.this, MainLayoutActivity.class);
        intent.putExtra(Symbol.USER.GetValue(), gson.toJson(user));
        startActivity(intent);
    }

    class BlurImage implements LoadImageResponse {
        ImageView imgView;
        Context context;
        View view;

        public BlurImage(FirebaseStorageHandler storageHandler, Context context, View view, String linkImage, ImageView imageView){
            this.imgView = imageView;
            this.context = context;
            this.view = view;
            storageHandler.LoadAccountImageFromLink(linkImage, this, imageView);
        }

        @Override
        public void LoadSuccess(Uri uri) {
            Glide.with(context).load(uri)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                    .into(imgView);
        }

        @Override
        public void LoadFail() {
            Utilies.SetImageDrawable(context, imgView, R.drawable.frame_background);
        }
    }

    void CheckAndRequestPermissionForTakingPhoto(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, TAKE_PHOTO_FRONT_SIDE_REQUEST);
        }
    }

    void CheckAndRequestPermissionForPickingPhoto(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, RequestCode.PICK_IMAGE_REQUEST);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RequestCode.TAKE_PHOTO_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED
                    && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                if(!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) &&
                        !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                }
            }
        }
        else if(requestCode == RequestCode.PICK_IMAGE_REQUEST){
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                if(!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                }
            }
        }
    }

}
