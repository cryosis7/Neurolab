package com.soteria.neurolab.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.soteria.neurolab.R;

/**
 *  Holds the information for the alert dialog with displays the disclaimer to the user.
 *  In order to call this in your class, use the following code:
 *
 *  DisclaimerAlertDialog dad = new DisclaimerAlertDialog();
 *  dad.showDisclaimer(this, getResources());
 *
 */
public class DisclaimerAlertDialog {
    public void showDisclaimer(Context callingClass, Resources resources) {
        final AlertDialog.Builder disclaimerBuilder = new AlertDialog.Builder(callingClass);

        disclaimerBuilder.setTitle(resources.getString(R.string.disclaimer));
        disclaimerBuilder.setMessage(resources.getString(R.string.disclaimer_body));

        //If the confirm button is pressed, close the dialog
        disclaimerBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        
        final AlertDialog showDisclaimer = disclaimerBuilder.create();
        //Change the button colour of the alert dialog to be the primary colour
        showDisclaimer.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                showDisclaimer.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
            }
        });
        showDisclaimer.show();
        TextView bodyText = showDisclaimer.findViewById(android.R.id.message);
        bodyText.setTextSize(24);
    }

    public void showDisclaimerWithCancel(final Context callingClass, final Resources resources, final CheckBox checkBox) {
        final AlertDialog.Builder disclaimerBuilder = new AlertDialog.Builder(callingClass);

        disclaimerBuilder.setTitle(resources.getString(R.string.disclaimer))
                .setMessage(resources.getText(R.string.disclaimer_body))
                .setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        checkBox.setChecked(false);
                    }
                });

        final AlertDialog showDisclaimer = disclaimerBuilder.create();

        //Change the button colour of the alert dialog to be the primary colour
        showDisclaimer.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                showDisclaimer.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
                showDisclaimer.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(20);
            }
        });
        showDisclaimer.show();
        TextView bodyText = showDisclaimer.findViewById(android.R.id.message);
        bodyText.setTextSize(24);
    }
}
