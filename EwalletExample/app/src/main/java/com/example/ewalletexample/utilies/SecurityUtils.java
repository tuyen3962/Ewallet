package com.example.ewalletexample.utilies;

import android.os.Build;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.RequiresApi;

public class SecurityUtils {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String EncodeStringBase64(byte[] bytes){
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] DecodeStringBase64(String encodedString){
        return Base64.decode(encodedString, Base64.NO_WRAP);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String EncryptDataByPublicKey(String publicKeyText, String data)  {
        try {
            byte[] decoded = Base64.decode(publicKeyText, Base64.NO_WRAP);
            X509EncodedKeySpec x509PublicKey = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(x509PublicKey);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.encodeToString(encryptedData, Base64.NO_WRAP);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PublicKey generatePublicKey(String publicKeyString){
        try {
            byte[] decoded = Base64.decode(publicKeyString, Base64.NO_WRAP);
            X509EncodedKeySpec x509PublicKey = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(x509PublicKey);
            return publicKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encryptRSA(PublicKey publicKey, String data) {

        try {
            byte[] rawData = data.getBytes(StandardCharsets.UTF_8);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedData = cipher.doFinal(rawData);
            return Base64.encodeToString(encryptedData, Base64.NO_WRAP);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static SecretKey generateAESKey() {

        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            return generator.generateKey();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static SecretKey generateAESKeyFromText(String text){
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(DecodeStringBase64(text), "AES");

            return secretKeySpec;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String generateAESKeyString(){
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            SecretKey secretKey = generator.generateKey();
            return EncodeStringBase64(secretKey.getEncoded());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encryptHmacSha256(SecretKey secretKey, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] encryptedData = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return Base64.encodeToString(encryptedData, Base64.NO_WRAP);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encryptAES(SecretKey secretKey, String data) {
        try {
            byte[] rawData = data.getBytes(StandardCharsets.UTF_8);

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedData = cipher.doFinal(rawData);
            return Base64.encodeToString(encryptedData, Base64.NO_WRAP);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decryptAES(SecretKey secretKey, String encryptedDataBase64) {
        try {
            byte[] encryptedData = Base64.decode(encryptedDataBase64, Base64.NO_WRAP);

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] rawOutput = cipher.doFinal(encryptedData);
            return new String(rawOutput, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String EncryptStringByShareKey(String shareKeyText, String text){
        SecretKey sharekey = generateAESKeyFromText(shareKeyText);
        return SecurityUtils.encryptHmacSha256(sharekey, text);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String EncryptStringBySecretKey(SecretKey secretKey, String shareKeyText, String text){
        String encryptText = EncryptStringByShareKey(shareKeyText, text);
        return SecurityUtils.encryptAES(secretKey, encryptText);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String DecryptAESbyTwoSecretKey(SecretKey secretKey1, SecretKey secretKey2, String data){
        String decryptBySecretKey1 = decryptAES(secretKey1, data);
        return decryptAES(secretKey2, decryptBySecretKey1);
    }
}