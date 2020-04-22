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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ewalletexample.Server.request.RequestServerAPI;
import com.example.ewalletexample.Server.request.RequestServerFunction;
import com.example.ewalletexample.Server.user.update.UpdateUserAPI;
import com.example.ewalletexample.Server.user.update.UpdateUserResponse;
import com.example.ewalletexample.Symbol.ErrorCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.dialogs.AlertDialogTakePicture;
import com.example.ewalletexample.dialogs.AlertDialogTakePictureFunction;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.ResponseMethod;
import com.example.ewalletexample.service.ServerAPI;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.websocket.WebsocketClient;
import com.example.ewalletexample.service.websocket.WebsocketResponse;
import com.example.ewalletexample.utilies.TextVisibilityManagement;
import com.example.ewalletexample.utilies.dataJson.HandlerJsonData;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class PersonalDetailActivity extends AppCompatActivity implements AlertDialogTakePictureFunction, ResponseMethod,
        ResponseModelByKey<UserModel>, DatePickerDialog.OnDateSetListener, WebsocketResponse, UpdateUserResponse {
    private final static String DIALOG_TAG = "DIALOG";
    private final static int TAKE_PHOTO_REQUEST = 100;
    private final static int PICK_IMAGE_REQUEST = 101;

    FirebaseStorageHandler firebaseStorageHandler;
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;

    Button btnUploadImage, btnVerifyUploadImage, btnCancelUploadImage,
            btnEditUserDetail, btnFinishEditUserDetail, btnCancelEditUserDetail;

    TextView tvDateOfBirth, tvAddress, tvCMND, tvFullname;
    EditText etAddress;
    CircleImageView imgAccount;
    ProgressBarManager progressBarManager;

    private TextVisibilityManagement managementTextViewVisibility;
    private TextVisibilityManagement managementEditTextVisibility;

    private UserModel model;
    private AlertDialogTakePicture dialogTakePicture;
    private User user;

    private String currentPhotoPath;
    private File photoFile;
    private Uri photoUri;
    private boolean canTakePhoto = false, canPickImage = false;
    WebsocketClient client;
    long balance;
    boolean changeBalance, uploadImage;

    UpdateUserAPI updateAPI;

    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_detail);
        Initialize();
        LoadDataFromIntent();
        dialogTakePicture = new AlertDialogTakePicture(this);
        LoadImageAccount();
        FillUserProfileIntoEditText();
        ShowUserProfile();
        CheckAndRequestPermissionForTakingPhoto();
        CheckAndRequestPermissionForPickingPhoto();
    }

    void Initialize(){
        uploadImage = false;
        changeBalance = false;
        balance = 0;
        client = new WebsocketClient(this);
        datePickerDialog = new DatePickerDialog(this,this,1998,1,1);
        btnCancelEditUserDetail = findViewById(R.id.btnCancelEditUserDetail);
        btnVerifyUploadImage = findViewById(R.id.btnVerifyUploadImage);
        btnCancelUploadImage = findViewById(R.id.btnCancelUploadImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnEditUserDetail = findViewById(R.id.btnEditUserDetail);
        btnFinishEditUserDetail = findViewById(R.id.btnFinishEditUserDetail);

        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        tvAddress = findViewById(R.id.tvAddress);
        tvCMND = findViewById(R.id.tvCMND);
        tvFullname = findViewById(R.id.tvFullName);

        managementTextViewVisibility = new TextVisibilityManagement(tvAddress, tvCMND);

        etAddress = findViewById(R.id.etAddress);

        managementEditTextVisibility = new TextVisibilityManagement(etAddress);

        imgAccount = findViewById(R.id.imgAccount);

        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar),
                btnVerifyUploadImage, btnCancelUploadImage, btnUploadImage, btnCancelEditUserDetail,
                btnFinishEditUserDetail, btnEditUserDetail);
        firebaseStorageHandler = new FirebaseStorageHandler(imgAccount, FirebaseStorage.getInstance(), this);
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference());
    }

    void LoadDataFromIntent(){
        progressBarManager.ShowProgressBar("Loading");
        user = new User();
        Intent intent = getIntent();
        String fullname = intent.getStringExtra(Symbol.FULLNAME.GetValue());
        String userid = intent.getStringExtra(Symbol.USER_ID.GetValue());
        String imgLink = intent.getStringExtra(Symbol.IMAGE_ACCOUNT_LINK.GetValue());
        user.setUserId(userid);
        user.setFullName(fullname);
        user.setImgAccountLink(imgLink);
        firebaseDatabaseHandler.GetModelByKey(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE, userid, UserModel.class, this);

        updateAPI = new UpdateUserAPI(user.getUserId(), this);
    }

    @Override
    public void UpdateWallet(String userid, long balance) {
        if(userid.equalsIgnoreCase(user.getUserId())){
            balance = balance;
            changeBalance = true;
        }
    }

    @Override
    public void GetModel(UserModel model){
        this.model = model;
        progressBarManager.HideProgressBar();
    }

    void LoadImageAccount(){
        if(user.getImgAccountLink().isEmpty()){
            imgAccount.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_person, null));
            return;
        }
        progressBarManager.ShowProgressBar("Loading");
        firebaseStorageHandler.LoadAccountImageFromLink(user.getImgAccountLink(), this);
    }

    void CheckAndRequestPermissionForTakingPhoto(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, TAKE_PHOTO_REQUEST);
        }
        else{
            canTakePhoto = true;
        }
    }

    void CheckAndRequestPermissionForPickingPhoto(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, PICK_IMAGE_REQUEST);
        }
        else{
            canPickImage =true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == TAKE_PHOTO_REQUEST) {
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
        else if(requestCode == PICK_IMAGE_REQUEST){
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

    public void BackToMainEvent(View view){
        Intent intent = new Intent();
        intent.putExtra(Symbol.IMAGE_ACCOUNT_LINK.GetValue(), user.getImgAccountLink());
        intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), changeBalance);
        intent.putExtra(Symbol.AMOUNT.GetValue(), balance);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void CancelUploadImageEvent(View view){
        LoadImageAccount();
        ShowButtonCancelEditImageAccount();
    }

    public void VerifyUploadImageEvent(View view){
        uploadImage = true;
        progressBarManager.ShowProgressBar("Cập nhật");
        firebaseStorageHandler.UploadImage(photoUri, this);
        ShowButtonCancelEditImageAccount();
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

    public void EditUserDetailEvent(View view){
        ShowEditTextUserDetail();
        HideTextViewUserDetail();
        ShowButtonEditUserDetail();
    }

    public void FinishEditUserDetailEvent(View view) {
        uploadImage = false;
        progressBarManager.ShowProgressBar("Loading");
        String dob = tvDateOfBirth.getText().toString();
        String address = etAddress.getText().toString();
        updateAPI.setDateOfBirth(dob);
        updateAPI.setAddress(address);
        updateAPI.UpdateUser();
    }

    public void CancelEditUserDetailEvent(View view){
        HideEditTextUserDetail();
        ShowTextViewUserDetail();
        ShowButtonCancelEditUserDetail();
        FillUserProfileIntoEditText();
    }

    public void ShowDatePickerDialog(View view){
        if(managementEditTextVisibility.IsVisible()){
            datePickerDialog.show();
        }
    }

    private void ShowEditTextUserDetail(){
        managementEditTextVisibility.ShowText();
    }

    private void HideEditTextUserDetail(){
        managementEditTextVisibility.HideText();
    }

    private void ShowTextViewUserDetail(){
        managementTextViewVisibility.ShowText();
    }

    private void HideTextViewUserDetail(){
        managementTextViewVisibility.HideText();
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

    private void ShowUserProfile(){
        tvFullname.setText(user.getFullName());
        tvAddress.setText(user.getAddress());
        tvCMND.setText(user.getCmnd());
        tvDateOfBirth.setText(user.getDateOfbirth());
    }

    private void FillUserProfileIntoEditText(){
        etAddress.setText(user.getAddress());
    }

    @Override
    public void TakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        CheckAndRequestPermissionForTakingPhoto();
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = firebaseStorageHandler.createImageFile();
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
                startActivityForResult(intent, TAKE_PHOTO_REQUEST);
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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            photoUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                imgAccount.setImageBitmap(bitmap);
                ShowButtonEditImageAccount();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if(requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK){
            try {
                photoFile = firebaseStorageHandler.compressToFile(photoFile);

                Glide.with(getApplicationContext())
                        .load(photoFile)
                        .into(imgAccount);

                firebaseStorageHandler.SetPictureInImageView(currentPhotoPath);
                photoUri = firebaseStorageHandler.AddImageToGallery(currentPhotoPath);
                ShowButtonEditImageAccount();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void SetMessageProgressBar(String message) {
        progressBarManager.SetMessage(message);
    }

    @Override
    public void HideProgressBar() {
        progressBarManager.HideProgressBar();
    }

    @Override
    public void GetImageServerFile(String serverFile) {
        user.setImgAccountLink(serverFile);
        model.setImgLink(serverFile);
        LoadImageAccount();
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

        String currentDateString = dayOfMonth+"/"+(month+1)+"/"+year;
        tvDateOfBirth.setText(currentDateString);
    }

    @Override
    public void UpdateSuccess() {
        if(!uploadImage){
            user.setDateOfbirth(tvDateOfBirth.getText().toString());
            user.setCmnd(tvCMND.getText().toString());
            user.setAddress(etAddress.getText().toString());
            ShowUserProfile();
            ShowTextViewUserDetail();
            ShowButtonCancelEditUserDetail();
        }
        progressBarManager.HideProgressBar();
    }

    @Override
    public void UpdateFail() {

    }
}