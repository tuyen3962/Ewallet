package com.example.ewalletexample.utilies;

import android.content.Context;
import android.os.Build;

import com.example.ewalletexample.R;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.RequiresApi;

public class Encryption {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String EncodeStringBase64(byte[] bytes){
        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] DecodeStringBase64(String encodedString){
        return Base64.getDecoder().decode(encodedString);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String EncryptSecretKeyByPublicKey(String publicKeyText, SecretKey secretKey)  {
        try {
            byte[] decoded = Base64.getDecoder().decode(publicKeyText);
            X509EncodedKeySpec x509PublicKey = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(x509PublicKey);

            String secretKeyText = Encryption.EncodeStringBase64(secretKey.getEncoded());
            byte[] rawData = secretKeyText.getBytes(StandardCharsets.UTF_8);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedData = cipher.doFinal(rawData);
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(encryptedData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static SecretKey generateAESKey() {

        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            //generator.init(SecureRandom.getInstance("NativePRNG"));

            SecretKey secretKey = generator.generateKey();

            return secretKey;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static SecretKey generateAESKeyFromText(String text){
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(text.getBytes(), "AES");

            return secretKeySpec;
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

            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(encryptedData);
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
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(encryptedData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decryptAES(SecretKey secretKey, String encryptedDataBase64) {
        try {
            byte[] encryptedData = Base64.getDecoder().decode(encryptedDataBase64);

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
    private static String EncryptStringByShareKey(String shareKeyText, String text){
        SecretKey sharekey = generateAESKeyFromText(shareKeyText);
        return Encryption.encryptHmacSha256(sharekey, text);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String EncryptStringBySecretKey(SecretKey secretKey, String shareKeyText, String text){
        String encryptText = EncryptStringByShareKey(shareKeyText, text);
        return Encryption.encryptAES(secretKey, encryptText);
    }
}
