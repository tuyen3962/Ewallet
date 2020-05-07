package com.example.ewalletexample.service.recycleview.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class RecycleViewFriendProfileAdapter extends RecyclerView.Adapter<RecycleViewFriendProfileAdapter.FriendProfileViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<UserSearchModel> models;
    private FirebaseStorageHandler storageHandler;
    private UserSelectFunction function;

    public RecycleViewFriendProfileAdapter(Context context, FirebaseStorageHandler storageHandler, UserSelectFunction function, List<UserSearchModel> models){
        this.context = context;
        this.models = models;
        this.storageHandler = storageHandler;
        this.function = function;
        inflater = LayoutInflater.from(this.context);
    }

    @NonNull
    @Override
    public FriendProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FriendProfileViewHolder holder = new FriendProfileViewHolder(inflater.inflate(R.layout.form_friend_profile, parent, false), function);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendProfileViewHolder holder, int position) {
        UserSearchModel model = models.get(position);
        holder.SetFullName(model.getFullName());
        holder.SetPhone(model.getPhone());
        holder.SetImageUser(context, storageHandler, model.getImgLink());
        holder.SetButtonEvent(model);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class FriendProfileViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgUserProfile;
        private TextView tvFullname, tvPhone;
        private View itemView;
        private UserSelectFunction function;

        public FriendProfileViewHolder(@NonNull View itemView, UserSelectFunction function) {
            super(itemView);
            this.itemView = itemView;
            this.function = function;
            imgUserProfile = itemView.findViewById(R.id.imgUserProfile);
            tvFullname = itemView.findViewById(R.id.tvFullName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
        }

        public void SetFullName(String text){
            tvFullname.setText(text);
        }

        public void SetPhone(String text){
            tvPhone.setText(text);
        }

        public void SetImageUser(Context context, FirebaseStorageHandler storageHandler, String link){
            Utilies.LoadImageFromFirebase(context, storageHandler, link, imgUserProfile);
        }

        public void SetButtonEvent(UserSearchModel model){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    function.SelectModel(model);
                }
            });
        }
    }
}
