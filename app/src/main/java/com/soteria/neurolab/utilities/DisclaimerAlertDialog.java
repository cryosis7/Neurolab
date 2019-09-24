package com.soteria.neurolab.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
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
    public void showDisclaimer(Context callingClass, Resources calledResource) {
        final AlertDialog.Builder disclaimerBuilder = new AlertDialog.Builder(callingClass);
        //Sets the alertDialog title
        disclaimerBuilder.setTitle("Disclaimer");
        //Inserts the disclaimer into the alertDialog
        disclaimerBuilder.setMessage(calledResource.getString(R.string.disclaimer_body));

        //Sets the positive button name to "confirm" and if pressed closes the dialog
        disclaimerBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener()  {
        @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
         });

        //Create the alertDialog
        final AlertDialog showDisclaimer = disclaimerBuilder.create();
        //Change the button colour of the alert dialogs positive button to be the primary colour
        showDisclaimer.setOnShowListener( new DialogInterface.OnShowListener() {
        @Override
            public void onShow(DialogInterface arg0) {
                showDisclaimer.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
            }
        });
    //Displays the alertDialog
    showDisclaimer.show();
    //Sets the text size of the disclaimer message to be 24dp
    TextView bodyText = showDisclaimer.findViewById(android.R.id.message);
    bodyText.setTextSize(24);
    }
}
