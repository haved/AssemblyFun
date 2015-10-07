package me.havard.assemblyfun.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import me.havard.assemblyfun.R;

/** A helper class for creating alert dialogs
 * Created by Havard on 07.10.2015.
 */
public class DialogHelper {
    public static AlertDialog.Builder makeDialogBuilder(Context context, int title, int message, boolean cancelable, DialogInterface.OnClickListener okListener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.create();
        builder.setPositiveButton(R.string.dialog_button_OK, okListener);
        if(cancelable) {
            builder.setNegativeButton(R.string.dialog_button_Cancel, null);
            builder.setCancelable(true);
        }
        return builder;
    }
}
