package com.example.ewalletexample.utilies;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ewalletexample.R;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.google.gson.Gson;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class Utilies {
    private Gson gson = new Gson();

    public static void SetTitle(AppCompatActivity activity, Context context, String title){
        ActionBar actionBar = activity.getSupportActionBar();
        TextView textView = new TextView(context);
        textView.setText(title);
        textView.setTextSize(20);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(context.getResources().getColor(R.color.SoftWhite));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(textView);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public static void SetImageDrawable(Context context, CircleImageView imageView, int id){
        imageView.setImageDrawable(context.getResources().getDrawable(id, null));
    }

    public static void SetImageDrawable(Context context, CircleImageView imageView){
        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_person, null));
    }

    public static void SetImageDrawable(Context context, ImageView imageView, int id){
        imageView.setImageDrawable(context.getResources().getDrawable(id, null));
    }

    public static void SetImageDrawable(Context context, ImageView imageView){
        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_person, null));
    }

    public static String GetStringNumberByLength(int length){
        Random random = new Random();
        String result = "";
        for (int i = 0 ; i < length; i++){
            result += String.valueOf(random.nextInt(10));
        }

        return result;
    }

    public static void HideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void LoadImageFromFirebase(Context context, FirebaseStorageHandler storageHandler, String imageLink, ImageView image){
        if (imageLink.equalsIgnoreCase("")){
            SetImageDrawable(context, image);
        }
        else{
            storageHandler.LoadAccountImageFromLink(imageLink, image);
        }
    }

    public static void InitializeRecycleView(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager, RecyclerView.Adapter adapter){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public static void AddFragmentIntoActivity(AppCompatActivity appCompatActivity, int fragmentId, Fragment fragment, String tag){
        appCompatActivity.getSupportFragmentManager().beginTransaction().replace(fragmentId, fragment, tag).commit();
    }

    public static List<String> LoadListContact(Context context){
        List<String> contacts = new ArrayList<>();

        Cursor cursor = null;
        ContentResolver contentResolver = context.getContentResolver();
        try{
            cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null,null,null, null);
        } catch (Exception e){
            Log.d("TAG", "Error on contact : " + e.getMessage());
        }

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                            , null
                            , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                            , new String[]{contact_id}
                            , null);

                    while (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumber = phoneNumber.replaceAll("\\(","");
                        phoneNumber = phoneNumber.replaceAll("\\)","");
                        phoneNumber = phoneNumber.replaceAll("-", "");
                        contacts.add(phoneNumber.replaceAll(" ",""));
                    }
                    phoneCursor.close();
                }
            }
        }
        return contacts;
    }

    public static String HandleBalanceTextView(String balance){
        int index = balance.length()-3;
        int startIndexAddDot = 3, indexIncrementAddDot = 4;
        int indexAddDot = startIndexAddDot;

        while(balance.length() > indexAddDot && index > 0){
            String headText = balance.substring(0,index);
            String lastText = balance.substring(index);
            balance = headText + "," + lastText;

            indexAddDot += indexIncrementAddDot;
            index -= startIndexAddDot;
        }

        return balance;
    }

    public static byte[] ConvertStringToByte(String code){
        return code.getBytes(Charset.forName("UTF-8"));
    }

    public static String ConvertByteToString(byte[] bytes){
        return new String(bytes);
    }
}
