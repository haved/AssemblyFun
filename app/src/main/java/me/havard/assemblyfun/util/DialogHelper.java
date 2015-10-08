package me.havard.assemblyfun.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import me.havard.assemblyfun.R;

/** A helper class for creating alert dialogs
 * Created by Havard on 07.10.2015.
 */
public class DialogHelper {
    /**
     *
     * @param context A context
     * @param title the resource id of the string that is the title of the dialog
     * @param message the resource id of the string that is the message of the dialog
     * @param doButtonLister A listener for the doButton
     * @param doButtonText the resource id of the string that is the text of the do button. Only used if doButtonListener != null
     * @param nothingButton the resource id of the string that is the text of the do nothing button. Pass -1 for no button
     * @param dismissible weather or not you can dismiss by tapping outside of the dialog
     * @return The AlertDialog.Builder instance. Use .show() on it to show the dialog.
     */
    public static AlertDialog.Builder makeDialogBuilder(Context context, int title, int message, DialogInterface.OnClickListener doButtonLister, int doButtonText, int nothingButton, boolean dismissible)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.create();
        if(doButtonLister != null)
            builder.setPositiveButton(doButtonText, doButtonLister);
        if(nothingButton!=-1)
            builder.setNeutralButton(nothingButton, null);
        builder.setCancelable(dismissible);
        return builder;
    }
}
