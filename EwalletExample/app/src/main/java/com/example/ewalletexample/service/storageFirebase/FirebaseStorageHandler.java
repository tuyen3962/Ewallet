package com.example.ewalletexample.service.storageFirebase;

import android.content.Context;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.example.ewalletexample.R;
import com.example.ewalletexample.service.ResponseMethod;
import com.example.ewalletexample.utilies.FileProcessor;
import com.example.ewalletexample.utilies.ImageProcessor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class FirebaseStorageHandler {
    private StorageReference storageReference;
    private Context currentContext;
    private CircleImageView imgView;

    private FileProcessor fileProcessor;
    private ImageProcessor imageProcessor;

    public FirebaseStorageHandler(CircleImageView imgView, FirebaseStorage storage, Context applicationContext){
        storageReference = storage.getReference();
        this.imgView = imgView;
        this.currentContext = applicationContext;
        fileProcessor = new FileProcessor(applicationContext);
        imageProcessor = new ImageProcessor(applicationContext);
    }

    public FirebaseStorageHandler(FirebaseStorage storage, Context applicationContext){
        storageReference = storage.getReference();
        currentContext = applicationContext;
    }

    public void LoadAccountImageFromLink(String linkImg, final ResponseMethod method){
        storageReference.child(linkImg).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(currentContext).load(uri).into(imgView);
                method.HideProgressBar();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imgView.setImageDrawable(ResourcesCompat.getDrawable(currentContext.getResources(), R.drawable.ic_action_account, null));
            }
        });
    }

    public void GetUriImageFromServerFile(String link, final ResponseImageUri response){

        storageReference.child(link).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                response.GetImageUri(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                response.GetImageUri(null);
            }
        });
    }

    public File createImageFile() throws IOException {
        return fileProcessor.createImageFile();
    }

    public File compressToFile(File photoFile) throws IOException {
        return fileProcessor.compressToFile(photoFile);
    }

    public void SetPictureInImageView(String currentPhotoPath){
        imageProcessor.SetPictureInImageView(currentPhotoPath,imgView);
    }

    public Uri AddImageToGallery(String currentPhotoPath){
        return imageProcessor.AddImageToGallery(currentPhotoPath);
    }

    public void UploadImage(Uri filePath, final ResponseMethod method){
        final String serverFile = "images/"+ UUID.randomUUID().toString();

        storageReference.child(serverFile).putFile(filePath).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                        .getTotalByteCount());
                method.SetMessageProgressBar("Uploaded "+(int)progress+"%");
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    method.HideProgressBar();
                    imgView.setImageBitmap(null);
                    method.GetImageServerFile(serverFile);
                }
            }
        });
    }
}
