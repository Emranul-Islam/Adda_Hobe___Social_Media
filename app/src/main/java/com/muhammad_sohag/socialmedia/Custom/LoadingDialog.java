package com.muhammad_sohag.socialmedia.Custom;

import android.app.Activity;
import android.view.LayoutInflater;
import androidx.appcompat.app.AlertDialog;

import com.muhammad_sohag.socialmedia.R;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog alertDialog;

   public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

   public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.progress_dialog_layout, null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
    }

   public void dismissLoadingDialog() {
        alertDialog.dismiss();
    }
}
