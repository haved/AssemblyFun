package me.havard.assemblyfun.data;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.SimpleCursorAdapter;

import me.havard.assemblyfun.R;

/** An adapter for showing the metadata of solutions to a task.
 * Created by Havard on 05.10.2015.
 */
public class SolutionCursorAdapter extends SimpleCursorAdapter
{
    private static final String[] FROM_COLUMNS = new String[] {};
    private static final int[] TO_TEXT_VIEWS = new int[]{};

    public SolutionCursorAdapter(Context context, Cursor cursor) {
        super(context, R.layout.task_list_item, cursor, FROM_COLUMNS, TO_TEXT_VIEWS, 0);
    }

    @Override
    public void bindView(@NonNull View view, @NonNull Context context, @NonNull Cursor cursor) {
        super.bindView(view, context, cursor);
    }
}
