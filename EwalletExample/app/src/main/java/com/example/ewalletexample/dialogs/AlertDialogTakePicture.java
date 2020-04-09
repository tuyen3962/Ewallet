package com.example.ewalletexample.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AlertDialogTakePicture extends DialogFragment {

    AlertDialogTakePictureFunction function;

    public AlertDialogTakePicture(AlertDialogTakePictureFunction function){
        this.function = function;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final CharSequence[] options = {"Chụp ảnh","Chọn ảnh từ bộ sưu tầm", "Hủy bỏ"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tải ảnh");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                dialog.dismiss();
                if (options[item].equals("Chụp ảnh"))
                {
                    function.TakePhoto();
                }
                else if (options[item].equals("Chọn ảnh từ bộ sưu tầm")) {
                    function.ChoosePicture();
                }
            }
        });

        return builder.create();
    }
}
