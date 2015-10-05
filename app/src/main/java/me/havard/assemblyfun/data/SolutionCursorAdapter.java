package me.havard.assemblyfun.data;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
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
    private static final String[] FROM_COLUMNS = new String[] {SolutionsTable.NAME};
    private static final int[] TO_TEXT_VIEWS = new int[]{R.id.solution_list_item_name};

    public SolutionCursorAdapter(Context context, Cursor cursor) {
        super(context, R.layout.solution_list_item, cursor, FROM_COLUMNS, TO_TEXT_VIEWS, 0);
    }

    @Override
    public void bindView(@NonNull View view, @NonNull Context context, @NonNull Cursor cursor) {
        super.bindView(view, context, cursor);
        int quality = cursor.getInt(cursor.getColumnIndex(SolutionsTable.SOLUTION_QUALITY));
        String statusText;
        if(quality==0)
            statusText = context.getResources().getString(R.string.label_task_screen_solution_failed);
        else
            statusText = String.format("%s: %.2f,  %s: %d, %s: %.2f",
                    context.getResources().getString(R.string.label_task_screen_speed), cursor.getFloat(cursor.getColumnIndex(SolutionsTable.SPEED)),
                    context.getResources().getString(R.string.label_task_screen_size), cursor.getInt(cursor.getColumnIndex(SolutionsTable.SIZE)),
                            context.getResources().getString(R.string.label_task_screen_memuse), cursor.getFloat(cursor.getColumnIndex(SolutionsTable.MEMUSE)));
        ((TextView)view.findViewById(R.id.solution_list_item_status)).setText(statusText);

    }
}
