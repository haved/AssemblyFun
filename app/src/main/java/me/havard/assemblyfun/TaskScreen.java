package me.havard.assemblyfun;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import me.havard.assemblyfun.data.Difficulty;
import me.havard.assemblyfun.data.SQLiteCursorLoader;
import me.havard.assemblyfun.data.TaskInfoAndRecordsCursorLoader;
import me.havard.assemblyfun.data.tables.TaskRecordsTable;
import me.havard.assemblyfun.data.tables.TaskinfoTable;
import me.havard.assemblyfun.util.MonthLabels;

public class TaskScreen extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRAS_TASK_ID = "extrasTaskID";
    private static final int LOADER_ID_TASK_CURSOR = 0;

    private static final String TASK_QUERY = "SELECT * FROM " + TaskinfoTable.TABLE_NAME + " WHERE " + TaskinfoTable._ID_TaskIDs + " = ?";

    private long mLocalID;

    private TextView mTaskTitle, mTaskDesc, mTaskDiff, mTaskDate, mTaskAuthor, mTaskRecordsText;
    private LinearLayout mButtonList;
    private RelativeLayout mOnlineRow, mSelfPublishedRow;
    private Button mLocalButton, mFavouriteButton;
    private ImageView mLocalIcon, mFavouriteIcon, mSolveIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_screen);

        mTaskTitle = (TextView) findViewById(R.id.task_screen_task_title);
        mTaskDesc = (TextView) findViewById(R.id.task_screen_task_desc);
        mTaskDiff = (TextView) findViewById(R.id.task_screen_task_diff);
        mTaskDate = (TextView) findViewById(R.id.task_screen_task_date);
        mTaskAuthor = (TextView) findViewById(R.id.task_screen_task_author);
        mTaskRecordsText = (TextView) findViewById(R.id.task_screen_task_records);

        mButtonList = (LinearLayout)findViewById(R.id.task_screen_button_list);
        mOnlineRow = (RelativeLayout)findViewById(R.id.task_screen_online_row);
        mSelfPublishedRow = (RelativeLayout)findViewById(R.id.task_screen_self_published_row);

        mLocalButton = (Button) findViewById(R.id.task_screen_local_button);
        mFavouriteButton = (Button) findViewById(R.id.task_screen_favourite_button);

        mLocalIcon = (ImageView) findViewById(R.id.task_screen_local_icon);
        mSolveIcon = (ImageView) findViewById(R.id.task_screen_solved_icon);
        mFavouriteIcon = (ImageView) findViewById(R.id.task_screen_favourite_icon);

        mTaskTitle.setText(R.string.label_loading);
        mButtonList.setVisibility(View.GONE);

        mLocalID = getIntent().getExtras().getLong(EXTRAS_TASK_ID);
        getLoaderManager().initLoader(LOADER_ID_TASK_CURSOR, null, this);
    }

    protected void useCursor(Cursor cursor)
    {
        cursor.moveToFirst();
        if(cursor.isAfterLast()) {
            mTaskTitle.setText("No task with task id '" + mLocalID + "' was found in the database!");
            return;
        }

        mTaskTitle.setText(cursor.getString(cursor.getColumnIndex(TaskinfoTable.NAME)));
        mTaskDesc.setText(cursor.getString(cursor.getColumnIndex(TaskinfoTable.DESC)));
        mTaskAuthor.setText(cursor.getString(cursor.getColumnIndex(TaskinfoTable.AUTHOR)));
        Difficulty diff = Difficulty.values()[cursor.getInt(cursor.getColumnIndex(TaskinfoTable.DIFFICULTY))];
        mTaskDiff.setText(diff.getLabelId());
        mTaskDiff.setTextColor(ContextCompat.getColor(this, diff.getColorId()));
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(TaskinfoTable.DATE)));
        mTaskDate.setText(getResources().getString(MonthLabels.RESOURCE_IDS[cl.get(Calendar.MONTH)])+ " " + cl.get(Calendar.DAY_OF_MONTH) + " " + cl.get(Calendar.YEAR));

        mButtonList.setVisibility(View.VISIBLE);
        int flags = cursor.getInt(cursor.getColumnIndex(TaskinfoTable.FLAGS));
        boolean local = TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_LOCAL), global = TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_GLOBAL),
                favourite = TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_FAVOURITE);
        mLocalButton.setText(global ?
                (local ? R.string.label_task_screen_remove_locally : R.string.label_task_screen_store_locally)
                : R.string.label_task_screen_only_locally);
        mLocalButton.setEnabled(global);
        mLocalIcon.setVisibility(local ? View.VISIBLE : View.INVISIBLE);
        mOnlineRow.setVisibility(TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_GLOBAL) ? View.VISIBLE:View.GONE);
        mSelfPublishedRow.setVisibility(TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_SELF_PUBLISHED) ? View.VISIBLE : View.GONE);
        mFavouriteButton.setText(favourite ? R.string.label_task_screen_un_favourite : R.string.label_task_screen_favourite);
        mFavouriteIcon.setVisibility(favourite ? View.VISIBLE : View.INVISIBLE);
        mSolveIcon.setVisibility(TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_SOLVED)?View.VISIBLE:View.INVISIBLE);

        String recordText;
        if(cursor.getColumnIndex(TaskRecordsTable.SPEED_REC)>=0)
            recordText = String.format("%s\n" +
                            "%s - '%s': %.2f, %s: %.2f\n" +
                            "%s - '%s': %.2f, %s: %.2f\n" +
                            "%s - '%s': %.2f, %s: %.2f", getResources().getString(R.string.label_task_screen_records__),
                    getResources().getString(R.string.label_task_screen_speed),
                    cursor.getString(cursor.getColumnIndex(TaskRecordsTable.SPEED_REC_NAME)), cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.SPEED_REC)),
                    getResources().getString(R.string.label_task_screen_records_you), cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.YOUR_SPEED_REC)),

                    getResources().getString(R.string.label_task_screen_size),
                    cursor.getString(cursor.getColumnIndex(TaskRecordsTable.SIZE_REC_NAME)), cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.SIZE_REC)),
                    getResources().getString(R.string.label_task_screen_records_you), cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.YOUR_SIZE_REC)),

                    getResources().getString(R.string.label_task_screen_memuse),
                    cursor.getString(cursor.getColumnIndex(TaskRecordsTable.MEMUSE_REC_NAME)), cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.MEMUSE_REC)),
                    getResources().getString(R.string.label_task_screen_records_you), cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.YOUR_MEMUSE_REC)));
        else
            recordText = getResources().getString(R.string.label_task_screen_no_records);

        mTaskRecordsText.setText(recordText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new TaskInfoAndRecordsCursorLoader(this, ((AssemblyFunApplication)getApplication()).getDatabase(), mLocalID);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        useCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Well. No reason to do anything really
    }
}
