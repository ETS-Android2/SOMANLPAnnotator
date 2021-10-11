package com.cyph.somanlpannotator.HelperMethods;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cyph.somanlpannotator.R;

/***
 * Controls all types of custom alert dialogs within the application
 */
public class ShowDialogWithMessage {

    /***
     * Shows an information alert dialog with one button to dismiss the dialog
     * @param context Activity context that calls this method
     * @param messageString The message/information to display on the dialog
     */
    public static void showDialogWithMessage(Context context, String messageString) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_unary_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialog_message);
        Button OK = (Button) dialog.findViewById(R.id.option_one);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            return;
        }

        message.setText(messageString);

        OK.setText(R.string.ok);

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
