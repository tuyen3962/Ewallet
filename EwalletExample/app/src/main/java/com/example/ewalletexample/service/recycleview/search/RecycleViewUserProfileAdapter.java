package com.example.ewalletexample.service.recycleview.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ewalletexample.R;
import com.example.ewalletexample.model.UserSearchModel;
import com.example.ewalletexample.service.UserSelectFunction;
import com.example.ewalletexample.service.storageFirebase.FirebaseStorageHandler;
import com.example.ewalletexample.utilies.Utilies;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleViewUserProfileAdapter extends RecyclerView.Adapter<RecycleViewUserProfileAdapter.UserProfileViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<UserSearchModel> searchModels;
    private FirebaseStorageHandler storageHandler;
    private UserSelectFunction function;

    public RecycleViewUserProfileAdapter(Context context, UserSelectFunction function, List<UserSearchModel> models, FirebaseStorageHandler storageHandler){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.storageHandler = storageHandler;
        this.searchModels = models;
        this.function = function;
    }

    public class UserProfileViewHolder extends RecyclerView.ViewHolder{
        Context context;
        FirebaseStorageHandler storageHandler;
        ImageView imgUser;
        TextView tvName, tvPhone;
        Button select;
        UserSelectFunction function;

        public UserProfileViewHolder(@NonNull View itemView, FirebaseStorageHandler storageHandler, Context context, UserSelectFunction function) {
            super(itemView);
            this.storageHandler = storageHandler;
            this.context = context;
            this.function = function;
            imgUser = itemView.findViewById(R.id.imgUserProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            select = itemView.findViewById(R.id.btnAdd);
        }

        public void SetName(String name){
            tvName.setText(name);
        }

        public void SetPhone(String phone){
            tvPhone.setText(phone);
        }

        public void SetImage(String image){
            Utilies.LoadImageFromFirebase(context, storageHandler, image, imgUser);
        }

        public void LoadButtonEvent(UserSearchModel model){
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    function.SelectModel(model);
                }
            });
        }
    }

    @NonNull
    @Override
    public UserProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserProfileViewHolder holder = new UserProfileViewHolder(inflater.inflate(R.layout.form_user_profile, parent, false), storageHandler, context, function);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileViewHolder holder, int position) {
        UserSearchModel model = searchModels.get(position);
        holder.SetName(model.getFullName());
        holder.SetPhone(model.getPhone());
        holder.SetImage(model.getImgLink());
        holder.LoadButtonEvent(model);
    }

    @Override
    public int getItemCount() {
        return searchModels.size();
    }
}
