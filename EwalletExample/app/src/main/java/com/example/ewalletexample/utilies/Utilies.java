package com.example.ewalletexample.utilies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ewalletexample.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class Utilies {
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
}
