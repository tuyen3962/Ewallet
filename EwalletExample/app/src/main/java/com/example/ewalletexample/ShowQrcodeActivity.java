package com.example.ewalletexample;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.utilies.Encryption;
import com.example.ewalletexample.utilies.Utilies;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;

public class ShowQrcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, View.OnClickListener {
    private static final int colorChosen = R.color.colorPrimary;
    private static final int textColorChosen = R.color.White;
    private static final int colorDefault = R.color.White;
    private static final int textColorDefault = R.color.Black;

    ImageView imgQrCode;
    Button btnGenerateQrCode, btnScanQr;
    ImageButton imgBack;
    View layoutQrCode;
    Toolbar toolbar;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    ZXingScannerView qrCodeScanner;
    String userId, encodedString;
    boolean isGenerate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_qrcode);

        Initialize();
        GetValueFromIntent();
        SetupUI();
    }

    void Initialize(){
        imgQrCode = findViewById(R.id.qr_image);
        btnGenerateQrCode = findViewById(R.id.btnGenerateQrCode);
        btnScanQr = findViewById(R.id.btnScanQrCode);
        imgBack = findViewById(R.id.btnBackToPreviousActivity);
        toolbar = findViewById(R.id.toolbarLayout);
        qrCodeScanner = findViewById(R.id.qrCodeScanner);
        layoutQrCode = findViewById(R.id.layoutQrCode);
        qrCodeScanner.setResultHandler(this);
        btnGenerateQrCode.setOnClickListener(this);
        btnScanQr.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        setSupportActionBar(toolbar);
        setScannerProperties();
    }

    private void setScannerProperties() {
        List<BarcodeFormat> list = new ArrayList<BarcodeFormat>();
        list.add(BarcodeFormat.QR_CODE);
        qrCodeScanner.setFormats(list);
        qrCodeScanner.setAutoFocus(true);
        qrCodeScanner.setLaserColor(R.color.colorAccent);
        qrCodeScanner.setMaskColor(R.color.colorAccent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void GetValueFromIntent(){
        Intent intent = getIntent();
        userId = intent.getStringExtra(Symbol.USER_ID.GetValue());
        isGenerate = intent.getStringExtra(Symbol.QRCODE.GetValue()).equalsIgnoreCase(Symbol.GENERATE.GetValue());
        encodedString = Encryption.EncodeStringBase64(Utilies.ConvertStringToByte(userId));
    }

    @SuppressLint("ResourceAsColor")
    void SetupUI(){
        GenerateBitmapQrCode(encodedString);
        if (isGenerate){
            SetColorChosenForButton(btnGenerateQrCode);
            qrCodeScanner.setVisibility(View.GONE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShowQrcodeActivity.this, new String[]{Manifest.permission.CAMERA},
                            0);
                    return;
                }
            }
            qrCodeScanner.startCamera();
            SetColorChosenForButton(btnScanQr);
            layoutQrCode.setVisibility(View.GONE);
        }
    }

    @SuppressLint("ResourceAsColor")
    void SetColorChosenForButton(Button button){
        button.setBackgroundColor(colorChosen);
        button.setTextColor(textColorChosen);
    }

    @SuppressLint("ResourceAsColor")
    void SetColorDefaultForButton(Button button){
        button.setBackgroundColor(colorDefault);
        button.setTextColor(textColorDefault);
    }

    void GenerateBitmapQrCode(String inputValue){
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        qrgEncoder = new QRGEncoder(
                inputValue, null,
                QRGContents.Type.TEXT,
                smallerDimension);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            imgQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            bitmap = null;
        }
    }

    @Override
    public void onClick(View view){
        if (btnScanQr.getId() == view.getId()){
            ScaneQr();
        } else if(btnGenerateQrCode.getId() == view.getId()) {
            GenerateQrCode();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    void GenerateQrCode(){
        if (isGenerate)
             return;

        isGenerate = true;
        if (bitmap != null){
            imgQrCode.setImageBitmap(bitmap);
            qrCodeScanner.setVisibility(View.GONE);
            imgQrCode.setVisibility(View.VISIBLE);
            SetColorChosenForButton(btnGenerateQrCode);
            SetColorDefaultForButton(btnScanQr);
            qrCodeScanner.stopCamera();
        }
    }

    void ScaneQr(){
        if (!isGenerate) return;

        isGenerate = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ShowQrcodeActivity.this, new String[]{Manifest.permission.CAMERA},
                        0);
                return;
            }
        }
        qrCodeScanner.setVisibility(View.VISIBLE);
        layoutQrCode.setVisibility(View.GONE);
        qrCodeScanner.startCamera();
        SetColorChosenForButton(btnScanQr);
        SetColorDefaultForButton(btnGenerateQrCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void handleResult(Result result) {
        if (result != null) {
            String text = result.getText();
            String decodedString = Utilies.ConvertByteToString(Encryption.DecodeStringBase64(text));
        }
    }
}
