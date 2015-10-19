package me.havard.assemblyfun.data;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import me.havard.assemblyfun.R;
import me.havard.assemblyfun.data.tables.SolutionsTable;

/** An adapter for showing the metadata of solutions to a task.
 * Created by Havard on 05.10.2015.
 */
public class SolutionCursorAdapter extends SimpleCursorAdapter
{
    private static final String[] FROM_COLUMNS = new String[] {SolutionsTable.TITLE};
    private static final int[] TO_TEXT_VIEWS = new int[]{R.id.solution_list_item_name};

    public SolutionCursorAdapter(Context context, Cursor cursor) {
        super(context, R.layout.solution_list_item, cursor, FROM_COLUMNS, TO_TEXT_VIEWS, 0);
    }

    @Override
    public void bindView(@NonNull View view, @NonNull Context context, @NonNull Cursor cursor) {
        super.bindView(view, context, cursor);
        int quality = cursor.getInt(cursor.getColumnIndex(SolutionsTable.SOLUTION_QUALITY));
        String statusText;
        if(quality==SolutionsTable.QUALITY_NEVER_RUN)
            statusText = context.getResources().getString(R.string.label_task_screen_solution_never_run);
        else if(quality==SolutionsTable.QUALITY_FAILS)
            statusText = context.getResources().getString(R.string.label_task_screen_solution_failed);
        else if(quality==SolutionsTable.QUALITY_SOLVED)
            statusText = String.format("%s: %.2f,  %s: %d, %s: %.2f",
                    context.getResources().getString(R.string.label_task_screen_speed), cursor.getFloat(cursor.getColumnIndex(SolutionsTable.SPEED)),
                    context.getResources().getString(R.string.label_task_screen_size), cursor.getInt(cursor.getColumnIndex(SolutionsTable.SIZE)),
                    context.getResources().getString(R.string.label_task_screen_memuse), cursor.getFloat(cursor.getColumnIndex(SolutionsTable.MEMUSE)));
        else {
            Log.e("Assembly Fun", "the quality of the task solution with id " + cursor.getLong(cursor.getColumnIndex(SolutionsTable._ID)) + " isn't a valid solution quality! (0 <= x < 3) Found quality was: " + quality);
            statusText = "The solution quality " + quality + " isn't recognized as a valid solution quality";
        }
        ((TextView)view.findViewById(R.id.solution_list_item_status)).setText(statusText);

    }
}
