package me.havard.assemblyfun.data;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import me.havard.assemblyfun.R;
import me.havard.assemblyfun.data.tables.TaskinfoTable;

/** A custom adapter for filling in many task_list_item
 * Created by Havard on 18.09.2015.
 */
public class TaskCursorAdapter extends SimpleCursorAdapter {

    private static final String[] FROM_COLUMNS = new String[] {TaskinfoTable.NAME, TaskinfoTable.DESC, TaskinfoTable.AUTHOR};
    private static final int[] TO_TEXT_VIEWS = new int[]{R.id.task_list_item_title, R.id.task_list_item_desc, R.id.task_list_item_author};

    public TaskCursorAdapter(Context context, Cursor cursor) {
        super(context, R.layout.task_list_item, cursor, FROM_COLUMNS, TO_TEXT_VIEWS, 0);
    }

    @Override
    public void bindView(@NonNull View view, @NonNull Context context, @NonNull Cursor cursor) {
        super.bindView(view, context, cursor);
        TextView diffText = (TextView) view.findViewById(R.id.task_list_item_difficulty);
        Difficulty diff = Difficulty.values()[cursor.getInt(cursor.getColumnIndex(TaskinfoTable.DIFFICULTY))];
        int flags = cursor.getInt(cursor.getColumnIndex(TaskinfoTable.FLAGS));
        diffText.setText(diff.getLabelId());
        diffText.setTextColor(ContextCompat.getColor(context, diff.getColorId()));

        view.findViewById(R.id.task_list_item_local).setVisibility(TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_LOCAL) ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.task_list_item_solved).setVisibility(TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_SOLVED)?View.VISIBLE:View.GONE);
        view.findViewById(R.id.task_list_item_published).setVisibility(TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_SELF_PUBLISHED)?View.VISIBLE:View.GONE);
        view.findViewById(R.id.task_list_item_favourite).setVisibility(TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_FAVOURITE)?View.VISIBLE:View.GONE);
        view.findViewById(R.id.task_list_item_global).setVisibility(TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_GLOBAL)?View.VISIBLE:View.GONE);
    }
}
