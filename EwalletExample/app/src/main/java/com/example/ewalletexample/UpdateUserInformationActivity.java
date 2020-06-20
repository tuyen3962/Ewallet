package com.example.ewalletexample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import android.widget.TextView;

import com.example.ewalletexample.Server.api.update.UpdateUserAPI;
import com.example.ewalletexample.Server.api.update.UpdateUserResponse;
import com.example.ewalletexample.Symbol.RequestCode;
import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.data.User;
import com.example.ewalletexample.data.UserNotifyEntity;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.AnimationManager;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.ResponseMethod;
import com.example.ewalletexample.service.UserSelectFunction;
import com.example.ewalletexample.service.notification.NotificationCreator;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.ResponseModelByKey;
import com.example.ewalletexample.service.recycleview.listitem.RecycleViewListSingleItem;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.service.toolbar.CustomToolbarContext;
import com.example.ewalletexample.service.websocket.WebsocketClient;
import com.example.ewalletexample.service.websocket.WebsocketResponse;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.example.ewalletexample.utilies.Utilies;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import javax.crypto.SecretKey;

public class UpdateUserInformationActivity extends AppCompatActivity implements ResponseMethod,
        ResponseModelByKey<UserModel>, DatePickerDialog.OnDateSetListener, UpdateUserResponse, UserSelectFunction<String> {

    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    FirebaseStorageHandler storageHandler;
    View btnUpload, dateOfBirthLayout, layoutListItem;
    RecyclerView rvListCities;
    TextView tvFullname, tvMonth, tvYear, tvDay, tvBack, tvListItemTitle;
    TextInputEditText etEmail;
    TextInputLayout input_email_layout;
    MaterialTextView etAddress;
    CircleImageView imgAccount;
    ProgressBarManager progressBarManager;
    Gson gson;
    User user;
    UserModel model;
    File photoFile;
    Uri photoUri;
    WebsocketClient client;
    boolean uploadImage;
    RecycleViewListSingleItem recycleViewListSingleItem;
    UpdateUserAPI updateAPI;
    AnimationManager animationManager;
    DatePickerDialog datePickerDialog;
    String update, secretKey1, secretKey2, currentPhotoPath;
    SecretKey firstKey, secondKey;
    String[] arrCities;
    CustomToolbarContext customToolbarContext;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_information);

        Initialize();

        GetValueFromIntent();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void Initialize(){
        uploadImage = false;
        gson = new Gson();
        datePickerDialog = new DatePickerDialog(this,this,1998,1,1);
        btnUpload = findViewById(R.id.btnUpload);
        dateOfBirthLayout = findViewById(R.id.dateOfBirthLayout);
        tvDay = findViewById(R.id.tvDay);
        tvMonth = findViewById(R.id.tvMonth);
        tvYear = findViewById(R.id.tvYear);
        tvBack = findViewById(R.id.tvBack);
        etEmail = findViewById(R.id.etEmail);
        layoutListItem = findViewById(R.id.layoutListItem);
        tvListItemTitle = findViewById(R.id.tvTitle);
        rvListCities = findViewById(R.id.rvListItems);
        tvFullname = findViewById(R.id.tvFullName);
        etAddress = findViewById(R.id.etAddress);
        animationManager = new AnimationManager(this);
        imgAccount = findViewById(R.id.imgAccount);
        input_email_layout = findViewById(R.id.input_email_layout);
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar),
                btnUpload, dateOfBirthLayout, etAddress, imgAccount);
        storageHandler = new FirebaseStorageHandler(imgAccount, FirebaseStorage.getInstance(), this);
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference());
        customToolbarContext = new CustomToolbarContext(this, this::BackPreviousActivity);
        ReadFile();
        tvListItemTitle.setText("Các thành phố");
        layoutListItem.setVisibility(View.GONE);
    }

    void ReadFile(){
        try {
            InputStream is = getAssets().open("cities.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            if (line != null){
                JSONObject jsonObject = new JSONObject(line);
                JSONArray jsonArray = jsonObject.getJSONArray("cities");
                arrCities = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++){
                    arrCities[i] = jsonArray.getString(i);
                }
                recycleViewListSingleItem = new RecycleViewListSingleItem(this, arrCities, this);
                Utilies.InitializeRecycleView(rvListCities, new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false), recycleViewListSingleItem);
            } else {
                arrCities = null;
                recycleViewListSingleItem = null;
            }
        } catch (FileNotFoundException e){
            Log.d("TAG", "Initialize: " + e.getMessage());
            arrCities = null;
        } catch (IOException e) {
            Log.d("TAG", "Initialize: " + e.getMessage());
            arrCities = null;
        } catch (JSONException e) {
            Log.d("TAG", "Initialize: " + e.getMessage());
            arrCities = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void GetValueFromIntent(){
        user = new User();
        Intent intent = getIntent();
        update = intent.getStringExtra(Symbol.UPDATE_SYMBOL.GetValue());
        user = gson.fromJson(intent.getStringExtra(Symbol.USER.GetValue()), User.class);
        secretKey1 = intent.getStringExtra(Symbol.SECRET_KEY_01.GetValue());
        firstKey = SecurityUtils.generateAESKeyFromText(secretKey1);
        secretKey2 = intent.getStringExtra(Symbol.SECRET_KEY_02.GetValue());
        secondKey = SecurityUtils.generateAESKeyFromText(secretKey2);
        client = new WebsocketClient(this, user.getUserId());
        if(update.equalsIgnoreCase(Symbol.UPDATE_FOR_INFORMATION.GetValue())){
            tvBack.setVisibility(View.GONE);
            user.setDate();
            LoadImageAccount();
            customToolbarContext.SetVisibilityImageButtonBack(View.VISIBLE);
        } else if (update.equalsIgnoreCase(Symbol.UPDATE_FOR_REGISTER.GetValue())){
            customToolbarContext.SetVisibilityImageButtonBack(View.GONE);
        }
        firebaseDatabaseHandler.GetModelByKey(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE, user.getUserId(), UserModel.class, this);
        FillUserDetail();
        updateAPI = new UpdateUserAPI(user.getUserId(), getString(R.string.public_key), this);
    }

    void FillUserDetail(){
        tvFullname.setText(user.getFullName());
        tvDay.setText(user.getDayOfBirth());
        tvMonth.setText(user.getMonthOfBirth());
        tvYear.setText(user.getYearOfBirth());
        if (!user.getAddress().isEmpty()){
            etAddress.setHint(user.getAddress());
        }

        if(!user.getEmail().isEmpty()){
            input_email_layout.setHint(user.getEmail());
        }
    }

    void LoadImageAccount(){
        if(user.getAvatar().isEmpty()){
            Utilies.SetImageDrawable(this, imgAccount);
            return;
        }

        storageHandler.LoadAccountImageFromLink(user.getAvatar(), imgAccount);
    }

    @Override
    public void GetModel(UserModel data) {
        this.model = data;
    }

    public void ShowDatePickerDialog(View view){
        datePickerDialog.show();
    }

    public void ShowListCitiesRecommend(View view){
        if (arrCities != null){
            animationManager.ShowAnimationView(layoutListItem);
        }
    }

    public void HideListItemLayout(View view){
        animationManager.HideAnimationView(layoutListItem);
    }

    @Override
    public void SelectModel(String model) {
        animationManager.HideAnimationView(layoutListItem);
        etAddress.setText(model);
    }

    public void TakePhoto(View view) {
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

    public void ChoosePicture(View view) {
        CheckAndRequestPermissionForPickingPhoto();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), RequestCode.PICK_IMAGE_REQUEST);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        } else if(requestCode == RequestCode.UPDATE_EMAIL){
            if (resultCode == RESULT_CANCELED){
                input_email_layout.setError("Xác thực thất bại");
                etEmail.setText("");
            } else if (resultCode == RESULT_OK){
                UpdateUser();
            }
        } else if (requestCode == RequestCode.UPDATE_REGISTER){
            boolean changeBalance = data.getBooleanExtra(Symbol.CHANGE_BALANCE.GetValue(), false);
            if (!changeBalance){
                data.putExtra(Symbol.CHANGE_BALANCE.GetValue(), client.IsUpdateBalance());
                data.putExtra(Symbol.AMOUNT.GetValue(), client.getNewBalance());
            }
            setResult(resultCode, data);
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void UpdateUser(){
        uploadImage = false;
        progressBarManager.ShowProgressBar("Loading");
        String dob = tvDay.getText().toString()+"/"+tvMonth.getText().toString()+"/"+tvYear.getText().toString();
        String address = etAddress.getText().toString();
        String email = etEmail.getText().toString();
        user.setAddress(address);
        user.setDateOfbirth(dob);
        user.setEmail(email);
        updateAPI.setDateOfBirth(dob);
        updateAPI.setAddress(address);
        updateAPI.setEmail(email);

        updateAPI.UpdateUser(firstKey, secondKey);
    }

    public void VerifyUploadImage(){
        progressBarManager.ShowProgressBar("Cập nhật");
        if(!user.getAvatar().isEmpty()){
            storageHandler.DeleteFileInStorage(user.getAvatar());
        }
        storageHandler.UploadImage(photoUri, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void GetImageServerFile(String serverFile) {
        uploadImage = true;
        user.setAvatar(serverFile);
        model.setImgLink(serverFile);
        firebaseDatabaseHandler.UpdateData(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue(), user.getUserId(), model);
        updateAPI.setImageProfile(serverFile);
        updateAPI.UpdateUser(firstKey, secondKey);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void UpdateUserInfo(View view){
        String email = etEmail.getText().toString();
        if (!email.equalsIgnoreCase("")){
            if (!CheckInputField.EmailIsValid(email)){
                etEmail.setError("Email không hợp lệ. Xin nhập lại.");
                return;
            }
            else if (!user.getEmail().equalsIgnoreCase("") && email.equalsIgnoreCase(user.getEmail())){
                etEmail.setError("Email trùng với email đã xác thực");
                return;
            } else {
                Intent intent = new Intent(UpdateUserInformationActivity.this, UpdateEmailActivity.class);
                intent.putExtra(Symbol.USER_ID.GetValue(), user.getUserId());
                intent.putExtra(Symbol.EMAIL.GetValue(), etEmail.getText().toString());
                startActivityForResult(intent, RequestCode.UPDATE_EMAIL);
                return;
            }
        }
        UpdateUser();
    }

    @Override
    public void UpdateSuccess() {
        if(uploadImage){
            LoadImageAccount();
        }
        else {
            if (update.equalsIgnoreCase(Symbol.UPDATE_FOR_REGISTER.GetValue())){
                Intent intent = new Intent(UpdateUserInformationActivity.this, VerifyAccountActivity.class);
                intent.putExtra(Symbol.UPDATE_SYMBOL.GetValue(), Symbol.UPDATE_FOR_REGISTER.GetValue());
                intent.putExtra(Symbol.USER.GetValue(), gson.toJson(user));
                intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), secretKey1);
                intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), secretKey2);
                startActivity(intent);
            } else if(update.equalsIgnoreCase(Symbol.UPDATE_FOR_INFORMATION.GetValue())){
                Intent intent = new Intent();
                intent.putExtra(Symbol.ADDRESS.GetValue(), user.getAddress());
                intent.putExtra(Symbol.DOB.GetValue(), user.getDateOfbirth());
                intent.putExtra(Symbol.IMAGE_ACCOUNT_LINK.GetValue(), user.getAvatar());
                intent.putExtra(Symbol.EMAIL.GetValue(), user.getEmail());
                intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), client.IsUpdateBalance());
                intent.putExtra(Symbol.AMOUNT.GetValue(), client.getNewBalance());
                setResult(RESULT_OK, intent);
                finish();
            }
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

    public void BackPreviousActivity(){
        Intent intent = new Intent();
        intent.putExtra(Symbol.IMAGE_ACCOUNT_LINK.GetValue(), user.getAvatar());
        intent.putExtra(Symbol.CHANGE_BALANCE.GetValue(), client.IsUpdateBalance());
        intent.putExtra(Symbol.AMOUNT.GetValue(), client.getNewBalance());
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void MoveToNextActivity(View view){
        Intent intent = new Intent(UpdateUserInformationActivity.this, VerifyAccountActivity.class);
        intent.putExtra(Symbol.UPDATE_SYMBOL.GetValue(), Symbol.UPDATE_FOR_REGISTER.GetValue());
        intent.putExtra(Symbol.USER.GetValue(), gson.toJson(user));
        intent.putExtra(Symbol.SECRET_KEY_01.GetValue(), secretKey1);
        intent.putExtra(Symbol.SECRET_KEY_02.GetValue(), secretKey2);
        startActivityForResult(intent, RequestCode.UPDATE_REGISTER);
    }

}
