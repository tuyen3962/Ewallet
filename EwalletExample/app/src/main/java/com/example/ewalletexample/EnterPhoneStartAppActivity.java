package com.example.ewalletexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.dialogs.ProgressBarManager;
import com.example.ewalletexample.model.UserModel;
import com.example.ewalletexample.service.CheckInputField;
import com.example.ewalletexample.service.MemoryPreference.SharedPreferenceLocal;
import com.example.ewalletexample.service.realtimeDatabase.FirebaseDatabaseHandler;
import com.example.ewalletexample.service.realtimeDatabase.HandleDataFromFirebaseDatabase;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

public class EnterPhoneStartAppActivity extends AppCompatActivity implements HandleDataFromFirebaseDatabase<UserModel> {

    ProgressBarManager progressBarManager;
    FirebaseDatabaseHandler<UserModel> firebaseDatabaseHandler;
    TextInputLayout inputLayoutPhone;
    TextInputEditText etPhone;
    String phone, userid;
    String[] phones;
    SharedPreferenceLocal local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone_start_app);
        CheckCurrentPhone();
        Initialize();
        Setup();
    }

    void CheckCurrentPhone(){
        local = new SharedPreferenceLocal(this, Symbol.NAME_PREFERENCES.GetValue());
        String userid = local.GetValueStringByKey(Symbol.KEY_USERID.GetValue());
        String phone = local.GetValueStringByKey(Symbol.KEY_PHONE.GetValue());
        String fullName = local.GetValueStringByKey(Symbol.KEY_FULL_NAME.GetValue());
        if(!phone.isEmpty() && !fullName.isEmpty() && !userid.isEmpty()){
            Intent intent = new Intent(EnterPhoneStartAppActivity.this, LoginActivity.class);
            intent.putExtra(Symbol.UPDATE_SYMBOL.GetValue(), Symbol.UPDATE_FOR_INFORMATION.GetValue());
            intent.putExtra(Symbol.USER_ID.GetValue(), userid);
            intent.putExtra(Symbol.PHONE.GetValue(), phone);
            intent.putExtra(Symbol.FULLNAME.GetValue(), fullName);
            startActivity(intent);
        }
    }

    void Initialize(){
        progressBarManager = new ProgressBarManager(findViewById(R.id.progressBar), findViewById(R.id.btnContinue));
        progressBarManager.HideProgressBar();
        inputLayoutPhone = findViewById(R.id.etPhoneNumber);
        etPhone = findViewById(R.id.etPhone);
        firebaseDatabaseHandler = new FirebaseDatabaseHandler<>(FirebaseDatabase.getInstance().getReference(), this);
        AddTextWatcher();
        GetListPhone();
    }

    void GetListPhone(){
        Set<String> stringSet = local.GetSetStringValueByKey(Symbol.KEY_PHONES.GetValue());
        phones = new String[stringSet.size()];
        if(phones.length == 0)
            return;
        Iterator<String> iterator = stringSet.iterator();
        int index = 0;
        while (iterator.hasNext()){
            phones[index] = iterator.next();
            index += 1;
        }
    }

    void AddTextWatcher(){
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(inputLayoutPhone.isErrorEnabled()){
                    inputLayoutPhone.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void Setup(){
        etPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowListPhoneRecommendDialog();
            }
        });
    }

    void ShowListPhoneRecommendDialog(){
        if(phones.length == 0)
            return;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Danh sách số điện thoại");
        builder.setItems(phones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etPhone.setText(phones[which]);
                dialog.dismiss();
            }
        });
        builder.setBackground(getResources().getDrawable(R.drawable.alert_dialog_bg));
        builder.show();
    }

    public void CheckPhoneNumber(View view){
        phone = inputLayoutPhone.getEditText().getText().toString();

        if(!CheckInputField.PhoneNumberIsValid(phone)){
            inputLayoutPhone.setError("Số điện thoại không hợp lệ");
        } else {
            firebaseDatabaseHandler.RegisterDataListener();
            progressBarManager.ShowProgressBar("Kiểm tra...");
        }
    }

    @Override
    public void HandleDataModel(UserModel model) {
        progressBarManager.HideProgressBar();
        if(model == null){
            ShowRegisterDialog();
        } else {
            ShowLoginDialog(model);
        }
    }

    @Override
    public void HandleDataSnapShot(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChild(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue())){
            for (DataSnapshot model : dataSnapshot.child(Symbol.CHILD_NAME_USERS_FIREBASE_DATABASE.GetValue()).getChildren()){
                UserModel userModel = model.getValue(UserModel.class);
                if(userModel != null && userModel.getPhone().equalsIgnoreCase(phone)){
                    userid = model.getKey();
                    firebaseDatabaseHandler.UnregisterValueListener(userModel);
                    return;
                }
            }
        }

        firebaseDatabaseHandler.UnregisterValueListener(null);
    }

    @Override
    public void HandlerDatabaseError(DatabaseError databaseError) {

    }

    void ShowRegisterDialog(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Đăng ký ví với số điện thoại");
        builder.setMessage("Bạn đang đăng kí với số diện thoại " + phone);
        builder.setBackground(getResources().getDrawable(R.drawable.alert_dialog_bg));
        builder.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(EnterPhoneStartAppActivity.this, RegisterByPhone.class);
                intent.putExtra(Symbol.PHONE.GetValue(), phone);
                startActivity(intent);
            }
        });
        builder.show();
    }

    void ShowLoginDialog(UserModel model){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Đăng nhập");
        builder.setMessage("Bạn đã nhập " + phone + "\nHệ thống sẽ gửi mã OTP tới số điện thoại này. Bạn muốn tiếp tục không?");
        builder.setBackground(getResources().getDrawable(R.drawable.alert_dialog_bg));
        builder.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(EnterPhoneStartAppActivity.this, VerifyByPhoneActivity.class);
                intent.putExtra(Symbol.PHONE.GetValue(), phone);
                intent.putExtra(Symbol.FULLNAME.GetValue(), model.getFullname());
                intent.putExtra(Symbol.USER_ID.GetValue(), userid);
                intent.putExtra(Symbol.REASION_VERIFY.GetValue(), Symbol.REASON_VERIFY_FOR_LOGIN.GetValue());
                startActivity(intent);
            }
        });
        builder.show();
    }
}
