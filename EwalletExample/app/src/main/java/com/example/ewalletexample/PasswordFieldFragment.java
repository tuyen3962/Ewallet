package com.example.ewalletexample;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ewalletexample.service.ControlListImage;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswordFieldFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordFieldFragment extends Fragment {
    private static String defaultText;
    private static final String DEFAULT_TEXT = "DEFAULT_TEXT";

    private int visibilityTextDrawable, invisibilityTextDrawable;
    private List<ImageView> imageViewList;
    private TextView tvHint;
    private ImageButton btnTextVisibility;
    private View passwordLayout;
    private LoginActivity loginActivity;
    private RegisterByPhone registerByPhone;
    private ControlListImage controlListImage;
    private boolean isShowText;

    public PasswordFieldFragment() {
        // Required empty public constructor
    }

    public PasswordFieldFragment(String defaultText){
        this.defaultText = defaultText;
    }

    public static PasswordFieldFragment newInstance(String defaultText) {
        PasswordFieldFragment fragment = new PasswordFieldFragment();
        Bundle args = new Bundle();
        args.putString(DEFAULT_TEXT, defaultText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            defaultText = getArguments().getString(DEFAULT_TEXT);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof LoginActivity){
            loginActivity = (LoginActivity) context;
            registerByPhone = null;
        } else if(context instanceof RegisterByPhone){
            registerByPhone = (RegisterByPhone) context;
            loginActivity = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password_field, container, false);
        Initialize(view);
        ShowDefaultPassword();
        isShowText = false;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowNumberKeyboard();
            }
        });
        btnTextVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckTextVisibility();
            }
        });
        return view;
    }

    public void SetHintText(String text){
        defaultText = text;
    }

    public void DisablePasswordField(){
        controlListImage.SetCanEditText(false);
    }

    void Initialize(View view){
        visibilityTextDrawable = R.drawable.ic_action_visibility;
        invisibilityTextDrawable = R.drawable.ic_action_unvisibility;
        imageViewList = new ArrayList<>();
        imageViewList.add(view.findViewById(R.id.imgPass01));
        imageViewList.add(view.findViewById(R.id.imgPass02));
        imageViewList.add(view.findViewById(R.id.imgPass03));
        imageViewList.add(view.findViewById(R.id.imgPass04));
        imageViewList.add(view.findViewById(R.id.imgPass05));
        imageViewList.add(view.findViewById(R.id.imgPass06));
        btnTextVisibility = view.findViewById(R.id.imgVisibilityBtn);
        tvHint = view.findViewById(R.id.hintTextPassword);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        controlListImage = new ControlListImage(imageViewList);
        controlListImage.SetCanEditText(false);
        controlListImage.SetListImageViewWithBitmap();
        tvHint.setText(defaultText);
    }

    public void ShowDefaultPassword(){
        if (controlListImage.getTextByImage().isEmpty()){
            tvHint.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.GONE);
        }
    }

    void ShowNumberKeyboard(){
        isShowText = false;
        tvHint.setVisibility(View.GONE);
        passwordLayout.setVisibility(View.VISIBLE);
        controlListImage.SetCanEditText(true);
        btnTextVisibility.setImageResource(invisibilityTextDrawable);

        if (loginActivity != null){
            loginActivity.ShowNumberKeyBoard();
        } else if(registerByPhone != null) {
            registerByPhone.ShowNumberKeyBoard(getId());
        }
    }

    void CheckTextVisibility(){
        isShowText = !isShowText;
        if(isShowText){
            passwordLayout.setVisibility(View.GONE);
            tvHint.setVisibility(View.VISIBLE);
            btnTextVisibility.setImageResource(visibilityTextDrawable);
            if(controlListImage.getTextByImage().isEmpty()){
                tvHint.setText(defaultText);
            } else {
                tvHint.setText(controlListImage.getTextByImage());
            }
        } else {
            passwordLayout.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.GONE);
            btnTextVisibility.setImageResource(invisibilityTextDrawable);
        }
    }

    public void CheckIncreaseIndex(String text){
        if(controlListImage.CanEditText()){
            controlListImage.CheckIncreaseIndex(text);
            if(isShowText){
                tvHint.setText(controlListImage.getTextByImage());
            }
        }
    }

    public void CheckDecreaseIndex(){
        if(controlListImage.CanEditText()){
            controlListImage.CheckDecreaseIndex();
            if(isShowText){
                if(controlListImage.getTextByImage().isEmpty()){
                    tvHint.setText(defaultText);
                } else {
                    tvHint.setText(controlListImage.getTextByImage());
                }
            }
        }
    }

    public String getTextByImage() {
        return controlListImage.getTextByImage();
    }

    public void ClearText(){
        controlListImage.Clear();
        tvHint.setText(defaultText);
    }
}
