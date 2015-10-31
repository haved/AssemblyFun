package me.havard.assemblyfun.util.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/** A TextView you can set to always show line numbers
 * Created by Havard on 31.10.2015.
 */
public class LineNumberText extends TextView {

    private EditText mEditText;
    private Watcher mWatcher = new Watcher();

    public LineNumberText(Context context){super(context);}
    public LineNumberText(Context context, AttributeSet set){super(context, set);}
    public LineNumberText(Context context, AttributeSet set, int i){super(context, set, i);}

    private int mEditVerticalScroll = 0;

    public void setEditText(EditText text) {
        mEditText=text;
        text.addTextChangedListener(mWatcher);
        this.setGravity(text.getGravity());
    }

    public void updateLineNumbers() {
        Log.d("Assembly Fun", "Updated line numbers");
        StringBuilder builder = new StringBuilder();
        int lineCount = mEditText.getLineCount();
        for(int i = 1; i <= lineCount; i++)
            builder.append(i).append("\n");

        LineNumberText.this.setText(builder.toString());
    }

    private class Watcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateLineNumbers();
        }
    }
}
