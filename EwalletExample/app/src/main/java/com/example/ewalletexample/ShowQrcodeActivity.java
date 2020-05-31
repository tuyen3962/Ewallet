package com.example.ewalletexample;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.service.toolbar.CustomToolbarContext;
import com.example.ewalletexample.service.toolbar.ToolbarEvent;
import com.example.ewalletexample.utilies.SecurityUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;

public class ShowQrcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, ToolbarEvent {
    private static final int colorChosen = R.color.colorPrimary;
    private static final int textColorChosen = R.color.White;
    private static final int colorDefault = R.color.White;
    private static final int textColorDefault = R.color.Black;

    ImageView imgQrCode;
    Button btnGenerateQrCode, btnScanQr;
    View layoutQrCode;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    ZXingScannerView qrCodeScanner;
    String userId, encodedString;
    CustomToolbarContext customToolbarContext;
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
        qrCodeScanner = findViewById(R.id.qrCodeScanner);
        layoutQrCode = findViewById(R.id.layoutQrCode);
        customToolbarContext = new CustomToolbarContext(this, this::BackToPreviousActivity);
        customToolbarContext.SetTitle("Mã QR");
        qrCodeScanner.setResultHandler(this);
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
        encodedString = SecurityUtils.EncodeStringBase64(userId.getBytes());
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

    public void GenerateQrCode(View view){
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

    public void ScanQr(View view){
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
            String decodedString = new String(SecurityUtils.DecodeStringBase64(text));
            Intent intent = new Intent();
            intent.putExtra(Symbol.USER_ID.GetValue(), decodedString);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void BackToPreviousActivity() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
