package com.example.splitmonk.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.splitmonk.R;

public class progressBarUtils {

    private static AlertDialog alertDialog = null;

    public static void showProgress(Context context, Activity activity){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.progress_bar, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }

    public static void hideProgress(){
        if(alertDialog != null){
            alertDialog.dismiss();
            alertDialog = null;
        }
    }
}
