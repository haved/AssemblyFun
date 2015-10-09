package me.havard.assemblyfun.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

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
            builder.setNegativeButton(nothingButton, null);
        builder.setCancelable(dismissible);
        return builder;
    }

    /**
     *
     * @param context A context
     * @param title the resource id of the string that is the title of the dialog. Pass -1 for no title
     * @param message the resource id of the string that is the message of the dialog. Pass -1 for no message
     * @param startText the text that is in the TextView to begin with.
     * @param listener A listener for the doButton
     * @param enterButtonText the resource id of the string that is the text of the enter button. Only shown if listener != null
     * @param cancelButtonText the resource id of the string that is the text of the cancel button. Pass -1 if the dialog isn't cancelable or dismissible
     * @return The AlertDialog.Builder instance. Use .show() on it to show the dialog.
     */
    public static AlertDialog.Builder makeInputDialogBuilder(Context context, int title, int message, CharSequence startText, final TextDialogListener listener, int enterButtonText, int cancelButtonText){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        if(title!=-1)
            dialog.setTitle(title);
        if(message!=-1)
            dialog.setMessage(message);
        final EditText text = new EditText(context);
        text.setInputType(InputType.TYPE_CLASS_TEXT);
        text.setText(startText);
        dialog.setView(text);

        if(listener!=null) {
            dialog.setPositiveButton(enterButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onTextEntered(text.getText().toString());
                }
            });
        }
        if(cancelButtonText != -1) {
            dialog.setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog.setCancelable(true);
        }

        return dialog;
    }

    public interface TextDialogListener {
        void onTextEntered(String text);
    }
}
