package com.soteria.neurolab.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.soteria.neurolab.R;

public class DisclaimerAlertDialog {
    public void showDisclaimer(Context callingClass, Resources calledResource) {
        final AlertDialog.Builder disclaimerBuilder = new AlertDialog.Builder(callingClass);
        disclaimerBuilder.setTitle("Disclaimer");
        disclaimerBuilder.setMessage(calledResource.getString(R.string.disclaimer_body));

        //If the confirm button is pressed, close the dialog
        disclaimerBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener()  {
        @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
         });
        final AlertDialog showDisclaimer = disclaimerBuilder.create();
        //Change the button colour of the alert dialog to be the primary colour
        showDisclaimer.setOnShowListener( new DialogInterface.OnShowListener() {
        @Override
            public void onShow(DialogInterface arg0) {
                showDisclaimer.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
            }
        });
    showDisclaimer.show();
    TextView bodyText = showDisclaimer.findViewById(android.R.id.message);
    bodyText.setTextSize(24);
    }
}
