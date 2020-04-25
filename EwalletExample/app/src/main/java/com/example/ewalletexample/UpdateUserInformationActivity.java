package com.example.ewalletexample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ewalletexample.Server.user.update.UpdateUserAPI;
import com.example.ewalletexample.Server.user.update.UpdateUserResponse;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.dialogs.AlertDialogTakePicture;
import com.example.ewalletexample.dialogs.AlertDialogTakePictureFunction;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.ResponseMethod;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.websocket.WebsocketClient;
import com.example.ewalletexample.service.websocket.WebsocketResponse;
import com.example.ewalletexample.utilies.Utilies;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class UpdateUserInformationActivity extends AppCompatActivity implements AlertDialogTakePictureFunction, ResponseMethod,
        ResponseModelByKey<UserModel>, DatePickerDialog.OnDateSetListener, UpdateUserResponse, WebsocketResponse {

    private final static String DIALOG_TAG = "DIALOG";

    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    FirebaseStorageHandler storageHandler;
    View btnUpload, dateOfBirthLayout;

    TextView tvFullname, tvMonth, tvYear, tvDay;
    EditText etAddress;
    CircleImageView imgAccount;
    ProgressBarManager progressBarManager;

    User user;
    UserModel model;
    AlertDialogTakePicture dialogTakePicture;
    String currentPhotoPath;
    File photoFile;
    Uri photoUri;
    boolean canTakePhoto = false, canPickImage = false;
    WebsocketClient client;
    long balance;
    boolean changeBalance, uploadImage;

    UpdateUserAPI updateAPI;

    DatePickerDialog datePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_information);

        Initialize();

        GetValueFromIntent();
    }

    void Initialize(){
        uploadImage = false;
        changeBalance = false;
        balance = 0;
        client = new WebsocketClient(this);

        datePickerDialog = new DatePickerDialog(this,this,1998,1,1);
        dialogTakePicture = new AlertDialogTakePicture(this);

        btnUpload = findViewById(R.id.btnUpload);
        dateOfBirthLayout = findViewById(R.id.dateOfBirthLayout);
        tvDay = findViewById(R.id.tvDay);
        tvMonth = findViewById(R.id.tvMonth);
        tvYear = findViewById(R.id.tvYear);

        tvFullname = findViewById(R.id.tvFullName);
        etAddress = findViewById(R.id.etAddress);

        imgAccount = findViewById(R.id.imgAccount);

        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar),
                btnUpload, dateOfBirthLayout, etAddress, imgAccount);
        storageHandler = new FirebaseStorageHandler(imgAccount, FirebaseStorage.getInstance(), this);
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference());
    }

    void GetValueFromIntent(){
        user = new User();
        Intent intent = getIntent();
        user.setUserId(intent.getStringExtra(Symbol.USER_ID.GetValue()));
        user.setFullName(intent.getStringExtra(Symbol.FULLNAME.GetValue()));
        user.setImgAccountLink(intent.getStringExtra(Symbol.IMAGE_ACCOUNT_LINK.GetValue()));
        user.setAddress(intent.getStringExtra(Symbol.ADDRESS.GetValue()));
        user.setDateOfbirth(intent.getStringExtra(Symbol.DOB.GetValue()));
        Log.d("TAG", "GetModelByKey GetValueFromIntent: " + user.getUserId());
        firebaseDatabaseHandler.GetModelByKey(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE, user.getUserId(), UserModel.class, this);
        FillUserDetail();
        LoadImageAccount();
        updateAPI = new UpdateUserAPI(user.getUserId(), this);
        updateAPI.setPin("");
        updateAPI.setEmail("");
    }

    void FillUserDetail(){
        tvFullname.setText(user.getFullName());
        tvDay.setText(user.getDayOfBirth());
        tvMonth.setText(user.getMonthOfBirth());
        tvYear.setText(user.getYearOfBirth());
    }

    void LoadImageAccount(){
        if(user.getImgAccountLink().isEmpty()){
            Utilies.SetImageDrawable(this, imgAccount);
            return;
        }

        storageHandler.LoadAccountImageFromLink(user.getImgAccountLink(), imgAccount);
    }

    @Override
    public void GetModel(UserModel data) {
        this.model = data;
    }

    public void ShowDatePickerDialog(View view){
        datePickerDialog.show();
    }

    public void UploadImageEvent(View view){
        CheckAndRequestPermissionForTakingPhoto();
        CheckAndRequestPermissionForPickingPhoto();
        if(!canPickImage && !canTakePhoto)
            return;
        ShowDialogTakePictureEvent();
    }

    public void ShowDialogTakePictureEvent(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogTakePicture.show(ft, DIALOG_TAG);
    }

    @Override
    public void TakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        CheckAndRequestPermissionForTakingPhoto();
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = storageHandler.createImageFile();
                currentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("TAG", "TakePhoto: " + ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.ewalletexample.fileprovider",
                        photoFile);

                this.photoFile = photoFile;

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, RequestCode.TAKE_PHOTO_REQUEST);
            }
        }
    }

    @Override
    public void ChoosePicture() {
        if(!canPickImage)
        {
            CheckAndRequestPermissionForPickingPhoto();
            return;
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RequestCode.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestCode.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            photoUri = data.getData();
            VerifyUploadImage();
        }
        else if(requestCode == RequestCode.TAKE_PHOTO_REQUEST && resultCode == RESULT_OK){
            try {
                photoFile = storageHandler.compressToFile(photoFile);
                photoUri = storageHandler.AddImageToGallery(currentPhotoPath);
                VerifyUploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void VerifyUploadImage(){
        progressBarManager.ShowProgressBar("Cập nhật");
        storageHandler.DeleteFileInStorage(user.getImgAccountLink());
        storageHandler.UploadImage(photoUri, this);
    }

    @Override
    public void GetImageServerFile(String serverFile) {
        uploadImage = true;
        user.setImgAccountLink(serverFile);
        model.setImgLink(serverFile);
        firebaseDatabaseHandler.UpdateData(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue(), user.getUserId(), model);
        updateAPI.setImageProfile(serverFile);
        updateAPI.UpdateUser();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String day = dayOfMonth + "";
        if(dayOfMonth < 10){
            day = "0" + day;
        }
        tvDay.setText(day);
        String monthInYear = (month+1) + "";
        if(month+1 < 10){
            monthInYear = "0" + monthInYear;
        }
        tvMonth.setText(monthInYear);
        tvYear.setText(year + "");
    }

    public void UpdateUserInfo(View view){
        uploadImage = false;
        progressBarManager.ShowProgressBar("Loading");
        String dob = tvDay.getText().toString()+"/"+tvMonth.getText().toString()+"/"+tvYear.getText().toString();
        String address = etAddress.getText().toString();
        user.setAddress(address);
        user.setDateOfbirth(dob);
        updateAPI.setDateOfBirth(dob);
        updateAPI.setAddress(address);
        updateAPI.UpdateUser();
    }

    @Override
    public void UpdateSuccess() {
        if(uploadImage){
            LoadImageAccount();
        }
        else {
            Intent intent = new Intent();
            intent.putExtra(Symbol.ADDRESS.GetValue(), user.getAddress());
            intent.putExtra(Symbol.DOB.GetValue(), user.getDateOfbirth());
            intent.putExtra(Symbol.IMAGE_ACCOUNT_LINK.GetValue(), user.getImgAccountLink());
            intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), changeBalance);
            intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void UpdateFail() {

    }

    @Override
    public void SetMessageProgressBar(String message) {
        progressBarManager.ShowProgressBar(message);
    }

    @Override
    public void HideProgressBar() {
        progressBarManager.HideProgressBar();
    }

    void CheckAndRequestPermissionForTakingPhoto(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, RequestCode.TAKE_PHOTO_REQUEST);
        }
        else{
            canTakePhoto = true;
        }
    }

    void CheckAndRequestPermissionForPickingPhoto(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, RequestCode.PICK_IMAGE_REQUEST);
        }
        else{
            canPickImage =true;
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
                    canTakePhoto = false;
                }
            }
            else{
                canTakePhoto = true;
            }
        }
        else if(requestCode == RequestCode.PICK_IMAGE_REQUEST){
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                if(!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    canPickImage = false;
                }
            }
            else{
                canPickImage = true;
            }
        }
    }

    @Override
    public void UpdateWallet(String userid, long balance) {
        if(userid.equalsIgnoreCase(this.user.getUserId())){
            this.balance = balance;
            changeBalance = true;
        }
    }

    public void BackPreviousActivity(View view){
        Intent intent = new Intent();
        intent.putExtra(Symbol.IMAGE_ACCOUNT_LINK.GetValue(), user.getImgAccountLink());
        intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), changeBalance);
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
