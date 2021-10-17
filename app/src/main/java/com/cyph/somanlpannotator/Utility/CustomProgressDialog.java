package com.cyph.somanlpannotator.Utility;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyph.somanlpannotator.R;

public class CustomProgressDialog {
    private Context context;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;

    public CustomProgressDialog(Context context) {
        this.context = context;
    }

    public void show() {
        if (context != null) {
            this.dialogBuilder = new AlertDialog.Builder(this.context);
            final View dialogView = LayoutInflater.from(this.context).inflate(R.layout.custom_progress_dialog, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            this.alertDialog = dialogBuilder.create();
            this.alertDialog.show();
            this.alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void dismiss() {
        if (alertDialog != null)
            alertDialog.dismiss();
    }
}
