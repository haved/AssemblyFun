package me.havard.assemblyfun.util.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/** An EditText that lets you listen for scrolling
 * Created by Havard on 31.10.2015.
 */
public class ScrollListenEditText extends EditText {

    public ScrollListenEditText(Context context) {super(context);}
    public ScrollListenEditText(Context context, AttributeSet attrs) {super(context, attrs);}
    public ScrollListenEditText(Context context, AttributeSet attrs, int defStyleAttr) {super(context, attrs, defStyleAttr);}

    private OnScrollListener mOnScrollListener;

    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    @Override
    public void onScrollChanged(int horizontal, int vertical, int oldHorizontal, int oldVertical) {
        super.onScrollChanged(horizontal, vertical, oldHorizontal, oldVertical);
        mOnScrollListener.onScrollChanged(this, horizontal, vertical, oldHorizontal, oldVertical);
    }

    public interface OnScrollListener {
        void onScrollChanged(View v, int horizontal, int vertical, int oldHorizontal, int oldVertical);
    }
}
